import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.CoreMapExpressionExtractor;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class HAN {

    public static void main(String[] args) {

        // Create StanfordCoreNLP object with HAN annotator
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, han");
        props.setProperty("han.model", "edu/stanford/nlp/models/han/gigaword_han.ontonotes.crf.ser.gz");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Sentence to analyze
        String sentence = "The technology industry is growing rapidly with new advancements in machine learning and natural language processing.";

        // Create an empty Annotation just with the given sentence
        edu.stanford.nlp.pipeline.Annotation document = new edu.stanford.nlp.pipeline.Annotation(sentence);

        // Run all Annotators on this text
        pipeline.annotate(document);

        // Iterate over the sentences in the document
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sent : sentences) {

            // Get the subjects and fields using HAN annotations
//            List<String> subjects = sent.get(CoreAnnotations.HANSubjectsAnnotation.class);
//            List<String> fields = sent.get(CoreAnnotations.HANFieldsAnnotation.class);

            // Print out the identified subjects and fields
//            System.out.println("Subjects: " + subjects);
//            System.out.println("Fields: " + fields);
        }
    }
}
