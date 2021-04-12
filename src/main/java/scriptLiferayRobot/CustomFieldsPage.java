package scriptLiferayRobot;

import java.io.IOException;
import java.util.HashMap;

public class CustomFieldsPage extends SimplePageImpl {

	public CustomFieldsPage(String siteDomain, String authToken, HashMap<String, String> cookies) throws IOException {
		super(siteDomain, PropertiesUtil.getInstance().getPropertie("site.customFields.url"), cookies, authToken);
	}

	public void setFieldValue(Long value) throws IOException {
		HashMap<String, String> formData = new HashMap<String, String>();
		formData.put("_165_ExpandoAttribute--last-stg-publication--", String.valueOf(value));
		formData.put("_165_groupId", "10184");
		formData.put("_165_cmd", "update");
		formData.put("_165_liveGroupId", "10184");
		formData.put("_165_stagingGroupId", "0");
		formData.put("_165_ExpandoAttributeName--last-stg-publication--", "last-stg-publication");
		
		
		super.submit(formData);
	}

}
