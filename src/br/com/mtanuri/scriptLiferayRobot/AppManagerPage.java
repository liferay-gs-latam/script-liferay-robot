package br.com.mtanuri.scriptLiferayRobot;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AppManagerPage extends SimplePageImpl {

	public AppManagerPage(String siteDomain, HashMap<String, String> cookies) throws IOException {
		super(siteDomain, PropertiesUtil.getInstance().getPropertie("site.appManager.url"), cookies);
	}

	public boolean isMacroDeployed() throws IOException {
		return isPortletDeployed("smiles-memberships-portlet");
	}

	public boolean isOptinDeployed() throws IOException {
		return isPortletDeployed("smiles-account-portlet");
	}

	private boolean isPortletDeployed(String portletName) throws IOException {
		Elements elements = this.getDoc()
				.select('.' + PropertiesUtil.getInstance().getPropertie("site.probe.app.element"));
		for (Element element : elements) {
			if (element.selectFirst(
					'.' + PropertiesUtil.getInstance().getPropertie("site.probe.app.element.child")) != null
					&& element
							.selectFirst(
									'.' + PropertiesUtil.getInstance().getPropertie("site.probe.app.element.child"))
							.text().contains(portletName)) {
				if (element.selectFirst(".plugins") != null
						&& element.selectFirst(".plugins").selectFirst(".summary") != null) {
					return true;
				}
			}
		}
		return false;
	}
}
