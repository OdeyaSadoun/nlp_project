import java.util.*;

public class GetType {
    private static final Map<String, String> labelingMap = new HashMap<>();

    static {
        labelingMap.put("age", "Long");
        labelingMap.put("number", "Long");
        labelingMap.put("amount", "Long");
        labelingMap.put("cost", "Double");
        labelingMap.put("price", "Double");
        labelingMap.put("profit", "Double");
        labelingMap.put("sum", "Double");
        labelingMap.put("salary", "Double");
        labelingMap.put("date", "DateTime");
        labelingMap.put("name", "Char");
    }

    public static String getLabel(String word) {
        if (labelingMap.containsKey(word)) {
            return labelingMap.get(word);
        } else {
            return "Bool";
        }
    }

    public static void main(String[] args) {
        String word1 = "age";
        String label1 = getLabel(word1);
        System.out.println(word1 + " : " + label1);

        String word2 = "price";
        String label2 = getLabel(word2);
        System.out.println(word2 + " : " + label2);
    }

}
