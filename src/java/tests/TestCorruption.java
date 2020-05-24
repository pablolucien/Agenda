package tests;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class TestCorruption {
	private static final int THREADS = 2;
	private static final CountDownLatch LATCH =
			new CountDownLatch(THREADS);
	private static final int ONE_THOUSAND = 1000;
	private static final BankAccount HEINZ =
			new BankAccount(ONE_THOUSAND);
	private static final int ONE_HUNDRED = 100;

	private TestCorruption() {
	}

	public static void main(final String[] args) {
		for (int i = 0; i < THREADS; i++) {
			addThread();
		}
		final Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println(HEINZ.getBalance());
			}
		}, ONE_HUNDRED, ONE_THOUSAND);
	}

	private static void addThread() {
		new Thread() {
			{
				start();
			}

			@Override
			public void run() {
				LATCH.countDown();
				try {
					LATCH.await();
				} catch (final InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
				while (true) {
					HEINZ.deposit(ONE_HUNDRED);
					HEINZ.withdraw(ONE_HUNDRED);
				}
			}
		};
	}
}