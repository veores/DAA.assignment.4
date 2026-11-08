package algorithms;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class SearchTest {

    static Stream<StringSearch> searches() {
        return Stream.of(new NaiveSearch(), new KmpSearch());
    }

    @Test
    void lpsReferenceArrays() {
        assertArrayEquals(new int[] {0, 1, 2, 0, 1, 2, 3, 3}, KmpSearch.lps("AAACAAAA"));
        assertArrayEquals(new int[] {0, 0, 1, 2, 0, 1, 2, 3, 4}, KmpSearch.lps("ABABCABAB"));
    }

    @ParameterizedTest
    @MethodSource("searches")
    void referenceMatches(StringSearch s) {
        assertEquals(List.of(10), s.search("ABABDABACDABABCABAB", "ABABCABAB"));
        assertEquals(List.of(0, 1, 2, 3), s.search("AAAAA", "AA"));
        assertEquals(List.of(0, 2, 4), s.search("ABABABA", "ABA"));
    }

    @ParameterizedTest
    @MethodSource("searches")
    void noMatchReturnsEmptyList(StringSearch s) {
        assertTrue(s.search("abcdef", "xyz").isEmpty());
        assertTrue(s.search("", "a").isEmpty());
    }

    @ParameterizedTest
    @MethodSource("searches")
    void emptyPatternThrows(StringSearch s) {
        assertThrows(IllegalArgumentException.class, () -> s.search("abc", ""));
        assertThrows(IllegalArgumentException.class, () -> s.search("", ""));
    }

    @ParameterizedTest
    @MethodSource("searches")
    void patternLongerThanTextReturnsEmptyList(StringSearch s) {
        assertTrue(s.search("abc", "abcd").isEmpty());
    }

    @ParameterizedTest
    @MethodSource("searches")
    void matchAtBothEnds(StringSearch s) {
        assertEquals(List.of(0, 3), s.search("abcabc", "abc"));
        assertEquals(List.of(0), s.search("abc", "abc"));
    }

    @Test
    void naiveAndKmpAgreeOnRandomTexts() {
        Random r = new Random(7);
        NaiveSearch naive = new NaiveSearch();
        KmpSearch kmp = new KmpSearch();
        for (int t = 0; t < 100; t++) {
            String text = word(r, r.nextInt(60), 2);
            String pattern = word(r, 1 + r.nextInt(6), 2);
            assertEquals(naive.search(text, pattern), kmp.search(text, pattern), text + " / " + pattern);
        }
    }

    @Test
    void kmpComparisonsStayLinear() {
        KmpSearch kmp = new KmpSearch();
        String text = "a".repeat(5000);
        String pattern = "a".repeat(99) + "b";
        kmp.search(text, pattern);
        assertTrue(kmp.comparisons() <= 2L * (text.length() + pattern.length()));
    }

    @Test
    void naiveWorstCaseIsNTimesM() {
        NaiveSearch naive = new NaiveSearch();
        naive.search("a".repeat(1000), "a".repeat(9) + "b");
        assertEquals((1000L - 10 + 1) * 10, naive.comparisons());
    }

    private static String word(Random r, int length, int alphabet) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + r.nextInt(alphabet)));
        }
        return sb.toString();
    }
}
