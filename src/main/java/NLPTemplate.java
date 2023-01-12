import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.Pair;
import org.apache.commons.lang3.StringEscapeUtils;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NLPTemplate {
    String sentence;
    String subject;
    String field;
    String fieldType;

    public NLPTemplate(String sen) throws IOException {
        sentence = sen;
        subject = "";
        field = "";
        fieldType = "";
    }

    private static List<String> readFileAsListString(String fileName) throws Exception {
        Path path = Paths.get(fileName);
        List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
        return allLines;
    }

    private String translateSentenceFromHebrewToEnglish(String sentence) throws IOException {
        Translator translate = new Translator(sentence);
        String englishTranslation = translate.translate("he", "en", sentence);
        String sentenceToPos = StringEscapeUtils.unescapeHtml4(englishTranslation);
        return sentenceToPos;
    }

    private List<Integer> getCharsIndexes(String sentence, char ch) {
        List<Integer> charsIndexes = new ArrayList<>();
        for (int i = 0; i < sentence.length(); i++) {
            if (sentence.charAt(i) == ch) {
                charsIndexes.add(i);
            }
        }
        return charsIndexes;
    }

    private void replaceBetweenTwoCharsFromTwoIndexesInSentence(String sentence, List<Integer> charsIndexes, char oldCh, char newCh){
        String substring;
        String newSubstring;
        for (int i = 0; i < charsIndexes.size(); i += 2) {
            int start = charsIndexes.get(i) + 1;
            int end = charsIndexes.get(i + 1);
            substring = sentence.substring(start, end);
            newSubstring = substring.replace(oldCh, newCh);
            sentence = sentence.substring(0, start) + newSubstring + sentence.substring(end);
        }
    }

    private String getSentenceToRecognizeTemplates(String sentence) throws IOException {
        String sentenceToPos = translateSentenceFromHebrewToEnglish(sentence);
        List<Integer> charsIndexes = getCharsIndexes(sentenceToPos, '"');
        replaceBetweenTwoCharsFromTwoIndexesInSentence(sentenceToPos, charsIndexes, ' ', '_');
        return sentenceToPos;
    }

    private boolean containsUnderscore(String word) {
        return word.matches(".*_.*");
    }

    private boolean findSubjectsAndFields(List<CoreLabel> tokens) {
        List<Pair<String, String>> subjectsAndFields = new ArrayList<Pair<String, String>>();
        for (int i = 0; i < tokens.size(); i++) {

            CoreLabel token = tokens.get(i);

            // If the token is a noun and is followed by a possessive case marker, it is a subject
            //שדה של נושא
            if (token.tag().startsWith("N") && i < tokens.size() - 2 && tokens.get(i + 1).tag().equals("POS")) {
                subject = token.word();
                field = tokens.get(i + 2).word();
                subjectsAndFields.add(new Pair<>(subject, field));
            } else if (token.tag().startsWith("N")) {
                subject = token.word();
                subjectsAndFields.add(new Pair<>(subject, null));
            } else if (containsUnderscore(token.word())) {
                field = "";
                subject = "";
            } else if (token.tag().startsWith("JJ")) {
                field = token.word();
                if (subjectsAndFields.get(subjectsAndFields.size() - 1).second != null) {
                    subjectsAndFields.add(new Pair<>(subjectsAndFields.get(subjectsAndFields.size() - 1).first, field));
                } else {
                    subjectsAndFields.get(subjectsAndFields.size() - 1).setSecond(field);
                }
            }
        }
        return false;
    }

    private void readNLPTemplate(String sentence) throws IOException {
        // Set up the Stanford CoreNLP pipeline
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        String sentenceToPos = getSentenceToRecognizeTemplates(sentence);

        System.out.println(sentenceToPos);

        CoreDocument doc = new CoreDocument(sentenceToPos);
        pipeline.annotate(doc);

        findSubjectsAndFields(doc.tokens());

        if (subject != "") {
            // Print the subject and field
            System.out.println("Subject: " + subject);
            System.out.println("Field: " + field);
        }
    }

    public static void main(String[] args) throws IOException {

        List<String> data = new ArrayList<String>();
        try {
            data = readFileAsListString("sentences.txt");
            System.out.println(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NLPTemplate t;
        for (int i = 0; i < data.size(); i++) {
            try {
                t = new NLPTemplate(data.get(i));
                System.out.println(data.get(i));
                 t.readNLPTemplate(data.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}