import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;

import java.util.Properties;

public class NER {
    public static void main(String[] args) {
        // Initialize the Stanford CoreNLP pipeline
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
        props.setProperty("ner.model", "../to/model.ser.gz");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Annotate the text
        String text = "Odeya is blue";
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        // Extract entities
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                System.out.println("Token: " + token.originalText() + ", Entity: " + ner);
            }
        }

        // Extract relationships
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            tree.pennPrint();
        }
    }
}