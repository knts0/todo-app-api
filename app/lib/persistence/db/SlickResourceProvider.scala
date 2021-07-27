/**
  * This is a sample of Todo Application.
  * 
  */

package lib.persistence.db

import slick.jdbc.JdbcProfile

// Tableを扱うResourceのProvider
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
trait SlickResourceProvider[P <: JdbcProfile] {

  implicit val driver: P
  object UserTable extends UserTable
  //Point 以降が正しく設定されていることを確認
  object TodoTable extends TodoTable
  object TodoCategoryTable extends TodoCategoryTable

  // --[ テーブル定義 ] --------------------------------------
  lazy val AllTables = Seq(
    UserTable,
    //Point 以降も正しく設定されていることを確認
    TodoTable,
    TodoCategoryTable
  )
}
