import java.sql.*;
import java.util.*;

public class GetType {

    final static String JDBC_URL = "jdbc:sqlserver://LOCALHOST\\SQLEXPRESS:1433;databaseName=logistcourse1;SelectMethod=Cursor";
    final static String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    final static String USERNAME = "logistcourse1";
    final static String PASSWORD = "logistcourse1";
    final static String DEFAULT_TYPE = "Double";

    public static String getLabel(String word, String sentence, Boolean pluralWord, Connection conn, Statement stmt, ResultSet rs) {

        if(word == null){
            return DEFAULT_TYPE;
        }
        //check if the word exist in the type table:
        if(isWordExistInVARTYPETable(word, conn, stmt, rs) != null){
            return isWordExistInVARTYPETable(word, conn, stmt, rs);
        }
        if(pluralWord){
            return "Bool"; // boolean type value
        }
        if(getNumericWord(sentence)){
            return "Long"; // numeric type value
        }

        return DEFAULT_TYPE;
    }

    public static boolean getNumericWord(String sentence) {
        // Split the sentence based on non-numeric characters
        String[] parts = sentence.split("[^0-9]+");

        // Iterate over the parts
        for (String part : parts) {
            if (!part.isEmpty()) {
                // Return true if a non-empty part is found
                return true;
            }
        }

        // Return false if the sentence does not contain a number
        return false;
    }

    public static void insertToDatabase(String hebrewWord, String type, Connection conn, Statement stmt){



        try{
            String insertQuery = "INSERT INTO VARTYPE (HEBREW_WORD, VAR_TYPE) VALUES (?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);

            preparedStatement.setString(1, hebrewWord);
            preparedStatement.setString(2, type);

            preparedStatement.executeUpdate();
            preparedStatement.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



    public static void deleteVARTYPETable(Connection conn, Statement stmt , ResultSet rs) throws SQLException {
        try {
            // SQL query to check if the table exists
            String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'VARTYPE'";
            rs = stmt.executeQuery(checkTableQuery);
            rs.next();
            int tableCount = rs.getInt(1);

            if (tableCount != 0) {
                // delete the table if it exist
                String deleteTableQuery = "DROP TABLE VARTYPE";
                stmt.execute(deleteTableQuery);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createVARTYPETableIfNotExists(Connection conn, Statement stmt, ResultSet rs) {


        try {

            // SQL query to check if the table exists
            String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'VARTYPE'";
            rs = stmt.executeQuery(checkTableQuery);
            rs.next();
            int tableCount = rs.getInt(1);

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
                knownWords.put("מאושרת", "Bool");
                knownWords.put("מספר_קטלוגי", "Long");



                for (Map.Entry<String, String> entry : knownWords.entrySet()) {
                    String hebrewWord = entry.getKey();
                    String type = entry.getValue();
                    insertToDatabase(hebrewWord, type, conn, stmt);
                }
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }
//        finally {
//            try {
//                if (stmt != null) stmt.close();
//                if (conn != null) conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private static String isWordExistInVARTYPETable(String word, Connection conn, Statement stmt, ResultSet rs) {

        PreparedStatement preparedStatement = null;

        try {

            String selectQuery = "SELECT VAR_TYPE FROM VARTYPE WHERE HEBREW_WORD = ?";
            preparedStatement = conn.prepareStatement(selectQuery);
            preparedStatement.setString(1, word);

            rs = preparedStatement.executeQuery();

            if (rs.next()) {
                // Word exists in the table
                String type = rs.getString("VAR_TYPE");
                System.out.println("VAR_TYPE: " + type);
                return type;
            }

            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                //if (rs != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                //if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
       // createVARTYPETableIfNotExists();
    }
}
