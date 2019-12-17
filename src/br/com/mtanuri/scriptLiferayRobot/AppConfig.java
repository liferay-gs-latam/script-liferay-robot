package br.com.mtanuri.scriptLiferayRobot;

public class AppConfig {

	private String scriptName;
	private String printStatus;
	private String siteDomain;
	private int maxAttempts = 150;
	private int clusterSize = 16;

	private String user;
	private String pass;

	public AppConfig(String user, String pass) {
		this.user = user;
		this.pass = pass;
	}

	public AppConfig withScriptName(String scriptName) {
		this.scriptName = scriptName;
		return this;
	}

	public AppConfig withPrintStatus(String printStatus) {
		this.printStatus = printStatus;
		return this;
	}

	public AppConfig withSiteDomain(String siteDomain) {
		this.siteDomain = siteDomain;
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

	public String getSiteDomain() {
		return siteDomain;
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public int getClusterSize() {
		return clusterSize;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

}
