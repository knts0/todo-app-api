/** to do sample project
  */

package controllers

import json.reads.{JsValueCreateTodo, JsValueEditTodo}

import javax.inject._
import json.writes.{JsValueTodo, JsValueTodoList}
import play.api.mvc._
import play.api.i18n.I18nSupport
import model.view.ViewValueTodoCreate
import model.ViewValueError
import play.api.libs.json.Json
import play.api.libs.json._
import service.TodoService
import service.TodoCategoryService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TodoController @Inject() (val controllerComponents: ControllerComponents)
    extends BaseController
    with I18nSupport {

  import forms.TodoForm._

  def index() = Action.async { implicit req =>
    for {
      todoList <- TodoService.all
    } yield {
      val jsValue = JsValueTodoList.apply(todoList)
      Ok(Json.toJson(jsValue))
    }
  }

  def create() = Action.async { implicit req =>
    for {
      categories <- TodoCategoryService.all
    } yield {
      val vv = ViewValueTodoCreate(categories)
      Ok(views.html.todo.Create(vv, todoAddForm))
    }
  }

  def save() = Action(parse.json).async { implicit req =>
    req.body
      .validate[JsValueCreateTodo]
      .fold(
        errors => {
          Future.successful(
            BadRequest(Json.obj("message" -> JsError.toJson(errors)))
          )
        },
        todoData => {
          for {
            _ <- TodoService.add(todoData)
          } yield {
            Ok(Json.obj("result" -> "OK!"))
          }
        }
      )
  }

  def edit(id: Long) = Action.async { implicit req =>
    (for {
      todo <- TodoService.get(id)
    } yield {
      Ok(
        Json.toJson(
          JsValueTodo.apply(todo)
        )
      )
    }) recover { 
      case _: Exception =>
            NotFound(views.html.error.page404(ViewValueError()))
    }
  }

  def update(id: Long) = Action.async(parse.json) { implicit req =>
    req.body
      .validate[JsValueEditTodo]
      .fold(
        errors => {
          Future.successful(
            BadRequest(Json.obj("message" -> JsError.toJson(errors)))
          )
        },
        todoData =>
          (
            for {
              _ <- TodoService.update(id, todoData)
            } yield NoContent
          ) recover { case _: Exception =>
            NotFound
          }
      )
  }

  def delete(id: Long) = Action.async { implicit req =>
    (for {
      _ <- TodoService.delete(id)
    } yield Redirect(routes.TodoController.index())) recover {
      case _: Exception =>
        NotFound(views.html.error.page404(ViewValueError()))
    }
  }
}
