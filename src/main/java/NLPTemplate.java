import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
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

    public NLPTemplate(String sen) {
        sentence = sen;
        subject = "";
        field = "";
        fieldType = "";
    }

    /*****************************************************************************************************/
    /*Analysis*/

    /**
     * Find all the subjects and fields in the sentence.
     *
     * @param tokens list of tokens from the sentence.
     * @return List of Pairs that have in every pair subject and field.
     */
    private List<Pair<String, String>> findSubjectsAndFields(List<CoreLabel> tokens) {

        /*מוצא שדה ונושא*/
        List<Pair<String, String>> subjectsAndFields = new ArrayList<Pair<String, String>>();
        for (int i = 0; i < tokens.size(); i++) {

            CoreLabel token = tokens.get(i);

            if (!token.word().contains("IGNORE")) {
                // If the token is a noun and is followed by a possessive case marker, it is a subject
                //שדה של נושא
                if (containsUnderscore(token.word())) {
                    field = "";
                    subject = "";
                }else if(token.word().equals("of") && tokens.get(i - 1).tag().startsWith("N")){
                    if(tokens.get(i + 1).word().equals("a") || tokens.get(i + 1).word().equals("an") || tokens.get(i + 1).word().equals("the")){
                        subject = tokens.get(i + 2).word();
                        field = tokens.get(i - 1).word();
                        i = i+2;
                        if(subjectsAndFields.get(subjectsAndFields.size() - 1).first.equals(field)){
                            subjectsAndFields.get(subjectsAndFields.size() - 1).setFirst(subject);
                            subjectsAndFields.get(subjectsAndFields.size() - 1).setSecond(field);
                        }
                        else
                        subjectsAndFields.add(new Pair<>(subject, field));
                    }
                    else {
                        subject = tokens.get(i + 1).word();
                        field = tokens.get(i - 1).word();
                        i = i + 1;

                        if (subjectsAndFields.get(subjectsAndFields.size() - 1).first == field) {
                            subjectsAndFields.get(subjectsAndFields.size() - 1).setFirst(subject);
                            subjectsAndFields.get(subjectsAndFields.size() - 1).setSecond(field);
                        } else
                            subjectsAndFields.add(new Pair<>(subject, field));

                    }
                } else if (token.tag().startsWith("N") && i < tokens.size() - 2 && tokens.get(i + 1).tag().equals("POS")) {
                    if(tokens.get(i + 2).tag().equals("POS")){
                        subject = token.word();
                        field = tokens.get(i + 3).word();
                        i += 3; // to over the current field
                        subjectsAndFields.add(new Pair<>(subject, field));
                        System.out.println(subjectsAndFields.toString());
                    }
                    else {
                        subject = token.word();
                        field = tokens.get(i + 2).word();
                        i += 2; // to over the current field
                        subjectsAndFields.add(new Pair<>(subject, field));
                        System.out.println(subjectsAndFields.toString());
                    }
                } else if (token.tag().startsWith("N")) {
                    subject = token.word();
                    subjectsAndFields.add(new Pair<>(subject, null));


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
        return subjectsAndFields;
    }
    private List<Pair<String, String>> findSubjectsAndFields(List<CoreLabel> tokens, SemanticGraph dependencies) {

        /*מוצא שדה ונושא*/
        List<Pair<String, String>> subjectsAndFields = new ArrayList<Pair<String, String>>();
        for (int i = 0; i < tokens.size(); i++) {

            CoreLabel token = tokens.get(i);

            if (!token.word().contains("IGNORE")) {
                // If the token is a noun and is followed by a possessive case marker, it is a subject
                //שדה של נושא
                if (containsUnderscore(token.word())) {
                    field = "";
                    subject = "";
                }else if(token.word().equals("of") && tokens.get(i - 1).tag().startsWith("N")){
                    if(tokens.get(i + 1).word().equals("a") || tokens.get(i + 1).word().equals("an") || tokens.get(i + 1).word().equals("the")){
                        subject = tokens.get(i + 2).word();
                        field = tokens.get(i - 1).word();
                        i = i+2;
                        if(subjectsAndFields.get(subjectsAndFields.size() - 1).first.equals(field)){
                            subjectsAndFields.get(subjectsAndFields.size() - 1).setFirst(subject);
                            subjectsAndFields.get(subjectsAndFields.size() - 1).setSecond(field);
                        }
                        else
                            subjectsAndFields.add(new Pair<>(subject, field));
                    }
                    else {
                        subject = tokens.get(i + 1).word();
                        field = tokens.get(i - 1).word();
                        i = i + 1;

                        if (subjectsAndFields.get(subjectsAndFields.size() - 1).first == field) {
                            subjectsAndFields.get(subjectsAndFields.size() - 1).setFirst(subject);
                            subjectsAndFields.get(subjectsAndFields.size() - 1).setSecond(field);
                        } else
                            subjectsAndFields.add(new Pair<>(subject, field));

                    }
                } else if (token.tag().startsWith("N") && i < tokens.size() - 2 && tokens.get(i + 1).tag().equals("POS")) {
                    if(tokens.get(i + 2).tag().equals("POS")){
                        subject = token.word();
                        field = tokens.get(i + 3).word();
                        i += 3; // to over the current field
                        subjectsAndFields.add(new Pair<>(subject, field));
                        System.out.println(subjectsAndFields.toString());
                    }
                    else {
                        subject = token.word();
                        field = tokens.get(i + 2).word();
                        i += 2; // to over the current field
                        subjectsAndFields.add(new Pair<>(subject, field));
                        System.out.println(subjectsAndFields.toString());
                    }
                } else if (token.tag().startsWith("N")) {
                    subject = token.word();
                    subjectsAndFields.add(new Pair<>(subject, null));


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

        for (SemanticGraphEdge edge : dependencies.edgeListSorted()) {
            String rel = edge.getRelation().toString();
            System.out.println("rel " + rel);
            String gov = edge.getGovernor().lemma();
            System.out.println("gov " + gov);
            String dep = edge.getDependent().lemma();
            System.out.println("dep " + dep);


            if (rel.equals("nsubj")) {
                subjectsAndFields.add(new Pair<>(gov, dep));
            }
        }

        return subjectsAndFields;
    }
    public List<Pair<String, String>> readNLPTemplate1() throws IOException{
        // Set up the Stanford CoreNLP pipeline

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        String sentenceToNLP = getCleanSentenceForAnalysis(this.sentence);

        System.out.println(sentenceToNLP);

        CoreDocument doc = new CoreDocument(sentenceToNLP);
        pipeline.annotate(doc);

        //NLP
        List<Pair<String, String>> subjectsAndFields = findSubjectsAndFields(doc.tokens());

        return subjectsAndFields;
    }
    public List<Pair<String, String>> readNLPTemplate() throws IOException{
    // Set up the Stanford CoreNLP pipeline

    Properties props = new Properties();
    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    String sentenceToNLP = getCleanSentenceForAnalysis(this.sentence);

    System.out.println(sentenceToNLP);

    CoreDocument doc = new CoreDocument(sentenceToNLP);
    pipeline.annotate(doc);
    //NLP
    List<Pair<String, String>> subjectsAndFields = findSubjectsAndFields(doc.tokens(), doc.sentences().get(0).dependencyParse());

    return subjectsAndFields;
}
    public void analysisNLPTemplate() throws IOException {
        List<Pair<String, String>> subjectsAndFields = readNLPTemplate();
        if (!isSubjectsAndFieldsInDB(subjectsAndFields)){
            addSubjectsAndFieldsToDB(subjectsAndFields);
        }
    }

    /************************************************************************************************************/
    /*DataBase*/
    private void addSubjectsAndFieldsToDB(List<Pair<String, String>> subjectsAndFields) {
        /*query to insert to db*/
    }
    private boolean isSubjectsAndFieldsInDB(List<Pair<String, String>> subjectsAndFields) {
        /*query to search in db*/
        return false;
    }

    /*************************************************************************************************************/
    /*Translate*/
    private String translateSentenceFromHebrewToEnglish(String sentenceBeforeTranslation) throws IOException {
        Translator translate = new Translator(sentenceBeforeTranslation);
        String englishTranslation = translate.translate("he", "en", sentenceBeforeTranslation);
        //fix the html's signs:
        String sentenceAfterTranslation = StringEscapeUtils.unescapeHtml4(englishTranslation);
        return sentenceAfterTranslation;
    }

    /*************************************************************************************************************/
    /*Clean Sentence*/
    /**
     * Checks if there is the word in the list.
     *
     * @param word the word being tested.
     * @param list the list.
     * @return true if the word is in the list and else if not.
     */
    private boolean wordExistsInConstantsList(List<String> list, String word) {
        return list.contains(word);
    }

    /**
     * Checks if there is an underscore in the word.
     *
     * @param word the word being tested.
     * @return true if there is underscore in the word and else if not.
     */
    boolean containsUnderscore(String word) {
        return word.matches(".*_.*");
    }

    /**
     * Mark the words that belong to the template basic.
     *
     * @param sentence sentence.
     * @return the sentence after add _IGNORE to the templates words
     */
    private String markNotNecessaryTemplatesWords(String sentence) {

        for (String word : sentence.split(" ")) {
            if (wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_VALUE_OPERATOR, word) ||
//                wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_COMPARISON_OPERATOR, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_NUMERIC_OPERATOR, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_SUM_OPERATOR, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_EDGE_OPERATOR, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_EXISTENCE_OPERATOR, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_DATE_OPERATOR, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_TIME_EXPRESSION, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_SEVERITY_DEGREE, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_TYPE, word) ||
                    wordExistsInConstantsList(LogistConstants.CONSTANT_LIST_BUILT_IN_TEMPLATE_WORDS, word))

                sentence = sentence.replace(word, word + "_IGNORE");
        }
        return sentence;
    }

    /**
     * Checks for a desired character ch at which indexes it is found in the given sentence.
     *
     * @param sentence sentence.
     * @param ch the desired character to test.
     * @return The function returns a list of indexes where the desired character is found,
     * and if there is no desired character, an empty list will be returned.
     */
    List<Integer> getCharsIndexes(String sentence, char ch) {
        List<Integer> charsIndexes = new ArrayList<>();
        for (int i = 0; i < sentence.length(); i++) {
            if (sentence.charAt(i) == ch) {
                charsIndexes.add(i);
            }
        }
        return charsIndexes;
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
    private String replaceBetweenTwoCharsFromTwoIndexesInSentence(String sentence, List<Integer> charsIndexes, char oldCh, char newCh) {
        String substring;
        String newSubstring;
        for (int i = 0; i < charsIndexes.size(); i += 2) {
            int start = charsIndexes.get(i) + 1;
            int end = charsIndexes.get(i + 1);
            substring = sentence.substring(start, end);
            newSubstring = substring.replace(oldCh, newCh);
            sentence = sentence.substring(0, start) + newSubstring + sentence.substring(end);
        }
        return sentence;
    }

    /**
     * A function that returns the sentence after removing unnecessary words from it that interfere with the parsing of the sentence.
     *
     * @param sentence the sentence.
     * @return the sentence after clean.
     * @throws IOException
     */
    private String getCleanSentenceForAnalysis(String sentence) throws IOException {
        //remove some words that belongs to the template:
        String sentenceAfterClean = markNotNecessaryTemplatesWords(sentence);
        //translate the sentence:
        String sentenceAfterTranslate = translateSentenceFromHebrewToEnglish(sentenceAfterClean);
        //find the indexes of " in order to add underscore between the words that in "":
        List<Integer> charsIndexes = getCharsIndexes(sentenceAfterTranslate, '"');
        //replace the space between the words that in "" with underscore:
        String sentenceToAnalysis = replaceBetweenTwoCharsFromTwoIndexesInSentence(sentenceAfterTranslate, charsIndexes, ' ', '_');
        //remove the stop words from the sentence:
        String sentenceToAnalysisWithoutStopWords = removeStopWords(sentenceToAnalysis);
        return sentenceToAnalysisWithoutStopWords;
    }

    /**************************************************************************************************************/
    /*Help function*/

    private static List<String> readFileAsListString(String fileName) throws Exception {
        Path path = Paths.get(fileName);
        List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
        return allLines;
    }


    /*************************************************************************************************************/

    public static void main(String[] args) throws IOException {

        List<String> data = new ArrayList<String>();
        try {
            data = readFileAsListString("sentences.txt");
            System.out.println(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NLPTemplate t;
        t = new NLPTemplate("אם חולצה היא כתומה");
        t.readNLPTemplate();
//        for (int i = 0; i < data.size(); i++) {
//            try {
//                t = new NLPTemplate(data.get(i));
//                System.out.println(data.get(i));
//                t.analysisNLPTemplate();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
