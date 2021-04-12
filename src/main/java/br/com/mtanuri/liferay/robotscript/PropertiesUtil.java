package br.com.mtanuri.liferay.robotscript;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

	private static PropertiesUtil single_instance = null;

	private Properties prop = new Properties();

	private PropertiesUtil() throws IOException {
		InputStream resourceAsStream = getClass().getResourceAsStream("/config.properties");
		prop.load(resourceAsStream);
		resourceAsStream.close();
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
