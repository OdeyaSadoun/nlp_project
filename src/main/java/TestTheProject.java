import java.io.*;
import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestTheProject {

    final static String JDBC_URL = "jdbc:sqlserver://LOCALHOST\\SQLEXPRESS:1433;databaseName=logistcourse1;SelectMethod=Cursor";
    final static String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    final static String USERNAME = "logistcourse1";
    final static String PASSWORD = "logistcourse1";

    public static void main(String[] args) {

        //updateTables();
        Connection conn = null;
        Statement stmt = null;

        //Connect to the database
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
        //System.out.println(TranslateWithoutInternet.retrieveEnglishValuesFromHebrewValues("אחוז_בלון_ממחיר_בטוחה", conn, stmt, rs));
        try {
            // Read sentences from a text file.
            List<String> sentences = readSentences("sentencesForDEB_RULES.txt");
            String sentenceAfterAddUnderscoreInQuotes = "";
            int counterForPrint = 1;
            // For each sentence, call the readTemplate function from the static class AAA and pass the sentence to the function.
            for (String sentence : sentences) {
                sentenceAfterAddUnderscoreInQuotes = replaceSpacesWithUnderscoresInQuotes(sentence);
                System.out.println("Sentence num " + counterForPrint + " : " + sentenceAfterAddUnderscoreInQuotes);
                counterForPrint++;
                ClassifySentenceWithoutInternet.readTemplate(sentenceAfterAddUnderscoreInQuotes, conn, stmt, rs);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally{
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

        }

    }

    private static void updateTables(Connection conn, Statement stmt, ResultSet rs) {
        try {
            GetType.deleteVARTYPETable(conn, stmt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        GetType.createVARTYPETableIfNotExists(conn, stmt, rs);
        TranslateWithoutInternet.createCopingTableIfNotExists(conn, stmt, rs);
    }

    public static String removeQuotes(String sentence) {
        // Create a new string to store the output.
        String output = "";

        // Iterate over the characters in the sentence.
        for (int i = 0; i < sentence.length(); i++) {
            // If the character is not a quote, add it to the output string.
            if (sentence.charAt(i) != '"') {
                output += sentence.charAt(i);
            }
        }

        // Return the output string.
        return output;
    }

    public static String replaceSpacesWithUnderscoresInQuotes(String sentence) {
        // תחילה, נבנה מחרוזת חדשה שתתקבל את התוצאה הסופית.
        StringBuilder result = new StringBuilder();

        // נמצא את המקום הראשון שבו יש גרש פותח.
        int startQuoteIndex = sentence.indexOf('\"');

        // אם לא נמצא גרש פותח, התוצאה היא המחרוזת המקורית.
        if (startQuoteIndex == -1) {
            return sentence;
        }

        // נמצא את המקום האחרון שבו יש גרש סגור.
        int endQuoteIndex = sentence.lastIndexOf('\"');

        // אם לא נמצא גרש סגור, התוצאה היא המחרוזת המקורית.
        if (endQuoteIndex == -1) {
            return sentence;
        }

        // נבנה מחרוזת חדשה מהחלק בין הגרשיים.
        String quotePart = sentence.substring(startQuoteIndex + 1, endQuoteIndex);

        // מחליפים את כל הרווחים במחרוזת החדשה בקו תחתון.
        quotePart = quotePart.replaceAll(" ", "_");

        // מוסיפים את המחרוזת החדשה למחרוזת התוצאה הסופית.
        result.append(sentence.substring(0, startQuoteIndex + 1));
        result.append(quotePart);
        result.append('"');
        result.append(sentence.substring(endQuoteIndex + 1));

        // נחזור את המחרוזת התוצאה הסופית.
        return result.toString();
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Return the list of sentences.
        return sentences;
    }

}
