package ui.functions;

import model.Cosmetic;
import model.Event;
import model.EventLog;
import model.Warehouse;
import model.exceptions.LastRemoveException;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Constructs a new warehouse GUI backed by the Warehouse model and JSON persistence
public class WarehouseGUI {
    private static final String JSON_STORE = "./data/warehouse.json";
    private static final String DEFAULT_BRAND = "Unknown";
    private static final String[] DEFAULT_COSMETICS =
            {"Moisturizers", "Cleansers", "Sunscreen", "Eyeliner", "Foundation", "Blush"};

    private JComboBox<String> chooseCosmetic;
    private JTextField newCosmeticTextField;
    private JButton addButton;
    private JButton removeButton;
    private JButton saveButton;
    private JButton purchaseButton;
    private JLabel initialWarehouse;
    private JLabel currentWarehouse;
    private JLabel actionLabel;
    private JLabel dateTime;
    private EventLog eventLog;
    private Warehouse warehouse;
    private final JsonWriter jsonWriter;
    private final JsonReader jsonReader;
    private final List<String> purchasedProducts;

    // EFFECTS: Initializes the state of warehouse GUI
    public WarehouseGUI() {
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        purchasedProducts = new ArrayList<>();
        warehouse = new Warehouse("wh1");

        initializeComponents();
        configureButtons();
        curDateTime();

        if (askUserForLoad()) {
            loadState();
        } else {
            initializeDefaultCosmetics();
        }

        showFrame();
    }

    // EFFECTS: Initializes various Swing components (buttons, labels, text fields) required for the GUI.
    private void initializeComponents() {
        chooseCosmetic = new JComboBox<>();
        chooseCosmetic.setBounds(300, 100, 200, 30);
        chooseCosmetic.setEditable(false);

        currentWarehouse = new JLabel("Current warehouse's products");
        currentWarehouse.setBounds(305, 50, 200, 30);

        initialWarehouse = new JLabel("Input a new cosmetic product");
        initialWarehouse.setBounds(105, 50, 200, 30);

        newCosmeticTextField = new JTextField();
        newCosmeticTextField.setBounds(100, 100, 200, 30);

        addButton = new JButton("Add a cosmetic product");
        addButton.setBounds(300, 150, 230, 30);

        removeButton = new JButton("Remove a cosmetic product");
        removeButton.setBounds(300, 200, 230, 30);

        purchaseButton = new JButton("Purchase cosmetic products");
        purchaseButton.setBounds(300, 300, 230, 30);

        actionLabel = new JLabel();
        actionLabel.setBounds(320, 70, 300, 20);

        dateTime = new JLabel();
        dateTime.setBounds(50, 20, 200, 20);

        saveButton = new JButton("Save current warehouse state");
        saveButton.setBounds(300, 250, 230, 30);

        eventLog = EventLog.getInstance();
    }

    // EFFECTS: Adds action listeners to buttons for performing specific actions.
    private void configureButtons() {
        addButton.addActionListener(this::addCosmetic);
        removeButton.addActionListener(this::removeCosmetic);
        purchaseButton.addActionListener(this::purchaseCosmetic);
        saveButton.addActionListener(this::saveState);
    }

    // MODIFIES: this
    // EFFECTS: Adds a new cosmetic product (by its type) to the warehouse, by typing the name
    //          in provided textField. Save the adding behaviour to eventLog.
    private void addCosmetic(ActionEvent e) {
        String newCosmetic = newCosmeticTextField.getText().trim();
        if (!newCosmetic.isEmpty()) {
            warehouse.addCosmetic(new Cosmetic(DEFAULT_BRAND, newCosmetic));
            refreshComboBox();
            actionLabel.setText("You successfully added a new: " + newCosmetic);
            eventLog.logEvent(new Event("Add a new cosmetic product: "
                    + newCosmetic + " to the warehouse."));
            newCosmeticTextField.setText("");
        } else {
            actionLabel.setText("Please enter a valid cosmetic product.");
        }
    }

    // MODIFIES: this
    // EFFECTS: Removes the selected cosmetic product from the warehouse; at least one product
    //          must remain in the warehouse. Save the removing behaviour to eventLog.
    private void removeCosmetic(ActionEvent e) {
        int selectedIndex = chooseCosmetic.getSelectedIndex();
        if (selectedIndex < 0) {
            actionLabel.setText("There is no cosmetic product to remove.");
            return;
        }
        Cosmetic theCosmetic = warehouse.viewCosmetics().get(selectedIndex);
        try {
            warehouse.removeCosmetic(theCosmetic);
            refreshComboBox();
            actionLabel.setText("You successfully removed: " + theCosmetic.getCosType());
            eventLog.logEvent(new Event("Remove the cosmetic product: "
                    + theCosmetic.getCosType() + " from the warehouse."));
        } catch (LastRemoveException ex) {
            actionLabel.setText("At least one cosmetic product must remain in the warehouse.");
        }
    }

