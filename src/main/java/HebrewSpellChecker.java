import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HebrewSpellChecker {
    public static int levenshteinDistance(String word1, String word2) {
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];

        for (int i = 0; i <= word1.length(); i++) {
            for (int j = 0; j <= word2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = (word1.charAt(i - 1) == word2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + cost,
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                }
            }
        }

        return dp[word1.length()][word2.length()];
    }

    public static boolean findSameWordFromKTCLASSTable(String newWord, int levenshteinThreshold, Connection connection) {
        List<String> words = getWORDColumnValues("KTCLASS", connection);

        return isDuplicate(newWord, words, levenshteinThreshold);
    }

    public static String sameWordFromKTCLASSTable(String newWord, int levenshteinThreshold , Connection connection) {
        List<String> words = getWORDColumnValues("KTCLASS", connection);

        return isDuplicateReturnWord(newWord, words, levenshteinThreshold);
    }

    public static boolean findSameWordFromKTATTRIBUTETable(String hebrewSubject, String hebrewField, int levenshteinThreshold, Connection conn) {
        List<String> classCodeNames = getClassCodeNames(hebrewSubject, conn);

        if (classCodeNames.isEmpty()) {
            //there is no class like this in db (or levenshtein or not exist)

            if (!findSameWordFromKTCLASSTable(hebrewSubject, levenshteinThreshold, conn)) {//no exist
                return false; // No matching class code name found
            }
            else{
                //there is levenshtein same to this word
                String newHebrewSubjectFromDB = sameWordFromKTCLASSTable(hebrewSubject, levenshteinThreshold, conn);
                classCodeNames = getClassCodeNames(newHebrewSubjectFromDB, conn);
            }
        }

        List<String> words = getWORDColumnValues("KTATTRIBUTE", conn);

        for (String classCodeName : classCodeNames) {
//            String hebrewSubjectFromEnglish = getHebrewNameFromEnglish(classCodeName);

            if (isDuplicateWithClassCode(hebrewField, classCodeName, words, levenshteinThreshold, conn)) {
                return true;
            }
        }

        return false;
    }

    public static List<String> getClassCodeNames(String hebrewSubject, Connection connection) {
        List<String> classCodeNames = new ArrayList<>();

        try {
            String getClassCodeNameQuery = "SELECT CLASS_CODE_NAME FROM KTCLASS WHERE NAME = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(getClassCodeNameQuery);
            preparedStatement.setString(1, hebrewSubject);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String classCodeName = resultSet.getString("class_code_name");
                classCodeNames.add(classCodeName);
            }

            resultSet.close();
            preparedStatement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return classCodeNames;
    }

    public static boolean isDuplicateWithClassCode(String hebrewField, String classCodeName, List<String> database, int levenshteinThreshold , Connection connection) {
        for (String existingWord : database) {
            int distance = levenshteinDistance(hebrewField, existingWord);
            if (distance <= levenshteinThreshold) {
                if (isMatchInKTATTRIBUTE(existingWord, classCodeName, connection)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isMatchInKTATTRIBUTE(String hebrewField, String classCodeName, Connection connection) {
        try {
            String query = "SELECT * FROM KTATTRIBUTE WHERE NAME = ? AND CLASS_CODE_NAME = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, hebrewField);
            preparedStatement.setString(2, classCodeName);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean isMatch = resultSet.next();

            resultSet.close();
            preparedStatement.close();

            return isMatch;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static List<String> getWORDColumnValues(String tableName, Connection connection) {
        List<String> columnValues = new ArrayList<>();

        try {
            String sql = "";

            if(tableName.equals("KTCLASS")){
                sql = "SELECT NAME FROM KTCLASS";
            }
            else if(tableName.equals("KTATTRIBUTE")){
                sql = "SELECT NAME,CLASS_CODE_NAME FROM KTATTRIBUTE";
            }

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String value = resultSet.getString(1); // Column index starts from 1
                columnValues.add(value);
            }

            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return columnValues;
    }

    public static boolean isDuplicate(String newWord, List<String> database, int levenshteinThreshold) {
        for (String existingWord : database) {
            int distance = levenshteinDistance(newWord, existingWord);

            if (distance <= levenshteinThreshold) {
                return true;
            }
        }

        return false;
    }

    public static String isDuplicateReturnWord(String newWord, List<String> database, int levenshteinThreshold) {
        for (String existingWord : database) {
            int distance = levenshteinDistance(newWord, existingWord);

            if (distance <= levenshteinThreshold) {
                return existingWord;
            }
        }
        return null;
    }

    public static boolean isSameWordInDBInKTCLASSTable(String newWord, int levenshteinThreshold, Connection connection){
        return findSameWordFromKTCLASSTable(newWord,levenshteinThreshold, connection);
    }

    public static boolean isSameWordInDBInKTATTRIBUTETable(String hebrewSubject, String hebrewField, int levenshteinThreshold, Connection conn){
        //field same:
        return findSameWordFromKTATTRIBUTETable(hebrewSubject, hebrewField, levenshteinThreshold, conn);
    }

    public static void main(String[] args) {
    }
}
