package model;

import org.json.JSONObject;
import persistence.Writable;

// Construct a cosmetic product.
public class Cosmetic implements Writable {
    private String cosBrand;
    private String cosType;

    // EFFECTS: initialize a cosmetic with its name, effect, and number of
    // products stored in the warehouse
    public Cosmetic(String brand, String type) {
        cosBrand = brand;
        cosType = type;

    }

    // EFFECTS: get the brand's name of this certain cosmetic product
    public String getCosBrand() {
        return cosBrand;
    }

    // EFFECTS: get the type of this certain cosmetic product
    public String getCosType() {
        return cosType;
    }

    // EFFECTS: write the cosmetic product to json
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("cosmeticBrand", cosBrand);
        json.put("cosmeticType", cosType);
        return json;
    }
}
