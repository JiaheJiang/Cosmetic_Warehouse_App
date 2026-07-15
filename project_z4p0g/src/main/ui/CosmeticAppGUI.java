package ui;

import ui.functions.WarehouseGUI;
import ui.theme.CardPanel;
import ui.theme.GradientPanel;
import ui.theme.RoundedButton;
import ui.theme.Theme;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Construct a CosmeticAppGUI: the themed login window that guards the warehouse
public class CosmeticAppGUI implements ActionListener {
    private static final String USERNAME = "Crystal";
    private static final String PASSWORD = "666666";

    private JTextField userText;
    private JPasswordField passwordText;
    private JButton loginButton;
    private JLabel statusLabel;
    private JFrame frame;

    // EFFECTS: initialize the login interface of CosmeticApp
    public CosmeticAppGUI() {
        Theme.installLookAndFeel();
        initializeFrame();
        frame.setContentPane(buildBackdrop());
        frame.getRootPane().setDefaultButton(loginButton);
        frame.setVisible(true);
        userText.requestFocusInWindow();
    }

    // EFFECTS: construct a new CosmeticAppGUI on the Swing event dispatch thread
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CosmeticAppGUI::new);
    }

    // EFFECTS: authenticate the user's identity when the login button (or Enter) is pressed
    @Override
    public void actionPerformed(ActionEvent e) {
        authenticateUser();
    }

    // EFFECTS: open the warehouse app only if the user typed the correct username and password
    private void authenticateUser() {
        String user = userText.getText();
        String password = new String(passwordText.getPassword());

        if (user.equals(USERNAME) && password.equals(PASSWORD)) {
            statusLabel.setForeground(Theme.SUCCESS);
            statusLabel.setText("Login successful!");
            openCosmeticGUI();
            closeLoginInterface();
        } else {
            statusLabel.setForeground(Theme.DANGER);
            statusLabel.setText("Login failed. Please check your credentials.");
        }
    }

    // EFFECTS: open the warehouse application after entering the correct username and password
    private void openCosmeticGUI() {
        new WarehouseGUI();
    }

    // EFFECTS: the login interface will disappear after entering the warehouse application
    private void closeLoginInterface() {
        frame.dispose();
    }

    // EFFECTS: initialize the login frame
    private void initializeFrame() {
        frame = new JFrame("Cosmetic Warehouse Login");
        frame.setSize(440, 540);
        frame.setMinimumSize(new Dimension(400, 500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }

    // EFFECTS: build the gradient backdrop with the login card centred on it
    private JPanel buildBackdrop() {
        GradientPanel backdrop = new GradientPanel(new GridBagLayout());
        backdrop.add(buildCard(), new GridBagConstraints());
        return backdrop;
    }

    // EFFECTS: build the white login card with title, credential fields, button, and status line
    private JPanel buildCard() {
        CardPanel card = new CardPanel(new GridBagLayout(), 30);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        addTitleRows(card, gbc);
        addCredentialFields(card, gbc);
        addButtonAndStatus(card, gbc);
        return card;
    }

    // MODIFIES: card
    // EFFECTS: add the heading and subtitle rows to the login card
    private void addTitleRows(JPanel card, GridBagConstraints gbc) {
        JLabel title = new JLabel("Cosmetic Warehouse", SwingConstants.CENTER);
        title.setFont(Theme.font(Font.BOLD, 22));
        title.setForeground(Theme.PRIMARY_DARK);
        card.add(title, gbc);

        JLabel subtitle = new JLabel("Sign in to manage your inventory", SwingConstants.CENTER);
        subtitle.setFont(Theme.font(Font.PLAIN, 13));
        subtitle.setForeground(Theme.TEXT_MUTED);
        gbc.insets = new Insets(4, 0, 0, 0);
        card.add(subtitle, gbc);
    }

    // MODIFIES: card
    // EFFECTS: add the labelled username and password fields to the login card
    private void addCredentialFields(JPanel card, GridBagConstraints gbc) {
        gbc.insets = new Insets(22, 0, 4, 0);
        card.add(Theme.fieldCaption("Username"), gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        userText = Theme.textField(18);
        card.add(userText, gbc);

        gbc.insets = new Insets(14, 0, 4, 0);
        card.add(Theme.fieldCaption("Password"), gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        passwordText = Theme.passwordField(18);
        card.add(passwordText, gbc);
    }

    // MODIFIES: card
    // EFFECTS: add the login button, status line, and demo-account hint to the login card
    private void addButtonAndStatus(JPanel card, GridBagConstraints gbc) {
        loginButton = RoundedButton.primary("Login");
        loginButton.addActionListener(this);
        gbc.insets = new Insets(24, 0, 0, 0);
        card.add(loginButton, gbc);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(Theme.font(Font.PLAIN, 12));
        gbc.insets = new Insets(12, 0, 0, 0);
        card.add(statusLabel, gbc);

        JLabel hint = new JLabel("Demo account: Crystal / 666666", SwingConstants.CENTER);
        hint.setFont(Theme.font(Font.PLAIN, 11));
        hint.setForeground(Theme.TEXT_MUTED);
        gbc.insets = new Insets(16, 0, 0, 0);
        card.add(hint, gbc);
    }
}
