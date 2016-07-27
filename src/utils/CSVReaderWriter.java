package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import entities.User;

public class CSVReaderWriter implements FileOperations {
	public static final String FILE = "Bank.csv";

	@Override
	public Map<String, User> readDatabase() throws IOException {
		Map<String, User> map = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
			String tmp = null;
			while ((tmp = reader.readLine()) != null) {
				addUserFromString(map, tmp);
			}
		}
		return map;
	}

	@Override
	public void writeDatabase(Map<String, User> map) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE))) {
			for (User u : map.values()) {
				writer.write(u.toString());
				writer.newLine();
			}
		}
	}


	private void addUserFromString(Map<String, User> map, String tmp) {
		String[] tmpFields = tmp.split(",");
		if (tmpFields[2].equals("ADMIN")) {
			map.put(tmpFields[0], new User(tmpFields[0], tmpFields[1], tmpFields[2]));
		} else if (tmpFields[2].equals("CLIENT")) {
			map.put(tmpFields[0], new User(tmpFields[0], tmpFields[1], tmpFields[2], new BigDecimal(tmpFields[3])));
		}
	}

	/*

	public Map<String, User> readDatabasefromCsv() throws IOException {
		Map<String, User> map = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
			String tmp = null;
			while ((tmp = reader.readLine()) != null) {
				addUserFromString(map, tmp);
			}
		}
		return map;
	}

	public void writeDatabaseToCsv(Map<String, User> map) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE))) {
			for (User u : map.values()) {
				writer.write(u.toString());
				writer.newLine();
			}
		}
	}

	*/
}
