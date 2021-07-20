package helpers

import lib.model.TodoCategory

object TodoCategoryHelper {
  def colorOptions(): Seq[(String, String)] = {
    Seq(
      (TodoCategory.Color.RED.code.toString,    TodoCategory.Color.RED.name),
      (TodoCategory.Color.GREEN.code.toString,  TodoCategory.Color.GREEN.name),
      (TodoCategory.Color.BLUE.code.toString,   TodoCategory.Color.BLUE.name),
      (TodoCategory.Color.YELLOW.code.toString, TodoCategory.Color.YELLOW.name)
    )
  }

  def colorStyle(state: TodoCategory.Color): String = {
    state match {
      case TodoCategory.Color.RED    => "todo-category__color--red"
      case TodoCategory.Color.GREEN  => "todo-category__color--green"
      case TodoCategory.Color.BLUE   => "todo-category__color--blue"
      case TodoCategory.Color.YELLOW => "todo-category__color--yellow"
    }
  }
}