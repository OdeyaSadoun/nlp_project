import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tools {

    public static List<String> operators =
            Arrays.asList(
                    "@",
                    "!",
                    "+",
                    "-",
                    "*",
                    "/",
                    "(",
                    ")",
                    ",",
                    ">",
                    ">=",
                    "<",
                    "<=",
                    "==",
                    "גדול שווה",
                    "קטן שווה",
                    "קטן",
                    "גדול",
                    "שווה");

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
        result.append(sentence, 0, startQuoteIndex + 1);
        result.append(quotePart);
        result.append('"');
        result.append(sentence.substring(endQuoteIndex + 1));

        // נחזור את המחרוזת התוצאה הסופית.
        return result.toString();
    }

    public static String removeParenthesis(String word) {

        // Check if the string is empty or if it contains only one character.
        if (word.isEmpty() || word.length() == 1) {
            return word;
        }

        // Check if the first character is a parenthesis.
        if (word.charAt(0) == '(' || word.charAt(0) == ')') {
            word = word.substring(1);
        }

        // Check if the last character is a parenthesis.
        if (word.charAt(word.length() - 1) == ')' || word.charAt(word.length() - 1) == '(') {
            word = word.substring(0, word.length() - 1);
        }

        return word;
    }

    static boolean isOperator(String s) {
        return operators.contains(s);
    }

    static boolean isNumericNumber(String s) {

        // Check if the string is empty or null.
        if (s == null || s.isEmpty()) {
            return false;
        }

        // Check if the string contains any non-numeric characters.
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c) && c != '.') {
                return false;
            }
        }

        // If the string contains only digits, return true.
        return true;
    }

    public static String[] createNewArrayFromString(String inputString) {
        List<String> outputList = new ArrayList<>();
        StringBuilder wordBuilder = new StringBuilder();

        for (char c : inputString.toCharArray()) {
            if (c == '_') {
                outputList.add(wordBuilder.toString());
                wordBuilder = new StringBuilder();
            } else {
                wordBuilder.append(c);
            }
        }

        outputList.add(wordBuilder.toString()); // Add the last word after the last underscore
        String[] outputArray = new String[outputList.size()];
        outputArray = outputList.toArray(outputArray);

        return outputArray;
    }

    public static String[] createNewArrayFromCharList(char[] inputArray) {
        List<String> outputList = new ArrayList<>();
        StringBuilder wordBuilder = new StringBuilder();

        for (char c : inputArray) {
            if (c == '_') {
                outputList.add(wordBuilder.toString());
                wordBuilder = new StringBuilder();
            } else {
                wordBuilder.append(c);
            }
        }

        outputList.add(wordBuilder.toString()); // Add the last word after the last underscore
        String[] outputArray = new String[outputList.size()];
        outputArray = outputList.toArray(outputArray);

        return outputArray;
    }

    public static String changePluralWordToSingle(String pluralSubject) {
        StringBuilder finalWordBuilder = new StringBuilder();
        String[] arrString = createNewArrayFromString(pluralSubject);

        int lastIndex = arrString.length - 1;

        for (int j = 0; j < arrString.length; j++) {
            String s = arrString[j];
            StringBuilder wordBuilder = new StringBuilder();

            if (s.equals("ימים")) {
                wordBuilder.append("יום");
            }
            else if (s.equals("שנים")) {
                wordBuilder.append("שנה");
            }
            else if (s.equals("עבירות")) {
                wordBuilder.append("עבירה");
            }
            else if (s.equals("תאימות")) {
                wordBuilder.append("תאימות");
            }
            else if (s.equals("מעמ")) {
                wordBuilder.append("מעמ");
            }
            else if (s.equals("זכאות")) {
                wordBuilder.append("זכאות");
            }
            else if (s.equals("תקינות")) {
                wordBuilder.append("תקינות");
            }
            // Check if the word ends with a plural suffix such as "ות" or "ים".
            else if (s.endsWith("ות") || s.endsWith("ים")) {
                // If so, remove the plural suffix.
                wordBuilder.append(s, 0, s.length() - 2) ;

                if (wordBuilder.toString().endsWith("מ")
                        || wordBuilder.toString().endsWith("נ")
                        || wordBuilder.toString().endsWith("פ")
                        || wordBuilder.toString().endsWith("צ")
                        || wordBuilder.toString().endsWith("כ")) {

                    // If so, replace the final letter with its corresponding singular letter.
                    char lastChar = wordBuilder.charAt(wordBuilder.length() - 1);
                    wordBuilder.deleteCharAt(wordBuilder.length() - 1);
                    switch (lastChar) {
                        case 'מ':
                            wordBuilder.append('ם');
                            break;
                        case 'נ':
                            wordBuilder.append ('ן');
                            break;
                        case 'פ':
                            wordBuilder.append ('ף' );
                            break;
                        case 'צ':
                            wordBuilder.append('ץ');
                            break;
                        case 'כ':
                            wordBuilder.append ('ך');
                            break;
                    }
                }
            }

            // Check if the final letter is a Hebrew final letter.

            else{
                wordBuilder = new StringBuilder(s);
            }
            finalWordBuilder.append(wordBuilder);

            if (j != lastIndex) {
                finalWordBuilder.append('_');
            }
        }

        return removeUnderscore(finalWordBuilder.toString());
    }

    public static String reverseString(String word) {
        // Create a new string to store the output.
        StringBuilder output = new StringBuilder();

        // Iterate over the characters in the word, in reverse order.
        for (int i = word.length() - 1; i >= 0; i--) {
            // Add the character to the output string.
            output.append(word.charAt(i));
        }

        // Return the output string.
        return output.toString();
    }

    public static char[] breakWordIntoLetters(String word) {
        char[] letters = new char[word.length()];
        for (int i = 0; i < word.length(); i++) {
            letters[i] = word.charAt(i);
        }

        return letters;
    }

    public static String removeUnderscore(String word) {
        // Check if the last character is an underscore
        if (word.endsWith("_")) {
            // Remove the trailing underscore and return the corrected word
            return word.substring(0, word.length() - 1);
        } else {
            // If the last character is not an underscore, return the word as it is
            return word;
        }
    }

    public static char[] removeCharAtIndex(char[] array, int index) {
        if (index < 0 || index >= array.length) {
            // Index is out of bounds, return the original array
            return array;
        }

        char[] result = new char[array.length - 1];

        for (int i = 0, j = 0; i < array.length; i++) {
            if (i != index) {
                result[j++] = array[i];
            }
        }

        return result;
    }

    public static boolean stringContainsWord(String str, String word) {
        // אם המחרוזת ריקה, המילה לא קיימת
        if (str.isEmpty()) {
            return false;
        }

        // מחזיר את מיקום המילה הראשונה במחרוזת
        int index = str.indexOf(word);

        // אם המיקום הוא -1, המילה לא קיימת
        return index != -1;
    }

    public static String getHebrewSubject(Statement stmt) throws SQLException {
        String hebrewSubject = null;
        String queryClassIndex = "SELECT NAME FROM KTCLASS WHERE CLASS_INDEX = 1";
        ResultSet rs = stmt.executeQuery(queryClassIndex);
        if (rs.next()){
            hebrewSubject = rs.getString("NAME");
        }
        return hebrewSubject;
    }

    public static String getEnglishSubject(Statement stmt) throws SQLException {
        String englishSubject = null;
        String queryClassIndex = "SELECT CLASS_CODE_NAME FROM KTCLASS WHERE CLASS_INDEX = 1";
        ResultSet rs = stmt.executeQuery(queryClassIndex);
        if (rs.next()){
            englishSubject = rs.getString("CLASS_CODE_NAME");
        }
        return englishSubject;
    }

}
