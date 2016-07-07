package ctrl;

import java.io.IOException;
import java.util.Map;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class CreateUserController extends AbstractContoller {

	@Override
	protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
		Map<String, String> params = request.getParams();
		User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
		DataBase.addUser(user);
		response.sendRedirect("/index.html");
	}
}
