/**
  * This is a sample of Todo Application.
  * 
  */

package lib

package object persistence {

  val default = onMySQL
  
  //Point 以降が、ちゃんと追加されることを確認。ControllerでRepositoryをnewしてしまっている場合は、
  //      このファイルでのobject化を忘れてしまっているときがある。
  //      また、objectがシングルトンオブジェクトであることをわかっていない可能性があることを気づかせる
  object onMySQL {
    implicit lazy val driver = slick.jdbc.MySQLProfile
    object UserRepository extends UserRepository
    object TodoRepository extends TodoRepository
    object TodoCategoryRepository extends TodoCategoryRepository
  }
}
