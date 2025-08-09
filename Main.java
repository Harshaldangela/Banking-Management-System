package BankManagementSystem;

import javax.swing.*;
import java.awt.*;

public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}

			DatabaseManager dbManager = new DatabaseManager();
			BankService bankService = new BankService(dbManager);
			new LoginFrame(bankService);
		});
	}

	// Utility methods for consistent UI styling
	public static JFrame createFrame(String title, int width, int height) {
		JFrame frame = new JFrame(title);
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(new Color(240, 248, 255));
		return frame;
	}

	public static JLabel createLabel(String text, int fontSize) {
		JLabel label = new JLabel(text, SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.BOLD, fontSize));
		label.setForeground(new Color(25, 25, 112));
		return label;
	}

	public static JTextField createTextField(int columns) {
		JTextField textField = new JTextField(columns);
		textField.setFont(new Font("Arial", Font.PLAIN, 14));
		textField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
		));
		// Make clickable & visually consistent
		textField.setBackground(Color.WHITE);
		textField.setOpaque(true);
		textField.setFocusable(true);
		textField.setEnabled(true);

		// Simple focus effect
		textField.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusGained(java.awt.event.FocusEvent e) {
				textField.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(new Color(30, 144, 255), 2),
						BorderFactory.createEmptyBorder(5, 10, 5, 10)
				));
			}
			public void focusLost(java.awt.event.FocusEvent e) {
				textField.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
						BorderFactory.createEmptyBorder(5, 10, 5, 10)
				));
			}
		});

		return textField;
	}

	public static JPasswordField createPasswordField(int columns) {
		JPasswordField passwordField = new JPasswordField(columns);
		passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
		passwordField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
		));
		passwordField.setBackground(Color.WHITE);
		passwordField.setOpaque(true);
		passwordField.setFocusable(true);
		passwordField.setEnabled(true);

		passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusGained(java.awt.event.FocusEvent e) {
				passwordField.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(new Color(30, 144, 255), 2),
						BorderFactory.createEmptyBorder(5, 10, 5, 10)
				));
			}
			public void focusLost(java.awt.event.FocusEvent e) {
				passwordField.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
						BorderFactory.createEmptyBorder(5, 10, 5, 10)
				));
			}
		});
		return passwordField;
	}

	public static JButton createButton(String text, Color bgColor) {
		JButton button = new JButton(text);
		button.setFont(new Font("Arial", Font.BOLD, 14));
		button.setBackground(bgColor);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return button;
	}
}
