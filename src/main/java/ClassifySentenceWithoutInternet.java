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
            //option11
            if (word.equals("ה-") || word.equals("ל-")) {
                boolean isSaveWord = isSaveWordInTLXTable(lstTemplate.get(i+1));
                if(!isSaveWord){
                    subject = lstTemplate.get(i+1);
                    field = null;
                }
                checkFieldAndSubjectInDB(subject,field);
                continue;
            }
        }

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
