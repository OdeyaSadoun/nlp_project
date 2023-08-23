import org.apache.commons.text.similarity.LevenshteinDistance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HebrewSpellChecker {
    final static String JDBC_URL = "jdbc:sqlserver://LOCALHOST\\SQLEXPRESS:1433;databaseName=logistcourse1;SelectMethod=Cursor";
    final static String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    final static String USERNAME = "logistcourse1";
    final static String PASSWORD = "logistcourse1";
    public static int levenshteinDistance(String word1, String word2) {
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];

        for (int i = 0; i <= word1.length(); i++) {
            for (int j = 0; j <= word2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = (word1.charAt(i - 1) == word2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + cost,
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                }
            }
        }

        return dp[word1.length()][word2.length()];
    }

    public static boolean findSameWordFromKTCLASSTable(String newWord, int levenshteinThreshold) {
        List<String> words = getColumnValues("KTCLASS", "WORD");
        return isDuplicate(newWord, words, levenshteinThreshold);
    }

    public static boolean findSameWordFromKTATTRIBUTETable(String newWord, int levenshteinThreshold) {
        List<String> words = getColumnValues("KTATTRIBUTE", "WORD");
        return isDuplicate(newWord, words, levenshteinThreshold);
    }

    public static List<String> getColumnValues(String tableName, String columnName) {
        List<String> columnValues = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            String sql = "SELECT ? FROM ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, columnName);
            preparedStatement.setString(2, tableName);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String value = resultSet.getString(1); // Column index starts from 1
                columnValues.add(value);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return columnValues;
    }

    public static boolean isDuplicate(String newWord, List<String> database, int levenshteinThreshold) {
        for (String existingWord : database) {
            int distance = levenshteinDistance(newWord, existingWord);
            if (distance <= levenshteinThreshold) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSameWordInDB(String newWord, int levenshteinThreshold){
        return findSameWordFromKTCLASSTable(newWord,levenshteinThreshold) || findSameWordFromKTATTRIBUTETable(newWord,levenshteinThreshold);
    }

    public static void main(String[] args) {
        String newWord = "אמא"; // The word to be inserted
        List<String> database = new ArrayList<>(); // Existing words in the database
        database.add("אבא");
        database.add("אמה");
        database.add("שלום");

        int levenshteinThreshold = 2; // Adjust the threshold as needed

        if (isDuplicate(newWord, database, levenshteinThreshold)) {
            System.out.println("The word is a potential duplicate.");
        } else {
            // Insert the word into the database
            System.out.println("Inserting the word into the database.");
            database.add(newWord);
        }
    }
}
