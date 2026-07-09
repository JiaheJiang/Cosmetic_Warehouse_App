package ui;

import ui.functions.WarehouseGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// Construct a CosmeticAppGUI
public class CosmeticAppGUI implements ActionListener {
    private static final String USERNAME = "Crystal";
    private static final String PASSWORD = "666666";
    private static final String BACKGROUND_IMAGE_PATH = "background.jpg";
    private static final String BACKGROUND_IMAGE_URL =
            "https://img.freepik.com/premium-photo/purple-wallpaper-pink-sunrise-background_931878-26252.jpg";

    private JLabel label;
    private JLabel passwordLabel;
    private JLabel success;
    private JTextField userText;
    private JPasswordField passwordText;
    private JButton button;
    private JFrame frame;

    // Initialize the login interface of CosmeticApp
    public CosmeticAppGUI() {
        ensureBackgroundImage();

        initializeFrame();

        JPanel panel = createPanel();
        configureFrame(panel);

        initializeComponents(panel);

        frame.setVisible(true);
    }

    // EFFECTS: construct a new CosmeticAppGUI on the Swing event dispatch thread
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CosmeticAppGUI::new);
    }

    // EFFECTS: authenticate the user's identity
    @Override
    public void actionPerformed(ActionEvent e) {
        authenticateUser();
    }

    // EFFECT: Inside warehouse app will open only if the user have typed correct username and password
    private void authenticateUser() {
        String user = userText.getText();
        String password = new String(passwordText.getPassword());

        if (user.equals(USERNAME) && password.equals(PASSWORD)) {
            success.setText("Login successful!");
            openCosmeticGUI();
            closeLoginInterface();
        } else {
            success.setText("Login failed. " + "\n" + "Please check your credentials.");
        }
    }

    // EFFECTS: open warehouse application after entering correct username and password
    private void openCosmeticGUI() {
        new WarehouseGUI();
    }

    // EFFECTS: the login interface will disappear after entering warehouse application
    private void closeLoginInterface() {
        frame.dispose();
    }

    //EFFECTS: initialize the login frame
    private void initializeFrame() {
        frame = new JFrame("Cosmetic Warehouse Login");
        frame.setSize(350, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new JLabel(new ImageIcon(BACKGROUND_IMAGE_PATH)));
        frame.setLayout(new BorderLayout());
    }

    // EFFECTS: create a panel
    private JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    // EFFECTS: display the frame, add panel and border
    private void configureFrame(JPanel panel) {
        frame.add(panel, BorderLayout.CENTER);
    }

    // EFFECTS: initialize all
    private void initializeComponents(JPanel panel) {
        label = createLabel("Username", 16);
        userText = createTextField(20, 14);
        passwordLabel = createLabel("Password", 16);
        passwordText = createPasswordField(20, 14);
        button = createButton("Login", 16);
        success = createLabel("", 16);

        panel.add(label);
        panel.add(userText);
        panel.add(passwordLabel);
        panel.add(passwordText);
        panel.add(button);
        panel.add(success);
    }

    // EFFECTS: create a label
    private JLabel createLabel(String text, int fontSize) {
        JLabel newLabel = new JLabel(text);
        newLabel.setForeground(Color.WHITE);
        newLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
        return newLabel;
    }

    // EFFECTS: creates a text field
    private JTextField createTextField(int columns, int fontSize) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Arial", Font.PLAIN, fontSize));
        return textField;
    }

    // EFFECTS: create a password field
    private JPasswordField createPasswordField(int columns, int fontSize) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(new Font("Arial", Font.PLAIN, fontSize));
        return passwordField;
    }

    // EFFECTS: create a new button
    private JButton createButton(String text, int fontSize) {
        JButton newButton = new JButton(text);
        newButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        newButton.addActionListener(this);
        return newButton;
    }

    // EFFECTS: use the bundled background image for the login interface;
    //          download it only if the local file is missing
    private void ensureBackgroundImage() {
        Path targetPath = Path.of(BACKGROUND_IMAGE_PATH);
        if (Files.exists(targetPath)) {
            return;
        }
        try {
            URL url = new URL(BACKGROUND_IMAGE_URL);
            try (var in = url.openStream()) {
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            // the login screen still works without a background image
        }
    }
}
