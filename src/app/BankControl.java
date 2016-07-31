package app;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Scanner;

import entities.Bank;
import entities.User;
import utils.CSVReaderWriter;

public class BankControl {
	private static final int LOGIN = 1;
	private static final int EXIT = 0;

	private Bank bank;
	private CSVReaderWriter databaseManager;
	private Scanner scanner;

	public BankControl() throws IOException {
		bank = new Bank();
		databaseManager = new CSVReaderWriter();
		scanner = new Scanner(System.in);
		try {
			bank.setUsers(databaseManager.readDatabase());
		} catch (IOException e) {
			System.out.println("Blad odczytu bazy uzytkownikow - utworzono nowa baze");
			bank.setUsers(new HashMap<>());
			System.out.println("Nalezy utworzyc konto administratora!\n" + "Podaj login administratora: ");
			String adminLogin = scanner.nextLine();
			System.out.println("Podaj haslo administratora: ");
			String adminPassword = scanner.nextLine();
			bank.addUser(new User(adminLogin, adminPassword, "ADMIN", null));
			databaseManager.writeDatabase(bank.getUsers());
		}
	}

	public void controlLoop() throws IOException {
		int option = -1;

		while (option != EXIT) {
			printOptions();

			option = scanner.nextInt();
			scanner.nextLine();

			switch (option) {
			case LOGIN:
				User user = checkLoginAndPassword(scanner);
				if (user.getUserType().equals("ADMIN")) {
					printAdminAccount(user, scanner);
				} else if (user.getUserType().equals("CLIENT")) {
					printClientAccount(user, scanner);
				}
				break;

			case EXIT:
				break;
			default:
				printDefault();
			}
		}
		scanner.close();
	}

