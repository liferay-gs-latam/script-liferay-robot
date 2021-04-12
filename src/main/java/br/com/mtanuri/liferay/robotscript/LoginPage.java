package br.com.mtanuri.liferay.robotscript;

import java.io.IOException;
import java.util.HashMap;

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
		return !super.getDoc().title().contains(PropertiesUtil.getInstance().getPropertie("app.login.success.probe"));
	}

	public String getAuthToken() {
		int indexOf = super.getDoc().head().html().indexOf("Liferay.authToken");
		return super.getDoc().head().html().substring(indexOf + 21, indexOf + 29);
	}
}