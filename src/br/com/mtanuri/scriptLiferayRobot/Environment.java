package br.com.mtanuri.scriptLiferayRobot;

public class Environment {

	private String blueGreen = "blue";
	private String server = "prd";
	private String sufix = ".smiles.com.br";

	public Environment() {
		super();
	}

	public Environment(String blueGreen, String server, String sufix) {
		super();
		this.blueGreen = blueGreen;
		this.server = server;
		this.sufix = sufix;
	}

	public String getUrl() {
		return "https://" + server + "-" + blueGreen + sufix;
	}

	public void setBlueGreen(String blueGreen) {
		this.blueGreen = blueGreen;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setSufix(String sufix) {
		this.sufix = sufix;
	}

	@Override
	public String toString() {
		return "Environment [blueGreen=" + blueGreen + ", server=" + server + ", sufix=" + sufix + "]";
	}

}
