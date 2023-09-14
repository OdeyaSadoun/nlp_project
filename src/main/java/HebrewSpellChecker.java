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
        List<String> words = getWORDColumnValues("KTCLASS");
        return isDuplicate(newWord, words, levenshteinThreshold);
    }
    public static String sameWordFromKTCLASSTable(String newWord, int levenshteinThreshold) {
        List<String> words = getWORDColumnValues("KTCLASS");
        return isDuplicateReturnWord(newWord, words, levenshteinThreshold);
    }


    public static String getHebrewNameFromEnglish(String classCodeName) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "SELECT NAME FROM KTCLASS WHERE CLASS_CODE_NAME = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, classCodeName);

            ResultSet rs = preparedStatement.executeQuery();

            String hebrewName = null;
            if (rs.next()) {
                hebrewName = rs.getString("NAME");
            }

            rs.close();
            preparedStatement.close();
            connection.close();

            return hebrewName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null in case of an error
    }


    public static boolean findSameWordFromKTATTRIBUTETable(String hebrewSubject, String hebrewField, int levenshteinThreshold) {
        List<String> classCodeNames = getClassCodeNames(hebrewSubject);

        if (classCodeNames.isEmpty()) {
            if (!findSameWordFromKTCLASSTable(hebrewSubject, levenshteinThreshold)) {
                return false; // No matching class code name found
            }
            else{
                String newHebrewSubjectFromDB = sameWordFromKTCLASSTable(hebrewSubject, levenshteinThreshold);
                classCodeNames = getClassCodeNames(newHebrewSubjectFromDB);
            }
        }

        List<String> words = getWORDColumnValues("KTATTRIBUTE");




        for (String classCodeName : classCodeNames) {
//            String hebrewSubjectFromEnglish = getHebrewNameFromEnglish(classCodeName);
            if (isDuplicateWithClassCode(hebrewField, classCodeName, words, levenshteinThreshold)) {
                return true;
            }
        }

        return false;
    }

    public static List<String> getClassCodeNames(String hebrewSubject) {
        List<String> classCodeNames = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            String getClassCodeNameQuery = "SELECT CLASS_CODE_NAME FROM KTCLASS WHERE NAME = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(getClassCodeNameQuery);
            preparedStatement.setString(1, hebrewSubject);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String classCodeName = resultSet.getString("class_code_name");
                classCodeNames.add(classCodeName);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classCodeNames;
    }

    public static boolean isDuplicateWithClassCode(String hebrewField, String classCodeName, List<String> database, int levenshteinThreshold) {
        for (String existingWord : database) {
            int distance = levenshteinDistance(hebrewField, existingWord);
            if (distance <= levenshteinThreshold) {
//                if (isMatchInKTATTRIBUTE(hebrewField, classCodeName)) {
//                    return true;
//                }
                return true;
            }
        }
        return false;
    }

    public static boolean isMatchInKTATTRIBUTE(String hebrewField, String classCodeName) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            String query = "SELECT * FROM KTATTRIBUTE WHERE NAME = ? AND CLASS_CODE_NAME = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, hebrewField);
            preparedStatement.setString(2, classCodeName);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean isMatch = resultSet.next();

            resultSet.close();
            preparedStatement.close();
            connection.close();

            return isMatch;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static List<String> getWORDColumnValues(String tableName) {
        List<String> columnValues = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "";

            if(tableName == "KTCLASS"){
                sql = "SELECT NAME FROM KTCLASS";
            }
            else if(tableName == "KTATTRIBUTE"){
                sql = "SELECT NAME,CLASS_CODE_NAME FROM KTATTRIBUTE";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

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
    public static String isDuplicateReturnWord(String newWord, List<String> database, int levenshteinThreshold) {
        for (String existingWord : database) {
            int distance = levenshteinDistance(newWord, existingWord);

            if (distance <= levenshteinThreshold) {
                return existingWord;
            }
        }
        return null;
    }
    public static boolean isSameWordInDBInKTCLASSTable(String newWord, int levenshteinThreshold){
        return findSameWordFromKTCLASSTable(newWord,levenshteinThreshold);
    }

    public static boolean isSameWordInDBInKTATTRIBUTETable(String hebrewSubject, String hebrewField, int levenshteinThreshold){
        return findSameWordFromKTATTRIBUTETable(hebrewSubject, hebrewField, levenshteinThreshold);
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
