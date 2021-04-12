package br.com.mtanuri.liferay.robotscript;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;

public class SimplePageImpl implements Page {

	private HashMap<String, String> cookies;
	private String url;
	private String siteDomain;
	private Document doc;
	private Connection.Response response;

	public SimplePageImpl(String siteDomain, String url, HashMap<String, String> cookies) throws IOException {
		this.siteDomain = siteDomain;
		this.setUrl(url);
		this.cookies = cookies;
	}

	public SimplePageImpl(String siteDomain, String url) throws IOException {
		this.siteDomain = siteDomain;
		this.setUrl(url);
		this.cookies = new HashMap<String, String>();
	}

	public SimplePageImpl(String siteDomain, String url, HashMap<String, String> cookies, String authToken)
			throws IOException {
		this.siteDomain = siteDomain;
		this.setUrl(url.concat("&p_auth=" + authToken));
		this.cookies = cookies;
	}

	private void setUrl(String url) throws IOException {
		this.url = this.siteDomain + url;
	}

	@Override
	public void connect() throws IOException {
		String agent = AGENT;

		response = Jsoup.connect(url).method(Connection.Method.GET).userAgent(agent)
				.timeout(Integer.valueOf(PropertiesUtil.getInstance().getPropertie("app.connection.timeout")))
				.execute();
		doc = response.parse();
		this.cookies.putAll(response.cookies());
	}

	@Override
	public void connectWithCookies() throws IOException {
		String agent = AGENT;

		response = Jsoup.connect(url).method(Connection.Method.GET).userAgent(agent).cookies(this.cookies)
				.timeout(Integer.valueOf(PropertiesUtil.getInstance().getPropertie("app.connection.timeout")))
				.execute();
		doc = response.parse();
		this.cookies.putAll(response.cookies());
	}

	@Override
	public void submit(HashMap<String, String> formData) throws IOException {
		String agent = AGENT;

		response = Jsoup.connect(this.url).method(Connection.Method.POST).data(formData).cookies(this.cookies)
				.userAgent(agent)
				.timeout(Integer.valueOf(PropertiesUtil.getInstance().getPropertie("app.connection.timeout")))
				.execute();
		doc = response.parse();
		this.cookies.putAll(response.cookies());
	}

	@Override
	public HashMap<String, String> getCookies() {
		return cookies;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public Connection.Response getResponse() {
		return response;
	}

	@Override
	public Document getDoc() {
		return doc;
	}

	public String getSiteDomain() {
		return siteDomain;
	}

}
