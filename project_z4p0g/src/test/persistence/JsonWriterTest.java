package persistence;

import model.Cosmetic;
import model.Warehouse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JsonWriterTest extends JsonTest {
    //NOTE TO CPSC 210 STUDENTS: the strategy in designing tests for the JsonWriter is to
    //write data to a file and then use the reader to read it back in and check that we
    //read in a copy of what was written out.

    @Test
    void testWriterInvalidFile() {
        try {
            Warehouse wh = new Warehouse("My warehouse");
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyWarehouse() {
        try {
            Warehouse wh = new Warehouse("My warehouse");
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyWarehouse.json");
            writer.open();
            writer.write(wh);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyWarehouse.json");
            wh = reader.read();
            assertEquals("My warehouse", wh.getWarehouseName());
            assertEquals(0, wh.viewCosmetics().size());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralWarehouse() {
        try {
            Warehouse wh = new Warehouse("My warehouse");
            wh.addCosmetic(new Cosmetic("Lancome", "foundation"));
            wh.addCosmetic(new Cosmetic("Rare Beauty", "Bronzer"));
            JsonWriter writer = new JsonWriter("./data/testWriterGeneralWarehouse.json");
            writer.open();
            writer.write(wh);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralWarehouse.json");
            wh = reader.read();
            assertEquals("My warehouse", wh.getWarehouseName());
            List<Cosmetic> cosmetics = wh.viewCosmetics();
            assertEquals(2, cosmetics.size());
            checkCosmetic("Lancome", "foundation", cosmetics.get(0));
            checkCosmetic("Rare Beauty", "Bronzer", cosmetics.get(1));

        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}