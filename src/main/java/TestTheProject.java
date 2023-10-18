import java.io.*;
import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestTheProject {
  static final String JDBC_URL =
      "jdbc:sqlserver://LOCALHOST\\SQLEXPRESS:1433;databaseName=logistcourse1;SelectMethod=Cursor";
  static final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  static final String USERNAME = "logistcourse1";
  static final String PASSWORD = "logistcourse1";
  static final boolean printLogs = true;
  static final boolean withLevinshtainDistance = false;
  static final int levinshtainDistance = 1;

  public static void main(String[] args) throws SQLException {

    Connection conn = null;
    Statement stmt = null;

    // Connect to the database
    ResultSet rs = null;



    try {
      Class.forName(JDBC_DRIVER);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    try {
      conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      stmt = conn.createStatement();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    try {
      String hebrewSubject = Tools.getHebrewSubject(stmt);
      String englishSubject = Tools.getEnglishSubject(stmt);
      //System.out.println(englishSubject + "englishSubject");
      //System.out.println(hebrewSubject + "hebrewSubject");
      updateTables(conn, stmt, rs);

//      // Read sentences from a text file.
      List<String> sentences = readSentences("sentencesForDEB_RULES.txt");
      String sentenceAfterAddUnderscoreInQuotes;
      int counterForPrint = 1;
      // For each sentence, call the readTemplate function from the static class AAA and pass the
      // sentence to the function.
      for (String sentence : sentences) {
        sentenceAfterAddUnderscoreInQuotes = Tools.replaceSpacesWithUnderscoresInQuotes(sentence);
        if (printLogs) {
          System.out.println(
              "Sentence num " + counterForPrint + " : " + sentenceAfterAddUnderscoreInQuotes);
        }
        counterForPrint++;
        ClassifySentenceWithoutInternet.readTemplate(
            sentenceAfterAddUnderscoreInQuotes, conn, stmt, null, printLogs, englishSubject, hebrewSubject, withLevinshtainDistance, levinshtainDistance );
      }
    } catch (Exception e) {
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
  }

  private static void updateCopingTable(Connection conn, Statement stmt) {
    try {
      TranslateWithoutInternet.deleteCopyingTable(stmt);
      TranslateWithoutInternet.createCopyingTableIfNotExists(conn, stmt);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static void updateVARTYPETable(Connection conn, Statement stmt, ResultSet rs) {
    try {
      GetType.deleteVARTYPETable(stmt);
      GetType.createVARTYPETableIfNotExists(conn, stmt);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static void updateTables(Connection conn, Statement stmt, ResultSet rs) {
    updateCopingTable(conn, stmt);
    updateVARTYPETable(conn, stmt, rs);
  }

  public static List<String> readSentences(String filename) {
    // Create a list to store the sentences.
    List<String> sentences = new ArrayList<>();

    // Open the file.
    File file = new File(filename);
    try (FileReader reader = new FileReader(file)) {
      // Read the sentences from the file.
      BufferedReader bufferedReader = new BufferedReader(reader);
      String sentence;
      while ((sentence = bufferedReader.readLine()) != null) {
        sentences.add(sentence);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Return the list of sentences.
    return sentences;
  }
}
