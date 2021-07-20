package helpers

import lib.model.Todo
import lib.model.TodoCategory

object TodoHelper {
  def stateStyle(state: Todo.Status): String = {
    state match {
      case Todo.Status.WAITING => "todo_state--wating"
      case Todo.Status.IN_PROGRESS => "todo_state--inprogress"
      case Todo.Status.COMPLETED => "todo_state--completed"
    }
  }

  def stateOptions(): Seq[(String, String)] = {
    Seq(
      (Todo.Status.WAITING.code.toString, Todo.Status.WAITING.name),
      (Todo.Status.IN_PROGRESS.code.toString, Todo.Status.IN_PROGRESS.name),
      (Todo.Status.COMPLETED.code.toString, Todo.Status.COMPLETED.name)
    )
  }

  def categoryOptions(categories: Seq[TodoCategory.EmbeddedId]): Seq[(String, String)] = {
    ("", "----------") +: categories.map(category => (category.id.toString, category.v.name))
  }
}