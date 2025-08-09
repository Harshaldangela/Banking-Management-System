package BankManagementSystem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BankService {
    private final DatabaseManager dbManager;

    public BankService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public Account createAccount(String firstName, String lastName, String email, String phone,
                                 Account.AccountType accountType, double initialDeposit, String password) {
        // Validation
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (!SecurityUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (!SecurityUtil.isValidPhone(phone)) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        if (!SecurityUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 8 characters with uppercase, lowercase, and numbers");
        }
        if (initialDeposit < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative");
        }

        // Check if email already exists
        List<Account> accounts = dbManager.loadAccounts();
        boolean emailExists = accounts.stream()
                .anyMatch(acc -> acc.getEmail().equalsIgnoreCase(email));
        if (emailExists) {
            throw new IllegalArgumentException("Email already registered");
        }

        String accountNumber = SecurityUtil.generateAccountNumber();
        String passwordHash = SecurityUtil.hashPassword(password);

        Account account = new Account(accountNumber, firstName, lastName, email, phone,
                accountType, initialDeposit, passwordHash);

        if (initialDeposit > 0) {
            Transaction initialTransaction = new Transaction(
                    Transaction.TransactionType.DEPOSIT,
                    initialDeposit,
                    initialDeposit,
                    "Initial deposit"
            );
            account.addTransaction(initialTransaction);
        }

        accounts.add(account);
        dbManager.saveAccounts(accounts);

        return account;
    }

    public Account authenticate(String accountNumber, String password) {
        List<Account> accounts = dbManager.loadAccounts();
        Optional<Account> accountOpt = accounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(accountNumber))
                .findFirst();

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getStatus() == Account.AccountStatus.ACTIVE &&
                    SecurityUtil.verifyPassword(password, account.getPasswordHash())) {
                return account;
            }
        }
        return null;
    }

    public boolean deposit(String accountNumber, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        List<Account> accounts = dbManager.loadAccounts();
        Optional<Account> accountOpt = accounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(accountNumber))
                .findFirst();

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getStatus() != Account.AccountStatus.ACTIVE) {
                throw new IllegalStateException("Account is not active");
            }

            account.setBalance(account.getBalance() + amount);

            Transaction transaction = new Transaction(
                    Transaction.TransactionType.DEPOSIT,
                    amount,
                    account.getBalance(),
                    "Cash deposit"
            );
            account.addTransaction(transaction);

            dbManager.saveAccounts(accounts);
            return true;
        }
        return false;
    }

    public boolean withdraw(String accountNumber, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        List<Account> accounts = dbManager.loadAccounts();
        Optional<Account> accountOpt = accounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(accountNumber))
                .findFirst();

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getStatus() != Account.AccountStatus.ACTIVE) {
                throw new IllegalStateException("Account is not active");
            }

            if (account.getBalance() < amount) {
                throw new IllegalArgumentException("Insufficient funds");
            }

            account.setBalance(account.getBalance() - amount);

            Transaction transaction = new Transaction(
                    Transaction.TransactionType.WITHDRAWAL,
                    amount,
                    account.getBalance(),
                    "Cash withdrawal"
            );
            account.addTransaction(transaction);

            dbManager.saveAccounts(accounts);
            return true;
        }
        return false;
    }

    public boolean transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        List<Account> accounts = dbManager.loadAccounts();

        Optional<Account> fromAccountOpt = accounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(fromAccountNumber))
                .findFirst();

        Optional<Account> toAccountOpt = accounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(toAccountNumber))
                .findFirst();

        if (fromAccountOpt.isPresent() && toAccountOpt.isPresent()) {
            Account fromAccount = fromAccountOpt.get();
            Account toAccount = toAccountOpt.get();

            if (fromAccount.getStatus() != Account.AccountStatus.ACTIVE ||
                    toAccount.getStatus() != Account.AccountStatus.ACTIVE) {
                throw new IllegalStateException("One or both accounts are not active");
            }

            if (fromAccount.getBalance() < amount) {
                throw new IllegalArgumentException("Insufficient funds");
            }

            // Update balances
            fromAccount.setBalance(fromAccount.getBalance() - amount);
            toAccount.setBalance(toAccount.getBalance() + amount);

            // Add transactions
            Transaction fromTransaction = new Transaction(
                    Transaction.TransactionType.TRANSFER_OUT,
                    amount,
                    fromAccount.getBalance(),
                    "Transfer to " + toAccount.getFullName()
            );
            fromTransaction.setToAccount(toAccountNumber);
            fromAccount.addTransaction(fromTransaction);

            Transaction toTransaction = new Transaction(
                    Transaction.TransactionType.TRANSFER_IN,
                    amount,
                    toAccount.getBalance(),
                    "Transfer from " + fromAccount.getFullName()
            );
            toTransaction.setFromAccount(fromAccountNumber);
            toAccount.addTransaction(toTransaction);

            dbManager.saveAccounts(accounts);
            return true;
        }
        return false;
    }

    public Account getAccount(String accountNumber) {
        List<Account> accounts = dbManager.loadAccounts();
        return accounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);
    }

    public void calculateInterest() {
        List<Account> accounts = dbManager.loadAccounts();
        boolean updated = false;

        for (Account account : accounts) {
            if (account.getStatus() == Account.AccountStatus.ACTIVE && account.getBalance() > 0) {
                double interest = account.getBalance() * account.getInterestRate() / 12; // Monthly interest
                account.setBalance(account.getBalance() + interest);

                Transaction interestTransaction = new Transaction(
                        Transaction.TransactionType.INTEREST,
                        interest,
                        account.getBalance(),
                        "Monthly interest credit"
                );
                account.addTransaction(interestTransaction);
                updated = true;
            }
        }

        if (updated) {
            dbManager.saveAccounts(accounts);
        }
    }
}