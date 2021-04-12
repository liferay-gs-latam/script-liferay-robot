package scriptLiferayRobot;

import java.io.IOException;
import java.util.HashMap;

public class LicenseManagerPage extends SimplePageImpl {

	public LicenseManagerPage(String siteDomain, HashMap<String, String> cookies) throws IOException {
		super(siteDomain, PropertiesUtil.getInstance().getPropertie("site.licenseManager.url"), cookies);
	}

	public int getNumberOfLicenses() throws IOException {
		int table_elements = this.getDoc().select("."+PropertiesUtil.getInstance().getPropertie("site.licenseManager.element")).size();
		if (table_elements > 0) {
			return (table_elements - 1) / 2;
		}
		return 0;
	}
}
