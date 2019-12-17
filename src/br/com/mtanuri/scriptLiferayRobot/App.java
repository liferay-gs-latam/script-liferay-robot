package br.com.mtanuri.scriptLiferayRobot;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * 
 * @author marceltanuri
 *
 */
public class App {

	private static final Logger LOGGER = Logger.getLogger(App.class.getName());

	/**
	 * 
	 * Configuração do script:
	 * 
	 * (1) executa limpeza de cache
	 * 
	 * (2) executa statísticas de cache (requer editar caminho do output no
	 * properties)
	 * 
	 * (3) executa statísticas de cacheAPI (requer editar caminho do output no
	 * properties)
	 * 
	 * (4)executa redeploy da macro
	 * 
	 * (5) executa redeploy do optIn
	 * 
	 * (6) executa redeploy do ehCache
	 * 
	 * (7) limpa fila de stg paginas publicas
	 * 
	 * (8) limpa fila de stg paginas privadas
	 */
	public static void main(String[] args) throws IOException {

		if (args.length == 0) {
			LOGGER.warning("Invalid parameters");
			return;
		}

		if (args[0] == null) {
			LOGGER.warning("Invalid parameter! First parameter must be numeric:"
					+ "	 * (1) executa limpeza de cache\n" + "	 * \n"
					+ "	 * (2) executa statísticas de cache (requer editar caminho do output no\n"
					+ "	 * properties)\n" + "	 * \n"
					+ "	 * (3) executa statísticas de cacheAPI (requer editar caminho do output no\n"
					+ "	 * properties)\n" + "	 * \n" + "	 * (4)executa redeploy da macro\n" + "	 * \n"
					+ "	 * (5) executa redeploy do optin\n" + "	 * \n" + "	 * (6) executa redeploy do ehCache\n"
					+ "	 * \n" + "	 * (7) limpa fila de stg paginas publicas\n" + "	 * \n"
					+ "	 * (8) limpa fila de stg paginas privadas");
			return;
		}

		if (args[1] == null) {
			LOGGER.warning("Invalid parameter! Secound parameter must be String (blue or green)");
		}

		if (args[2] == null) {
			LOGGER.warning("Invalid parameter! Third parameter must be String (username)");
		}

		if (args[3] == null) {
			LOGGER.warning("Invalid parameter! Forth parameter must be String (password)");
		}

		int option = Integer.valueOf(args[0]);
		String env = args[1];
		String user = args[2];
		String pass = args[3];
		String outputFile = null;

		try {
			outputFile = args[4];
		} catch (Exception e) {

		}

		AppConfig config = config(option, env, user, pass, outputFile);

		run(config);
	}

	private static void run(AppConfig config) {
		LOGGER.info("Started: " + LOGGER.getName());

		LOGGER.info("************************************************************************************************"
				+ "\nHi! I'm Nicolas, the script executor robot!"
				+ "\nI'm going to help you by clearing DataBase Cache and runnnig groovy scripts in all cluster nodes at a customer site."
				+ "\nBe sure you are connected at customer VPN."
				+ "\nIf you get some handshake error try changing your internet connection from wi-fi to 4G."
				+ "\nNice to meet you! :)"
				+ "\n************************************************************************************************");

		LOGGER.info("Executing automation at " + config.getSiteDomain() + " ...");

		HashMap<String, String> map = new HashMap<String, String>();

		int attempts = 0;
		while (!(map.size() >= config.getClusterSize() || attempts >= config.getMaxAttempts())) {

			try {

				IpGetterPage ipGetterPage = new IpGetterPage(config.getSiteDomain());
				ipGetterPage.connect();

				if (!map.containsKey(ipGetterPage.getCurrentNode())) {

					LOGGER.info("Signing in " + ipGetterPage.getCurrentNode() + " ...");

					LoginPage loginPage = new LoginPage(config.getSiteDomain(), ipGetterPage.getCookies());
					loginPage.doLogin(config.getUser(), config.getPass());

					if (loginPage.isLoginSucess()) {
						LOGGER.info("Login success!");

						LOGGER.info("Executing script " + config.getScriptName() + " at "
								+ ipGetterPage.getCurrentNode() + " ...");
						Script script = Script.getInstance(config.getScriptName());
						ServerAdminPage runScript = new ServerAdminPage(config.getSiteDomain(),
								loginPage.getAuthToken(), ipGetterPage.getCookies()).runScript(script.getType(),
										script.getScriptCode());
						if (config.isPrintStatus()) {
							String folderName = config.getScriptName() + "_" + TimeUtil.getInstance().getTimeInMillis();
							String fileName = ipGetterPage.getCurrentNode();
							runScript.printScriptResult(folderName, fileName, config.getOutputFile());
						}

						LOGGER.info("Signing out " + ipGetterPage.getCurrentNode() + " ...");
						new SimplePageImpl(config.getSiteDomain(),
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

	private static AppConfig config(int key, String env, String user, String pass, String outputFile) {
		AppConfig config = null;
		switch (key) {
		case 1:
			config = new AppConfig(user, pass).withSiteDomain("https://prd-" + env + ".smiles.com.br")
					.withScriptName("clearCache.groovy");
			return config;

		case 2:

			config = new AppConfig(user, pass).withSiteDomain("https://prd-" + env + ".smiles.com.br")
					.withScriptName("cacheStatus.groovy").withPrintStatus(outputFile);
			return config;

		case 3:

			config = new AppConfig(user, pass).withSiteDomain("https://prd-" + env + ".smiles.com.br")
					.withScriptName("cacheStatusViaAPI.groovy").withPrintStatus(outputFile);
			return config;

		case 4:

			config = new AppConfig(user, pass).withSiteDomain("https://prd-" + env + ".smiles.com.br")
					.withScriptName("redeployClubeMacro.groovy");
			return config;

		case 5:

			config = new AppConfig(user, pass).withSiteDomain("https://prd-" + env + ".smiles.com.br")
					.withScriptName("redeployOptin.groovy");
			return config;

		case 6:

			config = new AppConfig(user, pass).withSiteDomain("https://prd-" + env + ".smiles.com.br")
					.withScriptName("redeployEhCache.groovy");
			return config;

		case 7:

			config = new AppConfig(user, pass).withSiteDomain("https://portal-stg-" + env + ".smiles.com.br")
					.withScriptName("limparFilaStagingPublicas.groovy").withClusterSize(1);
			return config;

		case 8:

			config = new AppConfig(user, pass).withSiteDomain("https://portal-stg-" + env + ".smiles.com.br")
					.withScriptName("limparFilaStagingPrivadas.groovy").withClusterSize(1);
			return config;

		case 9:

			config = new AppConfig(user, pass).withSiteDomain("https://portal-stg-" + env + ".smiles.com.br")
					.withScriptName("test.javascript").withClusterSize(1).withPrintStatus(outputFile);
			return config;

		default:
			return null;
		}
	}
}
