package br.com.mtanuri.liferay.robotscript;

public class AppConfig {

	private String scriptName;
	private String printStatus;
	private int maxAttempts = 200;
	private int clusterSize = 1;
	private Environment environment = new Environment();
	private boolean readOnly;

	private String user = "administrador";
	private String pass = "******";

	public AppConfig(String user, String pass) {
		this.user = user;
		this.pass = pass;
	}

	public AppConfig(String[] args) {

		if (args != null) {
			for (String arg : args) {
				if (arg.length() > 2) {
					String paramName = "-h";
					if (arg.toLowerCase().startsWith(paramName)) {
						environment.setServer(arg.replace(paramName, "").toLowerCase());
						continue;
					}

					paramName = "-sfx";
					if (arg.toLowerCase().startsWith(paramName)) {
						environment.setSufix(arg.replace(paramName, "").toLowerCase());
						continue;
					}

					paramName = "-e";
					if (arg.toLowerCase().startsWith(paramName)) {
						environment.setBlueGreen(arg.replace(paramName, "").toLowerCase());
						continue;
					}

					paramName = "-s";
					if (arg.toLowerCase().startsWith(paramName)) {
						scriptName = arg.replace(paramName, "");
						continue;
					}

					paramName = "-u";
					if (arg.toLowerCase().startsWith(paramName)) {
						user = arg.replace(paramName, "").toLowerCase();
						continue;
					}

					paramName = "-p";
					if (arg.toLowerCase().startsWith(paramName)) {
						pass = arg.replace(paramName, "");
						continue;
					}

					paramName = "-m";
					if (arg.toLowerCase().startsWith(paramName)) {
						maxAttempts = Integer.parseInt(arg.replace(paramName, ""));
						continue;
					}

					paramName = "-f";
					if (arg.toLowerCase().startsWith(paramName)) {
						printStatus = arg.replace(paramName, "");
						continue;
					}

					paramName = "-c";
					if (arg.toLowerCase().startsWith(paramName)) {
						clusterSize = Integer.parseInt(arg.replace(paramName, ""));
						continue;
					}

					paramName = "-r";
					if (arg.toLowerCase().startsWith(paramName)) {
						readOnly = Boolean.parseBoolean(arg.replace(paramName, ""));
						continue;
					}
				}
			}
		}

	}

	public AppConfig withScriptName(String scriptName) {
		this.scriptName = scriptName;
		return this;
	}

	public AppConfig withPrintStatus(String printStatus) {
		this.printStatus = printStatus;
		return this;
	}

	public AppConfig withMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
		return this;
	}

	public AppConfig withClusterSize(int clusterSize) {
		this.clusterSize = clusterSize;
		return this;
	}

	public String getScriptName() {
		return scriptName;
	}

	public boolean isPrintStatus() {
		return printStatus != null && !printStatus.isEmpty();
	}

	public String getOutputFile() {
		return this.printStatus;
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public int getClusterSize() {
		return clusterSize;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

	public String getPrintStatus() {
		return printStatus;
	}

	public Environment getEnvironment() {
		return environment;
	}

	@Override
	public String toString() {
		return "AppConfig [scriptName=" + scriptName + ", printStatus=" + printStatus + ", maxAttempts=" + maxAttempts
				+ ", readOnly=" + readOnly + ", clusterSize=" + clusterSize + ", environment=" + environment + ", user="
				+ user + ", pass=" + "**********" + "]";
	}

	public static String getDoc() {
		StringBuilder sb = new StringBuilder();
		sb.append("****** Config Help ******");
		sb.append("\n");
		sb.append("-s [scriptName]: The name of the script which is saved in the scripts package");
		sb.append("\n");
		sb.append("-f [printStatus]: Dir where the script output is going to be saved");
		sb.append("\n");
		sb.append("-m [maxAttempts]: Number of attempts that the robot is going to perform to connect on the server");
		sb.append("\n");
		sb.append(
				"-c [clusterSize]: Number of cluster that the robot is going to discovery and try connecting into to it.");
		sb.append("\n");
		sb.append("-h [server]: The server prefix hostname");
		sb.append("\n");
		sb.append("-e [blueGreen]: blue or green");
		sb.append("\n");
		sb.append("-sfx [sufix]: The server sufix hostname");
		sb.append("\n");
		sb.append("-u [user]: The username to access liferay portal");
		sb.append("\n");
		sb.append("-p [pass]: The user password");
		sb.append("\n");
		sb.append("-r [readOnly]: Readonly when executing full script validation");
		sb.append("\n");
		try {
			sb.append("more info, see: " + PropertiesUtil.getInstance().getPropertie("git.repository"));
		} catch (Exception e) {
		}
		return sb.toString();
	}
}
