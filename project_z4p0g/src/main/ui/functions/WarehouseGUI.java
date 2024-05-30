package ui.functions;

import model.Event;
import model.EventLog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Constructs a new warehouse GUI
public class WarehouseGUI {
    private JComboBox<String> chooseCosmetic;
    private JTextField newCosmeticTextField;
    private JButton addButton;
    private JButton removeButton;
    private JButton saveButton;
    private JButton purchaseButton; // Added button for purchasing
    private JLabel initialWarehouse;
    private JLabel currentWarehouse;
    private JLabel actionLabel;
    private JLabel dateTime;
    private EventLog eventLog;
    private String[] cosmetics = {"Moisturizers", "Cleansers", "Sunscreen", "Eyeliner", "Foundation", "Blush"};
    private List<String> purchasedProducts; // List to store purchased products

    // EFFECTS: Initializes the state of warehouse GUI
    public WarehouseGUI() {
        initializeComponents();
        configureButtons();
        configureFrame();
        curDateTime();
        purchasedProducts = new ArrayList<>(); // Initialize the list for purchased products

        boolean loadPreviousState = askUserForLoad();

        if (loadPreviousState) {
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

        purchaseButton = new JButton("Purchase cosmetic products"); // Added purchase button
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
        purchaseButton.addActionListener(this::purchaseCosmetic); // Added action for purchase button
        saveButton.addActionListener(this::saveState);
    }

    // EFFECTS: Configures the JFrame for the cosmetic warehouse application.
    private void configureFrame() {
        JFrame frame = new JFrame("Cosmetic warehouse!");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.add(chooseCosmetic);
        frame.add(newCosmeticTextField);
        frame.add(initialWarehouse);
        frame.add(addButton);
        frame.add(removeButton);
        frame.add(purchaseButton);
        frame.add(actionLabel);
        frame.add(dateTime);
        frame.add(saveButton);
        frame.add(currentWarehouse);
    }

    // EFFECTS: Adds a new cosmetic product to the chooseCosmetic combo box, by typing the name in provided textField.
    //          save the adding behaviour to eventLog
    private void addCosmetic(ActionEvent e) {
        if (e.getSource() == addButton) {
            String newCosmetic = newCosmeticTextField.getText().trim();
            if (!newCosmetic.isEmpty()) {
                actionLabel.setText("You successfully added a new: " + newCosmetic);
                chooseCosmetic.addItem(newCosmetic);
                EventLog.getInstance().logEvent(new Event("Add a new cosmetic product: "
                        + newCosmetic + " to the warehouse."));
                newCosmeticTextField.setText("");
            } else {
                actionLabel.setText("Please enter a valid cosmetic product.");
            }
        }
    }

    // EFFECTS: Removes the selected cosmetic product from the chooseCosmetic combo box.
    //          save the removing behaviour to eventLog
    private void removeCosmetic(ActionEvent e) {
        if (e.getSource() == removeButton) {
            String theCosmetic = chooseCosmetic.getItemAt(chooseCosmetic.getSelectedIndex()).toString();
            String msg = "You successfully removed: " + theCosmetic;
            EventLog.getInstance().logEvent(new Event("Remove the cosmetic product: "
                    + theCosmetic + " from the warehouse."));
            actionLabel.setText(msg);
            chooseCosmetic.removeItemAt(chooseCosmetic.getSelectedIndex());
        }
    }

    // EFFECTS: Adds the selected cosmetic product to the purchasedProducts list and displays a message with the
    //          purchased products.
    //          save the purchasing behaviour to eventLog
    private void purchaseCosmetic(ActionEvent e) {
        if (e.getSource() == purchaseButton) {
            String selectedProduct = chooseCosmetic.getItemAt(chooseCosmetic.getSelectedIndex()).toString();
            purchasedProducts.add(selectedProduct);
            actionLabel.setText("You successfully purchased: " + selectedProduct);

            EventLog.getInstance().logEvent(new Event("Purchase the cosmetic product: "
                    + selectedProduct + " from the warehouse."));


            // Print all purchased products as a string of messages
            StringBuilder purchasedMessage = new StringBuilder("Products Purchased:\n");
            for (String product : purchasedProducts) {
                purchasedMessage.append("- ").append(product).append("\n");
            }
            JOptionPane.showMessageDialog(null, purchasedMessage.toString(),
                    "Purchased Products", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // EFFECTS: Saves the current state of the warehouse to a file.
    //          Save the saving action to eventLog.
    private void saveState(ActionEvent ae) {
        if (ae.getSource() == saveButton) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("cosmetic_state.ser"))) {
                oos.writeObject(chooseCosmetic.getItemCount());
                for (int i = 0; i < chooseCosmetic.getItemCount(); i++) {
                    oos.writeObject(chooseCosmetic.getItemAt(i));
                }
                oos.writeObject(actionLabel.getText());

                EventLog.getInstance().logEvent(new Event("Save the current warehouse state."));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // EFFECTS: Displays a confirmation dialog for loading the last warehouse state.
    private boolean askUserForLoad() {
        int dialogResult = JOptionPane.showConfirmDialog(null,
                "Do you want to load the last warehouse state?", "Load State Confirmation",
                JOptionPane.YES_NO_OPTION);
        return dialogResult == JOptionPane.YES_OPTION;
    }

    // EFFECTS: Loads the last saved warehouse state from a file.
    //          Save the loading behaviour to eventLog.
    private void loadState() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("cosmetic_state.ser"))) {
            int itemCount = (int) ois.readObject();
            for (int i = 0; i < itemCount; i++) {
                chooseCosmetic.addItem((String) ois.readObject());
            }
            Object actionLabelText = ois.readObject();
            actionLabel.setText(actionLabelText.toString());

            EventLog.getInstance().logEvent(new Event("Load the last saved warehouse state."));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // EFFECTS: Initializes the default cosmetic products in the chooseCosmetic combo box.
    private void initializeDefaultCosmetics() {
        for (String cosmetic : cosmetics) {
            chooseCosmetic.addItem(cosmetic);
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


    // EFFECTS: Displays the main JFrame for the cosmetic warehouse application.
    private void showFrame() {
        JFrame frame = new JFrame("Cosmetics");
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
