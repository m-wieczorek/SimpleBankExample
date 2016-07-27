package entities;

import java.util.HashMap;
import java.util.Map;

public class Bank {
	private Map<String, User> users;

	public Bank() {
		setUsers(new HashMap<>());
	}

	public Bank(Map<String, User> users) {
		this.setUsers(users);
	}

	public Map<String, User> getUsers() {
		return users;
	}

	public void setUsers(Map<String, User> users) {
		this.users = users;
	}

	public void addUser(User user) {
		getUsers().put(user.getLogin(), user);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getUsers() == null) ? 0 : getUsers().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bank other = (Bank) obj;
		if (getUsers() == null) {
			if (other.getUsers() != null)
				return false;
		} else if (!getUsers().equals(other.getUsers()))
			return false;
		return true;
	}
}