import java.sql.Connection;
import java.sql.DriverManager;

public class DB {
    public static Connection getConnection() throws Exception {
        // Update these according to your database
        String url = "jdbc:mysql://localhost:3306/blood_bank_db"; // Replace with your DB name
        String user = "root";  // Replace with your MySQL username
        String pass = "malware@494";  // Replace with your MySQL password

        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, pass);
    }
}
