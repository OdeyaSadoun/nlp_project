import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class ClassifySentenceWithoutInternet {

    static String subject;
    static String pluralSubject;
    static String field;
    static String mainSubject = "mainSubject";
    final static String JDBC_URL = "jdbc:sqlserver://LOCALHOST\\SQLEXPRESS:1433;databaseName=logistcourse1;SelectMethod=Cursor";
    final static String USERNAME = "logistcourse1";
    final static String PASSWORD = "logistcourse1";

    public static String temp = "אם גיל של לקוח גדול מ- 18 אזי הסק ש- לקוח הוא בוגר";

    public static void readTemplate(String sentence){
        String[] template = sentence.split(" ");
        List<String> lstTemplate = Arrays.asList(template);
        try {
            findSubjectAndField(lstTemplate, sentence);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void findSubjectAndField(List<String> lstTemplate, String sentence) throws SQLException {
        String dataType;

        for (int i = 0; i < lstTemplate.size(); i++) {

            String word = lstTemplate.get(i);
            System.out.println("current word: " + word);

            boolean isSaveWord = isSaveWordInTLXTableORConstes(word);

            boolean isQuoted = word.startsWith("\"") && word.endsWith("\"");
            if (isQuoted) {
                // Skip the word
                continue;
            }

            if(word.equals("הפעל") || word.equals("חוקי")){
                i++;
                continue;
            }

            if(isNumericNumber(word) || GetType.getNumericWord(word)){
                continue;
            }

            if (i + 1 < lstTemplate.size() && !isSaveWord){
                //option5-6
                if(lstTemplate.get(i+1).equals("קיים") ||
                        lstTemplate.get(i+1).equals("לא") && i + 2 < lstTemplate.size() && lstTemplate.get(i+2).equals("קיים")){
                    subject = changePluralSubjectToSingle(word);
                    field = null;
                    dataType = GetType.getLabel(field, sentence, false);
                    System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                    checkFieldAndSubjectInDB(subject, field, dataType);
                    continue;
                }
                //option7
                isSaveWord = isSaveWordInTLXTableORConstes(lstTemplate.get(i+1)); //the next word
                if(!isSaveWord && !( lstTemplate.get(i + 1).equals("הוא") ||  lstTemplate.get(i + 1).equals("אינו") || lstTemplate.get(i + 1).equals("היא") ||  lstTemplate.get(i + 1).equals("איננו"))){
                    subject = changePluralSubjectToSingle(word);
                    field = changePluralSubjectToSingle(lstTemplate.get(i+1));
                    dataType = GetType.getLabel(field, sentence, false);
                    System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                    checkFieldAndSubjectInDB(subject, field, dataType);
                    continue;
                }
                //option8a
                //מילהלאשמורה [טקסט שמור] של כל מילהלאשמורה -  שדה והשני זה נושא
                if (lstTemplate.get(i + 1).equals("של") && i + 2 < lstTemplate.size() && lstTemplate.get(i + 2).equals("כל")) {
                    if(i + 3 < lstTemplate.size()) {
                        isSaveWord = isSaveWordInTLXTableORConstes(lstTemplate.get(i + 3));
                        if (!isSaveWord) {
                            subject = changePluralSubjectToSingle(lstTemplate.get(i + 3));
                            field = changePluralSubjectToSingle(word);
                            dataType = GetType.getLabel(field, sentence, false);
                            System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                            checkFieldAndSubjectInDB(subject, field, dataType);
                            continue;
                        }
                    }
                }
            }

            //option1
            if (i + 1 < lstTemplate.size() && lstTemplate.get(i + 1).equals("של")
                    || i + 2 < lstTemplate.size() && lstTemplate.get(i + 2).equals("של")) {
                continue;
            }

            if(word.equals("של") && i + 1 < lstTemplate.size() && i != 0){
                if(isSaveWordInTLXTableORConstes(lstTemplate.get(i-1)))
                    continue;
                field = changePluralSubjectToSingle(lstTemplate.get(i-1));
                if(lstTemplate.get(i+1).equals("כל") && i + 2 < lstTemplate.size()){
                    if(isSaveWordInTLXTableORConstes(lstTemplate.get(i+2)))
                        continue;
                    subject = changePluralSubjectToSingle(lstTemplate.get(i+2));
                }
                else{
                    if(isSaveWordInTLXTableORConstes(lstTemplate.get(i+1)))
                        continue;
                    subject = changePluralSubjectToSingle(lstTemplate.get(i+1));
                }
                dataType = GetType.getLabel(field, sentence, false);
                System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                checkFieldAndSubjectInDB(subject, field, dataType);
                continue;
            }

            //option2
            if (i != 0 && (word.equals("איננו") || word.equals("אינו") ||  word.equals("היא"))) {
                isSaveWord = isSaveWordInTLXTableORConstes(lstTemplate.get(i - 1));
                if(!isSaveWord && i + 1 < lstTemplate.size()){
                    subject = changePluralSubjectToSingle(lstTemplate.get(i-1));
                    isSaveWord = isSaveWordInTLXTableORConstes(lstTemplate.get(i + 1));
                    if(!isSaveWord && !isNumericNumber(subject) && !isNumericNumber( lstTemplate.get(i+1))){
                        field = changePluralSubjectToSingle(lstTemplate.get(i+1));
                        dataType = GetType.getLabel(field, sentence, false);
                        System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                        checkFieldAndSubjectInDB(subject, field, dataType);
                        continue;
                    }
                }
            }

            //option3
            //3. אם מילהלאשמורה  אינו/הוא  מילהשמורה  -  אזי הראשון הוא שדה  בתוך נושא מרכזי
            //4.  אם מילהלאשמורה{[הוא] /מילהשמורה}  (למשל אופרטור) – אזי הראשון הוא שדה בתוך נושא מרכזי
            if ((word.equals("אם") || word.equals("וגם") || word.equals("או") || word.equals("עדכן"))
                    && i + 2 < lstTemplate.size()) {
                isSaveWord = isSaveWordInTLXTableORConstes(lstTemplate.get(i + 1));
                boolean isSaveWord2 = isSaveWordInTLXTableORConstes(lstTemplate.get(i + 2));
                if (lstTemplate.size() == 3) { //תיקון עבור משפט 3 מילים אם מילה לא שמורה מילה שמורה, הלא שמורה שדה בנושא מרכזי
                    if (!isSaveWord && isSaveWord2) {

                        /**update values:*/
                        subject = mainSubject;
                        field = changePluralSubjectToSingle(lstTemplate.get(i + 1));
                        dataType = GetType.getLabel(field, sentence, false);
                        System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                        /*****************/

                        checkFieldAndSubjectInDB(subject, field, dataType);
                        continue;
                    }
                } else {
                    //עבור התיקון מילה לא שמורה מילה שמורה מילה שמורה, הלא שמורה שדה בנושא מרכזי
                    if (!isSaveWord) {
                        if (i + 3 < lstTemplate.size()) {
                            Boolean isSaveWord3 = isSaveWordInTLXTableORConstes(lstTemplate.get(i + 3));

                            if (isSaveWord2 && isSaveWord3) {
                                /**update values:*/
                                subject = mainSubject;
                                field = lstTemplate.get(i + 1);
                                dataType = GetType.getLabel(field, sentence, false);
                                System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                                /*****************/

                                checkFieldAndSubjectInDB(subject, field, dataType);
                                continue;
                            }
                        }

                        //option8b
                        //מילהלאשמורה [טקסט שמור] של כל מילהלאשמורה -  שדה והשני זה נושא  (אם)8
                        if (i + 2 < lstTemplate.size() && lstTemplate.get(i + 2).equals("של") && i + 3 < lstTemplate.size() && lstTemplate.get(i + 3).equals("כל")) {
                            if (i + 4 < lstTemplate.size()) {
                                isSaveWord = isSaveWordInTLXTableORConstes(lstTemplate.get(i + 4));
                                if (!isSaveWord) {
                                    /**update values:*/
                                    subject = lstTemplate.get(i + 4);
                                    field = lstTemplate.get(i + 1);
                                    dataType = GetType.getLabel(field, sentence, false);
                                    System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                                    /*****************/

                                    checkFieldAndSubjectInDB(subject, field, dataType);
                                    continue;
                                }
                            }
                        }

                        //option4
                        //4.  אם מילהלאשמורה{[הוא] /מילהשמורה}  (למשל אופרטור) – אזי הראשון הוא שדה בתוך נושא מרכזי
//                        if (i + 2 < lstTemplate.size() && (lstTemplate.get(i + 2).equals("היא") || lstTemplate.get(i + 2).equals("אינו") || lstTemplate.get(i + 2).equals("הוא"))) {
//                            continue;
//                        }

                        if (i + 2 < lstTemplate.size()){
                            isSaveWord = isSaveWordInTLXTableORConstes(lstTemplate.get(i + 2));
                            if(isSaveWord){
                                /**update values:*/
                                subject = mainSubject;
                                field = changePluralSubjectToSingle(lstTemplate.get(i + 1));
                                dataType = GetType.getLabel(field, sentence, false);
                                System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                                /*****************/

                                checkFieldAndSubjectInDB(subject, field, dataType);
                                continue;
                            }
                        }
                    }
                    else {
                        //option9-10
                        //  אם מילהשמורה טקסט שמור של כל ה-  מילהלא שמורה [מילהלאשמורה]  -  הראשון שדה השני נושא ברבים אחריו שדה לוואי
                        if (i + 2 < lstTemplate.size() && lstTemplate.get(i + 2).equals("של")
                                && i + 3 < lstTemplate.size() && lstTemplate.get(i + 3).equals("כל")) {
                            if(i + 4 < lstTemplate.size() && lstTemplate.get(i + 4).equals("ה-")) {
                                if (i + 5 < lstTemplate.size()) {
                                    /**update values:*/
                                    pluralSubject = lstTemplate.get(i + 5);
                                    subject = changePluralSubjectToSingle(pluralSubject);
                                    field = changePluralSubjectToSingle(lstTemplate.get(i + 1));
                                    dataType = GetType.getLabel(field, sentence, true);
                                    System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                                    /*****************/

                                    checkFieldAndSubjectInDB(subject, field, dataType);

                                    if (i + 6 < lstTemplate.size()) {
                                        isSaveWord = isSaveWordInTLXTableORConstes(lstTemplate.get(i + 6));
                                        if (!isSaveWord) {
                                            field = changePluralSubjectToSingle(lstTemplate.get(i + 6));
                                            dataType = GetType.getLabel(field, sentence, true);
                                            System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                                            checkFieldAndSubjectInDB(subject, field, dataType);
                                        }
                                    }
                                    continue;
                                }
                            }
                            else{
                                if (i + 4 < lstTemplate.size()) {
                                    /**update values:*/
                                    pluralSubject = lstTemplate.get(i + 4);
                                    subject = changePluralSubjectToSingle(pluralSubject);
                                    field = changePluralSubjectToSingle(lstTemplate.get(i + 1));
                                    dataType = GetType.getLabel(field, sentence, true);
                                    System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                                    /*****************/

                                    checkFieldAndSubjectInDB(subject, field, dataType);

                                    if (i + 5 < lstTemplate.size()) {
                                        isSaveWord = isSaveWordInTLXTableORConstes(lstTemplate.get(i + 5));
                                        if (!isSaveWord) {
                                            field = changePluralSubjectToSingle(lstTemplate.get(i + 5));
                                            dataType = GetType.getLabel(field, sentence, true);
                                            System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                                            checkFieldAndSubjectInDB(subject, field, dataType);
                                        }
                                    }
                                    continue;
                                }
                            }
                        }
                        //10 אם מילהשמורה טקסט שמור [מספר בעברית]   מילהלאשמורה [מילהלאשמורה]   -  הראשון שדה השני נושא ברבים אחריו שדה לוואי ו
                        if (i + 2 < lstTemplate.size() && isNumericNumber(lstTemplate.get(i + 2))) {
                            if (i + 3 < lstTemplate.size()) {
                                isSaveWord = isSaveWordInTLXTableORConstes(lstTemplate.get(i + 3));
                                if (!isSaveWord) {
                                    /**update values:*/
                                    pluralSubject = lstTemplate.get(i + 3);
                                    subject = changePluralSubjectToSingle(changePluralSubjectToSingle(pluralSubject));
                                    field = changePluralSubjectToSingle(lstTemplate.get(i + 1));
                                    dataType = GetType.getLabel(field, sentence, true);
                                    System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                                    /*****************/

                                    checkFieldAndSubjectInDB(subject, field, dataType);

                                    if (i + 4 < lstTemplate.size()) {
                                        isSaveWord = isSaveWordInTLXTableORConstes(lstTemplate.get(i + 4));
                                        if (!isSaveWord) {
                                            field = changePluralSubjectToSingle(lstTemplate.get(i + 4));
                                            dataType = GetType.getLabel(field, sentence, true);
                                            System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                                            checkFieldAndSubjectInDB(subject, field, dataType);
                                        }
                                    }
                                    continue;
                                }
                            }
                        }

                    }
                }
            }

            //option11
            if (i + 1 < lstTemplate.size() && (word.equals("ה-") || word.equals("ל-"))) {
                if(word.equals("ל-")){
                    if(i - 1 < lstTemplate.size() && i - 2 < lstTemplate.size()){
                        isSaveWord = isSaveWordInTLXTableORConstes(lstTemplate.get(i-1));
                        if(!isSaveWord && lstTemplate.get(i-2).equals("עדכן")){
                            /**update values:*/
                            subject = mainSubject;
                            field = lstTemplate.get(i-1);
                            dataType = GetType.getLabel(field, sentence, false);
                            System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                            /*****************/

                            checkFieldAndSubjectInDB(subject, field, dataType);
                            continue;
                        }
                    }
                }
                if(!isNumericNumber(lstTemplate.get(i + 1))) {
                    isSaveWord = isSaveWordInTLXTableORConstes(lstTemplate.get(i + 1));
                    if (!isSaveWord && !lstTemplate.get(i + 1).startsWith("\"") && !lstTemplate.get(i + 1).endsWith("\"")) {
                        /**update values:*/
                        subject = changePluralSubjectToSingle(lstTemplate.get(i + 1));
                        field = null;
                        dataType = GetType.getLabel(field, sentence, false);
                        System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                        /*****************/

                        checkFieldAndSubjectInDB(subject, field, dataType);
                        continue;
                    }
                }
            }

            //option12
            if (i + 1 < lstTemplate.size() && (word.equals("ו-") || word.equals("וגם"))) {
                isSaveWord = isSaveWordInTLXTableORConstes(lstTemplate.get(i+1));
                if(!isSaveWord){
                    /**update values:*/
                    subject = mainSubject;
                    field = changePluralSubjectToSingle(lstTemplate.get(i+1));
                    dataType = GetType.getLabel(field, sentence, false);
                    System.out.println("----------subject: " + subject + " field: " + field + " type: " + dataType + "----------");
                    /*****************/

                    checkFieldAndSubjectInDB(subject, field, dataType);
                }
            }
        }
    }

    private static boolean isNumericNumber(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }

        // Check if the string contains only digits.
        boolean containsDigits = true;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }

        // Check if the string is a Hebrew number or contains only digits.
//        if (s.equals("אחת") || s.equals("שתיים") || s.equals("שלוש") || s.equals("ארבע") || s.equals("חמש")
//                || s.equals("שש") || s.equals("שבע") || s.equals("שמונה") || s.equals("תשע") || s.equals("עשר")
//                || s.equals("אחד") || s.equals("שניים") || s.equals("שלושה") || s.equals("ארבעה") || s.equals("חמישה")
//                || s.equals("שישה") || s.equals("שיבעה") || s.equals("תשעה") || s.equals("עשרה")) {
//            return true;
//        }

        // If the string is not empty and does not represent a numeric value, return false.
        return false;
    }

    public static String changePluralSubjectToSingle(String pluralSubject) {
        if(pluralSubject == "ימים"){
            return "יום";
        }
        if(pluralSubject == "שנים"){
            return "שנה";
        }
        if(pluralSubject == "תאימות"){
            return "תאימות";
        }
        // Check if the word ends with a plural suffix such as "ות" or "ים".
        if (pluralSubject.endsWith("ות") || pluralSubject.endsWith("ים")) {
            // If so, remove the plural suffix.
            pluralSubject = pluralSubject.substring(0, pluralSubject.length() - 2);
        }

        // Check if the final letter is a Hebrew final letter.
        if (pluralSubject.endsWith("מ") || pluralSubject.endsWith("נ") || pluralSubject.endsWith("פ") || pluralSubject.endsWith("צ") || pluralSubject.endsWith("כ")) {
            // If so, replace the final letter with its corresponding singular letter.
            char lastChar = pluralSubject.charAt(pluralSubject.length() - 1);
            switch (lastChar) {
                case 'מ':
                    pluralSubject = pluralSubject.substring(0, pluralSubject.length() - 1) + 'ם';
                    break;
                case 'נ':
                    pluralSubject = pluralSubject.substring(0, pluralSubject.length() - 1) + 'ן';
                    break;
                case 'פ':
                    pluralSubject = pluralSubject.substring(0, pluralSubject.length() - 1) + 'ף';
                    break;
                case 'צ':
                    pluralSubject = pluralSubject.substring(0, pluralSubject.length() - 1) + 'ץ';
                    break;
                case 'כ':
                    pluralSubject = pluralSubject.substring(0, pluralSubject.length() - 1) + 'ך';
                    break;
            }
        }

        return pluralSubject;
    }

    public static String reverseString(String word) {
        // Create a new string to store the output.
        String output = "";

        // Iterate over the characters in the word, in reverse order.
        for (int i = word.length() - 1; i >= 0; i--) {
            // Add the character to the output string.
            output += word.charAt(i);
        }

        // Return the output string.
        return output;
    }

    private static boolean isSaveWordInTLXTableORConstes(String token) throws SQLException {

        if(token.startsWith("\"") && token.endsWith("\"")){
            return true;
        }

        if(isNumericNumber(token)){
            return true;
        }

        Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

        // Create a statement.

        //the tokens in the database is revers
        String reversToken = reverseString(token);


        PreparedStatement statement = connection.prepareStatement("SELECT token FROM ZTRLPTLX WHERE token = ?");

        // Set the parameter.
        statement.setString(1, reversToken);

        // Execute the query.
        ResultSet resultSet = statement.executeQuery();

        // Check if the word is in the table.
        return resultSet.next();
    }

    private static void checkFieldAndSubjectInDB(String subject, String field, String dataType) {
        String hebrewField = field;
        String englishField;
        String hebrewSubject = subject;
        String englishSubject;
        if (hebrewField == null) {
            englishField = null;
        } else {
            englishField = TranslateWithoutInternet.retrieveEnglishValuesFromHebrewValues(hebrewField);
        }
        if(subject == "mainSubject"){
            englishSubject = "main_class";
            hebrewSubject = "נושא_ראשי";
        }
        else {
            englishSubject = TranslateWithoutInternet.retrieveEnglishValuesFromHebrewValues(hebrewSubject);
        }
        System.out.println("**********subject in hebrew: " + hebrewSubject + " field in hebrew: " + hebrewField + " subject in english: " + englishSubject + " field in english: " + englishField + "----------");
        SaveToDatabase.addSubjectToDatabase(hebrewField, englishField, hebrewSubject, englishSubject, dataType);
    }

    public static void main(String[] args) {
        readTemplate(temp);
    }
}
