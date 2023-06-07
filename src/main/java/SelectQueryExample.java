import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SelectQueryExample {

    public static void main(String[] args) {

       addSubjectToDatabase("ורוד" , "pink", "ילדה", "girl");

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
            String type_name=GetType.getLabel(englishField);
            String logist="לוגיסט";

            //fields for ktclass:
            int activationOrder = 0;
            int classIndex = 0;

            //for ktattribute:
            int overlapPosition = 0;
            int attributeIndex = 0;
            int keyType = 0; //always 0 (unless it is the default attribute in a new class - then it is 1)
            int ioMode = 0;
            int sortNumber = 0;
            int sortDirection = 0;


            try {
                //Connect to the database
                Class.forName(jdbcDriver);
                conn = DriverManager.getConnection(jdbcUrl, username, password);


                stmt = conn.createStatement();

                //query to find class index to sent to insert query
                String query_CLASS_INDEX = "SELECT MAX(CLASS_INDEX) + 1 AS next_index FROM KVCLASS";
                rs = stmt.executeQuery(query_CLASS_INDEX);
                if (rs.next()) {
                    classIndex = rs.getInt("next_index");
                    System.out.println("Next class_index: " + classIndex);
                }

                //Check if subject is in KTCLASS table
                String checkSubjectQuery = "SELECT CLASS_CODE_NAME FROM KTCLASS WHERE CLASS_CODE_NAME = '" + englishSubject + "'";
                rs = stmt.executeQuery(checkSubjectQuery);
                // If the subject is not found, add it to the KTCLASS table
                if (!rs.next()) {
                    String insertSubjectQuery = "INSERT INTO KTCLASS (CLASS_CODE_NAME, NAME, OWNER, ACTIVATION_ORDER, CLASS_INDEX, CREATION_DATE, UPDATE_DATE) VALUES ('" + englishSubject + "','" +hebrewSubject+ "','" + logist + "','" + activationOrder + "','" + classIndex+ "','" + current_date + "', '" + current_date + "')";
                    stmt.executeUpdate(insertSubjectQuery);
                }

                //query to find attribute index to sent to insert query
                String query_ATTRIBUTE_INDEX = "SELECT COALESCE(MAX(ATTRIBUTE_INDEX), 0) + 1 AS next_index FROM KVATTRIBUTE WHERE CLASS_CODE_NAME = '" + englishSubject + "'";
                rs = stmt.executeQuery(query_ATTRIBUTE_INDEX);
                if (rs.next()) {
                    attributeIndex = rs.getInt("next_index");
                    System.out.println("Next attribute_index: " + attributeIndex);
                }

                //Add corresponding field to KTATTRIBUTE table

                String getClassIdQuery = "SELECT ATTR_CODE_NAME FROM KTATTRIBUTE WHERE CLASS_CODE_NAME = '" + englishSubject + "' AND ATTR_CODE_NAME = '" + englishField + "'";
                rs = stmt.executeQuery(getClassIdQuery);
                if (!rs.next()) {
                    String insertSubjectQuery = "INSERT INTO KTATTRIBUTE (CLASS_CODE_NAME, ATTR_CODE_NAME, NAME, TYPE_NAME, OVERLAP_POSITION,ATTRIBUTE_INDEX, CREATION_DATE, UPDATE_DATE, KEY_TYPE, IO_MODE, SORT_NUMBER, SORT_DIRECTION) VALUES ('" + englishSubject+ "','" + englishField+ "', '" + hebrewField+ "', '" + type_name+ "', '" + overlapPosition + "', '" + attributeIndex + "', '" + current_date + "', '" + current_date + "', '" + keyType + "', '" + ioMode + "', '" + sortNumber + "', '" + sortDirection + "')";
                    stmt.executeUpdate(insertSubjectQuery);
                }
                //Close the database resources
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
