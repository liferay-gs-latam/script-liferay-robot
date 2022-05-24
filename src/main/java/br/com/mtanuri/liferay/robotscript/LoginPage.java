package br.com.mtanuri.liferay.robotscript;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LoginPage extends SimplePageImpl {

	public LoginPage(String siteDomain, HashMap<String, String> cookies) throws IOException {
		super(siteDomain, PropertiesUtil.getInstance().getPropertie("site.login.url"), cookies);
	}

	public void doLogin(String user, String pass) throws IOException {
		HashMap<String, String> formData = new HashMap<String, String>();
		formData.put("_58_login", user);
		formData.put("_58_password", pass);
		formData.put("_58_redirect", "/web/guest/home");

		super.submit(formData);
	}

	public boolean isLoginSucess() throws IOException {

		Elements scripts = super.getDoc().getElementsByTag("script");
		Element targetScript = new Element("script");
		

		System.out.println("Scripts da pagina");

		for (Element script : scripts) {
			if(script.html().contains("Liferay.currentURL")) {
				targetScript = script;
				break;
			}
		}

		String[] code = targetScript.html().split("\n");
		String jsLine = "";

		for (String line : code) {
			if(line.contains("Liferay.currentURL")){
				jsLine = line;
				break;
			}
		}
		String[] jsVar = jsLine.split("=");

		return !jsVar[1].contains(PropertiesUtil.getInstance().getPropertie("app.login.success.probe"));
	}

	public String getAuthToken() {
		int indexOf = super.getDoc().head().html().indexOf("Liferay.authToken");
		return super.getDoc().head().html().substring(indexOf + 21, indexOf + 29);
	}
}