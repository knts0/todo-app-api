/** to do sample project
  */

package controllers

import javax.inject._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n.I18nSupport

import model.view.ViewValueCategoryList
import model.view.ViewValueCategoryCreate
import model.view.ViewValueCategoryEdit
import model.ViewValueError

import service.TodoService
import service.TodoCategoryService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class TodoCategoryController @Inject() (
    val controllerComponents: ControllerComponents
) extends BaseController
    with I18nSupport {

  //Point フォーム用のオブジェクト化をしているか？
  import forms.TodoCategoryForm._

  //Point DBアクセスある箇所を Action.async としているか？
  def index() = Action.async { implicit req =>
    
    //Point DBアクセスをfor文でかけているか？ 
    //      flatMap/Mapの場合にはforで書いてみてもらう。※階層がシンプルに
    for {
      //Point このサンプルではControllerをシンプル化したいので、Serviceオブジェクトをつくって呼び出しているが、
      //      直接ここで、リポジトリを呼び出してもらっても構わない。
      //      ただし、newしている場合には、「lib.persistence.onMySQL.TodoRepository」の理解度や、
      //      newするものとしなくて良いものの違いを理解してもらう必要あり。
      categories <- TodoCategoryService.all
    } yield {
      
      val vv = ViewValueCategoryList(categories)
      
      //Point 基本的には、ViewValue（のリスト化）で、HTMLにわたすデータを作成
      //      そのため、ViewValueは共通化できない部分は、それぞれの画面に合わせて作る必要がある。
      //　　　　以下のようなviewへのパラメータとしては、ViewValue系で１つ、下のcreateメソッドなどにもあるが、Formで1つというシンプルな状態になっているかを確認
      Ok(views.html.category.List(vv))
    }
  }

  def create() = Action { implicit req =>
    val vv = ViewValueCategoryCreate()

    //Point ViewValue, Formが引数になった例
    Ok(views.html.category.Create(vv, categoryForm))
  }

  def save() = Action.async { implicit req =>
    //Point この辺りのbindFormRequest()、エラー時、成功時の書き方を使いこなしているか？
    //      以下のドキュメントや、Playのドキュメントを読めているか？を確認する必要がある
    //      https://nextbeat-external.atlassian.net/wiki/spaces/DEVNEWEDU/pages/2737078458/4-2-0.+ToDo
    categoryForm
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[CategoryForm]) => {
          val vv = ViewValueCategoryCreate()
          Future.successful(Ok(views.html.category.Create(vv, formWithErrors)))
        },
        (categoryData: CategoryForm) => {
          for {
            //Point データ更新できているか？
            _ <- TodoCategoryService.add(categoryData)
          } yield {
            //Point 成功時にリダイレクトしているか？
            Redirect(routes.TodoCategoryController.index)
          }
        }
      )

  }

  def edit(id: Long) = Action.async { implicit req =>
    (for {
      category <- TodoCategoryService.get(id)
    } yield {
      val vv = ViewValueCategoryEdit(category)
      Ok(
        views.html.category.Edit(
          vv,
          categoryForm.fill(
            CategoryForm(
              name = category.name,
              slug = category.slug,
              color = category.color.code
            )
          )
        )
      )
    }) recover { case _: Exception =>
      //Point 余裕のある人には、予期せぬ場合にどのような書き方があるか？などを質問してもよい。
      NotFound(views.html.error.page404(ViewValueError()))
    }

  }

  def update(id: Long) = Action.async { implicit req =>
    categoryForm
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[CategoryForm]) => {
          for {
            category <- TodoCategoryService.get(id)
          } yield {
            val vv = ViewValueCategoryEdit(category)
            Ok(
              views.html.category.Edit(
                vv,
                formWithErrors
              )
            )
          }
        },
        (categoryData: CategoryForm) =>
          (
            for {
              _ <- TodoCategoryService.update(id, categoryData)
            } yield Redirect(routes.TodoCategoryController.index)
          ) recover { case _: Exception =>
            NotFound(views.html.error.page404(ViewValueError()))
          }
      )

  }

  def delete(id: Long) = Action.async { implicit req =>
    (for {
      _ <- TodoCategoryService.delete(id)
    } yield Redirect(routes.TodoCategoryController.index)) recover {
      case _: Exception =>
        NotFound(views.html.error.page404(ViewValueError()))
    }
  }

}
