package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

/**
 *
 * This controller demonstrates the use of the built-in Authenticator with scala
 *
 */
object SecuredApplication extends Controller with Secured {

  /**
   * Data for Login Form
   */
  case class LoginData(email: String, password: String)

  /**
   * Form wrapping the login data and verifying the correct  user
   */
  val loginForm: Form[LoginData] = Form(
    mapping(
      "email" -> email,
      "password" -> text)(LoginData.apply)(LoginData.unapply)
      verifying ("email and/or password incorrect!", fields => fields match {
        case login: LoginData =>
          login.email == "daniel@example.com" && login.password == "secret"
      }))


  // ********** ACTIONS *********** //
      
  /**
   * show log in form
   */
  def login = Action {
    Ok(views.html.login(loginForm))
  }

  /**
   * validate log in form - show errors if log in invalid - redirect to
   * secured area otherwise
   */
  def validateLogin = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      loginData => Redirect(routes.SecuredApplication.secured).withSession("username" -> loginData.email))
  }

  /**
   * secured area
   */
  def secured = SecuredAction { userEmail =>
    implicit request =>
      Ok("Hello " + userEmail)
  }
  
  /**
   * logging out by clearing the session
   */
  def logout = Action {
    Ok("logged out").withSession()
  }

}

trait Secured {

  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("username")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.SecuredApplication.login)

  /**
   * Defines a SecuredAction which takes a function which takes user string and maps it to a Result
   * If there is no user, execute onUnauthorized 
   */
  def SecuredAction(f: => String => Request[AnyContent] => Result) =
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
}