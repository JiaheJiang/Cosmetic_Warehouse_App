package ui.functions;

import model.Cosmetic;
import model.Event;
import model.EventLog;
import model.Warehouse;
import model.exceptions.LastRemoveException;
import persistence.JsonReader;
import persistence.JsonWriter;
import ui.theme.CardPanel;
import ui.theme.RoundedButton;
import ui.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Constructs the themed warehouse window backed by the Warehouse model and JSON persistence
public class WarehouseGUI {
    private static final String JSON_STORE = "./data/warehouse.json";
    private static final String DEFAULT_BRAND = "Unknown";
    private static final String[] DEFAULT_COSMETICS =
            {"Moisturizers", "Cleansers", "Sunscreen", "Eyeliner", "Foundation", "Blush"};
    private static final String[] TABLE_COLUMNS = {"Brand", "Type"};

    private JFrame frame;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JTextField typeField;
    private JTextField brandField;
    private JLabel statusLabel;
    private JLabel countLabel;
    private JLabel dateTime;
    private JTextArea activityArea;
    private EventLog eventLog;
    private Warehouse warehouse;
    private final JsonWriter jsonWriter;
    private final JsonReader jsonReader;
    private final List<String> purchasedProducts;

    // EFFECTS: initializes the warehouse GUI, first asking whether to load the saved state
    public WarehouseGUI() {
        this(askUserForLoad());
    }

    // EFFECTS: initializes the warehouse GUI; loads the saved state when loadSavedState is
    //          true, otherwise starts from the default products
    public WarehouseGUI(boolean loadSavedState) {
        Theme.installLookAndFeel();
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        purchasedProducts = new ArrayList<>();
        warehouse = new Warehouse("wh1");
        eventLog = EventLog.getInstance();

        buildFrame();

        if (loadSavedState) {
            loadState();
        } else {
            initializeDefaultCosmetics();
        }

        startClock();
        frame.setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: adds the product typed in the form to the warehouse; the brand defaults to
    //          "Unknown" when left blank. Saves the adding behaviour to the event log.
    private void addCosmetic(ActionEvent e) {
        String type = typeField.getText().trim();
        String brand = brandField.getText().trim();
        if (type.isEmpty()) {
            setStatus("Please enter a valid cosmetic product.", true);
            return;
        }
        warehouse.addCosmetic(new Cosmetic(brand.isEmpty() ? DEFAULT_BRAND : brand, type));
        refreshTable();
        selectRow(tableModel.getRowCount() - 1);
        setStatus("You successfully added a new: " + type, false);
        logEvent("Add a new cosmetic product: " + type + " to the warehouse.");
        typeField.setText("");
        brandField.setText("");
        typeField.requestFocusInWindow();
    }

    // MODIFIES: this
    // EFFECTS: removes the selected product from the warehouse; at least one product must
    //          remain. Saves the removing behaviour to the event log.
    private void removeCosmetic(ActionEvent e) {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow < 0) {
            setStatus("There is no cosmetic product to remove.", true);
            return;
        }
        Cosmetic theCosmetic = warehouse.viewCosmetics().get(selectedRow);
        try {
            warehouse.removeCosmetic(theCosmetic);
            refreshTable();
            selectRow(Math.min(selectedRow, tableModel.getRowCount() - 1));
            setStatus("You successfully removed: " + theCosmetic.getCosType(), false);
            logEvent("Remove the cosmetic product: " + theCosmetic.getCosType() + " from the warehouse.");
        } catch (LastRemoveException ex) {
            setStatus("At least one cosmetic product must remain in the warehouse.", true);
        }
    }

