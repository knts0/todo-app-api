package controllers

import javax.inject._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n.I18nSupport

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import lib.model.Todo

import forms.TodoForm
import forms.TodoEditData

import model.ViewValueTodoList
import model.ViewValueTodoAddForm
import model.ViewValueTodoEditForm
import model.ViewValueTodoDetail

import useCase.TodoUseCase
import useCase.TodoCategoryUseCase

class TodoController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {
  def index() = Action.async { implicit req =>
    for {
      todoList <- TodoUseCase.all
      categories <- TodoCategoryUseCase.all
    } yield {
      val vv = ViewValueTodoList(todoList, categories)
      Ok(views.html.todo.Index(vv))
    }
  }

  def add() = Action.async { implicit req =>
    for {
      categories <- TodoCategoryUseCase.all
    } yield {
      val form = TodoForm.add
      val vv = ViewValueTodoAddForm(categories)
      Ok(views.html.todo.Add(vv, form))
    }
  }

  def create() = Action.async { implicit req =>
    val form = TodoForm.add

    form.bindFromRequest.fold(
      formWithErrors => {
        for {
          categories <- TodoCategoryUseCase.all
        } yield {
          val form = TodoForm.add
          val vv = ViewValueTodoAddForm(categories)

          // binding failure, you retrieve the form containing errors:
          BadRequest(views.html.todo.Add(vv, formWithErrors))
        }
      },
      todoData => {
        for {
          id <- TodoUseCase.create(todoData.title, todoData.body, todoData.categoryId)
        } yield {
          /* binding success, you get the actual value. */
          Redirect(routes.TodoController.show(id))
        }
      }
    )
  }

  def edit(id: Long) = Action.async { implicit req =>
    for {
      todo       <- getTodo(id)
      categories <- TodoCategoryUseCase.all
    } yield {
      val vv = ViewValueTodoEditForm(todo.id, categories)
      val form = TodoForm.edit.fill(TodoEditData(todo.v.title, todo.v.body, todo.v.state.code, todo.v.categoryId))
      Ok(views.html.todo.Edit(vv, form))
    }
  }

  def update(id: Long) = Action.async { implicit req => 
    val form = TodoForm.edit

    form.bindFromRequest.fold(
      formWithErrors => {
        for {
          todo       <- getTodo(id)
          categories <- TodoCategoryUseCase.all
        } yield {
          val vv = ViewValueTodoEditForm(todo.id, categories)
          // binding failure, you retrieve the form containing errors:
          BadRequest(views.html.todo.Edit(vv, formWithErrors))
        }
      },
      todoData => {
        for {
          todo <- getTodo(id)
          _    <- TodoUseCase.update(todo, todoData.title, todoData.body, Todo.Status(todoData.state.toShort), todoData.categoryId)
        } yield {
          Redirect(routes.TodoController.show(id))
        }
      }
    )
  }

  def show(id: Long) = Action.async { implicit req =>
    for {
      todo <- getTodo(id)
      category <- TodoCategoryUseCase.get(todo.v.categoryId)
    } yield {
      val vv = ViewValueTodoDetail(todo, category)
      Ok(views.html.todo.Show(vv))
    }
  }

  def remove(id: Long) = Action.async { implicit req =>
    for {
      todo <- TodoUseCase.remove(Todo.Id(id))
    } yield {
      todo match {
        case None => Redirect(routes.TodoController.index)
        case _    => Redirect(routes.TodoController.index).flashing("success" -> "Todoを削除しました。")
      }
    }
  }

  private def getTodo(id: Long): Future[Todo.EmbeddedId] = {
    TodoUseCase.get(Todo.Id(id)).map(
      _ match {
        case Some(todo) => todo
        case _ => throw new Exception // TODO 404ページに遷移
      }
    )
  }
}
