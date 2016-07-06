package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			HttpRequest request = new HttpRequest(in);
			log.debug("request init done");
			
			HttpResponse response = new HttpResponse(out);
			response.setHeaders(request.getHeaders());
			log.debug("response init done");
			
			boolean logined = isLogin(request.getHeader("Cookies"));
			log.debug("isLogin: {}", logined);
			
			String url = getDefaultUrl(request.getPath());
			log.debug("url: {}", url);
			
			if ("/user/create".equals(url)) {
				Map<String, String> params = request.getParams();
				User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
				log.debug("user : {}", user);
				DataBase.addUser(user);
				response.forward("/index.html");
				
			} else if ("/user/login".equals(url)) {
				Map<String, String> params = request.getParams();
				User user = DataBase.findUserById(params.get("userId"));
				if (user != null) {
					if (user.login(params.get("password"))) {
						response.addHeader("Set-Cookie", "logined=true");
						response.forward("/index.html");
					} else {
						response.forward("/user/login_failed.html");
					}
				} else {
					response.forward("/user/login_failed.html");
				}
				
			} else if ("/user/list".equals(url)) {
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
				
			} else {
				response.forward(url);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
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

	private String getDefaultUrl(String path) {
		if (path == null || path.equals("/")) {
			return "/index.html";
		}
		return path;
	}
}
