import java.sql.*;

public class SaveToDatabase {

    // method that checks if input exists in database and saves it if it doesn't
    public void saveInput(String input) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // establish connection to database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "username", "password");

            // prepare query to check if input exists in database
            String query = "SELECT COUNT(*) FROM mytable WHERE mycolumn = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, input);

            // execute query
            rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            // if input doesn't exist in database, save it
            if (count == 0) {
                String insertQuery = "INSERT INTO mytable (mycolumn) VALUES (?)";
                stmt = conn.prepareStatement(insertQuery);
                stmt.setString(1, input);
                stmt.executeUpdate();
                System.out.println("Input saved to database.");
            } else {
                System.out.println("Input already exists in database.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // close database resources
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
