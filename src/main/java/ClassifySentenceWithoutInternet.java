import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassifySentenceWithoutInternet {

    static String sentence;
    static String subject;
    static String pluralSubject;
    static String field;
    static String mainSubject = "mainSubject";
    static String dataType = "Bool"; //default

    public static void readTemplate(){
        String[] template = sentence.split(" ");
        List<String> lstTemplate = Arrays.asList(template);
        try {
            findSubjectAndField(lstTemplate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void findSubjectAndField(List<String> lstTemplate) throws SQLException {
        for (int i = 0; i < lstTemplate.size(); i++) {

            String word = lstTemplate.get(i);
            System.out.println(word);

            boolean isSaveWord = isSaveWordInTLXTable(word);

            if (!isSaveWord){
                //option5-6
                if(lstTemplate.get(i+1).equals("קיים") ||
                        lstTemplate.get(i+1).equals("לא") && lstTemplate.get(i+2).equals("קיים")){
                    subject = word;
                    field = null;
                    checkFieldAndSubjectInDB(subject,field, dataType);
                    continue;
                }
                //option7
                isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i+1)); //the next word
                if(!isSaveWord){
                    subject = word;
                    field = lstTemplate.get(i+1);
                    checkFieldAndSubjectInDB(subject,field, dataType);
                    continue;
                }
                //option8a
                //מילהלאשמורה [טקסט שמור] של כל מילהלאשמורה -  שדה והשני זה נושא
                if (lstTemplate.get(i + 1).equals("של") && lstTemplate.get(i + 2).equals("כל")) {
                    isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i + 3));
                    if(!isSaveWord){
                        subject = lstTemplate.get(i+3);
                        field = word;
                        checkFieldAndSubjectInDB(subject,field, dataType);
                        continue;
                    }
                }

            }
            //option1
            if(word.equals("של")){
                field = lstTemplate.get(i-1);
                if(lstTemplate.get(i+1).equals("כל")){
                    subject = lstTemplate.get(i+2);
                }
                else{
                    subject = lstTemplate.get(i+1);
                }
                checkFieldAndSubjectInDB(subject,field, dataType);
                continue;
            }
            //option2
            if (word.equals("אינו") || word.equals("הוא")) {
                isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i - 1));
                if(!isSaveWord){
                    subject = lstTemplate.get(i-1);
                    isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i + 1));
                    if(!isSaveWord){
                        field = lstTemplate.get(i+1);
                        checkFieldAndSubjectInDB(subject,field, dataType);
                        continue;
                    }
                }
            }
            //option3
            //3. אם מילהלאשמורה  אינו/הוא  מילהשמורה  -  אזי הראשון הוא שדה  בתוך נושא מרכזי
            //4.  אם מילהלאשמורה{[הוא] /מילהשמורה}  (למשל אופרטור) – אזי הראשון הוא שדה בתוך נושא מרכזי
            if (word.equals("אם")) {
                isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i + 1));
                if (!isSaveWord) {
                    if (lstTemplate.get(i + 2).equals("הוא") || lstTemplate.get(i + 2).equals("אינו")) {
                        isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i + 3));
                        if (isSaveWord) {
                            subject = mainSubject;
                            field = lstTemplate.get(i+1);
                            checkFieldAndSubjectInDB(subject,field, dataType);
                            continue;
                        }
                    }
                    //option8b
                    //מילהלאשמורה [טקסט שמור] של כל מילהלאשמורה -  שדה והשני זה נושא  (אם)8
                    if (lstTemplate.get(i + 2).equals("של") && lstTemplate.get(i + 3).equals("כל")) {
                        isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i + 4));
                        if(!isSaveWord){
                            subject = lstTemplate.get(i+4);
                            field = lstTemplate.get(i+1);
                            checkFieldAndSubjectInDB(subject,field, dataType);
                            continue;
                        }
                    }
                    //option4
                    //4.  אם מילהלאשמורה{[הוא] /מילהשמורה}  (למשל אופרטור) – אזי הראשון הוא שדה בתוך נושא מרכזי
                    subject = mainSubject;
                    field = lstTemplate.get(i+1);
                    checkFieldAndSubjectInDB(subject,field, dataType);
                    continue;
                } else {
                    //option9-10
                    //  אם מילהשמורה טקסט שמור של כל ה-  מילהלא שמורה [מילהלאשמורה]  -  הראשון שדה השני נושא ברבים אחריו שדה לוואי
                    if (lstTemplate.get(i + 2).equals("של") && lstTemplate.get(i + 3).equals("כל") && lstTemplate.get(i + 4).equals("ה-")) {
                        pluralSubject = lstTemplate.get(i + 5);
                        subject = changePluralSubjectToSingle(pluralSubject);
                        field = lstTemplate.get(i + 1);
                        checkFieldAndSubjectInDB(subject, field, dataType);

                        if (lstTemplate.get(i + 6) != "") {
                            isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i + 6));
                            if (!isSaveWord) {
                                field = lstTemplate.get(i + 6);
                                checkFieldAndSubjectInDB(subject, field, dataType);
                            }
                        }
                        continue;
                    }
                    //10 אם מילהשמורה טקסט שמור [מספר בעברית]   מילהלאשמורה [מילהלאשמורה]   -  הראשון שדה השני נושא ברבים אחריו שדה לוואי ו
                    if (isNumericNumber(lstTemplate.get(i + 2))) {
                        isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i + 3));
                        if (!isSaveWord) {
                            pluralSubject = lstTemplate.get(i + 3);
                            subject = changePluralSubjectToSingle(pluralSubject);
                            field = lstTemplate.get(i + 1);
                            checkFieldAndSubjectInDB(subject, field, dataType);

                            if (lstTemplate.get(i + 4) != "") {
                                isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i + 6));
                                if (!isSaveWord) {
                                    field = lstTemplate.get(i + 4);
                                    checkFieldAndSubjectInDB(subject, field, dataType);
                                }
                            }
                            continue;
                        }
                    }
                    String pluralSubject = lstTemplate.get(i + 5);
                    subject = changePluralSubjectToSingle(pluralSubject);
                    field = lstTemplate.get(i + 1);
                    checkFieldAndSubjectInDB(subject, field, dataType);
                    if (lstTemplate.get(i + 6) != "") {
                        field = lstTemplate.get(i + 6);
                        checkFieldAndSubjectInDB(subject, field, dataType);
                    }
                    continue;
                }
            
