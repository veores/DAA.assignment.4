package algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class EditDistanceTest {

    static Stream<EditDistance> versions() {
        return Stream.of(new RecursiveEditDistance(), new TableEditDistance(), new TwoRowEditDistance());
    }

    @ParameterizedTest
    @MethodSource("versions")
    void referenceValues(EditDistance ed) {
        assertEquals(3, ed.distance("kitten", "sitting"));
        assertEquals(2, ed.distance("flaw", "lawn"));
        assertEquals(5, ed.distance("intention", "execution"));
        assertEquals(3, ed.distance("", "abc"));
        assertEquals(0, ed.distance("abc", "abc"));
    }

    @ParameterizedTest
    @MethodSource("versions")
    void emptyStrings(EditDistance ed) {
        assertEquals(0, ed.distance("", ""));
        assertEquals(3, ed.distance("abc", ""));
        assertEquals(3, ed.distance("", "abc"));
        assertEquals(1, ed.distance("", "x"));
    }

    @ParameterizedTest
    @MethodSource("versions")
    void symmetric(EditDistance ed) {
        assertEquals(ed.distance("abylai han", "abylai khan"), ed.distance("abylai khan", "abylai han"));
        assertEquals(1, ed.distance("abylai han", "abylai khan"));
    }

    @Test
    void threeVersionsAgreeOnRandomShortStrings() {
        Random r = new Random(1);
        RecursiveEditDistance rec = new RecursiveEditDistance();
        TableEditDistance table = new TableEditDistance();
        TwoRowEditDistance rows = new TwoRowEditDistance();
        for (int t = 0; t < 100; t++) {
            String a = word(r, r.nextInt(9));
            String b = word(r, r.nextInt(9));
            int expected = table.distance(a, b);
            assertEquals(expected, rec.distance(a, b), a + " / " + b);
            assertEquals(expected, rows.distance(a, b), a + " / " + b);
        }
    }

    @Test
    void countersAreMeasuredInsideTheAlgorithms() {
        TableEditDistance table = new TableEditDistance();
        TwoRowEditDistance rows = new TwoRowEditDistance();
        RecursiveEditDistance rec = new RecursiveEditDistance();
        table.distance("kitten", "sitting");
        rows.distance("kitten", "sitting");
        assertEquals(6 * 7, table.operations());
        assertEquals(6 * 7, rows.operations());
        assertEquals(7 * 8, table.memoryCells());
        assertEquals(2 * 8, rows.memoryCells());
        rec.distance("abcd", "abcd");
        assertEquals(5, rec.operations());
    }

    private static String word(Random r, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + r.nextInt(3)));
        }
        return sb.toString();
    }
}
