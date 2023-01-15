import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.Pair;
import java.util.List;
import java.util.Properties;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.commons.lang3.StringEscapeUtils;


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

    private boolean containsUnderscore(String word) {
        return word.matches(".*_.*");
    }
    private boolean findSubjectsAndFields(List<CoreLabel> tokens) {

        List<Pair<String, String>> subjectsAndFields = new ArrayList<Pair<String, String>>();
        for (int i = 0; i < tokens.size(); i++) {

            CoreLabel token = tokens.get(i);

            if(!token.word().contains("IGNORE")) {
                // If the token is a noun and is followed by a possessive case marker, it is a subject
                //שדה של נושא
                if (token.tag().startsWith("N") && i < tokens.size() - 2 && tokens.get(i + 1).tag().equals("POS")) {
                    subject = token.word();
                    field = tokens.get(i + 2).word();
                    i += 2; // to over the current field
                    subjectsAndFields.add(new Pair<>(subject, field));
                    System.out.println(subjectsAndFields.toString());

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
        }
        return false;
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
    private String getCleanSentenceForAnalysis(String sentence) throws IOException {
        //remove some words that belongs to the template:
        String sentenceAfterClean = markNotNecessaryTemplatesWords(sentence);
        //translate the sentence:
        String sentenceToTranslate = translateSentenceFromHebrewToEnglish(sentenceAfterClean);
        //find the indexes of " in order to add underscore between the words that in "":
        List<Integer> charsIndexes = getCharsIndexes(sentenceToTranslate, '"');
        //replace the space between the words that in "" with underscore:
        replaceBetweenTwoCharsFromTwoIndexesInSentence(sentenceToTranslate, charsIndexes, ' ', '_');
        //remove the stop words from the sentence:
        sentenceToTranslate = removeStopWords(sentenceToTranslate);
        return sentenceToTranslate;
    }
    private String markNotNecessaryTemplatesWords(String sentence){

        for (String word : sentence.split(" ")) {
            if (wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_VALUE_OPERATOR, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_COMPARISON_OPERATOR, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_NUMERIC_OPERATOR, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_SUM_OPERATOR, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_EDGE_OPERATOR, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_EXISTENCE_OPERATOR, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_DATE_OPERATOR, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_TIME_EXPRESSION, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_SEVERITY_DEGREE, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_TYPE, word))

                sentence = sentence.replace(word, word + "_IGNORE");
        }
        return sentence;
    }
    private static List<String> readFileAsListString(String fileName) throws Exception {
        Path path = Paths.get(fileName);
        List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
        return allLines;
    }
    private void readNLPTemplate(String sentence) throws IOException {
        // Set up the Stanford CoreNLP pipeline
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        String sentenceToNLP = getCleanSentenceForAnalysis(sentence);

        System.out.println(sentenceToNLP);

        CoreDocument doc = new CoreDocument(sentenceToNLP);
        pipeline.annotate(doc);

        //NLP
        findSubjectsAndFields(doc.tokens());

        if (subject != "") {
            // Print the subject and field
            System.out.println("Subject: " + subject);
            System.out.println("Field: " + field);
        }
    }
    private String removeStopWords(String sentence) throws IOException {
        String[] words = sentence.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!LogistConstants.ENGLISH_STOPWORDS.contains(word.toLowerCase())) {
                result.append(word + " ");
            }
        }
        return result.toString().trim();
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
    private String translateSentenceFromHebrewToEnglish(String sentence) throws IOException {
        Translator translate = new Translator(sentence);
        String englishTranslation = translate.translate("he", "en", sentence);
        //fix the html's signs:
        String sentenceToNLP = StringEscapeUtils.unescapeHtml4(englishTranslation);
        return sentenceToNLP;
    }
    private boolean wordExistsInConstantsList(List<String> list, String str) {
        return list.contains(str);
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