//                checkFieldAndSubjectInDB(subject,field);
//                continue;
            }
            //option11
            if (word.equals("ה-") || word.equals("ל-")) {
                isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i+1));
                if(!isSaveWord){
                    subject = lstTemplate.get(i+1);
                    field = null;
                    checkFieldAndSubjectInDB(subject,field, dataType);
                    continue;
                }
            }
            //option12
            if (word.equals("ו-")) {
                isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i+1));
                if(!isSaveWord){
                    subject = mainSubject;
                    field = lstTemplate.get(i+1);
                    checkFieldAndSubjectInDB(subject,field, dataType);
                }
            }
        }
    }

    private static boolean isNumericNumber(String s) {
    // Check if the string is empty or null.
        if (s == null || s.isEmpty()) {
            return false;
        }

    // Check if the string contains only digits.
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

    // Check if the string is a Hebrew number.
        if (s.equals("אחת") || s.equals("שתיים") || s.equals("שלוש") || s.equals("ארבע") || s.equals("חמש") || s.equals("שש") || s.equals("שבע") || s.equals("שמונה") || s.equals("תשע") || s.equals("עשר")
        || s.equals("אחד") || s.equals("שניים") || s.equals("שלושה") || s.equals("ארבעה") || s.equals("חמישה") || s.equals("שישה") || s.equals("שיבעה") || s.equals("תשעה") || s.equals("עשרה")) {
            return true;
        }

    // If the string is not empty, does not contain only digits, and is not a Hebrew number, it is not a number.
        return false;
    }

    public static String changePluralSubjectToSingle(String pluralSubject) {
        // Check if the word ends with a plural suffix such as "ות" or "ים".
        if (pluralSubject.endsWith("ות") || pluralSubject.endsWith("ים")) {
            // If so, remove the plural suffix.
            pluralSubject = pluralSubject.substring(0, pluralSubject.length() - 2);
        }

        // Check if the final letter is a Hebrew final letter.
        if (pluralSubject.endsWith("מ") || pluralSubject.endsWith("נ") || pluralSubject.endsWith("פ") || pluralSubject.endsWith("צ") || pluralSubject.endsWith("כ")) {
            // If so, replace the final letter with its corresponding singular letter.
            char lastChar = pluralSubject.charAt(pluralSubject.length() - 1);
            switch (lastChar) {
                case 'מ':
                    pluralSubject = pluralSubject.substring(0, pluralSubject.length() - 1) + 'ם';
                    break;
                case 'נ':
                    pluralSubject = pluralSubject.substring(0, pluralSubject.length() - 1) + 'ן';
                    break;
                case 'פ':
                    pluralSubject = pluralSubject.substring(0, pluralSubject.length() - 1) + 'ף';
                    break;
                case 'צ':
                    pluralSubject = pluralSubject.substring(0, pluralSubject.length() - 1) + 'ץ';
                    break;
                case 'כ':
                    pluralSubject = pluralSubject.substring(0, pluralSubject.length() - 1) + 'ך';
                    break;
            }
        }

        return pluralSubject;
    }


    private static boolean isSaveWordInTLXTable(String token) throws SQLException {
        // Connect to the SQL Server database.
        String jdbcUrl = "jdbc:sqlserver://LOCALHOST\\SQLEXPRESS:1433;databaseName=logistcourse1;SelectMethod=Cursor";
        String jdbcDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        // Database credentials
        String username = "logistcourse1";
        String password = "logistcourse1";
        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        // Create a statement.


        PreparedStatement statement = connection.prepareStatement("SELECT token FROM ZTRLPTLX WHERE token = ?");

        // Set the parameter.
        statement.setString(1, token);

        // Execute the query.
        ResultSet resultSet = statement.executeQuery();

        // Check if the word is in the table.
        return resultSet.next();
    }



    private static void checkFieldAndSubjectInDB(String subject, String field, String dataType) {
        String hebrewField = field;
        String englishField;
        String hebrewSubject = subject;
        String englishSubject;
        if (hebrewField == null) {
            englishField = null;
        } else {
            englishField = translateFromHebrewToEnglishWithoutInternet(hebrewField);
        }
        englishSubject = translateFromHebrewToEnglishWithoutInternet(hebrewSubject);
        SaveToDatabase.addSubjectToDatabase(hebrewField, englishField, hebrewSubject, englishSubject);
    }

    private static String translateFromHebrewToEnglishWithoutInternet(String hebrewWord) {
        return "";
    }

    public static void main(String[] args) {
    }
}
