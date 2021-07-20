package model

import lib.model.TodoCategory

case class ViewValueTodoCategoryForm(id: Option[TodoCategory.Id], home: ViewValueHome) {
  def isNew(): Boolean = {
    id.map(_ => false).getOrElse(true)
  }
}

object ViewValueTodoCategoryForm {
  def apply(): ViewValueTodoCategoryForm = {
    val home = ViewValueHome(
      title  = "Todoカテゴリ作成",
      cssSrc = Seq("reset.css", "main.css"),
      jsSrc  = Seq("main.js")
    )
    ViewValueTodoCategoryForm(None, home)
  }

  def apply(id: TodoCategory.Id): ViewValueTodoCategoryForm = {
    val home = ViewValueHome(
      title  = "Todoカテゴリ編集",
      cssSrc = Seq("reset.css", "main.css"),
      jsSrc  = Seq("main.js")
    )
    ViewValueTodoCategoryForm(Option(id), home)
  }
}
