package model.test;

import model.Cosmetic;
import model.Warehouse;
import model.exceptions.LastRemoveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseTest {
    private Warehouse testWarehouse;
    private Cosmetic cos1;
    private Cosmetic cos2;

    @BeforeEach
    void runBefore() {

        testWarehouse = new Warehouse("wh1");
        cos1 = new Cosmetic("Lancome", "Eyeliner");
        cos2 = new Cosmetic("Too Faced", "Contour");
    }

    @Test
    void testConstructor() {
        assertEquals("wh1", testWarehouse.getWarehouseName());
        assertTrue(testWarehouse.viewCosmetics().isEmpty());

    }

    @Test
    void testAddCosmetic() {
        testWarehouse.addCosmetic(cos1);
        assertEquals(1, testWarehouse.viewCosmetics().size());
        assertEquals(cos1, testWarehouse.viewCosmetics().get(0));
    }

    @Test
    void testAddMultipleCosmetic() {
        testWarehouse.addCosmetic(cos1);
        testWarehouse.addCosmetic(cos2);
        assertEquals(2, testWarehouse.viewCosmetics().size());
        assertEquals(cos1, testWarehouse.viewCosmetics().get(0));
        assertEquals(cos2, testWarehouse.viewCosmetics().get(1));
    }


    @Test
    void testDeleteCosmetic() {
        testWarehouse.addCosmetic(cos1);
        try {
            testWarehouse.removeCosmetic(cos1);
            fail("Should have caught LastRemoveException.");
        } catch (LastRemoveException e) {
            // expected
        }
    }

    @Test
    void testDeleteCosmeticLeft() {
        testWarehouse.addCosmetic(cos1);
        testWarehouse.addCosmetic(cos2);
        try {
            testWarehouse.removeCosmetic(cos1);
        } catch (LastRemoveException e) {
            fail("Shouldn't throw the LastRemoveException.");
        }
        assertEquals(1, testWarehouse.viewCosmetics().size());
        assertEquals(cos2, testWarehouse.viewCosmetics().get(0));
    }

    @Test
    void testDeleteMultipleCosmetic() {
        testWarehouse.addCosmetic(cos1);
        testWarehouse.addCosmetic(cos2);
        testWarehouse.addCosmetic(cos1);
        testWarehouse.addCosmetic(cos1);
        try {
            testWarehouse.removeCosmetic(cos1);
            testWarehouse.removeCosmetic(cos2);
        } catch (LastRemoveException e) {
            fail("Shouldn't throw the LastRemoveException.");
        }
        assertEquals(2, testWarehouse.viewCosmetics().size());
        assertEquals(cos1, testWarehouse.viewCosmetics().get(0));
        assertEquals(cos1, testWarehouse.viewCosmetics().get(1));
    }

    @Test
    void testViewCosmeticsTypesEmpty() {
        assertEquals(0, testWarehouse.viewCosmeticsTypes().size());
    }

    @Test
    void testViewCosmeticsTypesOne() {
        testWarehouse.addCosmetic(cos1);
        assertEquals(1, testWarehouse.viewCosmeticsTypes().size());
        assertEquals(cos1.getCosType(), testWarehouse.viewCosmeticsTypes().get(0));
    }

    @Test
    void testViewCosmeticsTypesMultiple() {
        testWarehouse.addCosmetic(cos1);
        testWarehouse.addCosmetic(cos2);
        assertEquals(2, testWarehouse.viewCosmeticsTypes().size());
        assertEquals(cos1.getCosType(), testWarehouse.viewCosmeticsTypes().get(0));
        assertEquals(cos2.getCosType(), testWarehouse.viewCosmeticsTypes().get(1));
    }


}
