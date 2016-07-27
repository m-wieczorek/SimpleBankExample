package app;

import java.io.IOException;


public class BankApp {

	public static final String appNameAndVersion = "SimpleBank v 0.2\n";

	public static void main(String[] args) throws IOException {
		System.out.println(appNameAndVersion);
		BankControl bankControl = new BankControl();
		bankControl.controlLoop();
	}
}
