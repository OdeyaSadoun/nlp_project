import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestTheProject {

    public static void main(String[] args) {
        // Read sentences from a text file.
        List<String> sentences = readSentences("sentences28_09_2023.txt");
        String sentenceAfterAddUnderscoreInQuotes = "";
        int counterForPrint = 1;
        // For each sentence, call the readTemplate function from the static class AAA and pass the sentence to the function.
        for (String sentence : sentences) {
            sentenceAfterAddUnderscoreInQuotes = replaceSpacesWithUnderscoresInQuotes(sentence);
            System.out.println("Sentence num " + counterForPrint + " : " +sentenceAfterAddUnderscoreInQuotes);
            counterForPrint++;
            ClassifySentenceWithoutInternet.readTemplate(sentenceAfterAddUnderscoreInQuotes);
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

    public static String replaceSpacesWithUnderscoresInQuotes(String sentence) {
        // תחילה, נבנה מחרוזת חדשה שתתקבל את התוצאה הסופית.
        StringBuilder result = new StringBuilder();

        // נמצא את המקום הראשון שבו יש גרש פותח.
        int startQuoteIndex = sentence.indexOf('\"');

        // אם לא נמצא גרש פותח, התוצאה היא המחרוזת המקורית.
        if (startQuoteIndex == -1) {
            return sentence;
        }

        // נמצא את המקום האחרון שבו יש גרש סגור.
        int endQuoteIndex = sentence.lastIndexOf('\"');

        // אם לא נמצא גרש סגור, התוצאה היא המחרוזת המקורית.
        if (endQuoteIndex == -1) {
            return sentence;
        }

        // נבנה מחרוזת חדשה מהחלק בין הגרשיים.
        String quotePart = sentence.substring(startQuoteIndex + 1, endQuoteIndex);

        // מחליפים את כל הרווחים במחרוזת החדשה בקו תחתון.
        quotePart = quotePart.replaceAll(" ", "_");

        // מוסיפים את המחרוזת החדשה למחרוזת התוצאה הסופית.
        result.append(sentence.substring(0, startQuoteIndex + 1));
        result.append(quotePart);
        result.append('"');
        result.append(sentence.substring(endQuoteIndex + 1));

        // נחזור את המחרוזת התוצאה הסופית.
        return result.toString();
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
