package scriptLiferayRobot;

import java.io.IOException;

public class IpGetterPage extends SimplePageImpl {

	public IpGetterPage(String siteDomain) throws IOException {
		super(siteDomain, PropertiesUtil.getInstance().getPropertie("site.ipGetter.url"));
	}

	public String getCurrentNode() throws IOException {
		return this.getDoc().selectFirst("#" + PropertiesUtil.getInstance().getPropertie("site.ipGetter.element")).text();
	}

}
