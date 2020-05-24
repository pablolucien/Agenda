package tests;

import java.io.Console;

public class PasswordTest
{

	private PasswordTest()
	{
		final Console cons;
		 final char[] passwd;
		 if ((cons = System.console()) != null &&
		     (passwd = cons.readPassword("[%s]", "Password:")) != null) {
		     final String pwd = new String(passwd);
		     System.out.println(pwd);
		     java.util.Arrays.fill(passwd, ' ');
		 }
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		new PasswordTest();
	}
}
