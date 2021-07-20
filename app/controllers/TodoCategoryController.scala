package controllers

import javax.inject._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n.I18nSupport

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import lib.model.TodoCategory

import forms.TodoCategoryForm
import forms.TodoCategoryData

import model.ViewValueHome
import model.ViewValueTodoCategoryList
import model.ViewValueTodoCategoryForm

import useCase.TodoCategoryUseCase

class TodoCategoryController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {
  def index() = Action.async { implicit req =>
    for {
      categories <- TodoCategoryUseCase.all
    } yield {
      val vv = ViewValueTodoCategoryList(categories)
      Ok(views.html.todoCategory.Index(vv))
    }
  }

  def add() = Action { implicit req =>
    val form = TodoCategoryForm()
    val vv   = ViewValueTodoCategoryForm()

    Ok(views.html.todoCategory.Add(vv, form))
  }

  def create() = Action.async { implicit req =>
    val form = TodoCategoryForm()

    form.bindFromRequest.fold(
      formWithErrors => {
        Future {
          val vv = ViewValueTodoCategoryForm()
          BadRequest(views.html.todoCategory.Add(vv, formWithErrors))
        } 
      },
      todoCategoryData => {
        for {
          id <- TodoCategoryUseCase.create(todoCategoryData.name, todoCategoryData.slug, TodoCategory.Color(todoCategoryData.color.toShort))
        } yield {
          /* binding success, you get the actual value. */
          Redirect(routes.TodoCategoryController.index)
        }
      }
    )
  }

  def edit(id: Long) = Action.async { implicit req =>
    for {
      category <- getTodoCategory(id)
    } yield {
      val form = TodoCategoryForm().fill(TodoCategoryData(category.v.name, category.v.slug, category.v.color.code))
      val vv = ViewValueTodoCategoryForm(category.id)
      Ok(views.html.todoCategory.Edit(vv, form))
    }    
  }

  def update(id: Long) = Action.async { implicit req =>
    val form = TodoCategoryForm()

    form.bindFromRequest.fold(
      formWithErrors => {
        for {
          category <- getTodoCategory(id)
        } yield {
          val vv = ViewValueTodoCategoryForm(category.id)
          BadRequest(views.html.todoCategory.Edit(vv, formWithErrors))
        }
      },
      todoCategoryData => {
        for {
          category <- getTodoCategory(id)
          id       <- TodoCategoryUseCase.update(category, todoCategoryData.name, todoCategoryData.slug, TodoCategory.Color(todoCategoryData.color.toShort))
        } yield {
          /* binding success, you get the actual value. */
          Redirect(routes.TodoCategoryController.index)
        }
      }
    )
  }

  def remove(id: Long) = Action.async { implicit req =>
    for {
      todo <- TodoCategoryUseCase.remove(TodoCategory.Id(id))
    } yield {
      todo match {
        case None => Redirect(routes.TodoCategoryController.index)
        case _    => Redirect(routes.TodoCategoryController.index).flashing("success" -> "Todoを削除しました。")
      }
    }
  }

  private def getTodoCategory(id: Long): Future[TodoCategory.EmbeddedId] = {
    TodoCategoryUseCase.get(id).map(
      _ match {
        case Some(todoCategory) => todoCategory
        case _ => throw new Exception // TODO 404ページに遷移
      }
    )
  }
}