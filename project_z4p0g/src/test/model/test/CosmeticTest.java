package model.test;

import model.Cosmetic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CosmeticTest {
    private Cosmetic testCosmetic;

    @BeforeEach
    void runBefore() {
        testCosmetic = new Cosmetic("Dermalogica", "exfoliator");
    }

    @Test
    void testConstructor() {
        assertEquals("Dermalogica", testCosmetic.getCosBrand());
        assertEquals("exfoliator", testCosmetic.getCosType());

    }


}
