package tests;

class TestFinally {

	public static void main(final String[] args) {
		try  {
			System.out.println("¿Se ejecuta el finally?");
			System.exit(0);
		} finally {
			System.out.println("... Sí");
		}

	}
}