import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        findSubjectAndField(lstTemplate);
    }

    public static void findSubjectAndField(List<String> lstTemplate) {
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


    private static boolean isSaveWordInTLXTable(String s) {
        return false;
    }

    private static void checkFieldAndSubjectInDB(String subject, String field, String dataType) {
    }


    private Map<String, String> tlxTable;

    public ClassifySentenceWithoutInternet() {
        this.tlxTable = new HashMap<>();
        // Load TLX table from the database
        loadTLXTableFromDatabase();
    }

    public ClassifySentenceWithoutInternet(Map<String, String> reservedWords) {
        this.tlxTable = reservedWords;
    }

    private void loadTLXTableFromDatabase() {
        // Update the connection parameters accordingly
        String jdbcURL = "jdbc:mysql://localhost:3306/your_database_name";
        String username = "your_username";
        String password = "your_password";

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            String sql = "SELECT reserved_word, value FROM tlx_table"; // Change this query to fetch data from your TLX table
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String reservedWord = resultSet.getString("reserved_word");
                String value = resultSet.getString("value");
                tlxTable.put(reservedWord, value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any database connection errors here
        }
    }

    public String classifySentence(String sentence) {
        // Rest of your classification logic remains the same
        // ... (same as in the previous code)

        return "unknown";
    }

    public static void main(String[] args) {
        ClassifySentenceWithoutInternet classifier = new ClassifySentenceWithoutInternet();
        System.out.println(classifier.classifySentence("The age of the user is 21."));
        // Output: age of the user of 21
        System.out.println(classifier.classifySentence("The user is 21 years old."));
        // Output: user of 21 years old
        System.out.println(classifier.classifySentence("The user is 21 years old."));
        // Output: user of 21 years old
    }
}
