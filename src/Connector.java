import java.sql.Connection;
import java.sql.DriverManager;

public class Connector {
    private static Connection connection;

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/systemvote", "root", "");
            //System.out.println("Connection successful!");
        } catch (Exception e) {
            System.out.println(e);
        }
        return connection;
    }

    public static void main(String[] args) {
        Connection conn = getConnection();
    }
}
