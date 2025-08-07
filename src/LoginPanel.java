import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPanel(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("Admin Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 20, 10);
        add(titleLabel, gbc);

        gbc.gridwidth = 1;

        // Username
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        add(usernameField, gbc);

        // Password
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Login button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        add(loginButton, gbc);

        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        gbc.gridy++;
        add(errorLabel, gbc);

        // Action on login
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Dummy admin check (you can later link to DB)
                if (username.equals("admin") && password.equals("admin123")) {
                    cardLayout.show(mainPanel, "dashboard");
                } else {
                    errorLabel.setText("Invalid username or password.");
                }
            }
        });
    }
}
