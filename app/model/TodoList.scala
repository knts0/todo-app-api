package model

import lib.model.Todo
import lib.model.TodoCategory

import ViewValueTodoList._

case class ViewValueTodoList(items: Seq[Item], home: ViewValueHome) {
}

object ViewValueTodoList {
  case class Category(name: String, color: TodoCategory.Color)
  case class Item(id: Long, title: String, body: String, state: Todo.Status, category: Option[Category])

  def apply(
    todoList: Seq[Todo.EmbeddedId],
    categories: Seq[TodoCategory.EmbeddedId]
  ): ViewValueTodoList = {
    val items = for {
      todo <- todoList
    } yield {
      val category = categories.find(_.id == todo.v.categoryId).map(c => Category(c.v.name, c.v.color))
      ViewValueTodoList.Item(todo.id, todo.v.title, todo.v.body, todo.v.state, category)
    }

    val home = ViewValueHome(
      title  = "Todo一覧",
      cssSrc = Seq("reset.css", "main.css"),
      jsSrc  = Seq("main.js")
    )

    ViewValueTodoList(items, home)
  }
}