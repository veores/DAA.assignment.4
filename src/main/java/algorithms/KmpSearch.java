package algorithms;

import java.util.ArrayList;
import java.util.List;

public final class KmpSearch implements StringSearch {

    private long comparisons;

    public static int[] lps(String p) {
        return lps(p, null);
    }

    private static int[] lps(String p, long[] counter) {
        int m = p.length();
        int[] f = new int[m];
        int len = 0;
        int i = 1;
        while (i < m) {
            if (counter != null) {
                counter[0]++;
            }
            if (p.charAt(i) == p.charAt(len)) {
                len++;
                f[i] = len;
                i++;
            } else if (len > 0) {
                len = f[len - 1];
            } else {
                f[i] = 0;
                i++;
            }
        }
        return f;
    }

    @Override
    public List<Integer> search(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("pattern must not be empty");
        }
        long[] counter = new long[1];
        List<Integer> result = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        if (m > n) {
            comparisons = 0;
            return result;
        }
        int[] f = lps(pattern, counter);
        int i = 0;
        int j = 0;
        while (i < n) {
            counter[0]++;
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) {
                    result.add(i - j);
                    j = f[j - 1];
                }
            } else if (j > 0) {
                j = f[j - 1];
            } else {
                i++;
            }
        }
        comparisons = counter[0];
        return result;
    }

    @Override
    public long comparisons() {
        return comparisons;
    }

    @Override
    public String name() {
        return "kmp";
    }
}
