package BankManagementSystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DatabaseManager {
    private static final String DATA_DIR = "BankData";
    private static final String ACCOUNTS_FILE = DATA_DIR + "/accounts.json";
    private static final String BACKUP_DIR = DATA_DIR + "/backup";

    private final Gson gson;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public DatabaseManager() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            File dataDir = new File(DATA_DIR);
            File backupDir = new File(BACKUP_DIR);

            if (!dataDir.exists()) dataDir.mkdirs();
            if (!backupDir.exists()) backupDir.mkdirs();

            File accountsFile = new File(ACCOUNTS_FILE);
            if (!accountsFile.exists()) {
                accountsFile.createNewFile();
                saveAccounts(new ArrayList<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public List<Account> loadAccounts() {
        lock.readLock().lock();
        try {
            File file = new File(ACCOUNTS_FILE);
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            try (FileReader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<Account>>(){}.getType();
                List<Account> accounts = gson.fromJson(reader, listType);
                return accounts != null ? accounts : new ArrayList<>();
            }
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void saveAccounts(List<Account> accounts) {
        lock.writeLock().lock();
        try {
            // Create backup before saving
            createBackup();

            try (FileWriter writer = new FileWriter(ACCOUNTS_FILE)) {
                gson.toJson(accounts, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save accounts", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void createBackup() {
        try {
            File sourceFile = new File(ACCOUNTS_FILE);
            if (sourceFile.exists()) {
                String backupFileName = BACKUP_DIR + "/accounts_backup_" +
                        System.currentTimeMillis() + ".json";
                File backupFile = new File(backupFileName);

                try (FileInputStream fis = new FileInputStream(sourceFile);
                     FileOutputStream fos = new FileOutputStream(backupFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                }

                // Keep only last 10 backups
                cleanupOldBackups();
            }
        } catch (IOException e) {
            System.err.println("Failed to create backup: " + e.getMessage());
        }
    }

    private void cleanupOldBackups() {
        File backupDir = new File(BACKUP_DIR);
        File[] backups = backupDir.listFiles((dir, name) -> name.startsWith("accounts_backup_"));

        if (backups != null && backups.length > 10) {
            java.util.Arrays.sort(backups, (a, b) -> Long.compare(a.lastModified(), b.lastModified()));
            for (int i = 0; i < backups.length - 10; i++) {
                backups[i].delete();
            }
        }
    }
}