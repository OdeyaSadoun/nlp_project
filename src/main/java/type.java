import java.sql.*;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
public class type {

    public static void main(String[] args) {
        String inputString = "date of birth";
        String meaningType = identifyMeaningType(inputString);
        System.out.println("Meaning type of '" + inputString + "' is " + meaningType);
    }

    public static String identifyMeaningType(String inputString) {
        // Set up CoreNLP pipeline
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Annotate the input string
        Annotation doc = new Annotation(inputString);
        pipeline.annotate(doc);

        // Extract the part of speech tags of the input string
        List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
            for (CoreMap token : tokens) {
                String posTag = token.get(PartOfSpeechAnnotation.class);
                if (posTag.equals("CD")) {
                    return "date";
                }
                if (posTag.equals("CD")) {
                    return "bool";
                }
                if (posTag.equals("CD")) {
                    return "int";
                }
                if (posTag.equals("CD")) {
                    return "string";
                }
            }
        }
        return null;

    }


}
