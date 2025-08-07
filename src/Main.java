import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Blood Bank Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);

            // Card layout to switch between panels
            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            // Panels
            LoginPanel loginPanel = new LoginPanel(mainPanel, cardLayout);
            DashboardPanel dashboardPanel = new DashboardPanel(mainPanel, cardLayout);

            mainPanel.add(loginPanel, "login");
            mainPanel.add(dashboardPanel, "dashboard");

            frame.setContentPane(mainPanel);
            frame.setVisible(true);
        });
    }
}
