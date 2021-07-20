package model

import ViewValueTodoList._

import lib.model.Todo
import lib.model.TodoCategory

case class ViewValueTodoAddForm(categories: Seq[TodoCategory.EmbeddedId], home: ViewValueHome) {
}

case class ViewValueTodoEditForm(id: Todo.Id, categories: Seq[TodoCategory.EmbeddedId], home: ViewValueHome) {
}

object ViewValueTodoAddForm {
  def apply(categories: Seq[TodoCategory.EmbeddedId]): ViewValueTodoAddForm = {
    val home = ViewValueHome(
      title  = "Todo作成",
      cssSrc = Seq("reset.css", "main.css"),
      jsSrc  = Seq("main.js")
    )

    ViewValueTodoAddForm(categories, home)
  }
}

object ViewValueTodoEditForm {
  def apply(id: Todo.Id, categories: Seq[TodoCategory.EmbeddedId]): ViewValueTodoEditForm = {
    val home = ViewValueHome(
      title  = "Todo編集",
      cssSrc = Seq("reset.css", "main.css"),
      jsSrc  = Seq("main.js")
    )

    ViewValueTodoEditForm(id, categories, home)
  }
}
