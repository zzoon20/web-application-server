package ctrl;

import java.io.IOException;
import java.util.Map;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class LoginController extends AbstractContoller {

	@Override
	protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
		Map<String, String> params = request.getParams();
		User user = DataBase.findUserById(params.get("userId"));
		if (user != null) {
			if (user.login(params.get("password"))) {
				response.addHeader("Set-Cookie", "logined=true");
				response.sendRedirect("/index.html");
			} else {
				response.forward("/user/login_failed.html");
			}
		} else {
			response.forward("/user/login_failed.html");
		}
	}
}
