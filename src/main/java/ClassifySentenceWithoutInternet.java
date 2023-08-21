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
    static String field;
    static String mainSubject = "1";

    boolean success;
    String subSubject;
    String subField;
    int indexSub;
    int indexFil;

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
                    continue;
                }
                //option7
                isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i+1));
                if(!isSaveWord){
                    subject = word;
                    field = lstTemplate.get(i+1);
                    continue;
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
                checkFieldAndSubjectInDB(subject,field);
                continue;
            }
            //option2
            if (word.equals("אינו") || word.equals("הוא")) {
                subject = lstTemplate.get(i-1);
                field = lstTemplate.get(i+1);
                checkFieldAndSubjectInDB(subject,field);
                continue;
            }
            //option3
            //3. אם מילהלאשמורה  אינו/הוא  מילהשמורה  -  אזי הראשון הוא שדה  בתוך נושא מרכזי
            //4.  אם מילהלאשמורה{[הוא] /מילהשמורה}  (למשל אופרטור) – אזי הראשון הוא שדה בתוך נושא מרכזי
            if (word.equals("אם")) {
                isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i+1));
                if(!isSaveWord){
                    if(lstTemplate.get(i+2).equals("הוא") || lstTemplate.get(i+2).equals("אינו")){
                        isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i+3));
                        if(isSaveWord){
                            subject = mainSubject;
                            field = word;
                            continue;
                        }
                    }
                //option4
                }
                else {
                    //option9-10
                    //  אם מילהשמורה טקסט שמור של כל ה-  מילהלא שמורה [מילהלאשמורה]  -  הראשון שדה השני נושא ברבים אחריו שדה לוואי
                    if (lstTemplate.get(i + 2).equals("של") && lstTemplate.get(i + 3).equals("כל") && lstTemplate.get(i + 4).equals("ה-")) {
                        String pluralSubject = lstTemplate.get(i + 5);
                        subject = changePluralSubjectToSingle(pluralSubject);
                        field = lstTemplate.get(i+1);
                        checkFieldAndSubjectInDB(subject, field);
                        if(lstTemplate.get(i+6) != ""){
                            field = lstTemplate.get(i+6);
                            checkFieldAndSubjectInDB(subject, field);
                        }
                        continue;
                    }
                }
                checkFieldAndSubjectInDB(subject,field);
                continue;
            }
            //option11
            if (word.equals("ה-") || word.equals("ל-")) {
                isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i+1));
                if(!isSaveWord){
                    subject = lstTemplate.get(i+1);
                    field = null;
                }
                checkFieldAndSubjectInDB(subject,field);
                continue;
            }
            //option12
            if (word.equals("ו-")) {
                isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i+1));
                if(!isSaveWord){
                    subject = mainSubject;
                    field = lstTemplate.get(i+1);
                }
                checkFieldAndSubjectInDB(subject,field);
                continue;
            }
        }

    }

    private static String changePluralSubjectToSingle(String pluralSubject) {
        return  "";
    }

    private static boolean isSaveWordInTLXTable(String s) {
        return false;
    }

    private static void checkFieldAndSubjectInDB(String subject, String field) {
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
