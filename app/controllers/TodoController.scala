/** to do sample project
  */

package controllers

import json.reads.JsValueCreateTodo

import javax.inject._
import json.writes.JsValueTodoList
import play.api.mvc._
import play.api.data._
import play.api.i18n.I18nSupport
import model.view.ViewValueTodoCreate
import model.view.ViewValueTodoEdit
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
      categories <- TodoCategoryService.all
      todo <- TodoService.get(id)
    } yield {
      val vv = ViewValueTodoEdit(categories, todo)
      Ok(
        views.html.todo.Edit(
          vv,
          todoEditForm.fill(
            TodoEdit(
              title = todo.title,
              body = todo.body,
              state = todo.state.code,
              categoryId = todo.categoryId
            )
          )
        )
      )
    }) recover { 
      case _: Exception =>
            NotFound(views.html.error.page404(ViewValueError()))
    }
  }

  def update(id: Long) = Action.async { implicit req =>
    todoEditForm
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[TodoEdit]) => {
          for {
            categories <- TodoCategoryService.all
            todo <- TodoService.get(id)
          } yield {
            val vv = ViewValueTodoEdit(categories, todo)
            Ok(
              views.html.todo.Edit(
                vv,
                formWithErrors
              )
            )
          }
        },
        (todoData: TodoEdit) =>
          (
            for {
              _ <- TodoService.update(id, todoData)
            } yield Redirect(routes.TodoController.index())
          ) recover { case _: Exception =>
            NotFound(views.html.error.page404(ViewValueError()))
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