    // MODIFIES: this
    // EFFECTS: Adds the selected cosmetic product to the purchasedProducts list and displays a message with the
    //          purchased products. Save the purchasing behaviour to eventLog.
    private void purchaseCosmetic(ActionEvent e) {
        int selectedIndex = chooseCosmetic.getSelectedIndex();
        if (selectedIndex < 0) {
            actionLabel.setText("There is no cosmetic product to purchase.");
            return;
        }
        String selectedProduct = chooseCosmetic.getItemAt(selectedIndex);
        purchasedProducts.add(selectedProduct);
        actionLabel.setText("You successfully purchased: " + selectedProduct);

        eventLog.logEvent(new Event("Purchase the cosmetic product: "
                + selectedProduct + " from the warehouse."));

        StringBuilder purchasedMessage = new StringBuilder("Products Purchased:\n");
        for (String product : purchasedProducts) {
            purchasedMessage.append("- ").append(product).append("\n");
        }
        JOptionPane.showMessageDialog(null, purchasedMessage.toString(),
                "Purchased Products", JOptionPane.INFORMATION_MESSAGE);
    }

    // EFFECTS: Saves the current state of the warehouse to file as JSON.
    //          Save the saving action to eventLog.
    private void saveState(ActionEvent ae) {
        try {
            jsonWriter.open();
            jsonWriter.write(warehouse);
            jsonWriter.close();
            actionLabel.setText("Saved the warehouse state to " + JSON_STORE);
            eventLog.logEvent(new Event("Save the current warehouse state."));
        } catch (FileNotFoundException e) {
            actionLabel.setText("Unable to save the warehouse state to " + JSON_STORE);
        }
    }

    // EFFECTS: Displays a confirmation dialog for loading the last warehouse state.
    private boolean askUserForLoad() {
        int dialogResult = JOptionPane.showConfirmDialog(null,
                "Do you want to load the last warehouse state?", "Load State Confirmation",
                JOptionPane.YES_NO_OPTION);
        return dialogResult == JOptionPane.YES_OPTION;
    }

    // MODIFIES: this
    // EFFECTS: Loads the last saved warehouse state from file; falls back to the default
    //          cosmetics if the file cannot be read. Save the loading behaviour to eventLog.
    private void loadState() {
        try {
            warehouse = jsonReader.read();
            refreshComboBox();
            eventLog.logEvent(new Event("Load the last saved warehouse state."));
        } catch (IOException | org.json.JSONException e) {
            actionLabel.setText("Unable to load a saved state; starting with the default products.");
            initializeDefaultCosmetics();
        }
    }

    // MODIFIES: this
    // EFFECTS: Initializes the default cosmetic products in the warehouse.
    private void initializeDefaultCosmetics() {
        for (String cosmetic : DEFAULT_COSMETICS) {
            warehouse.addCosmetic(new Cosmetic(DEFAULT_BRAND, cosmetic));
        }
        refreshComboBox();
    }

    // MODIFIES: this
    // EFFECTS: Rebuilds the combo box so it mirrors the cosmetics currently in the warehouse.
    private void refreshComboBox() {
        chooseCosmetic.removeAllItems();
        for (String type : warehouse.viewCosmeticsTypes()) {
            chooseCosmetic.addItem(type);
        }
    }

    // EFFECTS: Sets the current date and time in the dateTime label.
    public void curDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        dateTime.setText(dtf.format(now));
    }

    // EFFECTS: Print the eventLog when window closes
    public void performOnWindowClose() {
        for (Event event : eventLog) {
            System.out.println(event);
        }
    }

    // EFFECTS: Builds and displays the main JFrame for the cosmetic warehouse application.
    private void showFrame() {
        JFrame frame = new JFrame("Cosmetic warehouse!");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.add(chooseCosmetic);
        frame.add(newCosmeticTextField);
        frame.add(addButton);
        frame.add(removeButton);
        frame.add(purchaseButton);
        frame.add(actionLabel);
        frame.add(saveButton);
        frame.add(currentWarehouse);
        frame.add(dateTime);
        frame.add(initialWarehouse);

        addWindowListener(frame);

        frame.setVisible(true);
    }

    // EFFECTS: add a window listener when window closes
    private void addWindowListener(JFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                performOnWindowClose();
            }
        });
    }
}
