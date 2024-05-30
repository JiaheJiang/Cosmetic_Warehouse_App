package ui;

import model.Cosmetic;
import model.Warehouse;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

// Construct a CosmeticApp which fulfil all user's story.
public class CosmeticApp {
    private static final String JSON_STORE = "./data/warehouse.json";
    private Scanner input;
    private Warehouse wh;
    private Cosmetic cos1;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    // EFFECTS: runs the cosmetic application
    public CosmeticApp() throws FileNotFoundException {
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        runCosmetic();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    private void runCosmetic() {
        boolean keepGoing = true;
        String command = null;

        init();

        while (keepGoing) {
            displayMenu();
            command = input.next();

            if (command.equals("q")) {
                keepGoing = false;
            } else {
                processCommand(command);
            }
        }

        System.out.println("\nGoodbye!");
    }

    // MODIFIES: this
    // EFFECTS: processes user command
    void processCommand(String command) {
        if (command.equals("o")) {
            makeNextMove();
        } else if (command.equals("v")) {
            viewWarehouse();
        } else if (command.equals("a")) {
            addCosmetics();
        } else if (command.equals("r")) {
            removeCosmetics();
        } else if (command.equals("s")) {
            saveWarehouse();
        } else if (command.equals("l")) {
            loadWarehouse();
        } else {
            System.out.println("Selection not valid...");
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes a warehouse
    private void init() {
        cos1 = new Cosmetic("Lancome", "Eyeliner");
        wh = new Warehouse("wh1");
        wh.addCosmetic(cos1);
        input = new Scanner(System.in);
        input.useDelimiter("\n");
    }

    // EFFECTS: displays menu of options to user
    private void displayMenu() {
        System.out.println("\nSelect from:");
        System.out.println("\to -> order from the warehouse");
        System.out.println("\tv -> view the products in the warehouse");
        System.out.println("\ta -> add cosmetics into the warehouse");
        System.out.println("\tr -> remove cosmetics from the warehouse");
        System.out.println("\ts -> save warehouse to file");
        System.out.println("\tl -> load warehouse from file");
        System.out.println("\tq -> quit");
    }


    // EFFECTS: if the customer wants to order, ask what they need
    public void makeNextMove() {
        System.out.println("Please enter your name: ");
        String customerName = null;
        customerName = input.next();
        System.out.println("Welcome to our warehouse " + customerName + "!");

        System.out.println("Please enter the type of product you want to buy: ");
        String nextCommand = null;
        nextCommand = input.next();

        if (!matchProductsToNeed(nextCommand).isEmpty()) {
            System.out.println(
                    "Here is your product " + matchProductsToNeed(nextCommand).get(0).getCosBrand() + "!");
        } else {
            System.out.println("Sorry we can't find the product you need in our warehouse.");
        }
    }

    // EFFECTS: matching the proper products for the lovely customer from the warehouse,
    //          and remove the cosmetic from the warehouse
    public List<Cosmetic> matchProductsToNeed(String s) {
        List<Cosmetic> matchingProducts = new ArrayList<>();
        List<Cosmetic> allProducts = wh.viewCosmetics();

        for (Cosmetic cos : allProducts) {
            if (cos.getCosType().equals(s)) {
                matchingProducts.add(cos);
            }
        }
        return matchingProducts;
    }

    // EFFECTS: view all cosmetics' types in the warehouse
    public void viewWarehouse() {
        System.out.println("All products in the warehouse are " + wh.viewCosmeticsTypes());
    }

    // REQUIRES: the cosmetic isn't in the warehouse yet
    // EFFECTS: add the cosmetic to the warehouse
    public void addCosmetics() {
        System.out.println("Please enter the brand of product: ");
        String cosmeticBrand = null;
        cosmeticBrand = input.next();

        System.out.println("Please enter the type of product: ");
        String cosmeticType = null;
        cosmeticType = input.next();

        Cosmetic newCosmetic = new Cosmetic(cosmeticBrand, cosmeticType);
        wh.addCosmetic(newCosmetic);
        System.out.println("Successfully add the product " + cosmeticBrand + " to our warehouse!");

    }

    // REQUIRES: the cosmetic already exists in the warehouse
    // EFFECTS: remove the cosmetic from the warehouse
    public void removeCosmetics() {
        System.out.println("Please enter the brand of product: ");
        String cosmeticBrand = null;
        cosmeticBrand = input.next();

        System.out.println("Please enter the type of product: ");
        String cosmeticType = null;
        cosmeticType = input.next();

        List<Cosmetic> allProducts = wh.viewCosmetics();
        for (Iterator<Cosmetic> cosit = allProducts.iterator(); cosit.hasNext();) {
            Cosmetic cos = cosit.next();
            if (cos.getCosType().equals(cosmeticType) && cos.getCosBrand().equals(cosmeticBrand)) {
                cosit.remove();
            }
        }
        System.out.println("Successfully remove the product " + cosmeticBrand + " from our warehouse!");
    }

    // EFFECTS: saves the warehouse to file
    private void saveWarehouse() {
        try {
            jsonWriter.open();
            jsonWriter.write(wh);
            jsonWriter.close();
            System.out.println("Saved " + wh.getWarehouseName() + " to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads warehouse from file
    private void loadWarehouse() {
        try {
            wh = jsonReader.read();
            System.out.println("Loaded " + wh.getWarehouseName() + " from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }
}


