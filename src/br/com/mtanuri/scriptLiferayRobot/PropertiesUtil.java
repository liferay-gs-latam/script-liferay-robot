package br.com.mtanuri.scriptLiferayRobot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

	private static PropertiesUtil single_instance = null;

	private Properties prop = new Properties();

	private PropertiesUtil() throws IOException {
		InputStream in = PropertiesUtil.class
				.getResourceAsStream("/br/com/mtanuri/scriptLiferayRobot/config/config.properties");
		prop.load(in);
		in.close();
	}

	public static PropertiesUtil getInstance() throws IOException {
		if (single_instance == null)
			single_instance = new PropertiesUtil();

		return single_instance;
	}

	public String getPropertie(String key) {
		return prop.get(key).toString();
	}

}
