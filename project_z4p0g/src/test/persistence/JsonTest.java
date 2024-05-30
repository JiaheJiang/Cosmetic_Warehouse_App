package persistence;


import model.Cosmetic;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTest {
    protected void checkCosmetic(String cosBrand, String cosType, Cosmetic cos) {
        assertEquals(cosBrand, cos.getCosBrand());
        assertEquals(cosType, cos.getCosType());
    }
}
