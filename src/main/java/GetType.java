import java.util.*;

public class GetType {
    private static final Map<String, String> labelingMap = new HashMap<>();

    static {
        labelingMap.put("age", "Long");
        labelingMap.put("number", "Long");
        labelingMap.put("amount", "Long");
        labelingMap.put("cycle", "Long");
        labelingMap.put("counter", "Long");
        labelingMap.put("count", "Long");
        labelingMap.put("ratio", "Double");
        labelingMap.put("cost", "Double");
        labelingMap.put("price", "Double");
        labelingMap.put("profit", "Double");
        labelingMap.put("sum", "Double");
        labelingMap.put("salary", "Double");
        labelingMap.put("date", "DateTime");
        labelingMap.put("name", "Char");
    }

    public static String getLabel(String word) {
        String[] parts = word.split("_");
        for (String part : parts) {
            if (labelingMap.containsKey(part)) {
                return labelingMap.get(part);
            }
        }
        return "Bool";

    }

    public static void main(String[] args) {
        String word1 = "age_1";
        String label1 = getLabel(word1);
        System.out.println(word1 + " : " + label1);

        String word2 = "price";
        String label2 = getLabel(word2);
        System.out.println(word2 + " : " + label2);
    }

}