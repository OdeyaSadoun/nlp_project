import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestTheProject {

    public static void main(String[] args) {
        // Read sentences from a text file.
        List<String> sentences = readSentences("sentences.txt");
        String sentenceWithoutQuotes = "";
        int counterForPrint = 1;
        // For each sentence, call the readTemplate function from the static class AAA and pass the sentence to the function.
        for (String sentence : sentences) {
            sentenceWithoutQuotes = removeQuotes(sentence);
            System.out.println("Sentence num " + counterForPrint + " : " +sentenceWithoutQuotes);
            counterForPrint++;
            ClassifySentenceWithoutInternet.readTemplate(sentenceWithoutQuotes);
        }
    }

    public static String removeQuotes(String sentence) {
        // Create a new string to store the output.
        String output = "";

        // Iterate over the characters in the sentence.
        for (int i = 0; i < sentence.length(); i++) {
            // If the character is not a quote, add it to the output string.
            if (sentence.charAt(i) != '"') {
                output += sentence.charAt(i);
            }
        }

        // Return the output string.
        return output;
    }

    public static List<String> readSentences(String filename) {
        // Create a list to store the sentences.
        List<String> sentences = new ArrayList<>();

        // Open the file.
        File file = new File(filename);
        try (FileReader reader = new FileReader(file)) {
            // Read the sentences from the file.
            BufferedReader bufferedReader = new BufferedReader(reader);
            String sentence;
            while ((sentence = bufferedReader.readLine()) != null) {
                sentences.add(sentence);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the list of sentences.
        return sentences;
    }

}
