package service

import lib.model.TodoCategory
import lib.model.TodoCategory._
import lib.persistence.onMySQL.TodoRepository
import lib.persistence.onMySQL.TodoCategoryRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import model.item.TodoCategoryItem
import forms.TodoCategoryForm._
import json.reads.JsValueCreateCategory

object TodoCategoryService {
  def all(): Future[Seq[TodoCategoryItem]] = {
    for {
      todoCategoryList <- TodoCategoryRepository.all
    } yield todoCategoryList.map(category =>
      TodoCategoryItem(
        //Point 各データは、xxxx.id でデータを取り出せるが、それ以外は xxxx.v 以下に格納されているので注意
        //      https://medium.com/nextbeat-engineering/%E8%87%AA%E7%A4%BEoss-ixias-%E3%81%AE%E7%B4%B9%E4%BB%8B-ixais-model%E3%83%91%E3%83%83%E3%82%B1%E3%83%BC%E3%82%B8%E3%81%AE%E3%82%B5%E3%83%B3%E3%83%97%E3%83%AB%E3%82%B3%E3%83%BC%E3%83%89-d6e0e5d8e8aa
        //      などを再度見て貰う必要がある。
        category.id,
        category.v.name,
        category.v.slug,
        category.v.color
      )
    )
  }

  def add(jsValueCreateCategory: JsValueCreateCategory): Future[Long] = {
    TodoCategoryRepository.add(
      TodoCategory(
        name = jsValueCreateCategory.name,
        slug = jsValueCreateCategory.slug,
        //Point Formからの値を受ける段階で、「TodoCategory.Color」とかの型に受け渡していることを確認
        color = TodoCategory.Color(jsValueCreateCategory.color)
      )
    )
  }

  def update(id: Long, form: CategoryForm): Future[Option[Long]] = {
    for {
      categoryOpt <- TodoCategoryRepository.get(TodoCategory.Id(id))
    } yield {
      categoryOpt match {
        case Some(category) => {
          TodoCategoryRepository.update(
            new TodoCategory.EmbeddedId(
              //Point copyメソッドを活用して、さっくり作っていることを確認
              category.v.copy(
                name = form.name,
                slug = form.slug,
                color = TodoCategory.Color(form.color.toShort)
              )
            )
          )
          category.v.id
        }
        case None => throw new Exception("データなし")
      }
    }
  }

  def get(id: Long): Future[TodoCategoryItem] = {
    for {
      categoryOpt <- TodoCategoryRepository.get(TodoCategory.Id(id))
    } yield {
      categoryOpt match {
        case Some(category) =>
          TodoCategoryItem(
            category.id,
            category.v.name,
            category.v.slug,
            category.v.color
          )
        case None => throw new Exception("データなし")
      }
    }
  }

  def delete(id: Long): Future[Option[Long]] = {
    for {
      categoryOpt <- TodoCategoryRepository.remove(TodoCategory.Id(id))
    } yield {
      categoryOpt match {
        case Some(category) => {
          for {
            _ <- TodoRepository.removeByCategoryId(category.id)
          } yield category.v.id
          category.v.id
        }
        case None => throw new Exception("データなし")
      }
    }
  }

}
