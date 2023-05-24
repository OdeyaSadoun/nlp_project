import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;

import java.util.*;

public class GetType {

    private static final Map<String, Class<?>> wordTypes = new HashMap<>();

    static {
        wordTypes.put("age", Integer.class);
        wordTypes.put("date of birth", Date.class);
        wordTypes.put("boolean", Boolean.class);
        wordTypes.put("char", Character.class);
        wordTypes.put("string", String.class);
    }

    //private static final POSModel posModel = new POSModel("en-pos-maxent.bin");
    //private static final SentenceDetectorME sentenceDetector = new SentenceDetectorME(new opennlp.tools.sentdetect.SentenceModel("en-sent.bin"));

    public static Class<?> getWordType(String word) {
        List<Class<?>> possibleTypes = new ArrayList<>();

        // Extract features from the word
        /**
         * String[] spans = sentenceDetector.sentDetect(word);
         *         if (spans.length > 0) {
         *             String span = spans[0];
         *             String[] tokens = word.substring(span.getStart(), span.getEnd()).split(" ");
         *             for (String token : tokens) {
         *                 String posTag = POSTaggerME.getTag(posModel, token);
         *                 possibleTypes.add(getPossibleType(posTag));
         *             }
         *         }
         *
         */

        // Return the most likely type
        // return possibleTypes.stream().max(Comparator.comparingInt(Class::getSimpleName)).get();
        return null;
    }

    private static Class<?> getPossibleType(String posTag) {
        switch (posTag) {
            case "NN":
                return Integer.class;
            case "NNP":
                return String.class;
            case "NNS":
                return List.class;
            case "NP":
                return Map.class;
            case "PRP":
                return Boolean.class;
            case "VBD":
                return Date.class;
            default:
                return Object.class;
        }
    }
}
