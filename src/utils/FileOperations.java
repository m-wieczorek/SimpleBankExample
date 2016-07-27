package utils;

import java.io.IOException;
import java.util.Map;
import entities.User;

public interface FileOperations {
	public Map<String, User> readDatabase() throws IOException;
	public void writeDatabase(Map<String, User> map) throws IOException;

}
