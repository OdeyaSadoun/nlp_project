import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FieldNameExtractor {
    public static void main(String[] args) {
        // Define the input sentence
        //  String sentence = "If the daily_supplier_credit_ratio of the last_financial_report is greater than 125, then add 1 to the counter_conditions_for_the_supplier_for_continuing_activity of the last_financial_report";
        String sentence = "If a customer's 4th_quarter_turnover is greater than a customer's 3rd_quarter_turnover and also a customer's 3rd_quarter_turnover is greater than a customer's 2nd_quarter_turnover and also a customer's 2nd_quarter_turnover is greater than a customer's 1st_quarter_turnover, then open an alert with an alert_code of \"153\", description for \"increase in turnover for an employee in - last 4 quarters\"";
        // Create a StanfordCoreNLP object with the required annotators
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Annotate the sentence
        Annotation document = new Annotation(sentence);
        pipeline.annotate(document);

        // Iterate over the annotated sentences
        for (CoreMap annotatedSentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            // Get the tokens of the sentence
            List<CoreLabel> tokens = annotatedSentence.get(CoreAnnotations.TokensAnnotation.class);

            // Initialize variables for subject and field names
//            String subject = null;
            List<String> fieldNames = new ArrayList<>();
            List<String> subjects = new ArrayList<>();
            // Iterate over the tokens
            for (int i = 0; i < tokens.size(); i++) {
                CoreLabel token = tokens.get(i);

                // Modify and add your custom pattern matching logic here
                // Example: Check for possessive relationship
                if (i > 0 && i < tokens.size() - 1) {
                    CoreLabel previousToken = tokens.get(i - 1);
                    CoreLabel nextToken = tokens.get(i + 1);

                    if (token.originalText().equals("of")) { //field of subject

                        if (nextToken.word().matches("(?i)(a|an|the|all)")) {
                            nextToken = tokens.get(i + 2);
                        }
                        if (subjects.contains(nextToken.originalText())) {
                            //changed when insert to db
                            fieldNames.add(previousToken.originalText());
                        } else if (isSubject(nextToken)) {
                            subjects.add((nextToken.originalText()));
                        }
                    } else if (token.originalText().equals("'s")) { //subject's field

                        if (subjects.contains(previousToken.originalText())) {
                            //changed when insert to db
                            fieldNames.add(nextToken.originalText());
                        } else if (isSubject(previousToken)) {
                            subjects.add((previousToken.originalText()));
                        }
                    }

                    // Add more custom pattern matching logic as needed for your specific use case
                    // ...

                }
            }

                // Print the extracted subject and field names
                System.out.println("Subject: " + subjects);
                System.out.println("Field Names: " + fieldNames);
            }
        }

    private static boolean isSubject(CoreLabel token) {
        // Implement your logic to identify the subject based on contextual cues
        // This can involve various heuristics, rules, or machine learning techniques

        // Example: Check if the token is a noun or a named entity
        String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
        String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
        return pos.startsWith("NN") || ner.equals("PERSON");
    }
}
