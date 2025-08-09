package BankManagementSystem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Account {
	private String accountNumber;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private AccountType accountType;
	private double balance;
	private String passwordHash;
	private LocalDateTime createdDate;
	private AccountStatus status;
	private List<Transaction> transactions;
	private double interestRate;

	public enum AccountType {
		SAVINGS(0.03), CHECKING(0.01), PREMIUM(0.05);

		private final double defaultInterestRate;

		AccountType(double defaultInterestRate) {
			this.defaultInterestRate = defaultInterestRate;
		}

		public double getDefaultInterestRate() {
			return defaultInterestRate;
		}
	}

	public enum AccountStatus {
		ACTIVE, SUSPENDED, CLOSED
	}

	public Account() {
		this.transactions = new ArrayList<>();
		this.createdDate = LocalDateTime.now();
		this.status = AccountStatus.ACTIVE;
	}

	public Account(String accountNumber, String firstName, String lastName, String email,
				   String phoneNumber, AccountType accountType, double initialBalance, String passwordHash) {
		this();
		this.accountNumber = accountNumber;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.accountType = accountType;
		this.balance = initialBalance;
		this.passwordHash = passwordHash;
		this.interestRate = accountType.getDefaultInterestRate();
	}

	// Getters and Setters
	public String getAccountNumber() { return accountNumber; }
	public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }

	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getPhoneNumber() { return phoneNumber; }
	public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

	public AccountType getAccountType() { return accountType; }
	public void setAccountType(AccountType accountType) { this.accountType = accountType; }

	public double getBalance() { return balance; }
	public void setBalance(double balance) { this.balance = balance; }

	public String getPasswordHash() { return passwordHash; }
	public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

	public LocalDateTime getCreatedDate() { return createdDate; }
	public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

	public AccountStatus getStatus() { return status; }
	public void setStatus(AccountStatus status) { this.status = status; }

	public List<Transaction> getTransactions() { return transactions; }
	public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

	public double getInterestRate() { return interestRate; }
	public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

	public String getFullName() {
		return firstName + " " + lastName;
	}

	public void addTransaction(Transaction transaction) {
		this.transactions.add(transaction);
	}
}