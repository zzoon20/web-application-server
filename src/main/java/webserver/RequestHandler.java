package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctrl.Controller;
import http.HttpRequest;
import http.HttpResponse;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;
	private Map<String, Controller> ctrlMap;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
		
		ctrlMap = new HashMap<String, Controller>();
		ctrlMap.put("/user/create", new ctrl.CreateUserController());
		ctrlMap.put("/user/login", new ctrl.LoginController());
		ctrlMap.put("/user/list", new ctrl.ListUserController());
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			HttpRequest request = new HttpRequest(in);
			log.debug("request init!");
			HttpResponse response = new HttpResponse(out);
			response.setHeaders(request.getHeaders());
			log.debug("response init!");
			
			String url = getDefaultUrl(request.getPath());
			
			Controller ctrl = ctrlMap.get(url);
			if(ctrl == null) {
				response.forward(url);
				return;
			}
			log.debug("url call ctrl : {}", url);
			ctrl.service(request, response);
			
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private String getDefaultUrl(String path) {
		if (path == null || path.equals("/")) {
			return "/index.html";
		}
		return path;
	}
}
