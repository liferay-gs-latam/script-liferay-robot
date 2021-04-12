package br.com.mtanuri.liferay.robotscript;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;

public interface Page {

	public static final String AGENT = "\"Mozilla/5.0 (Windows NT\" +\n"
			+ "          \" 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2\"";

	void connect() throws IOException;
	
	void connectWithCookies() throws IOException;

	public void submit(HashMap<String, String> formData) throws IOException;

	public HashMap<String, String> getCookies();
	
	public String getUrl();

	public Connection.Response getResponse();

	public Document getDoc();

}
