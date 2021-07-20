
package useCase

import scala.concurrent.Future
import scala.concurrent.duration.Duration

import lib.persistence.onMySQL.TodoCategoryRepository

import lib.model.TodoCategory
import lib.model.TodoCategory._

object TodoCategoryUseCase {
  def get(id: Long): Future[Option[EmbeddedId]] = {
    TodoCategoryRepository.get(Id(id))
  }

  def all(): Future[Seq[EmbeddedId]] = {
    TodoCategoryRepository.all()
  }

  def create(name: String, slug: String, color: Color): Future[Id] = {
    val entity = TodoCategory(name, slug, color)
    TodoCategoryRepository.add(entity)
  }

  def update(category: EmbeddedId, name: String, slug: String, color: Color): Future[Option[EmbeddedId]] = {
    val newCategory = category.v.copy(name = name, slug = slug, color = color)
    TodoCategoryRepository.update(new TodoCategory.EmbeddedId(newCategory))
  }

  def remove(id: Id): Future[Option[EmbeddedId]] = {
    TodoCategoryRepository.remove(id)
  }
}
