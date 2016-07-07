package ctrl;

import java.io.IOException;

import http.HttpRequest;
import http.HttpResponse;

public abstract class AbstractContoller implements Controller {

	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {
		if ("POST".equals(request.getMethod())) {
			doPost(request, response);
		}
		doGet(request, response);
	}

	protected void doPost(HttpRequest request, HttpResponse response) throws IOException {

	}

	protected void doGet(HttpRequest request, HttpResponse response) throws IOException {

	}
}
