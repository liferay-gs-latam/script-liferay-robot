package br.com.mtanuri.liferay.robotscript;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AppManagerPage extends SimplePageImpl {

	public AppManagerPage(String siteDomain, HashMap<String, String> cookies) throws IOException {
		super(siteDomain, PropertiesUtil.getInstance().getPropertie("site.appManager.url"), cookies);
	}

	public boolean isPortletDeployed(String portletName, boolean mustToHavePlugins) throws IOException {
		Elements elements = this.getDoc()
				.select('.' + PropertiesUtil.getInstance().getPropertie("site.probe.app.element"));
		for (Element element : elements) {
			if (element.selectFirst(
					'.' + PropertiesUtil.getInstance().getPropertie("site.probe.app.element.child")) != null
					&& element
							.selectFirst(
									'.' + PropertiesUtil.getInstance().getPropertie("site.probe.app.element.child"))
							.text().contains(portletName)) {
				if (!mustToHavePlugins) {
					return true;
				}
				if (element.selectFirst(".plugins") != null
						&& element.selectFirst(".plugins").selectFirst(".summary") != null) {
					return true;
				}
			}
		}
		return false;
	}
}
