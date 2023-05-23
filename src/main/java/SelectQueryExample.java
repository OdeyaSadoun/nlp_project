import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SelectQueryExample {

    public static void main(String[] args) {

       addSubjectToDatabase("כחול" , "blue", "רצפה", "floor");

    }


    public static String getDateWithMS() {
        String date = LocalDateTime.now().format(  DateTimeFormatter.ISO_LOCAL_DATE_TIME )
                .replace( "T" , " " );
        return date.substring(0, date.length() - 4);
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
            String current_date = getDateWithMS();
            String type_name="null";
            String logist="לוגיסט";
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


                    String insertSubjectQuery = "INSERT INTO KTCLASS (CLASS_CODE_NAME, NAME, OWNER, UPDATE_DATE, CREATION_DATE) VALUES ('" + englishSubject + "','" +hebrewSubject+ "','" + logist + "','" + current_date + "', '" + current_date + "')";
                    stmt.executeUpdate(insertSubjectQuery);
                }

                // Step 3: Add corresponding field to KTATTRIBUTE table
                String getClassIdQuery = "SELECT ATTR_CODE_NAME FROM KTATTRIBUTE WHERE CLASS_CODE_NAME = '" + englishSubject + "' AND ATTR_CODE_NAME = '" + englishField + "'";
                rs = stmt.executeQuery(getClassIdQuery);
                if (!rs.next()) {
                    String insertSubjectQuery = "INSERT INTO KTATTRIBUTE (CLASS_CODE_NAME,ATTR_CODE_NAME, NAME, TYPE_NAME,UPDATE_DATE, CREATION_DATE ) VALUES ('" + englishSubject+ "','" + englishField+ "', '" + hebrewField+ "', '" + type_name+ "', '" + current_date + "', '" + current_date + "')";
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
