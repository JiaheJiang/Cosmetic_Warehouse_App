package model;

import model.exceptions.LastRemoveException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

// Construct a warehouse
public class Warehouse implements Writable {
    private final String warehouseName;
    private final List<Cosmetic> allCosmetics;

    // EFFECTS: initialize a warehouse with name, and an empty list of warehouse products
    public Warehouse(String name) {
        warehouseName = name;
        allCosmetics = new ArrayList<>();
    }

    // EFFECTS: get the name of the warehouse
    public String getWarehouseName() {
        return warehouseName;
    }

    // MODIFIES: this
    // EFFECTS: add the cosmetic to the warehouse
    public void addCosmetic(Cosmetic cos) {

        allCosmetics.add(cos);
    }

    // MODIFIES: this
    // EFFECTS: remove the cosmetic from the warehouse, if tries to remove the last item in warehouse,
    //          throw a LastRemoveException
    public void removeCosmetic(Cosmetic cos) throws LastRemoveException {
        if (allCosmetics.size() > 1) {
            allCosmetics.remove(cos);
        } else {
            throw new LastRemoveException();
        }
    }

    // EFFECTS: get a view of all available cosmetics in the warehouse
    public List<Cosmetic> viewCosmetics() {
        return allCosmetics;
    }

    // EFFECTS: get a view of all available cosmetics names in the warehouse
    public List<String> viewCosmeticsTypes() {
        List<String> cosmeticsTypes = new ArrayList<>();
        for (Cosmetic c : allCosmetics) {
            cosmeticsTypes.add(c.getCosType());
        }
        return cosmeticsTypes;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", warehouseName);
        json.put("cosmetics", cosmeticsToJson());
        return json;
    }

    // EFFECTS: returns cosmetic products in this warehouse as a JSON array
    private JSONArray cosmeticsToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Cosmetic c : allCosmetics) {
            jsonArray.put(c.toJson());
        }

        return jsonArray;
    }

}
