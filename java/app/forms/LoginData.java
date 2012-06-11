package forms;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;

public class LoginData {

	@Required @Email
	public String email;
	
	@Required
	public String password;
	
	public String validate() {
		if (email.equals("daniel@example.com") && password.equals("secret")) return null;
		return "email and/or password incorrect!";
	}
	
}
