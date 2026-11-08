package experiment;

import java.util.Random;

public final class Generator {

    public static final long BASE_SEED = 20261108L;

    private Generator() {
    }

    public static long seed(String type, int n, int m) {
        return BASE_SEED * 31 + type.hashCode() * 1_000_003L + n * 7_919L + m;
    }

    public static String random(Random r, int length, int alphabet) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + r.nextInt(alphabet)));
        }
        return sb.toString();
    }

    public static String repeat(char c, int length) {
        char[] chars = new char[length];
        java.util.Arrays.fill(chars, c);
        return new String(chars);
    }

    public static String[] editPair(String type, int n) {
        Random r = new Random(seed(type, n, n));
        switch (type) {
            case "random": {
                String a = random(r, n, 26);
                String b = random(r, n, 26);
                return new String[] {a, b};
            }
            case "identical": {
                String a = random(r, n, 26);
                return new String[] {a, a};
            }
            case "disjoint":
                return new String[] {repeat('a', n), repeat('b', n)};
            default:
                throw new IllegalArgumentException("unknown input type: " + type);
        }
    }

    public static String[] searchCase(String type, int n, int m) {
        Random r = new Random(seed(type, n, m));
        switch (type) {
            case "random26": {
                String text = random(r, n, 26);
                String pattern = random(r, m, 26);
                return new String[] {text, pattern};
            }
            case "random_binary": {
                String text = random(r, n, 2);
                String pattern = random(r, m, 2);
                return new String[] {text, pattern};
            }
            case "worst_aab":
                return new String[] {repeat('a', n), repeat('a', m - 1) + "b"};
            case "all_match":
                return new String[] {repeat('a', n), repeat('a', m)};
            default:
                throw new IllegalArgumentException("unknown input type: " + type);
        }
    }
}
