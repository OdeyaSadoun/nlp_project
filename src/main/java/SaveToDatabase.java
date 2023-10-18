import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaveToDatabase {
  //static final boolean WITHLEVINSHTAINDISTANCE = true;
  //static final int LEVINSHTAINDISTANCE = 1;
  static final String PASSWORD = "logistcourse1";

  public static void main(String[] args) {}


  public static String getDateWithMS() {
    String date =
        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " ");

    return date.substring(0, date.length() - 4);
  }

  /**
   * Function with a query to find class index to sent to insert class query
   *
   * @param englishSubject for check if the class index need to be 1
   */
  private static int getClassIndex(String englishSubject, Statement stmt, boolean printLogs)
      throws SQLException {
    String queryClassIndex = "SELECT MAX(CLASS_INDEX) + 1 AS next_index FROM KVCLASS";
    ResultSet rs = stmt.executeQuery(queryClassIndex);
    int classIndex = 0;

    if (rs.next()) {
      if (englishSubject.equals("mainSubject")) {
        classIndex = 1;
        if (printLogs) {
          System.out.println("main class_index: " + classIndex);
        }
      } else {
        classIndex = rs.getInt("next_index");
        if (printLogs) {
          System.out.println("Next class_index: " + classIndex);
        }
      }
    }

    return classIndex;
  }

  /**
   * Function with a query to check if subject is in KTCLASS table
   *
   * @param subject the subject that need to check if exist in DB
   * @param conn    for the connection to DB
   */
  private static boolean isSubjectInKTCLASSTable(String subject, Connection conn)
      throws SQLException {
    String checkSubjectInKTCLASSTableQuery = "SELECT NAME FROM KTCLASS WHERE NAME = ?";
    PreparedStatement preparedStatement = conn.prepareStatement(checkSubjectInKTCLASSTableQuery);
    preparedStatement.setString(1, subject);
    ResultSet rs = preparedStatement.executeQuery();

    // return if the subject exists in the table
    return rs.next();
  }

  /**
   * Function to insert subject to KTCLASS table
   *
   * @param englishSubject  the english subject
   * @param hebrewSubject   the hebrew subject
   * @param activationOrder for the table
   * @param classIndex      the index for this subject
   * @param current_date    for the date
   * @param conn            for the connection to DB
   */
  private static void insertSubject(
      String englishSubject,
      String hebrewSubject,
      int activationOrder,
      int classIndex,
      String current_date,
      Connection conn)
      throws SQLException {
    String insertSubjectQuery =
        "INSERT INTO KTCLASS (CLASS_CODE_NAME, NAME, OWNER, ACTIVATION_ORDER, CLASS_INDEX, CREATION_DATE, UPDATE_DATE) VALUES (?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement preparedStatement = conn.prepareStatement(insertSubjectQuery);
    preparedStatement.setString(1, englishSubject);
    preparedStatement.setString(2, hebrewSubject);
    preparedStatement.setString(3, "לוגיסט");
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
   * @param conn           for the connection to DB
   */
  private static int getAttributeIndex(
      String englishSubject, Connection conn, boolean printLogs) throws SQLException {
    String queryAttributeIndex =
        "SELECT COALESCE(MAX(ATTRIBUTE_INDEX), 0) + 1 AS next_index FROM KVATTRIBUTE WHERE CLASS_CODE_NAME = ?";
    PreparedStatement preparedStatement = conn.prepareStatement(queryAttributeIndex);
    preparedStatement.setString(1, englishSubject);
    ResultSet rs = preparedStatement.executeQuery();
    int attributeIndex = 0;

    if (rs.next()) {
      attributeIndex = rs.getInt("next_index");
      if (printLogs) {
        System.out.println("Next attribute_index: " + attributeIndex);
      }
    }

    return attributeIndex;
  }

  /**
   * Function to check if the field is existed in the DB
   *
   * @param ensubject the subject that match to this field
   * @param field     the field we check if exist in the DB
   * @param conn      for the connection to DB
   */
  private static boolean isSubjectInKTATTRIBUTETable(
      String ensubject, String field, Connection conn) throws SQLException {
    String getClassIdQuery = "SELECT NAME FROM KTATTRIBUTE WHERE CLASS_CODE_NAME = ? AND NAME = ?";
    PreparedStatement preparedStatement = conn.prepareStatement(getClassIdQuery);

    preparedStatement.setString(1, ensubject);
    preparedStatement.setString(2, field);
    ResultSet rs = preparedStatement.executeQuery();

    return rs.next();
  }

  /**
   * Function to insert field to KTATTRIBUTE table
   *
   * @param englishSubject the english subject that match to the field
   * @param englishField   the english field
   * @param hebrewField    the hebrew field
   * @param type_name      for the table
   * @param attributeIndex the index for this field
   * @param current_date   for the date
   * @param keyType        for the table
   * @param ioMode         for the table
   * @param conn           for the connection to DB
   */
  private static void insertAttribute(
      String englishSubject,
      String englishField,
      String hebrewField,
      String type_name,
      int attributeIndex,
      String current_date,
      int keyType,
      int ioMode,
      Connection conn)
      throws SQLException {
    String insertSubjectQuery =
        "INSERT INTO KTATTRIBUTE (CLASS_CODE_NAME, ATTR_CODE_NAME, NAME, TYPE_NAME, OVERLAP_POSITION, ATTRIBUTE_INDEX, CREATION_DATE, UPDATE_DATE, KEY_TYPE, IO_MODE, SORT_NUMBER, SORT_DIRECTION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement preparedStatement = conn.prepareStatement(insertSubjectQuery);
    preparedStatement.setString(1, englishSubject);
    preparedStatement.setString(2, englishField);
    preparedStatement.setString(3, hebrewField);
    preparedStatement.setString(4, type_name);
    preparedStatement.setString(5, String.valueOf(0));
    preparedStatement.setString(6, String.valueOf(attributeIndex));
    preparedStatement.setString(7, current_date);
    preparedStatement.setString(8, current_date);
    preparedStatement.setString(9, String.valueOf(keyType));
    preparedStatement.setString(10, String.valueOf(ioMode));
    preparedStatement.setString(11, String.valueOf(0));
    preparedStatement.setString(12, String.valueOf(0));
    preparedStatement.executeUpdate();
    preparedStatement.close();
  }

  public static void addSubjectToDatabase(
      String hebrewField,
      String englishField,
      String hebrewSubject,
      String englishSubject,
      String type_name,
      Connection conn,
      Statement stmt,
      boolean printLogs,
      boolean withLevinshtainDistance,
      int levinshtainDistance) {
    // Define constants for the field names in the KTCLASS and KTATTRIBUTE tables
    final String CURRENT_DATE = getDateWithMS();
    final int ACTIVATION_ORDER = 0;
    final int IO_MODE = 0;

    // More variables:
    int classIndex;
    int attributeIndex = 0;
    int keyType = 0; // always 0 (unless it is the default attribute in a new class - then it is 1)

    try {
      // Find class index:
      classIndex = getClassIndex(englishSubject, stmt, printLogs);

      // ******************************************************************************************/
      // Check if subject is in KTCLASS table
      if (!isSubjectInKTCLASSTable(hebrewSubject, conn)) {
        if (isSubjectInKTCLASSTable(englishSubject, conn)) {
          if (printLogs) {
            String ex =
                "There is problem with english subject because there is same that saved in database with hebrew subject that not same!";
            System.out.println(ex);
            throw new SQLException(ex);
          }
        } else {
          if (printLogs) {
            System.out.println("subject not in KTCLASS table - add to DB");
            System.out.println("before levinshtain distance");
          }
          if (withLevinshtainDistance) {
            if (printLogs) {
              System.out.println("WITHLEVINSHTAINDISTANCE- class: " + withLevinshtainDistance);
            }
            if (!HebrewSpellChecker.isSameWordInDBInKTCLASSTable(
                hebrewSubject, levinshtainDistance, conn)) {
              if (printLogs) {
                System.out.println("after levinshtain distance- not same");
              }
              insertSubject(
                  englishSubject, hebrewSubject, ACTIVATION_ORDER, classIndex, CURRENT_DATE, conn);
            }

          } else {
            if (printLogs) {
              System.out.println("not with lev sub");
            }
            insertSubject(
                englishSubject, hebrewSubject, ACTIVATION_ORDER, classIndex, CURRENT_DATE, conn);
          }
        }
      }
      // ****************************************************************************************/

      // Find attribute index to sent to insert query
      if (getAttributeIndex(englishSubject, conn, printLogs) != 0) {
        attributeIndex = getAttributeIndex(englishSubject, conn, printLogs);
      }
      // ****************************************************************************************/

      // Check if subject is in KTATTRIBUTE table
      if (englishField != null && hebrewField != null) {
        if (!isSubjectInKTATTRIBUTETable(englishSubject, hebrewField, conn)) {
          if (isSubjectInKTATTRIBUTETable(englishSubject, englishField, conn)) {
            if (printLogs) {
              String ex =
                  "There is problem with english subject or filed because there is same that saved in database with hebrew subject or filed that not same!";
              System.out.println(ex);

              throw new SQLException(ex);
            }
          } else {
            if (printLogs) {
              System.out.println("field not in KTATTRIBUT table - add to DB");
            }
            if (withLevinshtainDistance) {
              if (printLogs) {
                System.out.println("WITHLEVINSHTAINDISTANCE - attr: " + withLevinshtainDistance);
              }
              if (!HebrewSpellChecker.isSameWordInDBInKTATTRIBUTETable(
                  hebrewSubject, hebrewField, levinshtainDistance, conn)) {

                insertAttribute(
                    englishSubject,
                    englishField,
                    hebrewField,
                    type_name,
                    attributeIndex,
                    CURRENT_DATE,
                    keyType,
                    IO_MODE,
                    conn);
              }
            } else {
              if (printLogs) {
                System.out.println("not with lev attr");
              }
              insertAttribute(
                  englishSubject,
                  englishField,
                  hebrewField,
                  type_name,
                  attributeIndex,
                  CURRENT_DATE,
                  keyType,
                  IO_MODE,
                  conn);
            }
          }
        }
      }
      // ****************************************************************************************/
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
