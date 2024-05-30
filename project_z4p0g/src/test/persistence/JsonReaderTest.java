package persistence;


import model.Cosmetic;
import model.Warehouse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JsonReaderTest extends JsonTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            Warehouse wh = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyWarehouse() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyWarehouse.json");
        try {
            Warehouse wh = reader.read();
            assertEquals("My warehouse", wh.getWarehouseName());
            assertEquals(0, wh.viewCosmetics().size());
        } catch (IOException e) {
            fail("Couldn't read the file");
        }
    }

    @Test
    void testReaderGeneralWarehouse() {

        JsonReader reader = new JsonReader("./data/testReaderGeneralWarehouse.json");
        try {
            Warehouse wh = new Warehouse("My warehouse");
            wh.addCosmetic(new Cosmetic("Lancome", "foundation"));
            wh.addCosmetic(new Cosmetic("Rare Beauty", "Bronzer"));
            JsonWriter writer = new JsonWriter("./data/testReaderGeneralWarehouse.json");
            writer.open();
            writer.write(wh);
            writer.close();

            wh = reader.read();
            assertEquals("My warehouse", wh.getWarehouseName());
            List<Cosmetic> cosmetics = wh.viewCosmetics();
            assertEquals(2, cosmetics.size());
            checkCosmetic("Lancome", "foundation", cosmetics.get(0));
            checkCosmetic("Rare Beauty", "Bronzer", cosmetics.get(1));

        } catch (IOException e) {
            fail("Couldn't read the file.");
        }
    }
}