package experiment;

import algorithms.EditDistance;
import algorithms.KmpSearch;
import algorithms.NaiveSearch;
import algorithms.RecursiveEditDistance;
import algorithms.StringSearch;
import algorithms.TableEditDistance;
import algorithms.TwoRowEditDistance;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Experiment {

    private static final int RUNS = 5;
    private static final int PATTERN_LENGTH = 100;

    private static final String[] EDIT_INPUTS = {"random", "identical", "disjoint"};
    private static final String[] SEARCH_INPUTS = {"random26", "random_binary", "worst_aab", "all_match"};

    private static final int[] EDIT_SMALL_SIZES = {3, 6, 9, 12};
    private static final int[] EDIT_LARGE_SIZES = {250, 500, 1000, 2000, 4000};
    private static final int[] TEXT_SIZES = {20_000, 80_000, 320_000, 1_280_000};
    private static final int[] PATTERN_SIZES = {10, 20, 40, 80, 160};
    private static final int PATTERN_SWEEP_TEXT = 200_000;

    private Experiment() {
    }

    public static void main(String[] args) throws IOException {
        Path out = Path.of(args.length > 0 ? args[0] : "results/results.csv");
        if (out.getParent() != null) {
            Files.createDirectories(out.getParent());
        }
        warmUp();
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(out))) {
            w.println("family,algorithm,input,n,m,runs,time_ms,operations,memory_cells,result,seed");
            editSmall(w);
            editLarge(w);
            searchByText(w);
            searchByPattern(w);
        }
        System.out.println("written " + out);
    }

    private static void editSmall(PrintWriter w) {
        List<EditDistance> algos = List.of(
                new RecursiveEditDistance(), new TableEditDistance(), new TwoRowEditDistance());
        for (int n : EDIT_SMALL_SIZES) {
            for (String type : EDIT_INPUTS) {
                for (EditDistance algo : algos) {
                    measureEdit(w, "edit_small", algo, type, n);
                }
            }
        }
    }

    private static void editLarge(PrintWriter w) {
        List<EditDistance> algos = List.of(new TableEditDistance(), new TwoRowEditDistance());
        for (int n : EDIT_LARGE_SIZES) {
            for (String type : EDIT_INPUTS) {
                for (EditDistance algo : algos) {
                    measureEdit(w, "edit_large", algo, type, n);
                }
            }
        }
    }

    private static void searchByText(PrintWriter w) {
        List<StringSearch> algos = List.of(new NaiveSearch(), new KmpSearch());
        for (int n : TEXT_SIZES) {
            for (String type : SEARCH_INPUTS) {
                for (StringSearch algo : algos) {
                    measureSearch(w, "search_text", algo, type, n, PATTERN_LENGTH);
                }
            }
        }
    }

    private static void searchByPattern(PrintWriter w) {
        List<StringSearch> algos = List.of(new NaiveSearch(), new KmpSearch());
        for (int m : PATTERN_SIZES) {
            for (String type : new String[] {"random26", "worst_aab"}) {
                for (StringSearch algo : algos) {
                    measureSearch(w, "search_pattern", algo, type, PATTERN_SWEEP_TEXT, m);
                }
            }
        }
    }

    private static void measureEdit(PrintWriter w, String family, EditDistance algo, String type, int n) {
        String[] pair = Generator.editPair(type, n);
        double[] times = new double[RUNS];
        int result = 0;
        for (int r = 0; r < RUNS; r++) {
            long start = System.nanoTime();
            result = algo.distance(pair[0], pair[1]);
            times[r] = (System.nanoTime() - start) / 1e6;
        }
        w.println(String.format(Locale.ROOT, "%s,%s,%s,%d,%d,%d,%.6f,%d,%d,%d,%d",
                family, algo.name(), type, n, n, RUNS, median(times),
                algo.operations(), algo.memoryCells(), result, Generator.seed(type, n, n)));
        w.flush();
    }

    private static void measureSearch(PrintWriter w, String family, StringSearch algo, String type, int n, int m) {
        String[] tc = Generator.searchCase(type, n, m);
        double[] times = new double[RUNS];
        int result = 0;
        for (int r = 0; r < RUNS; r++) {
            long start = System.nanoTime();
            result = algo.search(tc[0], tc[1]).size();
            times[r] = (System.nanoTime() - start) / 1e6;
        }
        w.println(String.format(Locale.ROOT, "%s,%s,%s,%d,%d,%d,%.6f,%d,%d,%d,%d",
                family, algo.name(), type, n, m, RUNS, median(times),
                algo.comparisons(), 0L, result, Generator.seed(type, n, m)));
        w.flush();
    }

    private static double median(double[] values) {
        double[] copy = values.clone();
        Arrays.sort(copy);
        return copy[copy.length / 2];
    }

    private static void warmUp() {
        EditDistance[] eds = {new RecursiveEditDistance(), new TableEditDistance(), new TwoRowEditDistance()};
        StringSearch[] searches = {new NaiveSearch(), new KmpSearch()};
        long sink = 0;
        for (int round = 0; round < 300; round++) {
            String[] pair = Generator.editPair("random", 8);
            for (EditDistance ed : eds) {
                sink += ed.distance(pair[0], pair[1]);
            }
            String[] big = Generator.editPair("random", 200);
            sink += eds[1].distance(big[0], big[1]) + eds[2].distance(big[0], big[1]);
            String[] tc = Generator.searchCase("random_binary", 5000, 20);
            for (StringSearch s : searches) {
                sink += s.search(tc[0], tc[1]).size();
            }
        }
        if (sink == 42) {
            System.out.println();
        }
    }
}
