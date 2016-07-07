package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;
import util.IOUtils;
import webserver.RequestHandler;

public class HttpRequest {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private String method;
	private String path;
	private Map<String, String> params;
	private Map<String, String> headers;

	public HttpRequest(InputStream in) throws IOException {
		params = new HashMap<String, String>();
		headers = new HashMap<String, String>();

		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String requestLine = br.readLine();
		log.debug("requestLine: {}", requestLine);
		if (requestLine == null) {
			return;
		}

		String[] tokens = requestLine.split(" ");
		setMethod(tokens[0]);

		String requestUrl = tokens[1];
		int index = requestUrl.indexOf("?");
		setPath(requestUrl);

		if (index != -1) {
			setPath(requestUrl.substring(0, index));
			setParams(HttpRequestUtils.parseQueryString(requestUrl.substring(index + 1)));
		}

		Map<String, String> tmp = new HashMap<String, String>();
		String line = br.readLine();
		while (true) {
			Pair pair = HttpRequestUtils.parseHeader(line);
			if (pair == null)
				break;
			tmp.put(pair.getKey(), pair.getValue());
			log.debug("set header: {}", line);
			line = br.readLine();
		}
		setHeaders(tmp);
		log.debug("set setHeaders done");
		
		if(getMethod().equals("GET")) {
			return;
		}
		
		line = IOUtils.readData(br, Integer.parseInt(getHeader("Content-Length")));
		log.debug("post line : " + line);
		if (line != null) {
			log.debug("set Param: {}", line);
			setParams(HttpRequestUtils.parseQueryString(line));
		}

	}

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public String getHeader(String key) {
		return headers.get(key);
	}

	public String getParameter(String key) {
		return params.get(key);
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
}