    // MODIFIES: this
    // EFFECTS: adds the selected product to the purchasedProducts list and shows the running
    //          list of purchases. Saves the purchasing behaviour to the event log.
    private void purchaseCosmetic(ActionEvent e) {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow < 0) {
            setStatus("There is no cosmetic product to purchase.", true);
            return;
        }
        String selectedProduct = (String) tableModel.getValueAt(selectedRow, 1);
        purchasedProducts.add(selectedProduct);
        setStatus("You successfully purchased: " + selectedProduct, false);
        logEvent("Purchase the cosmetic product: " + selectedProduct + " from the warehouse.");
        showPurchaseSummary();
    }

    // EFFECTS: shows the running list of purchased products in a dialog
    private void showPurchaseSummary() {
        StringBuilder purchasedMessage = new StringBuilder("Products Purchased:\n");
        for (String product : purchasedProducts) {
            purchasedMessage.append("- ").append(product).append("\n");
        }
        JOptionPane.showMessageDialog(frame, purchasedMessage.toString(),
                "Purchased Products", JOptionPane.INFORMATION_MESSAGE);
    }

    // EFFECTS: saves the current state of the warehouse to file as JSON.
    //          Saves the saving action to the event log.
    private void saveState(ActionEvent ae) {
        try {
            jsonWriter.open();
            jsonWriter.write(warehouse);
            jsonWriter.close();
            setStatus("Saved the warehouse state to " + JSON_STORE, false);
            logEvent("Save the current warehouse state.");
        } catch (FileNotFoundException e) {
            setStatus("Unable to save the warehouse state to " + JSON_STORE, true);
        }
    }

    // EFFECTS: displays a confirmation dialog for loading the last warehouse state
    private static boolean askUserForLoad() {
        int dialogResult = JOptionPane.showConfirmDialog(null,
                "Do you want to load the last warehouse state?", "Load State Confirmation",
                JOptionPane.YES_NO_OPTION);
        return dialogResult == JOptionPane.YES_OPTION;
    }

    // MODIFIES: this
    // EFFECTS: loads the last saved warehouse state from file; falls back to the default
    //          cosmetics if the file cannot be read. Saves the loading behaviour to the event log.
    private void loadState() {
        try {
            warehouse = jsonReader.read();
            refreshTable();
            logEvent("Load the last saved warehouse state.");
        } catch (IOException | org.json.JSONException e) {
            setStatus("Unable to load a saved state; starting with the default products.", true);
            initializeDefaultCosmetics();
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes the default cosmetic products in the warehouse
    private void initializeDefaultCosmetics() {
        for (String cosmetic : DEFAULT_COSMETICS) {
            warehouse.addCosmetic(new Cosmetic(DEFAULT_BRAND, cosmetic));
        }
        refreshTable();
    }

    // MODIFIES: this
    // EFFECTS: rebuilds the table so it mirrors the cosmetics currently in the warehouse,
    //          keeps a row selected, and updates the product count
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Cosmetic cosmetic : warehouse.viewCosmetics()) {
            tableModel.addRow(new Object[]{cosmetic.getCosBrand(), cosmetic.getCosType()});
        }
        int count = tableModel.getRowCount();
        countLabel.setText(count == 1 ? "1 product" : count + " products");
        if (inventoryTable.getSelectedRow() < 0 && count > 0) {
            selectRow(0);
        }
    }

    // MODIFIES: this
    // EFFECTS: selects the given table row when it exists
    private void selectRow(int row) {
        if (row >= 0 && row < tableModel.getRowCount()) {
            inventoryTable.setRowSelectionInterval(row, row);
        }
    }

    // MODIFIES: this
    // EFFECTS: shows the outcome of the last action in the status bar, tinted red for errors
    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Theme.DANGER : Theme.TEXT);
    }

    // MODIFIES: this
    // EFFECTS: records the event in the shared event log and appends it to the activity panel
    private void logEvent(String description) {
        eventLog.logEvent(new Event(description));
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        activityArea.append("[" + stamp + "] " + description + "\n");
        activityArea.setCaretPosition(activityArea.getDocument().getLength());
    }

    // MODIFIES: this
    // EFFECTS: sets the current date and time in the clock label
    public void curDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        dateTime.setText(dtf.format(LocalDateTime.now()));
    }

    // MODIFIES: this
    // EFFECTS: starts a timer that refreshes the clock label every second
    private void startClock() {
        curDateTime();
        new Timer(1000, e -> curDateTime()).start();
    }

    // EFFECTS: print the eventLog when the window closes
    public void performOnWindowClose() {
        for (Event event : eventLog) {
            System.out.println(event);
        }
    }

    // MODIFIES: this
    // EFFECTS: builds the main frame with header, inventory table, side column, and status bar
    private void buildFrame() {
        frame = new JFrame("Cosmetic Warehouse");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 640);
        frame.setMinimumSize(new Dimension(840, 570));

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Theme.BACKGROUND);
        content.add(buildHeader(), BorderLayout.NORTH);
        content.add(buildCenter(), BorderLayout.CENTER);
        content.add(buildStatusBar(), BorderLayout.SOUTH);
        frame.setContentPane(content);

        addWindowListener(frame);
        frame.setLocationRelativeTo(null);
    }

    // EFFECTS: builds the plum header bar with the app title, warehouse name, and live clock
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.PRIMARY_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("Cosmetic Warehouse");
        title.setFont(Theme.font(Font.BOLD, 19));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Inventory of warehouse \"" + warehouse.getWarehouseName() + "\"");
        subtitle.setFont(Theme.font(Font.PLAIN, 12));
        subtitle.setForeground(new Color(0xD9C7E0));

        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setOpaque(false);
        titles.add(title);
        titles.add(subtitle);

        dateTime = new JLabel("", SwingConstants.RIGHT);
        dateTime.setFont(Theme.font(Font.PLAIN, 13));
        dateTime.setForeground(new Color(0xEADFF0));

        header.add(titles, BorderLayout.WEST);
        header.add(dateTime, BorderLayout.EAST);
        return header;
    }

    // EFFECTS: builds the central area: inventory card on the left, action column on the right
    private JPanel buildCenter() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Theme.BACKGROUND);
        center.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.weightx = 0.62;
        gbc.insets = new Insets(0, 0, 0, 12);
        center.add(buildInventoryCard(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.38;
        gbc.insets = new Insets(0, 0, 0, 0);
        center.add(buildSideColumn(), gbc);
        return center;
    }

    // EFFECTS: builds the card holding the inventory table, the product count, and the
    //          actions that operate on the selected row
    private JPanel buildInventoryCard() {
        CardPanel card = new CardPanel(new BorderLayout(0, 10), 16);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(Theme.sectionTitle("Inventory"), BorderLayout.WEST);
        countLabel = new JLabel("0 products");
        countLabel.setFont(Theme.font(Font.PLAIN, 12));
        countLabel.setForeground(Theme.TEXT_MUTED);
        top.add(countLabel, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(buildTableScrollPane(), BorderLayout.CENTER);
        card.add(buildActionRow(), BorderLayout.SOUTH);
        return card;
    }

    // EFFECTS: builds the row of actions below the table: purchase and remove act on the
    //          selected row, save persists the whole warehouse
    private JPanel buildActionRow() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JPanel selectionActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        selectionActions.setOpaque(false);
        JButton purchaseButton = RoundedButton.primary("Purchase selected");
        purchaseButton.addActionListener(this::purchaseCosmetic);
        selectionActions.add(purchaseButton);
        JButton removeButton = RoundedButton.danger("Remove selected");
        removeButton.addActionListener(this::removeCosmetic);
        selectionActions.add(removeButton);

        JButton saveButton = RoundedButton.tonal("Save warehouse");
        saveButton.addActionListener(this::saveState);

        row.add(selectionActions, BorderLayout.WEST);
        row.add(saveButton, BorderLayout.EAST);
        return row;
    }

    // EFFECTS: builds the styled, single-selection inventory table inside a scroll pane
    private JScrollPane buildTableScrollPane() {
        tableModel = createTableModel();
        inventoryTable = new JTable(tableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.setRowHeight(30);
        inventoryTable.setShowGrid(false);
        inventoryTable.setIntercellSpacing(new Dimension(0, 0));
        inventoryTable.setFont(Theme.font(Font.PLAIN, 13));
        inventoryTable.setSelectionBackground(Theme.PRIMARY_SOFT);
        inventoryTable.setSelectionForeground(Theme.PRIMARY_DARK);
        inventoryTable.setFillsViewportHeight(true);
        inventoryTable.setDefaultRenderer(Object.class, zebraRenderer());
        styleTableHeader();

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    // EFFECTS: builds the read-only table model behind the inventory table
    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // EFFECTS: builds the renderer that alternates row backgrounds for readability
    private DefaultTableCellRenderer zebraRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Theme.STRIPE);
                    c.setForeground(Theme.TEXT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        };
    }

    // MODIFIES: this
    // EFFECTS: styles the inventory table header to match the theme
    private void styleTableHeader() {
        JTableHeader header = inventoryTable.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 34));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, false, false, row, column);
                c.setBackground(Color.WHITE);
                c.setForeground(Theme.TEXT_MUTED);
                c.setFont(Theme.font(Font.BOLD, 12));
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                        BorderFactory.createEmptyBorder(0, 12, 0, 12)));
                return c;
            }
        });
    }

    // EFFECTS: builds the right-hand column: add-product form on top, activity log below
    private JPanel buildSideColumn() {
        JPanel column = new JPanel(new GridBagLayout());
        column.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 12, 0);
        column.add(buildAddCard(), gbc);

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        column.add(buildActivityCard(), gbc);
        return column;
    }

    // EFFECTS: builds the card with the new-product form (type required, brand optional)
    private JPanel buildAddCard() {
        CardPanel card = new CardPanel(new GridBagLayout(), 16);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        card.add(Theme.sectionTitle("Add a product"), gbc);

        gbc.insets = new Insets(12, 0, 4, 0);
        card.add(Theme.fieldCaption("Type (required)"), gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        typeField = Theme.textField(14);
        card.add(typeField, gbc);

        gbc.insets = new Insets(10, 0, 4, 0);
        card.add(Theme.fieldCaption("Brand (optional)"), gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        brandField = Theme.textField(14);
        card.add(brandField, gbc);

        addAddButton(card, gbc);
        return card;
    }

    // MODIFIES: this, card
    // EFFECTS: adds the Add button to the form and wires Enter in either field to it
    private void addAddButton(JPanel card, GridBagConstraints gbc) {
        JButton addButton = RoundedButton.primary("Add product");
        addButton.addActionListener(this::addCosmetic);
        typeField.addActionListener(this::addCosmetic);
        brandField.addActionListener(this::addCosmetic);
        gbc.insets = new Insets(14, 0, 0, 0);
        card.add(addButton, gbc);
    }

    // EFFECTS: builds the card with the live activity log
    private JPanel buildActivityCard() {
        CardPanel card = new CardPanel(new BorderLayout(0, 10), 16);
        card.add(Theme.sectionTitle("Activity log"), BorderLayout.NORTH);

        activityArea = new JTextArea();
        activityArea.setEditable(false);
        activityArea.setLineWrap(true);
        activityArea.setWrapStyleWord(true);
        activityArea.setFont(Theme.font(Font.PLAIN, 12));
        activityArea.setForeground(Theme.TEXT_MUTED);
        activityArea.setBackground(Theme.STRIPE);
        activityArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JScrollPane scroll = new JScrollPane(activityArea);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Theme.STRIPE);
        scroll.setMinimumSize(new Dimension(0, 120));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // EFFECTS: builds the bottom status bar that reports the outcome of the last action
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));

        statusLabel = new JLabel("Ready.");
        statusLabel.setFont(Theme.font(Font.PLAIN, 12));
        statusLabel.setForeground(Theme.TEXT);
        bar.add(statusLabel, BorderLayout.WEST);

        JLabel storeLabel = new JLabel("Data file: " + JSON_STORE);
        storeLabel.setFont(Theme.font(Font.PLAIN, 11));
        storeLabel.setForeground(Theme.TEXT_MUTED);
        bar.add(storeLabel, BorderLayout.EAST);
        return bar;
    }

    // MODIFIES: frame
    // EFFECTS: add a window listener that prints the event log when the window closes
    private void addWindowListener(JFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                performOnWindowClose();
            }
        });
    }
}
