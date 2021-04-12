package scripts

import com.liferay.portal.kernel.log.Log
import com.liferay.portal.kernel.log.LogFactoryUtil
import com.liferay.portal.kernel.staging.StagingUtil
import com.liferay.portal.kernel.util.DateUtil
import com.liferay.portal.kernel.util.GetterUtil
import com.liferay.portal.kernel.util.Validator
import com.liferay.portal.kernel.xml.Document
import com.liferay.portal.kernel.xml.Element
import com.liferay.portal.kernel.xml.Node
import com.liferay.portal.kernel.xml.SAXReaderUtil
import com.liferay.portal.model.Layout
import com.liferay.portal.model.LayoutSet
import com.liferay.portal.model.Portlet
import com.liferay.portal.model.PortletPreferences
import com.liferay.portal.service.GroupLocalServiceUtil
import com.liferay.portal.service.LayoutLocalServiceUtil
import com.liferay.portal.service.LayoutSetLocalServiceUtil
import com.liferay.portal.service.PortletLocalServiceUtil
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil
import com.liferay.portal.util.PortalUtil
import com.liferay.portal.util.PortletKeys
import com.liferay.portlet.PortletPreferencesFactoryUtil
 
try {
    String siteName = "Guest"
    boolean resetPublishDate = true;
    boolean isPrivateLayout = false;
 
    long companyId = PortalUtil.getDefaultCompanyId();
    long groupId = GroupLocalServiceUtil.getGroup(companyId, siteName).getGroupId()
 
    println "Site: " + siteName + ", groupId: " + groupId
 
    // Check the timestamps on the layout sets
    println "--- Layout Sets ---"
    checkLayoutSet(groupId, isPrivateLayout, resetPublishDate)
 
    // Check the timestamps for portlets themselves
    println "--- Site Portlets ---"
    checkPortletTimestamps(groupId, isPrivateLayout, resetPublishDate)
}
catch (Exception e) {
    Log log = LogFactoryUtil.getLog("adjust.timestamps.groovy");
    String msg = "Script failed: ";
    log.info(msg, e);
    println msg + e
}
 
def void checkLayoutSet(long groupId, boolean privateLayout, boolean resetPublishDate) {
    LayoutSet layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(groupId, privateLayout)
    String label = (privateLayout) ? "Private Pages" : "Public Pages"
 
    String lastPublishDate = layoutSet.getSettingsProperty("last-publish-date")
    if (Validator.isNull(lastPublishDate)) {
        println label + " has no last publish date."
    }
    else {
        println label + " last published at " + formatDate(lastPublishDate)
    }
 
    if (resetPublishDate) {
        Date publishDate = DateUtil.newDate();
        println "Updating publish date to " + publishDate;
        StagingUtil.updateLastPublishDate(groupId, privateLayout, publishDate);
    }
}
 
def void checkPortletTimestamps(long groupId, boolean privateLayout, boolean resetPublishDate) {
 
    String[] portletIds = ["15","20"]; //reset just journal and documents and media portlets
    Date publishDate = DateUtil.newDate();
    for (String portletId : portletIds) {
        PortletPreferences prefs =
                PortletPreferencesLocalServiceUtil.getPortletPreferences(groupId,
                        PortletKeys.PREFS_OWNER_TYPE_GROUP, PortletKeys.PREFS_PLID_SHARED, portletId);
 
        String xml = prefs.getPreferences();
 
        String portletName = portletId;
        Portlet portlet = PortletLocalServiceUtil.getPortletById(portletId);
        if (portlet != null) {
            portletName = portlet.getDisplayName()
        }
 
        if (Validator.isNull(xml)) {
            println portletName + " (" + portletId + "), no existing portlet preferences."
        }
        else {
            try {
                Document document = SAXReaderUtil.read(xml);
                Element root = document.getRootElement();
                Node node = root.selectSingleNode("/portlet-preferences/preference[name='last-publish-date']/value");
                if (node != null) {
                    String lastPublish = node.getText();
                    println portletName + " (" + portletId + "), last-publish-date: " + formatDate(lastPublish)
                }
            }
            catch (Exception e) {
                println "Unable to check preferences for portlet " + portletId + " - xml: " + xml;
            }
        }
 
        if (resetPublishDate) {
            println "Updating publish date for portlet " + portletId + " to " + publishDate;
            Layout layout = LayoutLocalServiceUtil.fetchLayout(groupId, privateLayout, 1);
 
            javax.portlet.PortletPreferences jxPreferences = PortletPreferencesFactoryUtil.getStrictPortletSetup(layout, portletId);
            StagingUtil.updateLastPublishDate(portletId, jxPreferences, publishDate)
        }
    }
}
 
def String formatDate(String timeInMillis) {
    long lastPublishDate = GetterUtil.getLong(timeInMillis)
    if (lastPublishDate > 0) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(lastPublishDate);
        return cal.getTime();
    }
    return null;
}