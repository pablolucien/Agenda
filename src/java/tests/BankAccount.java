package tests;

class BankAccount {
	private volatile int balance;

	public BankAccount(final int balance) {
		this.balance = balance;
	}

	public void deposit(final int amount) {
		final int check = balance + amount;
		balance = check;
		if (balance != check) {
			throw new AssertionError("Data Race Detected");
		}
	}

	public void withdraw(final int amount) {
		deposit(-amount);
	}

	public int getBalance() {
		return balance;
	}
}
