package tests;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.util.Random;

class TestRandom extends JFrame {
	private static final Random rnd = new Random();
    private static final long serialVersionUID = -5164857491742931226L;

    private TestRandom() {
		super("La donna è mobile");
		getContentPane().add(new JButton("Pulsame"));
		setSize(300, 300);
		setVisible(true);
	}

	// Common but flawed!
	private static int random(final int n) {
		return Math.abs(rnd.nextInt()) % n;
	}

	public static void main(final String[] args) {
		final TestRandom kk = new TestRandom();

		final int n = 2 * (Integer.MAX_VALUE / 3);
		int low = 0;
		for (int i = 0; i < 1000000; i++)
		if (random(n) < n/2)
			low++;
		System.out.println("Haciendolo ad hoc: " + low);

		low = 0;
		for (int i = 0; i < 1000000; i++)
		if (rnd.nextInt(n) < n/2)
			low++;
		System.out.println("Usando una API standard: " + low);


		System.out.println("1.0 / 10.0 = " + (1.0 / 10.0));
		System.out.println("1.03 - .42 = " + (1.03 - .42));
		System.out.println("Math.abs(Integer.MIN_VALUE) = "
			+ Math.abs(Integer.MIN_VALUE));
	}
}
