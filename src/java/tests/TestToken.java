package tests;

public class TestToken {
	
	public static void main(final String[] args) {
		final String[] testStrings = {
			"aaa|bbb|ccc|ddd",
			"|aaa|bbb|   |ddd|",
			"aaa|bbb|ccc|ddd|",
			"|aaa|bbb|ccc|ddd",
		};
		
		for (int ii = 0; ii < testStrings.length; ii++) {
			System.out.println(testStrings[ii]);
			for (int jj = 1; jj < 6; jj++) {
				try {
					System.out.println("\t" + jj + ": [" + dameToken(testStrings[ii], jj) + "]");
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Devuelve el número de token del string pasado delimitado por |.
	 * @param strOriginal
	 * @return iNumeroToken
	 */	
	private static String dameToken(final String strOriginal,
		final int iNumeroToken){
		final char chDelimiter = '|';
		String strSub = null;
		final int iLongStrOriginal = strOriginal.length();
		int iOrdenToken = 1;
		boolean blCorrecto = false;
		int i = 0;
		int j = strOriginal.indexOf(chDelimiter);
		while( j >= 0 && !blCorrecto) {
			strSub = strOriginal.substring(i,j);
			i = j + 1;
			j = strOriginal.indexOf(chDelimiter, i);
			if (j < 0) {j = iLongStrOriginal;}
			if (iOrdenToken == iNumeroToken) { blCorrecto = true; }
			iOrdenToken++;
		}
		return strSub;
	}

}