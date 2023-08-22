import java.sql.*;


public class TranslateWithoutInternet {
    public static void main(String[] args) {

        //insertToDatabase("ב" , "b");
        //System.out.println(retrieveHebrewValues("בא"));

    }



    public static char[] breakWordIntoLetters(String word) {
        char[] letters = new char[word.length()];

        for (int i = 0; i < word.length(); i++) {
            letters[i] = word.charAt(i);
        }

        return letters;
    }


    public static String retrieveHebrewValues(String word) {
        String jdbcUrl = "jdbc:sqlserver://LOCALHOST\\SQLEXPRESS:1433;databaseName=logistcourse1;SelectMethod=Cursor";
        String jdbcDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        // Database credentials
        String username = "logistcourse1";
        String password = "logistcourse1";
        // Database credentials
        Connection conn = null;
        Statement stmt = null;
        //Connect to the database
        ResultSet rs = null;

        char[] letters=breakWordIntoLetters(word);
        StringBuilder wordBuilder = new StringBuilder();
        try {
            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            stmt = conn.createStatement();
            for (char letter : letters) {
                String myLetter = String.valueOf(letter);
                String selectQuery = "SELECT English FROM Copying WHERE Hebrew = '" + myLetter + "'";
                rs = stmt.executeQuery(selectQuery);
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
            stmt = conn.createStatement();
            String insertQuery = "INSERT INTO Copying (Hebrew, English) VALUES ('" + hebrew + "', '" + english + "')";
            stmt.executeUpdate(insertQuery);
            stmt.close();
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
