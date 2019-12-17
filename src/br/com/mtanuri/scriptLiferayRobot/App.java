package br.com.mtanuri.scriptLiferayRobot;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * 
 * @author marceltanuri
 *
 */
public class App {

	private static final Logger LOGGER = Logger.getLogger(App.class.getName());

	public static void main(String[] args) throws IOException {
		if (Arrays.asList(args).contains("-help")) {
			LOGGER.info(AppConfig.getDoc());
			return;
		}
		AppConfig config = new AppConfig(args);
		run(config);
	}

	private static void run(AppConfig config) {
		LOGGER.info("Started: " + LOGGER.getName());

		LOGGER.info("************************************************************************************************"
				+ "\nHi! I'm Nicolas, the script executor robot!"
				+ "\nI'm going to help you by executing any script in a customer site."
				+ "\nBe sure you are connected at customer VPN."
				+ "\nIf you get some handshake error try changing your internet connection from wi-fi to 4G."
				+ "\ntype -help to get more information about the parameters you must to give me"
				+ "\nNice to meet you! :)"
				+ "\n************************************************************************************************");

		LOGGER.info(config.toString());
		LOGGER.info("Executing automation at " + config.getEnvironment().getUrl() + " ...");

		HashMap<String, String> map = new HashMap<String, String>();

		int attempts = 0;
		while (!(map.size() >= config.getClusterSize() || attempts >= config.getMaxAttempts())) {

			try {

				IpGetterPage ipGetterPage = new IpGetterPage(config.getEnvironment().getUrl());
				ipGetterPage.connect();

				if (!map.containsKey(ipGetterPage.getCurrentNode())) {

					LOGGER.info("Signing in " + ipGetterPage.getCurrentNode() + " ...");

					LoginPage loginPage = new LoginPage(config.getEnvironment().getUrl(), ipGetterPage.getCookies());
					loginPage.doLogin(config.getUser(), config.getPass());

					if (loginPage.isLoginSucess()) {
						LOGGER.info("Login success!");

						LOGGER.info("Executing script " + config.getScriptName() + " at "
								+ ipGetterPage.getCurrentNode() + " ...");
						Script script = Script.getInstance(config.getScriptName());
						ServerAdminPage runScript = new ServerAdminPage(config.getEnvironment().getUrl(),
								loginPage.getAuthToken(), ipGetterPage.getCookies()).runScript(script.getType(),
										script.getScriptCode());
						if (config.isPrintStatus()) {
							String folderName = config.getScriptName() + "_" + TimeUtil.getInstance().getTimeInMillis();
							String fileName = ipGetterPage.getCurrentNode();
							runScript.printScriptResult(folderName, fileName, config.getOutputFile());
						}

						LOGGER.info("Signing out " + ipGetterPage.getCurrentNode() + " ...");
						new SimplePageImpl(config.getEnvironment().getUrl(),
								PropertiesUtil.getInstance().getPropertie("site.logout.url"),
								ipGetterPage.getCookies());
					}

					else {
						LOGGER.warning("Login failed");
					}

					map.put(ipGetterPage.getCurrentNode(), ipGetterPage.getCurrentNode());
				}
			}

			catch (java.net.SocketTimeoutException e) {
				LOGGER.warning("time out");
			}

			catch (org.jsoup.HttpStatusException e) {
				LOGGER.warning("504");
			}

			catch (IOException e) {
				LOGGER.warning("connection error");
			}

			attempts++;
		}

		LOGGER.info("Finished with " + attempts + " attempts. Number of accessed nodes: " + String.valueOf(map.size()));
	}
}
