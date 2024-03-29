import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslateWithoutInternet {
  public static void main(String[] args) {}


  public static void deleteCopyingTable(Statement stmt) throws SQLException {
    try {
      // SQL query to check if the table exists
      String checkTableQuery =
          "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'Copying'";
      ResultSet rs = stmt.executeQuery(checkTableQuery);
      rs.next();
      int tableCount = rs.getInt(1);

      if (tableCount != 0) {
        // delete the table if it exist
        String deleteTableQuery = "DROP TABLE Copying";
        stmt.execute(deleteTableQuery);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void createCopyingTableIfNotExists(Connection conn, Statement stmt) {

    try {
      // SQL query to check if the table exists
      String checkTableQuery =
          "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'Copying'";
      ResultSet rs = stmt.executeQuery(checkTableQuery);
      rs.next();
      int tableCount = rs.getInt(1);

      if (tableCount == 0) {
        // Create the table if it doesn't exist
        String createTableQuery =
            "CREATE TABLE Copying (Hebrew VARCHAR(255), English VARCHAR(255))";
        stmt.executeUpdate(createTableQuery);

        // Fill the Coping table in DataBase:
        Map<String, String> hebrewToEnglishMapping = new HashMap<>();
        hebrewToEnglishMapping.put("א", "a");
        hebrewToEnglishMapping.put("ב", "b");
        hebrewToEnglishMapping.put("ג", "g");
        hebrewToEnglishMapping.put("ד", "d");
        hebrewToEnglishMapping.put("ה", "h");
        hebrewToEnglishMapping.put("ו", "o");
        hebrewToEnglishMapping.put("ז", "z");
        hebrewToEnglishMapping.put("ח", "ch");
        hebrewToEnglishMapping.put("ט", "t");
        hebrewToEnglishMapping.put("י", "y");
        hebrewToEnglishMapping.put("כ", "c");
        hebrewToEnglishMapping.put("ך", "ch");
        hebrewToEnglishMapping.put("ל", "l");
        hebrewToEnglishMapping.put("מ", "m");
        hebrewToEnglishMapping.put("ם", "m");
        hebrewToEnglishMapping.put("נ", "n");
        hebrewToEnglishMapping.put("ן", "n");
        hebrewToEnglishMapping.put("ס", "s");
        hebrewToEnglishMapping.put("ע", "a");
        hebrewToEnglishMapping.put("פ", "f");
        hebrewToEnglishMapping.put("ף", "ph");
        hebrewToEnglishMapping.put("צ", "tz");
        hebrewToEnglishMapping.put("ץ", "tz");
        hebrewToEnglishMapping.put("ק", "k");
        hebrewToEnglishMapping.put("ר", "r");
        hebrewToEnglishMapping.put("ש", "sh");
        hebrewToEnglishMapping.put("ת", "t");
        hebrewToEnglishMapping.put("0", "0");
        hebrewToEnglishMapping.put("1", "1");
        hebrewToEnglishMapping.put("2", "2");
        hebrewToEnglishMapping.put("3", "3");
        hebrewToEnglishMapping.put("4", "4");
        hebrewToEnglishMapping.put("5", "5");
        hebrewToEnglishMapping.put("6", "6");
        hebrewToEnglishMapping.put("7", "7");
        hebrewToEnglishMapping.put("8", "8");
        hebrewToEnglishMapping.put("9", "9");
        hebrewToEnglishMapping.put("_", "_");

        for (Map.Entry<String, String> entry : hebrewToEnglishMapping.entrySet()) {
          String hebrew = entry.getKey();
          String english = entry.getValue();
          insertToDatabase(hebrew, english, conn);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static String isWordExistInKTATTRIBUTETable(
      String word, Connection conn, boolean printLogs) {
    PreparedStatement preparedStatement = null;

    try {

      String selectQuery = "SELECT ATTR_CODE_NAME FROM KTATTRIBUTE WHERE NAME = ?";
      preparedStatement = conn.prepareStatement(selectQuery);
      preparedStatement.setString(1, word);

      ResultSet rs = preparedStatement.executeQuery();

      if (rs.next()) {
        // Word exists in the table
        String englishWord = rs.getString("ATTR_CODE_NAME");
        if (printLogs) {
          System.out.println("English Word: " + englishWord);
        }
        return englishWord;
      }

      return null;

    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    } finally {
      try {
        if (preparedStatement != null) preparedStatement.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  private static String isWordExistInKTCLASSTable(String word, Connection conn, boolean printLogs) {
    PreparedStatement preparedStatement = null;

    try {
      String selectQuery = "SELECT CLASS_CODE_NAME FROM KTCLASS WHERE NAME = ?";
      preparedStatement = conn.prepareStatement(selectQuery);
      preparedStatement.setString(1, word);

      ResultSet rs = preparedStatement.executeQuery();

      if (rs.next()) {
        // Word exists in the table
        String englishWord = rs.getString("CLASS_CODE_NAME");
        if (printLogs) {
          System.out.println("English Word: " + englishWord);
        }
        return englishWord;
      }

      return null;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    } finally {
      try {
        // if (rs != null) rs.close();
        if (preparedStatement != null) preparedStatement.close();
        // if (conn != null) conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public static void insertToDatabase(String hebrew, String english, Connection conn) {
    try {
      String insertQuery = "INSERT INTO Copying (Hebrew, English) VALUES (?, ?)";
      PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);

      preparedStatement.setString(1, hebrew);
      preparedStatement.setString(2, english);

      preparedStatement.executeUpdate();
      preparedStatement.close();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static String retrieveEnglishValuesFromHebrewValues(
      String word, Connection conn, boolean printLogs) {
    String translateWord;
    StringBuilder finalWordBuilder = new StringBuilder();

    // check if the hebrew word exist in database or in ktclass or in ktattribute:
    // if yes, we take the translation from the database
    if (isWordExistInKTCLASSTable(word, conn, printLogs) != null) {
      translateWord = isWordExistInKTCLASSTable(word, conn, printLogs);
    } else if (isWordExistInKTATTRIBUTETable(word, conn, printLogs) != null) {
      translateWord = isWordExistInKTATTRIBUTETable(word, conn, printLogs);
    }
    // if not, we use this function:
    else {
      char[] myLetters = Tools.breakWordIntoLetters(word);
      String[] arrString = Tools.createNewArrayFromCharList(myLetters);

      for (String s : arrString) {
        char[] letters = Tools.breakWordIntoLetters(s);
        StringBuilder wordBuilder = new StringBuilder();

        try {

          if (letters[0] == 'ב') {
            // b
            letters = Tools.removeCharAtIndex(letters, 0);
            wordBuilder.append('b');
          } else if (letters[0] == 'כ') {
            // k
            letters = Tools.removeCharAtIndex(letters, 0);
            wordBuilder.append('k');
          } else if (letters[0] == 'פ') {
            // p
            letters = Tools.removeCharAtIndex(letters, 0);
            wordBuilder.append('p');
          } else if (letters[0] == 'ו') {
            // v
            letters = Tools.removeCharAtIndex(letters, 0);
            wordBuilder.append('v');
          }

          // Prepare the query
          String selectQuery = "SELECT English FROM Copying WHERE Hebrew = ?";
          PreparedStatement preparedStatement = conn.prepareStatement(selectQuery);

          // Loop over the letters
          for (int i = 0; i < letters.length; i++) {
            ResultSet rs;
            if (letters[i] == 'י') {
              if (i <= letters.length - 1) {
                if (i + 1 < letters.length && (letters[i + 1] == 'י')) {
                  /// מכניסים i במקום i
                  wordBuilder.append('i');
                  ++i;
                }
                else {
                  // Set the parameter value
                  preparedStatement.setString(1, String.valueOf(letters[i]));
                  // Execute the query
                  rs = preparedStatement.executeQuery();
                  // If the query returns a row, get the English value
                  if (rs.next()) {
                    String EnglishValue = rs.getString("English");
                    wordBuilder.append(EnglishValue);
                  }
                }
              }
            } else if (letters[i] == 'פ') {
              if (i <= letters.length - 1) {
                if (i + 1 < letters.length && (letters[i + 1] == 'ף')) {
                  /// מכניסים p במקום i
                  wordBuilder.append('p');
                  ++i;
                } else {
                  wordBuilder.append('f');
//                if (letters[i] == 'ף') {
//                  if (printLogs) {
//                    System.out.println("זהים!!!");
//                  }
//                }
//                if (printLogs) {
//                  System.out.println(letters[i]);
//                }

//                  preparedStatement.setString(1, String.valueOf(letters[i]));
//                  // Execute the query
//                  rs = preparedStatement.executeQuery();
//                  // If the query returns a row, get the English value
//                  if (rs.next()) {
//                    String EnglishValue = rs.getString("English");
//                    wordBuilder.append(EnglishValue);
//                  }
                }
              }
              //wordBuilder.append('f');
            } else if (letters[i] == 'א') {
              if (i <= letters.length - 1) {
                if (i + 1 < letters.length && (letters[i + 1] == 'ו')) {
                  /// מתעלמים מ- א
                  continue;
                }
                wordBuilder.append('a');
              }
            } else if (letters[i] == 'כ') {
              if (i <= letters.length - 1) {
                if (i + 1 < letters.length ) {
                  wordBuilder.append('c');
                }
                else{
                  wordBuilder.append('c');
                  wordBuilder.append('h');
                }


              }
            } else if (letters[i] == 'ע') {
              if (i <= letters.length - 1) {
                if (i + 1 < letters.length && (letters[i + 1] == 'ו')) {
                  /// מתעלמים מ- ע
                }
                wordBuilder.append('a');
              }
            } else if (letters[i] == 'ב') {
              if (i == letters.length - 1) {
                /// מכניסים v במקום i
                wordBuilder.append('v');
                ++i;
              }
              else{
                wordBuilder.append('b');
              }
            } else {
              // Set the parameter value
              preparedStatement.setString(1, String.valueOf(letters[i]));
              // Execute the query
              rs = preparedStatement.executeQuery();
              // If the query returns a row, get the English value
              if (rs.next()) {
                String EnglishValue = rs.getString("English");
                wordBuilder.append(EnglishValue);
              }
            }
          }

          finalWordBuilder.append(wordBuilder);
          finalWordBuilder.append('_');

        } catch (SQLException e) {
          e.printStackTrace();
        }
      }

      return Tools.removeUnderscore(finalWordBuilder.toString());
    }

    return translateWord;
  }
}
