package br.com.mtanuri.scriptLiferayRobot;

import java.io.IOException;
import java.util.HashMap;

public class StgLastUpdateGetterPage extends SimplePageImpl {

	public StgLastUpdateGetterPage(String siteDomain, String authToken, HashMap<String, String> cookies) throws IOException {
		super(siteDomain, PropertiesUtil.getInstance().getPropertie("site.stgLastUpdateGetter.url"), cookies, authToken);
	}

	public void setLastUpdate() throws IOException {
		HashMap<String, String> formData = new HashMap<String, String>();
		formData.put("_137_cmd", "cacheDb");
		super.submit(formData);
	}

	public String getCurrentNode() throws IOException {
		return this.getDoc().selectFirst("#" + PropertiesUtil.getInstance().getPropertie("site.stgLastUpdateGetter.element"))
				.text();
	}
}