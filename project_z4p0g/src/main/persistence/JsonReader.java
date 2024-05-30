package persistence;

import model.Cosmetic;
import model.Warehouse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.json.*;

// Represents a reader that reads warehouse from JSON data stored in file
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads warehouse from file and returns it;
    // throws IOException if an error occurs reading data from file
    public Warehouse read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseWareHouse(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses warehouse from JSON object and returns it
    private Warehouse parseWareHouse(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        Warehouse wh = new Warehouse(name);
        addCosmetics(wh, jsonObject);
        return wh;
    }

    // MODIFIES: wh
    // EFFECTS: parses cosmetics from JSON object and adds them to warehouse
    private void addCosmetics(Warehouse wh, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("cosmetics");
        for (Object json : jsonArray) {
            JSONObject nextCosmetic = (JSONObject) json;
            addCosmetic(wh, nextCosmetic);
        }
    }

    // MODIFIES: wh
    // EFFECTS: parses cosmetics from JSON object and adds it to warehouse
    private void addCosmetic(Warehouse wh, JSONObject jsonObject) {
        String cosBrand = jsonObject.getString("cosmeticBrand");
        String cosType = jsonObject.getString("cosmeticType");
        Cosmetic c = new Cosmetic(cosBrand, cosType);
        wh.addCosmetic(c);
    }
}
