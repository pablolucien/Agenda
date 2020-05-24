/**
   StringArraySorter.java
   Sort tomado del ejemplo de multithreading del tutorial de Sun
*/
package misc.ingedigit;

class StringArraySorter {
    private static final boolean ignoreCase = false;

    private static void sort(final String[] a, final int lo0, final int hi0) {
		int lo = lo0;
		int hi = hi0;
        if(lo >= hi) {
			return;
		}
		final String mid = a[(lo + hi) / 2];
        while(lo < hi) {
            if(ignoreCase) {
                while(lo<hi && a[lo].toLowerCase().compareTo(mid.toLowerCase()) < 0) {
                    lo++;
                }
                while(lo<hi && a[hi].toLowerCase().compareTo(mid.toLowerCase()) > 0) {
                    hi--;
                }
            }
            else {
                while(lo<hi && a[lo].compareTo(mid) < 0) {
                    lo++;
                }
                while(lo<hi && a[hi].compareTo(mid) > 0) {
                    hi--;
                }
            }
            if(lo < hi) {
				final String T = a[lo];
				a[lo] = a[hi];
				a[hi] = T;
			}
		}
        if(hi < lo) {
		final int T = hi;
		hi = lo;
		lo = T;
		}
		sort(a, lo0, lo);
		sort(a, lo == lo0 ? lo+1 : lo, hi0);
	}

    public static void sort(final String[] a) {
		sort(a, 0, a.length-1);
    }
}
