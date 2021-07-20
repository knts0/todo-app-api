
package useCase

import scala.concurrent.Future

import lib.persistence.onMySQL.TodoRepository
import lib.model.Todo
import lib.model.Todo._
import lib.model.TodoCategory

object TodoUseCase {
  def get(id: Id): Future[Option[EmbeddedId]] = {
    TodoRepository.get(id)
  }

  def all(): Future[Seq[EmbeddedId]] = {
    TodoRepository.all()
  }

  def create(title: String, body: String, categoryId: Long): Future[Id] = {
    val entity = Todo(TodoCategory.Id(categoryId), title, body, Todo.Status.WAITING)
    TodoRepository.add(entity)
  }

  def update(todo: EmbeddedId, title: String, body: String, state: Status, categoryId: Long): Future[Option[EmbeddedId]] = {
    val newTodo = todo.v.copy(title = title, body = body, state = state, categoryId = TodoCategory.Id(categoryId))
    TodoRepository.update(new Todo.EmbeddedId(newTodo))
  }

  def remove(id: Id): Future[Option[EmbeddedId]] = {
    TodoRepository.remove(id)
  }
}
