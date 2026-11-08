package experiment;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GeneratorTest {

    @Test
    void sameSeedGivesSameEditInput() {
        assertArrayEquals(Generator.editPair("random", 50), Generator.editPair("random", 50));
    }

    @Test
    void sameSeedGivesSameSearchInput() {
        assertArrayEquals(Generator.searchCase("random26", 500, 20), Generator.searchCase("random26", 500, 20));
    }

    @Test
    void inputShapes() {
        String[] same = Generator.editPair("identical", 30);
        assertEquals(same[0], same[1]);
        String[] worst = Generator.searchCase("worst_aab", 100, 10);
        assertEquals(100, worst[0].length());
        assertEquals("aaaaaaaaab", worst[1]);
    }

    @Test
    void unknownTypeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Generator.editPair("nope", 5));
        assertThrows(IllegalArgumentException.class, () -> Generator.searchCase("nope", 5, 2));
    }
}
