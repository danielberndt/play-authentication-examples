package controllers;

import play.data.Form;
import play.mvc.Action;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;
import forms.LoginData;

/**
 * 
 * This controller demonstrates the use of the built-in Authenticator with java
 * 
 */
public class SecuredApplication extends Controller {

	private static Form<LoginData> loginForm = form(LoginData.class);

	/**
	 * show log in form
	 */
	public static Result login() {
		return ok(views.html.login.render(loginForm));
	}

	/**
	 * validate log in form - show errors if log in invalid - redirect to
	 * secured area otherwise
	 */
	public static Result validateLogin() {
		Form<LoginData> filledForm = loginForm.bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(views.html.login.render(filledForm));
		}
		session().put("username", filledForm.get().email);
		return redirect(routes.SecuredApplication.secured());
	}

	/**
	 * secured area
	 */
	@Security.Authenticated(MyAuthenticator.class)
	public static Result secured() {
		return ok("Hello " + request().username());
	}
	
	/**
	 * logging out by clearing the session
	 */
	public static Result logout() {
		session().clear();
		return ok("logged out");
	}

	
	
	
	
	
	
	
	
	
	public static class MyAuthenticator extends Security.Authenticator {

		/**
		 * Retrieves the username from the HTTP context
		 * 
		 * @return null if the user is not authenticated.
		 */
		public String getUsername(Context ctx) {
			String username = ctx.session().get("username");
			if (username==null) return null;
			return username.substring(0, username.indexOf('@'));
		}

		/**
		 * Generates an alternative result if the user is not authenticated
		 */
		public Result onUnauthorized(Context ctx) {
			return super.onUnauthorized(ctx);
		}
	}

	
	
	
	
	
	
	
	
	
	
	public static class RedirectIfLoggedIn extends Action.Simple {

		public Result call(Context ctx) throws Throwable {
			if (ctx.session().get("username")==null) {
				return delegate.call(ctx);
			}
			return redirect(routes.SecuredApplication.secured());
		}
	}

}