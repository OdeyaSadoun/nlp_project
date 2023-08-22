import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TranslateWithoutInternet {

    final static String JDBC_URL = "jdbc:sqlserver://LOCALHOST\\SQLEXPRESS:1433;databaseName=logistcourse1;SelectMethod=Cursor";
    final static String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    final static String USERNAME = "logistcourse1";
    final static String PASSWORD = "logistcourse1";

    public static void main(String[] args) {
        createCopingTableIfNotExists();
        System.out.println(retrieveEnglishValuesFromHebrewValues("אודיה"));
    }

    public static char[] breakWordIntoLetters(String word) {
        char[] letters = new char[word.length()];
        for (int i = 0; i < word.length(); i++) {
            letters[i] = word.charAt(i);
        }
        return letters;
    }

    public static void createCopingTableIfNotExists() {

        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            stmt = conn.createStatement();

            // SQL query to check if the table exists
            String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'Copying'";
            ResultSet resultSet = stmt.executeQuery(checkTableQuery);
            resultSet.next();
            int tableCount = resultSet.getInt(1);

            if (tableCount == 0) {
                // Create the table if it doesn't exist
                String createTableQuery = "CREATE TABLE Copying (Hebrew VARCHAR(255), English VARCHAR(255))";
                stmt.executeUpdate(createTableQuery);
            }
            stmt.close();
            conn.close();

            //Fill the Coping table in DataBase:
            Map<String, String> hebrewToEnglishMapping = new HashMap<>();
            hebrewToEnglishMapping.put("א", "a");
            hebrewToEnglishMapping.put("ב", "b");
            hebrewToEnglishMapping.put("ג", "g");
            hebrewToEnglishMapping.put("ד", "d");
            hebrewToEnglishMapping.put("ה", "h");
            hebrewToEnglishMapping.put("ו", "v");
            hebrewToEnglishMapping.put("ז", "z");
            hebrewToEnglishMapping.put("ח", "kh");
            hebrewToEnglishMapping.put("ט", "t");
            hebrewToEnglishMapping.put("י", "y");
            hebrewToEnglishMapping.put("כ", "k");
            hebrewToEnglishMapping.put("ך", "kh");
            hebrewToEnglishMapping.put("ל", "l");
            hebrewToEnglishMapping.put("מ", "m");
            hebrewToEnglishMapping.put("ם", "m");
            hebrewToEnglishMapping.put("נ", "n");
            hebrewToEnglishMapping.put("ן", "n");
            hebrewToEnglishMapping.put("ס", "s");
            hebrewToEnglishMapping.put("ע", "a");
            hebrewToEnglishMapping.put("פ", "p");
            hebrewToEnglishMapping.put("ף", "f");
            hebrewToEnglishMapping.put("צ", "ts");
            hebrewToEnglishMapping.put("ץ", "tz");
            hebrewToEnglishMapping.put("ק", "k");
            hebrewToEnglishMapping.put("ר", "r");
            hebrewToEnglishMapping.put("ש", "sh");
            hebrewToEnglishMapping.put("ת", "t");

            for (Map.Entry<String, String> entry : hebrewToEnglishMapping.entrySet()) {
                String hebrew = entry.getKey();
                String english = entry.getValue();
                insertToDatabase(hebrew, english);
            }
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static String retrieveEnglishValuesFromHebrewValues(String word) {

        // Database credentials
        Connection conn = null;
        Statement stmt = null;

        //Connect to the database
        ResultSet rs = null;

        //check if the hebrew word exist in database or in ktclass or in ktattribute:
        //if yes - we take the translate from the database
        if(isWordExistInKTCLASSTable(word) != null || isWordExistInKTATTRIBUTETable(word) != null){

        }
        //if not- we use this function:
        else{
            char[] letters=breakWordIntoLetters(word);
            StringBuilder wordBuilder = new StringBuilder();

            try {
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                stmt = conn.createStatement();

                // Prepare the query
                String selectQuery = "SELECT English FROM Copying WHERE Hebrew = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(selectQuery);

                // Loop over the letters
                for (char letter : letters) {
                    // Set the parameter value
                    preparedStatement.setString(1, String.valueOf(letter));

                    // Execute the query
                    rs = preparedStatement.executeQuery();

                    // If the query returns a row, get the English value
                    if (rs.next()) {
                        String EnglishValue = rs.getString("English");
                        wordBuilder.append(EnglishValue);
                    }
                }
                rs.close();
                stmt.close();
                conn.close();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return wordBuilder.toString();
        }
        return ""; //------------------need to fix------------------
    }

    private static String isWordExistInKTATTRIBUTETable(String word) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            String selectQuery = "SELECT ATT_CODE_NAME FROM KTATTRIBUTE WHERE NAME = ?";
            preparedStatement = conn.prepareStatement(selectQuery);
            preparedStatement.setString(1, word);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Word exists in the table
                String englishWord = resultSet.getString("ATT_CODE_NAME");
                System.out.println("English Word: " + englishWord);
                return englishWord;
            }

            return null;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static String isWordExistInKTCLASSTable(String word) {

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            String selectQuery = "SELECT CLASS_CODE_NAME FROM KTCLASS WHERE NAME = ?";
            preparedStatement = conn.prepareStatement(selectQuery);
            preparedStatement.setString(1, word);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Word exists in the table
                String englishWord = resultSet.getString("CLASS_CODE_NAME");
                System.out.println("English Word: " + englishWord);
                return englishWord;
            }

            return null;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void insertToDatabase(String hebrew, String english){
        String jdbcUrl = "jdbc:sqlserver://LOCALHOST\\SQLEXPRESS:1433;databaseName=logistcourse1;SelectMethod=Cursor";
        String jdbcDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        // Database credentials
        String username = "logistcourse1";
        String password = "logistcourse1";
        // Database credentials
        Connection conn = null;
        Statement stmt = null;
        //Connect to the database

        try{
            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            String insertQuery = "INSERT INTO Copying (Hebrew, English) VALUES (?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);

            preparedStatement.setString(1, hebrew);
            preparedStatement.setString(2, english);

            preparedStatement.executeUpdate();
            preparedStatement.close();

            conn.close();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                //if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
