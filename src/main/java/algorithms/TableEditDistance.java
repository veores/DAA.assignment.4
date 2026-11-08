package algorithms;

public final class TableEditDistance implements EditDistance {

    private long cells;
    private long memory;

    @Override
    public int distance(String a, String b) {
        int n = a.length();
        int m = b.length();
        int[][] d = new int[n + 1][m + 1];
        cells = 0;
        memory = (long) d.length * d[0].length;
        for (int i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            d[0][j] = j;
        }
        for (int i = 1; i <= n; i++) {
            char x = a.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                cells++;
                if (x == b.charAt(j - 1)) {
                    d[i][j] = d[i - 1][j - 1];
                } else {
                    d[i][j] = 1 + Math.min(d[i - 1][j], Math.min(d[i][j - 1], d[i - 1][j - 1]));
                }
            }
        }
        return d[n][m];
    }

    @Override
    public long operations() {
        return cells;
    }

    @Override
    public long memoryCells() {
        return memory;
    }

    @Override
    public String name() {
        return "table";
    }
}
