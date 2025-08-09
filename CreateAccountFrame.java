package BankManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CreateAccountFrame extends JFrame {
    private final BankService bankService;
    private JTextField firstNameField, lastNameField, emailField, phoneField, initialDepositField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<Account.AccountType> accountTypeCombo;

    public CreateAccountFrame(BankService bankService) {
        this.bankService = bankService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Create New Account");
        // don't call setSize; use pack()
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 248, 255));

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = Main.createLabel("Create New Account", 20);

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
        gbc.anchor = GridBagConstraints.WEST;

        // First Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = Main.createTextField(15);
        firstNameField.setToolTipText("Enter your first name");
        formPanel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        lastNameField = Main.createTextField(15);
        lastNameField.setToolTipText("Enter your last name");
        formPanel.add(lastNameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = Main.createTextField(15);
        emailField.setToolTipText("Enter your email address");
        formPanel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        phoneField = Main.createTextField(15);
        phoneField.setToolTipText("Enter your phone number (digits only)");
        formPanel.add(phoneField, gbc);

        // Account Type
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Account Type:"), gbc);
        gbc.gridx = 1;
        accountTypeCombo = new JComboBox<>(Account.AccountType.values());
        accountTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(accountTypeCombo, gbc);

        // Initial Deposit
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Initial Deposit:"), gbc);
        gbc.gridx = 1;
        initialDepositField = Main.createTextField(15);
        initialDepositField.setText("0.00");
        formPanel.add(initialDepositField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = Main.createPasswordField(15);
        formPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = Main.createPasswordField(15);
        formPanel.add(confirmPasswordField, gbc);

        // Password requirements
        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 2;
        JLabel passwordInfo = new JLabel("<html><small>Password must be at least 8 characters with uppercase, lowercase, and numbers</small></html>");
        passwordInfo.setForeground(Color.GRAY);
        formPanel.add(passwordInfo, gbc);

        // Account type info
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 2;
        JLabel accountInfo = new JLabel("<html><small><b>SAVINGS:</b> 3% interest | <b>CHECKING:</b> 1% interest | <b>PREMIUM:</b> 5% interest</small></html>");
        accountInfo.setForeground(new Color(70, 130, 180));
        formPanel.add(accountInfo, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton createButton = Main.createButton("CREATE ACCOUNT", new Color(34, 139, 34));
        JButton backButton = Main.createButton("BACK TO LOGIN", new Color(220, 20, 60));

        createButton.addActionListener(this::handleCreateAccount);
        backButton.addActionListener(this::handleBack);

        buttonPanel.add(createButton);
        buttonPanel.add(backButton);

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

        // request focus on first name so it is ready to type
        SwingUtilities.invokeLater(() -> {
            if (firstNameField != null) firstNameField.requestFocusInWindow();
        });

        // Enter key binding
        getRootPane().setDefaultButton(createButton);
    }

    private void handleCreateAccount(ActionEvent e) {
        try {
            // Get field values
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            Account.AccountType accountType = (Account.AccountType) accountTypeCombo.getSelectedItem();

            // Validate required fields
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                    phone.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate password confirmation
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match",
                        "Password Error", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
                confirmPasswordField.setText("");
                return;
            }

            // Validate initial deposit
            double initialDeposit = 0.0;
            try {
                String depositText = initialDepositField.getText().trim();
                if (!depositText.isEmpty()) {
                    initialDeposit = Double.parseDouble(depositText);
                    if (initialDeposit < 0) {
                        JOptionPane.showMessageDialog(this, "Initial deposit cannot be negative",
                                "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid initial deposit amount",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create account
            Account newAccount = bankService.createAccount(firstName, lastName, email, phone,
                    accountType, initialDeposit, password);

            JOptionPane.showMessageDialog(this,
                    "Account created successfully!\n\nAccount Number: " + newAccount.getAccountNumber() +
                            "\nAccount Type: " + accountType +
                            "\nInitial Balance: $" + String.format("%.2f", initialDeposit) +
                            "\n\nPlease save your account number for future logins.",
                    "Account Created", JOptionPane.INFORMATION_MESSAGE);

            // Go to dashboard
            new DashboardFrame(newAccount, bankService);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creating account: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleBack(ActionEvent e) {
        new LoginFrame(bankService);
        dispose();
    }
}
