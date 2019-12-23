package br.com.mtanuri.scriptLiferayRobot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Script {

	//private static Script single_instance = null;

	private String name;
	private final String path = "/br/com/mtanuri/scriptLiferayRobot/scripts/";

	private Script(String name) {
		this.name = name;
	}

	public static Script getInstance(String name) throws IOException {
		//if (single_instance == null)
		//	single_instance = new Script(name);

		return new Script(name);
	}

	public String getScriptCode() {
		StringBuilder sb = new StringBuilder();
		InputStream in = Script.class.getResourceAsStream(path + this.name);
		Scanner scanner = new Scanner(in);
		while (scanner.hasNextLine()) {
			sb.append(scanner.nextLine() + "\n");
		}
		scanner.close();
		return sb.toString();
	}

	public String getType() {
		return this.name.split("\\.")[1];
	}
}