	private void printOptions() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Witaj w prostym banku!\n");
		stringBuilder.append(LOGIN + " - zaloguj\n");
		stringBuilder.append(EXIT + " - wyjdz z programu");
		System.out.println(stringBuilder.toString());
	}

	private User checkLoginAndPassword(Scanner scanner) {
		User user = null;
		while (true) {
			System.out.println("Podaj login: ");
			String login = scanner.nextLine();
			System.out.println("Podaj haslo: ");
			String password = scanner.nextLine();
			if (bank.getUsers().get(login) != null) {
				user = bank.getUsers().get(login);
				if (password.equals(user.getPassword())) {
					return user;
				}
			}
		}
	}

	private void printAdminAccount(User user, Scanner scanner) throws IOException {
		final int ADD_USER = 1;
		final int REMOVE_USER = 2;
		int option = -1;

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("KONTO ADMINISTRATORA\n");
		stringBuilder.append("Witaj " + user.getLogin() + "!\n\n");
		stringBuilder.append(ADD_USER + " - dodaj uzytkownika\n");
		stringBuilder.append(REMOVE_USER + " - usun uzytkownika\n");
		stringBuilder.append("\n" + EXIT + " - wyloguj");

		while (option != EXIT) {
			System.out.println(stringBuilder.toString());

			option = scanner.nextInt();
			scanner.nextLine();

			switch (option) {
			case ADD_USER:
				addUser(scanner);
				save();
				break;
			case REMOVE_USER:
				removeUser(scanner, user);
				save();
				break;
			case EXIT:
				break;
			default:
				printDefault();
			}
		}
	}

	private void addUser(Scanner scanner) {
		User user = null;
		System.out.println("Podaj login nowego uzytkownika: ");
		String login = scanner.nextLine();
		if ((bank.getUsers().get(login)) == null) {
			System.out.println("Podaj haslo: ");
			String password = scanner.nextLine();
			System.out.println("Podaj typ konta (CLIENT/ADMIN): ");
			String userType = scanner.nextLine();
			if (userType.equals("ADMIN")) {
				user = new User(login, password, userType);
			} else if (userType.equals("CLIENT")) {
				System.out.println("Podaj poczatkowy stan konta [PLN]: ");
				BigDecimal accountBalance = scanner.nextBigDecimal();
				scanner.nextLine();
				user = new User(login, password, userType, accountBalance);
			}
			bank.addUser(user);
		} else {
			System.out.println("Podany login juz istnieje!");
		}
	}

	private void removeUser(Scanner scanner, User loggedUser) throws IOException {
		System.out.println("Podaj login uzytkownika do usuniecia: ");
		String login = scanner.nextLine();
		if (bank.getUsers().get(login) != null) {
			if (login.equals(loggedUser.getLogin())) {
				int adminsNumber = 0;
				for (User u : bank.getUsers().values()) {
					if (u.getUserType().equals("ADMIN")) {
						adminsNumber++;
					}
				}
				if (adminsNumber == 1) {
					System.out.println("Nie mozesz usunac ostatniego konta z uprawnieniami administratora!");
				} else {
					System.out.println("UWAGA! CZY NA PEWNO CHCESZ USUNAC WLASNE KONTO? (TAK/NIE)\n"
							+ "SPOWODUJE TO NATYCHMIASTOWE ZAMKNIECIE PROGRAMU!\n");
					if (scanner.nextLine().equals("TAK")) {
						bank.removeUser(bank.getUsers().get(login));
						save();
						System.exit(0);
					}
				}

			} else {
				bank.removeUser(bank.getUsers().get(login));
				System.out.println("Usunieto uzytkownika " + login);
			}
		} else {
			System.out.println("Nie znaleziono loginu w bazie!");
		}
	}

	private void printClientAccount(User user, Scanner scanner) throws IOException {
		final int TRANSFER = 1;
		int option = -1;

		while (option != EXIT) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Witaj " + user.getLogin() + "!\n");
			stringBuilder.append("Twoj stan konta: " + user.getAccountBalance() + " PLN\n\n");
			stringBuilder.append(TRANSFER + " - wykonaj przelew\n");
			stringBuilder.append("\n" + EXIT + " - wyloguj");

			System.out.println(stringBuilder.toString());

			option = scanner.nextInt();
			scanner.nextLine();

			switch (option) {
			case TRANSFER:
				transferMoney(scanner, user);
				save();
				break;
			case EXIT:
				break;
			default:
				printDefault();
			}
		}
	}

	private void transferMoney(Scanner scanner, User sender) {
		System.out.println("Podaj kwote przelewu [PLN]: ");
		BigDecimal transfer = scanner.nextBigDecimal();
		scanner.nextLine();
		if (transfer.compareTo(BigDecimal.ZERO) <= 0) {
			System.out.println("Przelew nie moze byc zrealizowany!\n" + "Kwota musi byc wieksza od zera!");
		} else {
			if (sender.getAccountBalance().compareTo(transfer) < 0) {
				System.out.println("Nie mozesz przelac kwoty wiekszej od aktualnego stanu konta!");

			} else {
				System.out.println("Podaj login adresata przelewu: ");
				String receiverLogin = scanner.nextLine();
				if ((bank.getUsers().get(receiverLogin)) != null) {
					User receiver = bank.getUsers().get(receiverLogin);
					if (receiver.getUserType().equals("CLIENT")) {
						if (receiver.getLogin().equals(sender.getLogin())) {
							System.out.println("Nie mozesz zlecic transferu do samego siebie!");
						} else {
							receiver.setAccountBalance(receiver.getAccountBalance().add(transfer));
							sender.setAccountBalance(sender.getAccountBalance().subtract(transfer));
							bank.getUsers().put(receiverLogin, receiver);
							bank.getUsers().put(sender.getLogin(), sender);
							System.out.println(
									"Przelano " + transfer.toString() + " PLN uzytkownikowi " + receiverLogin + "\n");
						}
					} else {
						System.out.println("Nie mozesz przelewac pieniedzy adminowi! :P");
					}
				} else {
					System.out.println("Podany uzytkownik nie istnieje!");
				}
			}
		}
	}

	private void save() throws IOException {
		databaseManager.writeDatabase(bank.getUsers());
	}

	private void printDefault() {
		System.out.println("Nie ma takiej opcji!\n");
	}

}