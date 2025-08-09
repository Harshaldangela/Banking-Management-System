package BankManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private final BankService bankService;
    private JTextField accountNumberField;
    private JPasswordField passwordField;

    public LoginFrame(BankService bankService) {
        this.bankService = bankService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Advanced Bank Management System - Login");
        // don't call setSize here, use pack() later for consistent sizing
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 248, 255));

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = Main.createLabel("🏦 SECURE BANK SYSTEM", 24);
        titleLabel.setForeground(new Color(25, 25, 112));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setOpaque(true);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Account Number
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Account Number:"), gbc);

        gbc.gridx = 1;
        accountNumberField = Main.createTextField(15);
        formPanel.add(accountNumberField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = Main.createPasswordField(15);
        formPanel.add(passwordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton loginButton = Main.createButton("LOGIN", new Color(34, 139, 34));
        JButton createAccountButton = Main.createButton("CREATE ACCOUNT", new Color(70, 130, 180));

        loginButton.addActionListener(this::handleLogin);
        createAccountButton.addActionListener(this::handleCreateAccount);

        buttonPanel.add(loginButton);
        buttonPanel.add(createAccountButton);

        // Add components
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Layout & show
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);

        // focus account number
        SwingUtilities.invokeLater(() -> {
            if (accountNumberField != null) accountNumberField.requestFocusInWindow();
        });

        // Enter key binding
        getRootPane().setDefaultButton(loginButton);
    }

    private void handleLogin(ActionEvent e) {
        String accountNumber = accountNumberField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (accountNumber.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Account account = bankService.authenticate(accountNumber, password);
            if (account != null) {
                new DashboardFrame(account, bankService);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid account number or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCreateAccount(ActionEvent e) {
        new CreateAccountFrame(bankService);
        dispose();
    }
}
