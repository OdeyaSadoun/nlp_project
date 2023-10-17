import java.sql.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetType {
  static final String DEFAULT_TYPE = "Double";

  public static String getLabel(
      String word, String sentence, Boolean pluralWord, Connection conn, boolean APPROVE_PRINTING) {

    if (word == null) {
      return DEFAULT_TYPE;
    }
    String[] arrString = Tools.createNewArrayFromString(word);
    for (int j = 0; j < arrString.length; j++) {
      // check if the word exist in the type table:
      if (isWordExistInVARTYPETable(arrString[j], conn, APPROVE_PRINTING) != null) {
        return isWordExistInVARTYPETable(arrString[j], conn, APPROVE_PRINTING);
      }
    }
    if(Tools.stringContainsWord(word, "שדהתאריכי") || Tools.stringContainsWord(word, "שדהתאריך")){
      return "DateTime";
    }
    if(Tools.stringContainsWord(word, "שדהבוליאני")){
      return "Bool";
    }
    if (pluralWord) {
      return "Bool"; // boolean type value
    }
    if (getNumericWord(sentence)) {
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

  public static void insertToDatabase(String hebrewWord, String type, Connection conn) {
    try {
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

  public static void deleteVARTYPETable(Statement stmt) throws SQLException {
    try {
      // SQL query to check if the table exists
      String checkTableQuery =
          "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'VARTYPE'";
      ResultSet rs = stmt.executeQuery(checkTableQuery);
      rs.next();
      int tableCount = rs.getInt(1);

      if (tableCount != 0) {
        // delete the table if it exists
        String deleteTableQuery = "DROP TABLE VARTYPE";
        stmt.execute(deleteTableQuery);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void createVARTYPETableIfNotExists(Connection conn, Statement stmt) {
    try {
      // SQL query to check if the table exists
      String checkTableQuery =
          "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'VARTYPE'";
      ResultSet rs = stmt.executeQuery(checkTableQuery);
      rs.next();
      int tableCount = rs.getInt(1);

      if (tableCount == 0) {
        // Create the table if it doesn't exist
        String createTableQuery =
            "CREATE TABLE VARTYPE (HEBREW_WORD VARCHAR(255), VAR_TYPE VARCHAR(255))";
        stmt.executeUpdate(createTableQuery);

        // Fill the VARTYPR table in DataBase:
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
        knownWords.put("שם", "Char");
        knownWords.put("מין", "Char");
        knownWords.put("עיר", "Char");
        knownWords.put("מדינה", "Char");
        knownWords.put("טלפון", "Char");
        knownWords.put("דואר", "Char");
        knownWords.put("אימייל", "Char");
        knownWords.put("מייל", "Char");
        knownWords.put("כתובת", "Char");
        knownWords.put("URL", "Char");
        knownWords.put("צבע", "Char");
        knownWords.put("גודל", "Double");
        knownWords.put("משקל", "Double");
        knownWords.put("טמפרטורה", "Double");
        knownWords.put("זמן", "DateTime");
        knownWords.put("מקום", "Char");
        knownWords.put("כביש", "Char");
        knownWords.put("נהר", "Char");
        knownWords.put("הר", "Char");
        knownWords.put("אגם", "Char");
        knownWords.put("ים", "Char");
        knownWords.put("אוקיינוס", "Char");
        knownWords.put("יבשת", "Char");
        knownWords.put("כוכב", "Char");
        knownWords.put("סטטוס", "Char");
        knownWords.put("סטאטוס", "Char");
        knownWords.put("קריטי", "Bool");
        knownWords.put("מאושרת", "Bool");
        knownWords.put("תקינות", "Char");
        knownWords.put("תאור", "Char");
        knownWords.put("סוג", "Char");
        knownWords.put("הגדרה", "Char");
        knownWords.put("הגדרת", "Char");
        knownWords.put("הערה", "Char");
        knownWords.put("הערת", "Char");
        knownWords.put("קובע", "Bool");
        knownWords.put("לא", "Bool");
        knownWords.put("עם", "Bool");
        knownWords.put("אחוז", "Double");
        knownWords.put("סה\"כ", "Double");
        knownWords.put("סהכ", "Double");
        knownWords.put("פוסלת", "Double");
        knownWords.put("בוצע", "Bool");
        knownWords.put("קוד", "Long");
        knownWords.put("דירוג", "Long");


        for (Map.Entry<String, String> entry : knownWords.entrySet()) {
          String hebrewWord = entry.getKey();
          String type = entry.getValue();
          insertToDatabase(hebrewWord, type, conn);
        }
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static String isWordExistInVARTYPETable(
      String word, Connection conn, boolean APPROVE_PRINTING) {
    PreparedStatement preparedStatement = null;

    try {

      String selectQuery = "SELECT VAR_TYPE FROM VARTYPE WHERE HEBREW_WORD = ?";
      preparedStatement = conn.prepareStatement(selectQuery);
      preparedStatement.setString(1, word);

      ResultSet rs = preparedStatement.executeQuery();

      if (rs.next()) {
        // Word exists in the table
        String type = rs.getString("VAR_TYPE");
        if (APPROVE_PRINTING) {
          System.out.println("VAR_TYPE: " + type);
        }
        return type;
      }

      return null;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    } finally {
      try {
        // if (rs != null) resultSet.close();
        if (preparedStatement != null) preparedStatement.close();
        // if (conn != null) conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    // createVARTYPETableIfNotExists();
  }
}
