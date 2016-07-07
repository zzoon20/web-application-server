package http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webserver.RequestHandler;

public class HttpResponse {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private DataOutputStream dos;
	private Map<String, String> headers;

	public HttpResponse(OutputStream out) {
		dos = new DataOutputStream(out);
		headers = new HashMap<String, String>();
	}
	
	public void addHeader(String key, String value) {
		Map<String, String> tmp = getHeaders();
		tmp.put(key, value);
		setHeaders(tmp);
	}

	public void forward(String url) throws IOException {
		log.debug("forward to {}", url);
		byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
		response200Header(body.length);
		responseBody(body);
	}

	public void forwardBody(String body) throws IOException {
		response200Header(body.getBytes().length);
		responseBody(body.getBytes());
	}
	
	private void response200Header(int lengthOfBodyContent) {
		try {
			log.debug("content-length = {}", lengthOfBodyContent);
			
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: "+ getContentType() + ";charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	private String getContentType(){
		String accept = headers.get("Accept");
		if(accept == null) {
			return "*/*";
		}
		return (accept.split(", "))[0];
	}
	private void responseBody(byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.writeBytes("\r\n");
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	public void sendRedirect(String url) {
		response302Header(url);
		responseBody("".getBytes());
	}

	private void response302Header(String url) {
		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Location: " + url + " \r\n");
			dos.writeBytes(processHeader());
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private String processHeader() {
		StringBuilder sb = new StringBuilder();
		Iterator<String> iterator = headers.keySet().iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			sb.append(key + ": " + headers.get(key) + " \r\n");
		}
		return sb.toString();
	}

	public DataOutputStream getDos() {
		return dos;
	}

	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
}
