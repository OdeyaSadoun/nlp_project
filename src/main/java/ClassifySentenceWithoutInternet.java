import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class ClassifySentenceWithoutInternet {

    private Map<String, String> tlxTable;

    public ClassifySentenceWithoutInternet() {
        this.tlxTable = new HashMap<>();
        // Load TLX table from the database
        loadTLXTableFromDatabase();
    }

    public ClassifySentenceWithoutInternet(Map<String, String> reservedWords) {
        this.tlxTable = reservedWords;
    }

    private void loadTLXTableFromDatabase() {
        // Update the connection parameters accordingly
        String jdbcURL = "jdbc:mysql://localhost:3306/your_database_name";
        String username = "your_username";
        String password = "your_password";

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            String sql = "SELECT reserved_word, value FROM tlx_table"; // Change this query to fetch data from your TLX table
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String reservedWord = resultSet.getString("reserved_word");
                String value = resultSet.getString("value");
                tlxTable.put(reservedWord, value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any database connection errors here
        }
    }

    public String classifySentence(String sentence) {
        // Rest of your classification logic remains the same
        // ... (same as in the previous code)

        return "unknown";
    }

    public static void main(String[] args) {
        ClassifySentenceWithoutInternet classifier = new ClassifySentenceWithoutInternet();
        System.out.println(classifier.classifySentence("The age of the user is 21."));
        // Output: age of the user of 21
        System.out.println(classifier.classifySentence("The user is 21 years old."));
        // Output: user of 21 years old
        System.out.println(classifier.classifySentence("The user is 21 years old."));
        // Output: user of 21 years old
    }
}
