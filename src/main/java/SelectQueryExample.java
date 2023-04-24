import java.sql.*;

public class SelectQueryExample {

    public static void main(String[] args) {
        addSubjectToDatabase("צהוב" , "yellow", "כדור", "ball");

    }

        public static void addSubjectToDatabase(String hebrewField, String englishField, String hebrewSubject, String englishSubject)
        {
            String jdbcUrl = "jdbc:sqlserver://LOCALHOST\\SQLEXPRESS:1433;databaseName=logistcourse1;SelectMethod=Cursor";
            String jdbcDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            // Database credentials
            String username = "logistcourse1";
            String password = "logistcourse1";
            // Database credentials
            ResultSet rs = null;
            Connection conn = null;
            Statement stmt = null;
            try {
                // Step 1: Connect to the database
                Class.forName(jdbcDriver);
                conn = DriverManager.getConnection(jdbcUrl, username, password);

                // Step 2: Check if subject is in KTCLASS table
                stmt = conn.createStatement();
                String checkSubjectQuery = "SELECT CLASS_CODE_NAME FROM KTCLASS WHERE CLASS_CODE_NAME = '" + englishSubject + "'";
                rs = stmt.executeQuery(checkSubjectQuery);

                // If the subject is not found, add it to the KTCLASS table
                if (!rs.next()) {
                    String insertSubjectQuery = "INSERT INTO KTCLASS (CLASS_CODE_NAME, NAME) VALUES ('" + englishSubject + "','" +hebrewSubject+ "')";
                    stmt.executeUpdate(insertSubjectQuery);
                }

                // Step 3: Add corresponding field to KTATTRIBUTE table
                String getClassIdQuery = "SELECT ATTR_CODE_NAME FROM KTATTRIBUTE WHERE CLASS_CODE_NAME = '" + englishSubject + "' AND ATTR_CODE_NAME = '" + englishField + "'";
                rs = stmt.executeQuery(getClassIdQuery);
                if (!rs.next()) {
                    String insertSubjectQuery = "INSERT INTO KTATTRIBUTE (CLASS_CODE_NAME,ATTR_CODE_NAME, NAME) VALUES ('" + englishSubject+ "','" + englishField+ "', '" + hebrewField+ "')";
                    stmt.executeUpdate(insertSubjectQuery);
                }
// Step 4: Close the database resources
                rs.close();
                stmt.close();
                conn.close();

            } catch (ClassNotFoundException e) {
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


        }
}
