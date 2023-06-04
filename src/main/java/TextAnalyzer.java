import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TextAnalyzer {
    private static final String SUBJECT_REGEX = "(?i)\\b(?:an?\\s+|the\\s+)?([A-Za-z\\s]+)'?s?\\b";
    private static final String FIELD_REGEX = "\"([^\"]+)\"";

    public static void main(String[] args) {
        List<String> sentences = getExampleSentences();

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        for (String sentence : sentences) {
            Annotation annotation = new Annotation(sentence);
            pipeline.annotate(annotation);

            List<CoreMap> sentencesList = annotation.get(CoreAnnotations.SentencesAnnotation.class);
            for (CoreMap sentenceMap : sentencesList) {
                List<CoreLabel> tokens = sentenceMap.get(CoreAnnotations.TokensAnnotation.class);
                List<String> words = new ArrayList<>();
                for (CoreLabel token : tokens) {
                    String word = token.get(CoreAnnotations.TextAnnotation.class);
                    words.add(word);
                }

                String subject = extractSubject(words);
                String field = extractField(words);

                if (subject != null && field != null) {
                    String output = "Subject: " + subject + ", Field: " + field;
                    System.out.println(output);
                }
            }
        }
    }

    private static String extractSubject(List<String> words) {
        String subject = null;
        String previousToken = null;
        for (String word : words) {
            if (previousToken != null && previousToken.matches(SUBJECT_REGEX)) {
                subject = previousToken.trim();
                break;
            }
            previousToken = word;
        }
        return subject;
    }

    private static String extractField(List<String> words) {
        String field = null;
        for (String word : words) {
            if (word.matches(FIELD_REGEX)) {
                field = word.replaceAll("\"", "");
                break;
            }
        }
        return field;
    }

    private static List<String> getExampleSentences() {
        // Return your list of example sentences here
        List<String> sentences = new ArrayList<>();
        sentences.add("So a bottle is orange");
        sentences.add("So cancel the employee who \"resigned from the job\"");
        sentences.add("So add 5 to an employee's salary");
        // ... add more sentences

        return sentences;
    }
}
