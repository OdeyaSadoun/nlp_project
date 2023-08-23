import java.sql.*;
import java.util.*;

public class GetType {

    final static String JDBC_URL = "jdbc:sqlserver://LOCALHOST\\SQLEXPRESS:1433;databaseName=logistcourse1;SelectMethod=Cursor";
    final static String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    final static String USERNAME = "logistcourse1";
    final static String PASSWORD = "logistcourse1";
    final static String DEFAULT_TYPE = "D8.2";

    public static String getLabel(String word, String sentence, Boolean pluralWord) {

        if(word == null){
            return DEFAULT_TYPE;
        }
        //check if the word exist in the type table:
        if(isWordExistInVARTYPETable(word) != null){
            return isWordExistInVARTYPETable(word);
        }
        if(pluralWord){
            return "Bool"; // boolean type value
        }
        if(getNumericWord(sentence) != null){
            return "Long"; // numeric type value
        }

        return DEFAULT_TYPE;
    }

    public static String getNumericWord(String sentence) {
        // Split the sentence into words.
        String[] words = sentence.split(" ");

        // Iterate over the words.
        for (String word : words) {
            // Check if the word is a number.
            if (word.matches("\\d+")) {
                // Return the word as a number.
                return word;
            }
            // Check if the word is an operator.
            if (word.equals("+") || word.equals("-") || word.equals("*") || word.equals("/") || word.equals("<") || word.equals(">") || word.equals("=") || word.equals("גדול מ") || word.equals("ועוד") || word.equals("פלוס") || word.equals("מינוס")) {
                // Return the word as an operator.
                return word;
            }
        }

        // Return null if the sentence does not contain a number or an operator.
        return null;
    }


    public static void insertToDatabase(String hebrewWord, String type){

        Connection conn = null;
        Statement stmt = null;

        try{
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String insertQuery = "INSERT INTO VARTYPE (HEBREW_WORD, VAR_TYPE) VALUES (?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);

            preparedStatement.setString(1, hebrewWord);
            preparedStatement.setString(2, type);

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
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createVARTYPETableIfNotExists() {

        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            stmt = conn.createStatement();

            // SQL query to check if the table exists
            String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'VARTYPE'";
            ResultSet resultSet = stmt.executeQuery(checkTableQuery);
            resultSet.next();
            int tableCount = resultSet.getInt(1);

            if (tableCount == 0) {
                // Create the table if it doesn't exist
                String createTableQuery = "CREATE TABLE VARTYPE (HEBREW_WORD VARCHAR(255), VAR_TYPE VARCHAR(255))";
                stmt.executeUpdate(createTableQuery);

                //Fill the VARTYPR table in DataBase:
                Map<String, String> knownWords = new HashMap<>();
                knownWords.put("גיל", "Long");
                knownWords.put("מספר", "Long");
                knownWords.put("כמות", "Long");
                knownWords.put("סיבוב", "Long");
                knownWords.put("מונה", "Long");
                knownWords.put("יחס", "Double");
                knownWords.put("מחיר", "Double");
                knownWords.put("עלות", "Double");
                knownWords.put("עולה", "Double");
                knownWords.put("ערך", "Double");
                knownWords.put("סכום", "Double");
                knownWords.put("משכורת", "Double");
                knownWords.put("תאריך", "DateTime");
                knownWords.put("תאריך_לידה", "DateTime");
                knownWords.put("שם", "Char");
                knownWords.put("מין", "Char");
                knownWords.put("עיר", "Char");
                knownWords.put("מדינה", "Char");
                knownWords.put("טלפון", "Char");
                knownWords.put("דואר_אלקטרוני", "Char");
                knownWords.put("אימייל", "Char");
                knownWords.put("מייל", "Char");
                knownWords.put("כתובת", "Char");
                knownWords.put("URL", "Char");
                knownWords.put("צבע", "Char");
                knownWords.put("גודל", "Double");
                knownWords.put("משקל", "Double");
                knownWords.put("טמפרטורה", "Double");
                knownWords.put("זמן", "DateTime");
                knownWords.put("מספר_טלפון", "Char");
                knownWords.put("שם_משפחה", "Char");
                knownWords.put("שם_פרטי", "Char");
                knownWords.put("שם_בדוי", "Char");
                knownWords.put("שם_חברה", "Char");
                knownWords.put("שם_מוצר", "Char");
                knownWords.put("שם_שירות", "Char");
                knownWords.put("שם_מקום", "Char");
                knownWords.put("מקום", "Char");
                knownWords.put("שם_חיה", "Char");
                knownWords.put("שם_צמח", "Char");
                knownWords.put("שם_עצם", "Char");
                knownWords.put("שם_חומר", "Char");
                knownWords.put("כביש", "Char");
                knownWords.put("נהר", "Char");
                knownWords.put("הר", "Char");
                knownWords.put("אגם", "Char");
                knownWords.put("ים", "Char");
                knownWords.put("אוקיינוס", "Char");
                knownWords.put("יבשת", "Char");
                knownWords.put("כוכב", "Char");
                knownWords.put("סטטוס", "Char");
                knownWords.put("קריטי", "Bool");
                knownWords.put("מספר_קטלוגי", "Long");



                for (Map.Entry<String, String> entry : knownWords.entrySet()) {
                    String hebrewWord = entry.getKey();
                    String type = entry.getValue();
                    insertToDatabase(hebrewWord, type);
                }
            }
            stmt.close();
            conn.close();


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

    private static String isWordExistInVARTYPETable(String word) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            String selectQuery = "SELECT VAR_TYPE FROM VARTYPE WHERE HEBREW_WORD = ?";
            preparedStatement = conn.prepareStatement(selectQuery);
            preparedStatement.setString(1, word);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Word exists in the table
                String type = resultSet.getString("VAR_TYPE");
                System.out.println("VAR_TYPE: " + type);
                return type;
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

    public static void main(String[] args) {
        createVARTYPETableIfNotExists();
//        insertToDatabase("סטטוס", "Char");
//        insertToDatabase("קריטי", "Bool");
//        insertToDatabase("מספר_קטלוגי", "Long");
        //System.out.println(getLabel("בלה","בלהבלהבלה", false));
    }
}
