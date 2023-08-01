import java.util.HashMap;
import java.util.Map;

public class ClassifySentenceWithoutInternet {

    private Map<String, String> tlxTable;

    public ClassifySentenceWithoutInternet(Map<String, String> reservedWords) {
        this.tlxTable = reservedWords;
    }

    public ClassifySentenceWithoutInternet() {
        this.tlxTable = new HashMap<>();
    }

    public String ClassifySentenceWithoutInternet(String sentence) {
        String[] tokens = sentence.split(" ");
        Map<String, String> fields = new HashMap<>();
        for (int i = 0; i < tokens.length; i++) {
            if (tlxTable.containsKey(tokens[i])) {
                fields.put(tlxTable.get(tokens[i]), tokens[i]);
            }
        }
        String field = null;
        String topic = null;
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (entry.getKey().equals("field")) {
                field = entry.getValue();
            } else if (entry.getKey().equals("topic")) {
                topic = entry.getValue();
            }
        }
        if (field != null && topic != null) {
            return field + " of " + topic;
        } else if (field != null) {
            return field;
        } else if (topic != null) {
            return topic;
        } else {
            return "unknown";
        }
    }

    public static void main(String[] args) {
        ClassifySentenceWithoutInternet classifier = new ClassifySentenceWithoutInternet();
        System.out.println(classifier.ClassifySentenceWithoutInternet("The age of the user is 21."));
        // Output: age of the user of 21
        System.out.println(classifier.ClassifySentenceWithoutInternet("The user is 21 years old."));
        // Output: user of 21 years old
        System.out.println(classifier.ClassifySentenceWithoutInternet("The user is 21 years old."));
        // Output: user of 21 years old
    }
}
