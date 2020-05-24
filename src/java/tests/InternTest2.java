package tests;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import static org.pclg.tools.StringTools.EMPTY_STRING_ARRAY;

class InternTest2 {
	private static String[] words = EMPTY_STRING_ARRAY;
	static String[] words2 = EMPTY_STRING_ARRAY;
	public static void main(final String[] args) throws IOException {
		final Reader r = new BufferedReader(new InputStreamReader(new FileInputStream("dictionary.txt")));
 		final StreamTokenizer st = new StreamTokenizer(r);
 		int tt;
 		final List<String> tokenList = new ArrayList<String>();
		while ((tt = st.nextToken()) != StreamTokenizer.TT_EOF) {
			if (tt == StreamTokenizer.TT_WORD) {
//				System.out.println("Word: " + st.toString());
//				tokenList.add(st.toString());
//				System.out.println("Word: " + st.sval);
				tokenList.add(st.sval);
			}
		}
		words = tokenList.toArray(words);
		//words2 = tokenList.toArray(words2);
		System.out.println("Hay " + words.length + " tokens");
		m0();
		m1();
		m2();
	}

	// Incorrecto
	private static void m0() {
		final long start = System.currentTimeMillis();
		int count = 1;
		final String[] unicas = new String[words.length];
		unicas[0] = words[0];
		outer:
		for (int ii = 0; ii < words.length; ii++) {
			int jj;
			for (jj = 0; jj < ii; jj++) {
				if (words[ii] == unicas[jj]) {
					continue outer;
				}
			}
			unicas[jj] = words[ii];
			count++;
		}
		System.out.println("Hay " + count + " unicas. Tiempo: "  + (System.currentTimeMillis() - start));
	}

	// Correcto pero lento
	private static void m1() {
		final long start = System.currentTimeMillis();
		int count = 1;
		final String[] unicas = new String[words.length];
		unicas[0] = words[0];
		outer:
		for (int ii = 0; ii < words.length; ii++) {
			int jj;
			for (jj = 0; jj < ii; jj++) {
				if (words[ii].equals(unicas[jj])) {
					continue outer;
				}
			}
			unicas[jj] = words[ii];
			count++;
		}
		System.out.println("Hay " + count + " unicas. Tiempo: "  + (System.currentTimeMillis() - start));
	}

	// Correcto y rápido
	private static void m2() {
		final long start = System.currentTimeMillis();
		for (int ii = 0; ii < words.length; ii++) {
			words[ii] = words[ii].intern();
		}
		int count = 1;
		final String[] unicas = new String[words.length];
		unicas[0] = words[0];
		outer:
		for (int ii = 0; ii < words.length; ii++) {
			int jj;
			for (jj = 0; jj < ii; jj++) {
				if (words[ii] == unicas[jj]) {
					continue outer;
				}
			}
			unicas[jj] = words[ii];
			count++;
		}
		System.out.println("Hay " + count + " unicas. Tiempo: "  + (System.currentTimeMillis() - start));
	}
}