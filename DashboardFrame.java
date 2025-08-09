package BankManagementSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardFrame extends JFrame {
    private final Account currentAccount;
    private final BankService bankService;
    private JLabel balanceLabel;
    private JLabel accountInfoLabel;
    private DefaultTableModel transactionTableModel;

    public DashboardFrame(Account account, BankService bankService) {
        this.currentAccount = account;
        this.bankService = bankService;
        initializeUI();
        refreshAccountData();
    }

    private void initializeUI() {
        setTitle("Bank Dashboard - " + currentAccount.getFullName());
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 248, 255));

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = createHeaderPanel();

        // Content panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // Account Overview tab
        JPanel overviewPanel = createOverviewPanel();
        tabbedPane.addTab("Account Overview", overviewPanel);

        // Transactions tab
        JPanel transactionsPanel = createTransactionsPanel();
        tabbedPane.addTab("Transactions", transactionsPanel);

        // Transfer tab
        JPanel transferPanel = createTransferPanel();
        tabbedPane.addTab("Transfer Money", transferPanel);

        // Account Settings tab
        JPanel settingsPanel = createSettingsPanel();
        tabbedPane.addTab("Settings", settingsPanel);

        // Footer panel
        JPanel footerPanel = createFooterPanel();

        // Add components
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 25, 112));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + currentAccount.getFullName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);

        accountInfoLabel = new JLabel();
        accountInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        accountInfoLabel.setForeground(Color.WHITE);

        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 32));
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(accountInfoLabel, BorderLayout.CENTER);
        headerPanel.add(balanceLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Quick Actions
        JPanel actionsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Quick Actions"));
        actionsPanel.setBackground(Color.WHITE);

        JButton depositButton = createActionButton("💰 Deposit Money", new Color(34, 139, 34));
        JButton withdrawButton = createActionButton("💸 Withdraw Money", new Color(220, 20, 60));
        JButton transferButton = createActionButton("🔄 Transfer Money", new Color(70, 130, 180));
        JButton interestButton = createActionButton("📈 Calculate Interest", new Color(255, 140, 0));

        depositButton.addActionListener(e -> showDepositDialog());
        withdrawButton.addActionListener(e -> showWithdrawDialog());
        transferButton.addActionListener(e -> showTransferDialog());
        interestButton.addActionListener(e -> calculateInterest());

        actionsPanel.add(depositButton);
        actionsPanel.add(withdrawButton);
        actionsPanel.add(transferButton);
        actionsPanel.add(interestButton);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(actionsPanel, gbc);

        return panel;
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Transaction table
        String[] columnNames = {"Date", "Type", "Amount", "Balance", "Description"};
        transactionTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable transactionTable = new JTable(transactionTableModel);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 12));
        transactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        transactionTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshButton = Main.createButton("Refresh", new Color(70, 130, 180));
        refreshButton.addActionListener(e -> refreshTransactions());
        buttonPanel.add(refreshButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTransferPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel titleLabel = Main.createLabel("Transfer Money", 20);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("To Account Number:"), gbc);
        gbc.gridx = 1;
        JTextField toAccountField = Main.createTextField(15);
        panel.add(toAccountField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        JTextField amountField = Main.createTextField(15);
        panel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton transferButton = Main.createButton("Transfer", new Color(70, 130, 180));
        transferButton.addActionListener(e -> {
            performTransfer(toAccountField.getText(), amountField.getText());
            toAccountField.setText("");
            amountField.setText("");
        });
        panel.add(transferButton, gbc);

        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel titleLabel = Main.createLabel("Account Settings", 20);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Account Information Display
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Account Number:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(currentAccount.getAccountNumber()), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(currentAccount.getEmail()), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(currentAccount.getPhoneNumber()), gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Account Type:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(currentAccount.getAccountType().toString()), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Interest Rate:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(String.format("%.2f%%", currentAccount.getInterestRate() * 100)), gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Created Date:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(currentAccount.getCreatedDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))), gbc);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout());
        footerPanel.setBackground(new Color(240, 248, 255));

        JButton logoutButton = Main.createButton("Logout", new Color(220, 20, 60));
        logoutButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?", "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                new LoginFrame(bankService);
                dispose();
            }
        });

        footerPanel.add(logoutButton);
        return footerPanel;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void refreshAccountData() {
        Account updatedAccount = bankService.getAccount(currentAccount.getAccountNumber());
        if (updatedAccount != null) {
            currentAccount.setBalance(updatedAccount.getBalance());
            currentAccount.setTransactions(updatedAccount.getTransactions());
        }

        balanceLabel.setText(String.format("$%.2f", currentAccount.getBalance()));
        accountInfoLabel.setText(String.format("Account: %s | Type: %s | Status: %s",
                currentAccount.getAccountNumber(),
                currentAccount.getAccountType(),
                currentAccount.getStatus()));

        refreshTransactions();
    }

    private void refreshTransactions() {
        transactionTableModel.setRowCount(0);
        List<Transaction> transactions = currentAccount.getTransactions();

        for (int i = transactions.size() - 1; i >= 0; i--) {
            Transaction transaction = transactions.get(i);
            Object[] row = {
                    transaction.getTimestamp().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")),
                    transaction.getType(),
                    String.format("$%.2f", transaction.getAmount()),
                    String.format("$%.2f", transaction.getBalanceAfter()),
                    transaction.getDescription()
            };
            transactionTableModel.addRow(row);
        }
    }

    private void showDepositDialog() {
        String amountStr = JOptionPane.showInputDialog(this, "Enter deposit amount:", "Deposit Money", JOptionPane.PLAIN_MESSAGE);
        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr.trim());
                bankService.deposit(currentAccount.getAccountNumber(), amount);
                refreshAccountData();
                JOptionPane.showMessageDialog(this, "Deposit successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showWithdrawDialog() {
        String amountStr = JOptionPane.showInputDialog(this, "Enter withdrawal amount:", "Withdraw Money", JOptionPane.PLAIN_MESSAGE);
        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr.trim());
                bankService.withdraw(currentAccount.getAccountNumber(), amount);
                refreshAccountData();
                JOptionPane.showMessageDialog(this, "Withdrawal successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showTransferDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField toAccountField = new JTextField();
        JTextField amountField = new JTextField();

        panel.add(new JLabel("To Account Number:"));
        panel.add(toAccountField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Transfer Money", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            performTransfer(toAccountField.getText(), amountField.getText());
        }
    }

    private void performTransfer(String toAccount, String amountStr) {
        if (toAccount == null || toAccount.trim().isEmpty() || amountStr == null || amountStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr.trim());
            bankService.transfer(currentAccount.getAccountNumber(), toAccount.trim(), amount);
            refreshAccountData();
            JOptionPane.showMessageDialog(this, "Transfer successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calculateInterest() {
        int result = JOptionPane.showConfirmDialog(this,
                "Calculate monthly interest for all accounts?",
                "Calculate Interest", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            bankService.calculateInterest();
            refreshAccountData();
            JOptionPane.showMessageDialog(this, "Interest calculated and applied!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}