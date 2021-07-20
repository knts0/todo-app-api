package model

import lib.model.TodoCategory

import model.ViewValueTodoCategoryList._

case class ViewValueTodoCategoryList(items: Seq[Item], home: ViewValueHome)

object ViewValueTodoCategoryList {
  case class Item(id: TodoCategory.Id, name: String, slug: String, color: TodoCategory.Color)

  def apply(
    categories: Seq[TodoCategory.EmbeddedId]
  ): ViewValueTodoCategoryList = {
    val items = for {
      category <- categories
    } yield {
      ViewValueTodoCategoryList.Item(category.id, category.v.name, category.v.slug, category.v.color)
    }

    val home = ViewValueHome(
      title  = "Todoカテゴリ一覧",
      cssSrc = Seq("reset.css", "main.css"),
      jsSrc  = Seq("main.js")
    )

    ViewValueTodoCategoryList(items, home)
  }
}
