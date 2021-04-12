package scriptLiferayRobot;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
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

	public static void main(String[] args) throws IOException, InterruptedException {
		if (Arrays.asList(args).contains("-help")) {
			LOGGER.info(AppConfig.getDoc());
			return;
		}
		AppConfig config = new AppConfig(args);
		run(config);
	}

	private static void run(AppConfig config) throws IOException, InterruptedException {
		LOGGER.info("************************************************************************************************"
				+ "\nHi! I'm Nicolas, the script executor robot!"
				+ "\nI'm going to help you by executing any script in a customer site."
				+ "\nBe sure you are connected at customer VPN."
				+ "\nIf you get some handshake error try changing your internet connection from wi-fi to 4G."
				+ "\ntype -help to get more information about the parameters you must to give me"
				+ "\nNice to meet you! :)" + "\nmore info, see: "
				+ PropertiesUtil.getInstance().getPropertie("git.repository")
				+ "\n************************************************************************************************");

		printBanner();
		LOGGER.info("Started: " + LOGGER.getName());
		LOGGER.info("Build Version: " + PropertiesUtil.getInstance().getPropertie("build.version"));
		LOGGER.info(config.toString());
		LOGGER.info("Executing automation at " + config.getEnvironment().getUrl() + " ...");

		Long time = System.currentTimeMillis();
		int clusterSize = config.getClusterSize();
		boolean hasNewPublication = false;

		if (config.getEnvironment().getServer().equals("prd")) {
			clusterSize = clusterSizeDicovery(config);
			if (config.getScriptName().equals("fullCache")) {
				String lastPublication = getLastPublication(time, config);
				if (hasNewPublication(lastPublication, config)) {
					hasNewPublication = true;
					if(!config.isReadOnly()) {
						updateExpandoValue(lastPublication, config);
					}
				} else {
					LOGGER.info("Finished with no attempts due to any new publications were found");
					return;
				}
			}
			if (config.getScriptName().equals("fullx")) { // dead code
				updateExpandoValue(time, config);
				LOGGER.info("Waiting for cache replication before starting validations ...");
				wait_(20);
			}
		}

		HashMap<String, String> map = new HashMap<String, String>();

		int attempts = 0;
		while (!(map.size() >= clusterSize || attempts >= config.getMaxAttempts())) {

			try {

				IpGetterPage ipGetterPage = new IpGetterPage(config.getEnvironment().getUrl());
				ipGetterPage.connect();

				if (!map.containsKey(ipGetterPage.getCurrentNode())) {

					LOGGER.info("Signing in " + ipGetterPage.getCurrentNode() + " ...");

					LoginPage loginPage = new LoginPage(config.getEnvironment().getUrl(), ipGetterPage.getCookies());
					loginPage.doLogin(config.getUser(), config.getPass());

					if (loginPage.isLoginSucess()) {
						LOGGER.info("Login success!");

						if (config.getScriptName().equals("full")) {

							// Portlets deploy status validation
							LOGGER.info(
									"Validating portlets deploy status at " + ipGetterPage.getCurrentNode() + " ...");
							AppManagerPage p = new AppManagerPage(config.getEnvironment().getUrl(),
									ipGetterPage.getCookies());
							p.connectWithCookies();

							String portletsToRedeploy_pluginRequired = PropertiesUtil.getInstance()
									.getPropertie("site.redeploy.portlets.withPlugins");

							String portletsToRedeploy_pluginNotRequired = PropertiesUtil.getInstance()
									.getPropertie("site.redeploy.portlets.withoutPlugins");

							String[] portletsToRedeploy_pluginRequiredArray = {};
							if (portletsToRedeploy_pluginRequired != null
									&& !portletsToRedeploy_pluginRequired.isEmpty()) {
								portletsToRedeploy_pluginRequiredArray = portletsToRedeploy_pluginRequired.split(",");
							}

							String[] portletsToRedeploy_pluginNotRequiredArray = {};
							if (portletsToRedeploy_pluginNotRequired != null
									&& !portletsToRedeploy_pluginNotRequired.isEmpty()) {
								portletsToRedeploy_pluginNotRequiredArray = portletsToRedeploy_pluginNotRequired
										.split(",");
							}

							String portletsNameConfig[][] = { portletsToRedeploy_pluginRequiredArray,
									portletsToRedeploy_pluginNotRequiredArray };

							for (int i = 0; i < portletsNameConfig.length; i++) {
								String[] portletsName = portletsNameConfig[i];
								for (String portletName : portletsName) {
									boolean mustToHavePlugins = i == 0;
									if (!p.isPortletDeployed(portletName, mustToHavePlugins)) {
										String scriptName = "redeploy-" + portletName.trim().replaceAll(" ", "")
												+ ".groovy";
										LOGGER.info(portletName + " IS NOT deployed correctly at "
												+ ipGetterPage.getCurrentNode() + " ...");
										if (!config.isReadOnly()) {
											LOGGER.info("Executing script " + scriptName + " at "
													+ ipGetterPage.getCurrentNode() + " ...");
											Script script = Script.getInstance(scriptName);
											new ServerAdminPage(config.getEnvironment().getUrl(),
													loginPage.getAuthToken(), ipGetterPage.getCookies())
															.runScript(script.getType(), script.getScriptCode());
										} else {
											LOGGER.info("Readonly mode is enabled");
										}
									} else {
										LOGGER.info(portletName + " is deployed correctly at "
												+ ipGetterPage.getCurrentNode() + " ...");
									}
								}
							}

							// Staging publication validation
							if (config.getEnvironment().getServer().equals("prdx")) { // dead code
								LOGGER.info("Validating cache replication health status at "
										+ ipGetterPage.getCurrentNode() + " ...");

								CustomFieldsPage c = new CustomFieldsPage(config.getEnvironment().getUrl(),
										loginPage.getAuthToken(), ipGetterPage.getCookies());
								c.connectWithCookies();
								String val = c.getDoc().selectFirst("#znux_null_null_last_2d_stg_2d_publication").val();
								if (!val.equals(String.valueOf(time))) {
									String clearCacheScript = "clearCache.groovy";
									LOGGER.info("Cache replication IS NOT working at " + ipGetterPage.getCurrentNode()
											+ " ...");
									if (!config.isReadOnly()) {
										LOGGER.info("Executing script " + clearCacheScript + " at "
												+ ipGetterPage.getCurrentNode() + " ...");
										Script script = Script.getInstance(clearCacheScript);
										new ServerAdminPage(config.getEnvironment().getUrl(), loginPage.getAuthToken(),
												ipGetterPage.getCookies()).runScript(script.getType(),
														script.getScriptCode());
									} else {
										LOGGER.info("Readonly mode is enabled");
									}
								} else {
									LOGGER.info("Cache replication is working correctly at "
											+ ipGetterPage.getCurrentNode() + " ...");
								}
							}
						}

						else if (config.getScriptName().equals("fullCache")) {
							if (hasNewPublication) {
								String clearCacheScript = "clearCache.groovy";
								LOGGER.info("Cache replication needed not executed yet at "
										+ ipGetterPage.getCurrentNode() + " ...");
								if (!config.isReadOnly()) {
									LOGGER.info("Executing script " + clearCacheScript + " at "
											+ ipGetterPage.getCurrentNode() + " ...");
									Script script = Script.getInstance(clearCacheScript);
									new ServerAdminPage(config.getEnvironment().getUrl(), loginPage.getAuthToken(),
											ipGetterPage.getCookies()).runScript(script.getType(),
													script.getScriptCode());
								} else {
									LOGGER.info("Readonly mode is enabled");
								}
							} else {
								LOGGER.info("Cache replication already executed at " + ipGetterPage.getCurrentNode()
										+ " ...");
							}
						}

						// Single script execution
						else {
							LOGGER.info("Executing script " + config.getScriptName() + " at "
									+ ipGetterPage.getCurrentNode() + " ...");
							Script script = Script.getInstance(config.getScriptName());
							ServerAdminPage runScript = new ServerAdminPage(config.getEnvironment().getUrl(),
									loginPage.getAuthToken(), ipGetterPage.getCookies()).runScript(script.getType(),
											script.getScriptCode());
							if (config.isPrintStatus()) {
								String folderName = config.getScriptName() + "_"
										+ TimeUtil.getInstance().getTimeInMillis();
								String fileName = ipGetterPage.getCurrentNode();
								runScript.printScriptResult(folderName, fileName, config.getOutputFile());
							}
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

	private static void wait_(int secounds) throws InterruptedException {
		for (int i = secounds; i >= 0; i--) {
			System.out.print(":::");
			Thread.sleep(1000);
		}
		System.out.println("");
	}

	private static void updateExpandoValue(String lastPublication, AppConfig config) throws InterruptedException {
		HashMap<String, String> map = new HashMap<String, String>();

		int attempts = 0;
		while (!(map.size() >= 1 || attempts >= 10)) {

			try {

				String newUrl = config.getEnvironment().getUrl().replace("prd", "pub");
				IpGetterPage ipGetterPage = new IpGetterPage(newUrl);
				ipGetterPage.connect();

				if (!map.containsKey(ipGetterPage.getCurrentNode())) {

					LOGGER.info("Signing in " + ipGetterPage.getCurrentNode() + " ...");

					LoginPage loginPage = new LoginPage(newUrl, ipGetterPage.getCookies());
					loginPage.doLogin(config.getUser(), config.getPass());

					if (loginPage.isLoginSucess()) {
						LOGGER.info("Login success!");

						String newScriptName = "updateExpandoValue.groovy";

						LOGGER.info(
								"Executing script " + newScriptName + " at " + ipGetterPage.getCurrentNode() + " ...");

						Script script = Script.getInstance(newScriptName);
						new ServerAdminPage(newUrl, loginPage.getAuthToken(), ipGetterPage.getCookies()).runScript(
								script.getType(),
								script.getScriptCode().replace("$time", String.valueOf(lastPublication)));

						String newScriptName2 = "clearCache.groovy";

						LOGGER.info(
								"Executing script " + newScriptName2 + " at " + ipGetterPage.getCurrentNode() + " ...");

						Script script2 = Script.getInstance(newScriptName2);
						new ServerAdminPage(newUrl, loginPage.getAuthToken(), ipGetterPage.getCookies())
								.runScript(script2.getType(), script2.getScriptCode());

						LOGGER.info("Signing out " + ipGetterPage.getCurrentNode() + " ...");
						new SimplePageImpl(newUrl, PropertiesUtil.getInstance().getPropertie("site.logout.url"),
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
		LOGGER.info("ExpandoValue Update finished with " + attempts + " attempts.");
	}

	private static void updateExpandoValue(Long time, AppConfig config) throws InterruptedException {
		HashMap<String, String> map = new HashMap<String, String>();

		int attempts = 0;
		while (!(map.size() >= 1 || attempts >= 10)) {

			try {

				String newUrl = config.getEnvironment().getUrl().replace("prd", "pub");
				IpGetterPage ipGetterPage = new IpGetterPage(newUrl);
				ipGetterPage.connect();

				if (!map.containsKey(ipGetterPage.getCurrentNode())) {

					LOGGER.info("Signing in " + ipGetterPage.getCurrentNode() + " ...");

					LoginPage loginPage = new LoginPage(newUrl, ipGetterPage.getCookies());
					loginPage.doLogin(config.getUser(), config.getPass());

					if (loginPage.isLoginSucess()) {
						LOGGER.info("Login success!");

						String newScriptName = "updateExpandoValue.groovy";

						LOGGER.info(
								"Executing script " + newScriptName + " at " + ipGetterPage.getCurrentNode() + " ...");

						Script script = Script.getInstance(newScriptName);
						new ServerAdminPage(newUrl, loginPage.getAuthToken(), ipGetterPage.getCookies()).runScript(
								script.getType(), script.getScriptCode().replace("$time", String.valueOf(time)));

						String newScriptName2 = "clearCache.groovy";

						LOGGER.info(
								"Executing script " + newScriptName2 + " at " + ipGetterPage.getCurrentNode() + " ...");

						Script script2 = Script.getInstance(newScriptName2);
						new ServerAdminPage(newUrl, loginPage.getAuthToken(), ipGetterPage.getCookies())
								.runScript(script2.getType(), script2.getScriptCode());

						LOGGER.info("Signing out " + ipGetterPage.getCurrentNode() + " ...");
						new SimplePageImpl(newUrl, PropertiesUtil.getInstance().getPropertie("site.logout.url"),
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
		LOGGER.info("ExpandoValue Update finished with " + attempts + " attempts.");
	}

	private static boolean hasNewPublication(String lastPublication, AppConfig config) throws InterruptedException {
		HashMap<String, String> map = new HashMap<String, String>();

		boolean newPublication = false;
		int attempts = 0;
		while (!(map.size() >= 1 || attempts >= 10)) {

			try {

				String newUrl = config.getEnvironment().getUrl().replace("prd", "pub");
				IpGetterPage ipGetterPage = new IpGetterPage(newUrl);
				ipGetterPage.connect();

				if (!map.containsKey(ipGetterPage.getCurrentNode())) {

					LOGGER.info("Signing in " + ipGetterPage.getCurrentNode() + " ...");

					LoginPage loginPage = new LoginPage(newUrl, ipGetterPage.getCookies());
					loginPage.doLogin(config.getUser(), config.getPass());

					if (loginPage.isLoginSucess()) {
						LOGGER.info("Login success!");

						LOGGER.info("Verifying if there is new staging publication at " + ipGetterPage.getCurrentNode()
								+ " ...");

						CustomFieldsPage c = new CustomFieldsPage(newUrl, loginPage.getAuthToken(),
								ipGetterPage.getCookies());
						c.connectWithCookies();
						String val = c.getDoc().selectFirst("#znux_null_null_last_2d_stg_2d_publication").val();
						if (!val.equals(lastPublication)) {
							LOGGER.info("Last clearing cache execution was at " + val);
							LOGGER.info("New publication has been found " + ipGetterPage.getCurrentNode() + " ...");
							newPublication = true;
						} else {
							LOGGER.info("Were not found new publications at " + ipGetterPage.getCurrentNode() + " ...");
						}

						LOGGER.info("Signing out " + ipGetterPage.getCurrentNode() + " ...");
						new SimplePageImpl(newUrl, PropertiesUtil.getInstance().getPropertie("site.logout.url"),
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
		LOGGER.info("Verifying new publications finished with " + attempts + " attempts.");
		return newPublication;
	}

	private static String getLastPublication(Long time, AppConfig config) throws InterruptedException {
		HashMap<String, String> map = new HashMap<String, String>();
		String lastPublication = "";
		int attempts = 0;
		while (!(map.size() >= 1 || attempts >= 10)) {

			try {

				String newUrl = config.getEnvironment().getUrl().replace("prd", "portal-stg");
				IpGetterPage ipGetterPage = new IpGetterPage(newUrl);
				ipGetterPage.connect();

				if (!map.containsKey(ipGetterPage.getCurrentNode())) {

					LOGGER.info("Signing in " + ipGetterPage.getCurrentNode() + " ...");

					LoginPage loginPage = new LoginPage(newUrl, ipGetterPage.getCookies());
					loginPage.doLogin(config.getUser(), config.getPass());

					if (loginPage.isLoginSucess()) {
						LOGGER.info("Login success!");

						String newScriptName = "selectLastPublication.groovy";

						LOGGER.info(
								"Executing script " + newScriptName + " at " + ipGetterPage.getCurrentNode() + " ...");

						Script script = Script.getInstance(newScriptName);
						lastPublication = new ServerAdminPage(newUrl, loginPage.getAuthToken(),
								ipGetterPage.getCookies()).runScript(script.getType(), script.getScriptCode())
										.getScriptOutput();
						String[] lines = lastPublication.split(System.getProperty("line.separator"));
						if (lines.length > 1) {
							lastPublication = lines[1];
						}

						LOGGER.info("Last publication was at " + lastPublication);

						LOGGER.info("Signing out " + ipGetterPage.getCurrentNode() + " ...");
						new SimplePageImpl(newUrl, PropertiesUtil.getInstance().getPropertie("site.logout.url"),
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
		LOGGER.info("Getting Last publication finished with " + attempts + " attempts.");
		return lastPublication;
	}

	private static int clusterSizeDicovery(AppConfig config) throws InterruptedException {
		HashMap<String, String> map = new HashMap<String, String>();
		int numberOfLicenses = 0;

		int attempts = 0;
		while (!(map.size() >= 1 || attempts >= 10)) {

			try {

				String newUrl = config.getEnvironment().getUrl().replace("prd", "pub");
				IpGetterPage ipGetterPage = new IpGetterPage(newUrl);
				ipGetterPage.connect();

				if (!map.containsKey(ipGetterPage.getCurrentNode())) {

					LOGGER.info("Signing in " + ipGetterPage.getCurrentNode() + " ...");

					LoginPage loginPage = new LoginPage(newUrl, ipGetterPage.getCookies());
					loginPage.doLogin(config.getUser(), config.getPass());

					if (loginPage.isLoginSucess()) {
						LOGGER.info("Login success!");

						LicenseManagerPage licenseManagerPage = new LicenseManagerPage(newUrl,
								ipGetterPage.getCookies());
						licenseManagerPage.connectWithCookies();

						LOGGER.info(
								"Discovering number of licenses " + " at " + ipGetterPage.getCurrentNode() + " ...");
						numberOfLicenses = licenseManagerPage.getNumberOfLicenses();
						LOGGER.info(numberOfLicenses + " licenses were discovered" + " at "
								+ ipGetterPage.getCurrentNode() + " ...");

						LOGGER.info("Signing out " + ipGetterPage.getCurrentNode() + " ...");
						new SimplePageImpl(newUrl, PropertiesUtil.getInstance().getPropertie("site.logout.url"),
								ipGetterPage.getCookies());

					} else {
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
		LOGGER.info("Discovery finished with " + attempts + " attempts.");
		return numberOfLicenses - 1;
	}

	private static void printBanner() {
		int width = 100;
		int height = 30;

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		g.setFont(new Font("SansSerif", Font.BOLD, 14));

		Graphics2D graphics = (Graphics2D) g;
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.drawString("ClusterTools", 0, 20);
		System.out.println("**************************************************************************************");

		for (int y = 0; y < height; y++) {
			StringBuilder sb = new StringBuilder();
			for (int x = 0; x < width; x++) {

				sb.append(image.getRGB(x, y) == -16777216 ? " " : "$");

			}

			if (sb.toString().trim().isEmpty()) {
				continue;
			}

			System.out.println(sb);
		}
		System.out.println("**************************************************************************************");

	}
}
