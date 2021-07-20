/**
  * This is a sample of Todo Application.
  * 
  */

package lib.persistence

import scala.concurrent.Future
import ixias.persistence.SlickRepository
import lib.model.TodoCategory
import slick.jdbc.JdbcProfile

// TodoCategoryRepository: TodoCategoryTableへのクエリ発行を行うRepository層の定義
//~~~~~~~~~~~~~~~~~~~~~~
case class TodoCategoryRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[TodoCategory.Id, TodoCategory, P]
  with db.SlickResourceProvider[P] {

  import api._

  /**
    * Get TodoCategory Data
    */
  def get(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(TodoCategoryTable, "slave") { _
      .filter(_.id === id)
      .result.headOption
  }

  /**
    * Add TodoCategory Data
   */
  def add(entity: EntityWithNoId): Future[Id] =
    RunDBAction(TodoCategoryTable) { slick =>
      slick returning slick.map(_.id) += entity.v
    }

  /**
   * Update TodoCategory Data
   */
  def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
    RunDBAction(TodoCategoryTable) { slick =>
      val row = slick.filter(_.id === entity.id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.update(entity.v)
        }
      } yield old
    }

  /**
   * Delete TodoCategory Data
   */
  def remove(id: Id): Future[Option[EntityEmbeddedId]] = {
    DBAction(TodoCategoryTable) { case (db, categorySlick) =>
    DBAction(TodoTable)         { case (_,  todoSlick)     =>
      val categoryRow = categorySlick.filter(_.id === id)
      val todoRows = for { todo <- todoSlick if todo.categoryId === id } yield todo.categoryId
      
      val action = for {
        old <- categorySlick.filter(_.id === id).result.headOption
        _   <- categoryRow.delete
        _   <- todoRows.update(TodoCategory.Id(0))
      } yield old

      db.run(action.transactionally)
    }}
  }

  /**
   * All Todo Data
   */
  def all(): Future[Seq[EntityEmbeddedId]] =
    RunDBAction(TodoCategoryTable, "slave") { _.result }
}