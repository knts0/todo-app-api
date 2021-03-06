/**
  * This is a sample of Todo Application.
  * 
  */

package lib.persistence.db

import java.time.LocalDateTime
import slick.jdbc.JdbcProfile
import ixias.persistence.model.Table

import lib.model.Todo
import lib.model.TodoCategory

// TodoTable: Todoテーブルへのマッピングを行う
//~~~~~~~~~~~~~~
case class TodoTable[P <: JdbcProfile]()(implicit val driver: P)
  extends Table[Todo, P] {
  import api._

  // Definition of DataSourceName
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  //Point DBアクセスできない場合は、このto_doの辺りを確認
  lazy val dsn = Map(
    "master" -> DataSourceName("ixias.db.mysql://master/to_do"),
    "slave"  -> DataSourceName("ixias.db.mysql://slave/to_do")
  )

  // Definition of Query
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  class Query extends BasicQuery(new Table(_)) {}
  lazy val query = new Query

  // Definition of Table
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  //Point DBアクセスできない場合は、このto_doの辺りを確認
  class Table(tag: Tag) extends BasicTable(tag, "to_do") {
    //Point カラム名があっていることを確認、
    //      また、「TodoCategory.Id」がLongでないこと、「Status」などを確認
    import Todo._
    // Columns
    /* @1 */ def id         = column[Id]              ("id",          O.UInt64, O.PrimaryKey, O.AutoInc)
    /* @2 */ def categoryId = column[TodoCategory.Id] ("category_id", O.UInt64)
    /* @3 */ def title      = column[String]          ("title",       O.Utf8Char255)
    /* @4 */ def body       = column[String]          ("body",        O.Text)
    /* @5 */ def state      = column[Status]          ("state",       O.UInt8)
    /* @6 */ def updatedAt  = column[LocalDateTime]   ("updated_at",  O.TsCurrent)
    /* @7 */ def createdAt  = column[LocalDateTime]   ("created_at",  O.Ts)

    //Point 特に「TodoCategory.Id」「Status」などを確認
    type TableElementTuple = (
      Option[Id], TodoCategory.Id, String, String, Status, LocalDateTime, LocalDateTime
    )

    // DB <=> Scala の相互のmapping定義
    def * = (id.?, categoryId, title, body, state, updatedAt, createdAt) <> (
      // Tuple(table) => Model
      // TODO: stateの変換でClassCastExceptionが出るが、サーバ再起動で直る
      // 原因が不明。数値型→ixiasEnumへの変換に問題あり？
      (t: TableElementTuple) => Todo(
        t._1, t._2, t._3, t._4, t._5, t._6, t._7
      ),
      // Model => Tuple(table)
      //Point 「LocalDateTime.now()」がupdatedAtの場所にあることを確認
      (v: TableElementType) => Todo.unapply(v).map { t => (
        t._1, t._2, t._3, t._4, t._5, LocalDateTime.now(), t._7
      )}
    )
  }
}
