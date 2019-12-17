package br.com.mtanuri.scriptLiferayRobot;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ServerAdminPage extends SimplePageImpl {

	public ServerAdminPage(String siteDomain, String authToken, HashMap<String, String> cookies) throws IOException {
		super(siteDomain, PropertiesUtil.getInstance().getPropertie("site.controlPanel.url"), cookies, authToken);
	}

	public void clearCacheDB() throws IOException {
		HashMap<String, String> formData = new HashMap<>();
		formData.put("_137_cmd", "cacheDb");
		super.submit(formData);
	}

	public ServerAdminPage runScript(String language, String script) throws IOException {
		HashMap<String, String> formData = new HashMap<>();
		formData.put("_137_cmd", "runScript");
		formData.put("_137_language", language);
		formData.put("_137_script", script);
		String redirectUrl = super.getSiteDomain()
				+ "/group/control_panel/manage/-/server/script?refererPlid=2645764&_137_cur=0";
		formData.put("_137_redirect", redirectUrl);
		super.submit(formData);
		return this;
	}

	public void printScriptResult(String folderName, String fileName, String outputFile) throws IOException {
		String dirOutput = outputFile;
		String pathname = dirOutput + FileSystems.getDefault().getSeparator() + folderName;
		File f = new File(pathname);
		if (!f.exists()) {
			f.mkdir();
		}
		String finalFileName = pathname + FileSystems.getDefault().getSeparator() + fileName;
		File f2 = new File(finalFileName);
		f2.createNewFile();
		Files.write(Paths.get(finalFileName), super.getDoc().getElementsByTag("pre").first().html().getBytes());
	}
}