import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaveToDatabase {

    final static String DEFAULT_TYPE = "D8.2";
    public static void main(String[] args) {
        addSubjectToDatabase("א", "a", "אא", "aa", "Bool");
    }

    public static String getDateWithMS() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .replace("T", " ");
        return date.substring(0, date.length() - 4);
    }


    /**
     * Function with a query to find class index to sent to insert class query
     *
     * @param englishSubject for check if the class index need to be 1
     */
    private static int getClassIndex(String englishSubject, Statement stmt) throws SQLException {
        String queryClassIndex = "SELECT MAX(CLASS_INDEX) + 1 AS next_index FROM KVCLASS";
        ResultSet rs = stmt.executeQuery(queryClassIndex);
        int classIndex = 0;
        if (rs.next()) {
            if(englishSubject == "mainSubject"){
                classIndex = 1;
                System.out.println("main class_index: " + classIndex);
            }
            else{
                classIndex = rs.getInt("next_index");
                System.out.println("Next class_index: " + classIndex);
            }
        }
        return classIndex;
    }


    /**
     * Function with a query to check if subject is in KTCLASS table
     *
     * @param englishSubject the subject that need to check if exist in DB
     * @param conn for the connection to DB
     */
    private static boolean isSubjectInKTCLASSTable(String englishSubject, Connection conn) throws SQLException {
        String checkSubjectInKTCLASSTableQuery = "SELECT CLASS_CODE_NAME FROM KTCLASS WHERE CLASS_CODE_NAME = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(checkSubjectInKTCLASSTableQuery);
        preparedStatement.setString(1, englishSubject);
        ResultSet rs = preparedStatement.executeQuery();

        //return if the subject exists in the table
        return rs.next();
    }


    /**
     * Function to insert subject to KTCLASS table
     *
     * @param englishSubject the english subject
     * @param hebrewSubject the hebrew subject
     * @param logist for the table
     * @param activationOrder for the table
     * @param classIndex the index for this subject
     * @param current_date for the date
     * @param conn for the connection to DB
     */
    private static void insertSubject(String englishSubject, String hebrewSubject, String logist, int activationOrder, int classIndex, String current_date, Connection conn) throws SQLException {
        String insertSubjectQuery = "INSERT INTO KTCLASS (CLASS_CODE_NAME, NAME, OWNER, ACTIVATION_ORDER, CLASS_INDEX, CREATION_DATE, UPDATE_DATE) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(insertSubjectQuery);

        preparedStatement.setString(1, englishSubject);
        preparedStatement.setString(2, hebrewSubject);
        preparedStatement.setString(3, logist);
        preparedStatement.setString(4, String.valueOf(activationOrder));
        preparedStatement.setString(5, String.valueOf(classIndex));
        preparedStatement.setString(6, current_date);
        preparedStatement.setString(7, current_date);

        preparedStatement.executeUpdate();
        preparedStatement.close();
    }


    /**
     * Function for query to find attribute index to sent to insert attribute query
     *
     * @param englishSubject the subject that need to find the last index of attribute
     * @param conn for the connection to DB
     */
    private static int getAttributeIndex(String englishSubject, Connection conn) throws SQLException {
        String queryAttributeIndex = "SELECT COALESCE(MAX(ATTRIBUTE_INDEX), 0) + 1 AS next_index FROM KVATTRIBUTE WHERE CLASS_CODE_NAME = ?";

        PreparedStatement preparedStatement = conn.prepareStatement(queryAttributeIndex);
        preparedStatement.setString(1, englishSubject);
        ResultSet rs = preparedStatement.executeQuery();

        int attributeIndex = 0;
        if (rs.next()) {
            attributeIndex = rs.getInt("next_index");
            System.out.println("Next attribute_index: " + attributeIndex);
        }

        return attributeIndex;
    }


    /**
     * Function to check if the field is existed in the DB
     *
     * @param englishSubject the subject that match to this field
     * @param englishField the field we check if exist in the DB
     * @param conn for the connection to DB
     */
    private static boolean isSubjectInKTATTRIBUTETable(String englishSubject, String englishField, Connection conn) throws SQLException {
        String getClassIdQuery = "SELECT ATTR_CODE_NAME FROM KTATTRIBUTE WHERE CLASS_CODE_NAME = ? AND ATTR_CODE_NAME = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(getClassIdQuery);

        preparedStatement.setString(1, englishSubject);
        preparedStatement.setString(2, englishField);
        ResultSet rs = preparedStatement.executeQuery();

        return rs.next();
    }


    /**
     * Function to insert field to KTATTRIBUTE table
     *
     * @param englishSubject the english subject that match to the field
     * @param englishField the english field
     * @param attributeIndex the index for this field
     * @param hebrewField the hebrew field
     * @param ioMode for the table
     * @param keyType for the table
     * @param overlapPosition for the table
     * @param sortDirection for the table
     * @param sortNumber for the table
     * @param type_name for the table
     * @param current_date for the date
     * @param conn for the connection to DB
     */
    private static void insertAttribute(String englishSubject, String englishField, String hebrewField, String type_name, int overlapPosition, int attributeIndex, String current_date, int keyType, int ioMode, int sortNumber, int sortDirection, Connection conn) throws SQLException {
        String insertSubjectQuery = "INSERT INTO KTATTRIBUTE (CLASS_CODE_NAME, ATTR_CODE_NAME, NAME, TYPE_NAME, OVERLAP_POSITION, ATTRIBUTE_INDEX, CREATION_DATE, UPDATE_DATE, KEY_TYPE, IO_MODE, SORT_NUMBER, SORT_DIRECTION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(insertSubjectQuery);

        preparedStatement.setString(1, englishSubject);
        preparedStatement.setString(2, englishField);
        preparedStatement.setString(3, hebrewField);
        preparedStatement.setString(4, type_name);
        preparedStatement.setString(5, String.valueOf(overlapPosition));
        preparedStatement.setString(6, String.valueOf(attributeIndex));
        preparedStatement.setString(7, current_date);
        preparedStatement.setString(8, current_date);
        preparedStatement.setString(9, String.valueOf(keyType));
        preparedStatement.setString(10, String.valueOf(ioMode));
        preparedStatement.setString(11, String.valueOf(sortNumber));
        preparedStatement.setString(12, String.valueOf(sortDirection));

        preparedStatement.executeUpdate();
        preparedStatement.close();

        conn.close();
    }


    public static void addSubjectToDatabase(String hebrewField, String englishField, String hebrewSubject, String englishSubject, String type_name) {

        // Define constants for the database connection information
        final String JDBC_URL = "jdbc:sqlserver://LOCALHOST\\SQLEXPRESS:1433;databaseName=logistcourse1;SelectMethod=Cursor";
        final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        final String USERNAME = "logistcourse1";
        final String PASSWORD = "logistcourse1";

        // Define constants for the field names in the KTCLASS and KTATTRIBUTE tables
        final String CURRENT_DATE = getDateWithMS();
        final String LOGIST = "לוגיסט";
        final int ACTIVATION_ORDER = 0;
        final int OVERLAP_POSITION = 0;
        final int IO_MODE = 0;
        final int SORT_NUMBER = 0;
        final int SORT_DIRECTION = 0;

        // Database credentials
        ResultSet rs = null;
        Connection conn = null;
        Statement stmt = null;

        // More variables:
        int classIndex = 0;
        int attributeIndex = 0;
        int keyType = 0; //always 0 (unless it is the default attribute in a new class - then it is 1)

        try {
            //Connect to the database
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            stmt = conn.createStatement();

            //Find class index:
            classIndex = getClassIndex(englishSubject, stmt);

            //Check if subject is in KTCLASS table
            if (!isSubjectInKTCLASSTable(englishSubject, conn)) {
                if(!HebrewSpellChecker.isSameWordInDB(hebrewSubject,1)) {
                    insertSubject(englishSubject, hebrewSubject, LOGIST, ACTIVATION_ORDER, classIndex, CURRENT_DATE, conn);
                }
            }

            //Find attribute index to sent to insert query
            if (getAttributeIndex(englishSubject,conn) != 0) {
                attributeIndex = getAttributeIndex(englishSubject,conn);
            }

            //Check if subject is in KTATTRIBUTE table
            if(englishField != null && hebrewField != null) {
                if (!isSubjectInKTATTRIBUTETable(englishSubject, englishField, conn)) {
                    if(!HebrewSpellChecker.isSameWordInDB(hebrewField,1)) {

                        insertAttribute(englishSubject, englishField, hebrewField, type_name, OVERLAP_POSITION,
                                attributeIndex, CURRENT_DATE, keyType, IO_MODE, SORT_NUMBER, SORT_DIRECTION, conn);
                    }
                }
            }

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
