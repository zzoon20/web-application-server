package ctrl;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import util.HttpRequestUtils;

public class ListUserController extends AbstractContoller {

	@Override
	protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
		boolean logined = isLogin(request.getHeader("Cookie"));
		
		if (!logined) {
			response.sendRedirect("/user/login.html");
			return;
		}

		Collection<User> users = DataBase.findAll();
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1'>");
		for (User user : users) {
			sb.append("<tr>");
			sb.append("<td>" + user.getUserId() + "</td>");
			sb.append("<td>" + user.getName() + "</td>");
			sb.append("<td>" + user.getEmail() + "</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		response.forwardBody(sb.toString());
	}
	
	private boolean isLogin(String line) {
		if(line == null){
			return false;
		}
		
		Map<String, String> cookies = HttpRequestUtils.parseCookies(line);
		String value = cookies.get("logined");
		if (value == null) {
			return false;
		}
		return Boolean.parseBoolean(value);
	}

}
