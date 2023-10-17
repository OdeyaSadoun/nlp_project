import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassifySentenceWithoutInternet {
  static String subject;
  static String pluralSubject;
  static String field;
  static String mainSubject = "mainSubject";

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

  public static void readTemplate(
      String sentence, Connection conn, Statement stmt, ResultSet rs, boolean APPROVE_PRINTING) {
    String[] template = sentence.split(" ");
    List<String> lstTemplate = Arrays.asList(template);
    try {
      findSubjectAndField(lstTemplate, sentence, conn, stmt, rs, APPROVE_PRINTING);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public static void findSubjectAndField(
      List<String> lstTemplate,
      String sentence,
      Connection conn,
      Statement stmt,
      ResultSet rs,
      boolean APPROVE_PRINTING)
      throws SQLException {
    String dataType;

    for (int i = 0; i < lstTemplate.size(); i++) {

      String word = lstTemplate.get(i);
      word = removeParenthesis(word); // הסרת הסוגרים שגורמים לזיהוי לא תקין של מילים שמורות בTLX
      if (APPROVE_PRINTING) {
        System.out.println("current word: " + word);
      }

      boolean isSaveWord = isSaveWordInTLXTableORConstes(word, conn);
      boolean isSaveWord2;

      boolean isQuoted = word.startsWith("\"") && word.endsWith("\"");
      if (isQuoted) {
        // Skip the word
        continue;
      }

      if (word.equals("הפעל") || word.equals("חוקי")) {
        i++;
        continue;
      }

      if (isNumericNumber(word)) {
        continue;
      }
      if (i + 2 < lstTemplate.size()) {
        // תוספת על הענין של אופרטורים 2 מילים לא שמורות ובינהן אופרטור שתיהן שדות
        // בנושא ראשי
        isSaveWord2 =
            isSaveWordInTLXTableORConstes(removeParenthesis(lstTemplate.get(i + 2)), conn);

        if (!isSaveWord && isOperator(removeParenthesis(lstTemplate.get(i + 1)))) {
          if (isOperator(removeParenthesis(lstTemplate.get(i + 2)))
              && i + 3 < lstTemplate.size()
              && !isSaveWordInTLXTableORConstes(removeParenthesis(lstTemplate.get(i + 3)), conn)) {
            // **update values: (second word)*/
            subject = mainSubject;
            field = changePluralSubjectToSingle(lstTemplate.get(i + 3));
            dataType = GetType.getLabel(field, sentence, false, conn, APPROVE_PRINTING);

            if (APPROVE_PRINTING) {
              System.out.println(
                  "----------subject: "
                      + subject
                      + " field: "
                      + field
                      + " type: "
                      + dataType
                      + "----------");
            }
            // *****************/

            checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
            // **update values: (first word) */
            subject = mainSubject;
            field = changePluralSubjectToSingle(word);
            dataType = GetType.getLabel(field, sentence, false, conn, APPROVE_PRINTING);

            if (APPROVE_PRINTING) {
              System.out.println(
                  "----------subject: "
                      + subject
                      + " field: "
                      + field
                      + " type: "
                      + dataType
                      + "----------");
            }
            // *****************/

            checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
            continue;
          }
          if (!isSaveWord2) {
            // **update values: (second word)*/
            subject = mainSubject;
            field = changePluralSubjectToSingle(lstTemplate.get(i + 2));
            dataType = GetType.getLabel(field, sentence, false, conn, APPROVE_PRINTING);

            if (APPROVE_PRINTING) {
              System.out.println(
                  "----------subject: "
                      + subject
                      + " field: "
                      + field
                      + " type: "
                      + dataType
                      + "----------");
            }
            // *****************/

            checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
            // **update values: (first word) */
            subject = mainSubject;
            field = changePluralSubjectToSingle(word);
            dataType = GetType.getLabel(field, sentence, false, conn, APPROVE_PRINTING);

            if (APPROVE_PRINTING) {
              System.out.println(
                  "----------subject: "
                      + subject
                      + " field: "
                      + field
                      + " type: "
                      + dataType
                      + "----------");
            }
            // *****************/

            checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);

            continue;
          }
        }
      }
      if (i + 1 < lstTemplate.size() && !isSaveWord) {
        // option5-6
        if (removeParenthesis(lstTemplate.get(i + 1)).equals("קיים")
            || removeParenthesis(lstTemplate.get(i + 1)).equals("לא")
                && i + 2 < lstTemplate.size()
                && removeParenthesis(lstTemplate.get(i + 2)).equals("קיים")) {
          subject = changePluralSubjectToSingle(word);
          field = null;
          dataType = GetType.getLabel(null, sentence, false, conn, APPROVE_PRINTING);

          if (APPROVE_PRINTING) {
            System.out.println(
                "----------subject: "
                    + subject
                    + " field: "
                    + field
                    + " type: "
                    + dataType
                    + "----------");
          }
          checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
          continue;
        }
        // option7
        isSaveWord = isSaveWordInTLXTableORConstes(lstTemplate.get(i + 1), conn); // the next word
        if (!isSaveWord
            && !(lstTemplate.get(i + 1).equals("הוא")
                || lstTemplate.get(i + 1).equals("אינו")
                || lstTemplate.get(i + 1).equals("היא")
                || lstTemplate.get(i + 1).equals("איננו")
                || lstTemplate.get(i + 1).equals("איננה")
                || lstTemplate.get(i + 1).equals("אינה"))) {
          subject = changePluralSubjectToSingle(word);
          field = changePluralSubjectToSingle(lstTemplate.get(i + 1));
          dataType = GetType.getLabel(field, sentence, false, conn, APPROVE_PRINTING);

          if (APPROVE_PRINTING) {
            System.out.println(
                "----------subject: "
                    + subject
                    + " field: "
                    + field
                    + " type: "
                    + dataType
                    + "----------");
          }

          checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
          continue;
        }
        // option8a
        // מילהלאשמורה [טקסט שמור] של כל מילהלאשמורה -  שדה והשני זה נושא
        if (lstTemplate.get(i + 1).equals("של")
            && i + 2 < lstTemplate.size()
            && lstTemplate.get(i + 2).equals("כל")) {
          if (i + 3 < lstTemplate.size()) {
            isSaveWord = isSaveWordInTLXTableORConstes(lstTemplate.get(i + 3), conn);
            if (!isSaveWord) {
              subject = changePluralSubjectToSingle(lstTemplate.get(i + 3));
              field = changePluralSubjectToSingle(word);
              dataType = GetType.getLabel(field, sentence, false, conn, APPROVE_PRINTING);

              if (APPROVE_PRINTING) {
                System.out.println(
                    "----------subject: "
                        + subject
                        + " field: "
                        + field
                        + " type: "
                        + dataType
                        + "----------");
              }

              checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
              continue;
            }
          }
        }
      }

      // option1
      if (i + 1 < lstTemplate.size() && lstTemplate.get(i + 1).equals("של")
          || i + 2 < lstTemplate.size() && lstTemplate.get(i + 2).equals("של")) {
        continue;
      }

      if (word.equals("של") && i + 1 < lstTemplate.size() && i != 0) {
        if (isSaveWordInTLXTableORConstes(removeParenthesis(lstTemplate.get(i - 1)), conn))
          continue;
        field = changePluralSubjectToSingle(lstTemplate.get(i - 1));
        if (lstTemplate.get(i + 1).equals("כל") && i + 2 < lstTemplate.size()) {
          if (isSaveWordInTLXTableORConstes(removeParenthesis(lstTemplate.get(i + 2)), conn))
            continue;
          subject = changePluralSubjectToSingle(lstTemplate.get(i + 2));
        } else {
          if (isSaveWordInTLXTableORConstes(lstTemplate.get(i + 1), conn)) continue;
          subject = changePluralSubjectToSingle(lstTemplate.get(i + 1));
        }
        dataType = GetType.getLabel(field, sentence, false, conn, APPROVE_PRINTING);

        if (APPROVE_PRINTING) {
          System.out.println(
              "----------subject: "
                  + subject
                  + " field: "
                  + field
                  + " type: "
                  + dataType
                  + "----------");
        }

        checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
        continue;
      }

      // option2
      if (i != 0
          && (word.equals("איננו")
              || word.equals("אינו")
              || word.equals("אינה")
              || word.equals("איננה")
              || word.equals("היא")
              || word.equals("הוא"))) {
        isSaveWord = isSaveWordInTLXTableORConstes(removeParenthesis(lstTemplate.get(i - 1)), conn);
        if (!isSaveWord && i + 1 < lstTemplate.size()) {
          subject = changePluralSubjectToSingle(lstTemplate.get(i - 1));
          isSaveWord =
              isSaveWordInTLXTableORConstes(removeParenthesis(lstTemplate.get(i + 1)), conn);
          if (!isSaveWord
              && !isNumericNumber(subject)
              && !isNumericNumber(lstTemplate.get(i + 1))) {
            field = changePluralSubjectToSingle(lstTemplate.get(i + 1));
            dataType = GetType.getLabel(field, sentence, false, conn, APPROVE_PRINTING);

            if (APPROVE_PRINTING) {
              System.out.println(
                  "----------subject: "
                      + subject
                      + " field: "
                      + field
                      + " type: "
                      + dataType
                      + "----------");
            }

            checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
            continue;
          }
        }
      }

      // option3
      // 3. אם מילהלאשמורה  אינו/הוא  מילהשמורה  -  אזי הראשון הוא שדה  בתוך נושא מרכזי
      // 4.  אם מילהלאשמורה{[הוא] /מילהשמורה}  (למשל אופרטור) – אזי הראשון הוא שדה בתוך נושא מרכזי
      if ((word.equals("אם") || word.equals("וגם") || word.equals("או") || word.equals("עדכן"))
          && i + 2 < lstTemplate.size()) {
        isSaveWord = isSaveWordInTLXTableORConstes(removeParenthesis(lstTemplate.get(i + 1)), conn);
        isSaveWord2 =
            isSaveWordInTLXTableORConstes(removeParenthesis(lstTemplate.get(i + 2)), conn);
        if (lstTemplate.size()
            == 3) { // תיקון עבור משפט 3 מילים אם מילה לא שמורה מילה שמורה, הלא שמורה שדה בנושא
          // מרכזי
          if (!isSaveWord && isSaveWord2) {

            // **update values:*/
            subject = mainSubject;
            field = changePluralSubjectToSingle(lstTemplate.get(i + 1));
            dataType = GetType.getLabel(field, sentence, false, conn, APPROVE_PRINTING);

            if (APPROVE_PRINTING) {
              System.out.println(
                  "----------subject: "
                      + subject
                      + " field: "
                      + field
                      + " type: "
                      + dataType
                      + "----------");
            }
            // *****************/

            checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
            continue;
          }
        } else {
          // עבור התיקון מילה לא שמורה מילה שמורה מילה שמורה, הלא שמורה שדה בנושא מרכזי
          if (!isSaveWord) {
            if (i + 3 < lstTemplate.size()) {
              boolean isSaveWord3 =
                  isSaveWordInTLXTableORConstes(removeParenthesis(lstTemplate.get(i + 3)), conn);

              if (isSaveWord2 && isSaveWord3) {
                // **update values:*/
                subject = mainSubject;
                field = changePluralSubjectToSingle(lstTemplate.get(i + 1));
                dataType = GetType.getLabel(field, sentence, false, conn, APPROVE_PRINTING);

                if (APPROVE_PRINTING) {
                  System.out.println(
                      "----------subject: "
                          + subject
                          + " field: "
                          + field
                          + " type: "
                          + dataType
                          + "----------");
                }
                // *****************/

                checkFieldAndSubjectInDB(
                    subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
                continue;
              }
            }

            // option8b
            // מילהלאשמורה [טקסט שמור] של כל מילהלאשמורה -  שדה והשני זה נושא  (אם)8
            if (i + 2 < lstTemplate.size()
                && lstTemplate.get(i + 2).equals("של")
                && i + 3 < lstTemplate.size()
                && lstTemplate.get(i + 3).equals("כל")) {
              if (i + 4 < lstTemplate.size()) {
                isSaveWord =
                    isSaveWordInTLXTableORConstes(removeParenthesis(lstTemplate.get(i + 4)), conn);
                if (!isSaveWord) {
                  // **update values:*/
                  subject = changePluralSubjectToSingle(lstTemplate.get(i + 4));
                  field = changePluralSubjectToSingle(lstTemplate.get(i + 1));
                  dataType = GetType.getLabel(field, sentence, false, conn, APPROVE_PRINTING);

                  if (APPROVE_PRINTING) {
                    System.out.println(
                        "----------subject: "
                            + subject
                            + " field: "
                            + field
                            + " type: "
                            + dataType
                            + "----------");
                  }
                  // *****************/

                  checkFieldAndSubjectInDB(
                      subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
                  continue;
                }
              }
            }

            // option4
            // 4.  אם מילהלאשמורה{[הוא] /מילהשמורה}  (למשל אופרטור) – אזי הראשון הוא שדה בתוך נושא
            // מרכזי

            if (i + 2 < lstTemplate.size()) {
              isSaveWord =
                  isSaveWordInTLXTableORConstes(removeParenthesis(lstTemplate.get(i + 2)), conn);
              if (isSaveWord) {
                // **update values:*/
                subject = mainSubject;
                field = changePluralSubjectToSingle(lstTemplate.get(i + 1));
                dataType = GetType.getLabel(field, sentence, false, conn, APPROVE_PRINTING);

                if (APPROVE_PRINTING) {
                  System.out.println(
                      "----------subject: "
                          + subject
                          + " field: "
                          + field
                          + " type: "
                          + dataType
                          + "----------");
                }
                // *****************/

                checkFieldAndSubjectInDB(
                    subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
                continue;
              }
            }
          } else {
            // option9-10
            //  אם מילהשמורה טקסט שמור של כל ה-  מילהלא שמורה [מילהלאשמורה]  -  הראשון שדה השני נושא
            // ברבים אחריו שדה לוואי
            if (i + 2 < lstTemplate.size()
                && lstTemplate.get(i + 2).equals("של")
                && i + 3 < lstTemplate.size()
                && lstTemplate.get(i + 3).equals("כל")) {
              if (i + 4 < lstTemplate.size() && lstTemplate.get(i + 4).equals("ה-")) {
                if (i + 5 < lstTemplate.size()) {
                  // **update values:*/
                  pluralSubject = lstTemplate.get(i + 5);
                  subject = changePluralSubjectToSingle(pluralSubject);
                  field = changePluralSubjectToSingle(lstTemplate.get(i + 1));
                  dataType = GetType.getLabel(field, sentence, true, conn, APPROVE_PRINTING);

                  if (APPROVE_PRINTING) {
                    System.out.println(
                        "----------subject: "
                            + subject
                            + " field: "
                            + field
                            + " type: "
                            + dataType
                            + "----------");
                  }
                  // *****************/

                  checkFieldAndSubjectInDB(
                      subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);

                  if (i + 6 < lstTemplate.size()) {
                    isSaveWord =
                        isSaveWordInTLXTableORConstes(
                            removeParenthesis(lstTemplate.get(i + 6)), conn);
                    if (!isSaveWord) {
                      field = changePluralSubjectToSingle(lstTemplate.get(i + 6));
                      dataType = GetType.getLabel(field, sentence, true, conn, APPROVE_PRINTING);

                      if (APPROVE_PRINTING) {
                        System.out.println(
                            "----------subject: "
                                + subject
                                + " field: "
                                + field
                                + " type: "
                                + dataType
                                + "----------");
                      }

                      checkFieldAndSubjectInDB(
                          subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
                    }
                  }
                  continue;
                }
              } else {
                if (i + 4 < lstTemplate.size()) {
                  // **update values:*/
                  pluralSubject = lstTemplate.get(i + 4);
                  subject = changePluralSubjectToSingle(pluralSubject);
                  field = changePluralSubjectToSingle(lstTemplate.get(i + 1));
                  dataType = GetType.getLabel(field, sentence, true, conn, APPROVE_PRINTING);

                  if (APPROVE_PRINTING) {
                    System.out.println(
                        "----------subject: "
                            + subject
                            + " field: "
                            + field
                            + " type: "
                            + dataType
                            + "----------");
                  }
                  // *****************/

                  checkFieldAndSubjectInDB(
                      subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);

                  if (i + 5 < lstTemplate.size()) {
                    isSaveWord =
                        isSaveWordInTLXTableORConstes(
                            removeParenthesis(lstTemplate.get(i + 5)), conn);
                    if (!isSaveWord) {
                      field = changePluralSubjectToSingle(lstTemplate.get(i + 5));
                      dataType = GetType.getLabel(field, sentence, true, conn, APPROVE_PRINTING);

                      if (APPROVE_PRINTING) {
                        System.out.println(
                            "----------subject: "
                                + subject
                                + " field: "
                                + field
                                + " type: "
                                + dataType
                                + "----------");
                      }

                      checkFieldAndSubjectInDB(
                          subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
                    }
                  }
                  continue;
                }
              }
            }
            // 10 אם מילהשמורה טקסט שמור [מספר בעברית]   מילהלאשמורה [מילהלאשמורה]   -  הראשון שדה
            // השני נושא ברבים אחריו שדה לוואי ו
            if (i + 2 < lstTemplate.size()
                && isNumericNumber(removeParenthesis(lstTemplate.get(i + 2)))) {
              if (i + 3 < lstTemplate.size()) {
                isSaveWord =
                    isSaveWordInTLXTableORConstes(removeParenthesis(lstTemplate.get(i + 3)), conn);
                if (!isSaveWord) {
                  // **update values:*/
                  pluralSubject = lstTemplate.get(i + 3);
                  subject = changePluralSubjectToSingle(changePluralSubjectToSingle(pluralSubject));
                  field = changePluralSubjectToSingle(lstTemplate.get(i + 1));
                  dataType = GetType.getLabel(field, sentence, true, conn, APPROVE_PRINTING);

                  if (APPROVE_PRINTING) {
                    System.out.println(
                        "----------subject: "
                            + subject
                            + " field: "
                            + field
                            + " type: "
                            + dataType
                            + "----------");
                  }
                  // *****************/

                  checkFieldAndSubjectInDB(
                      subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);

                  if (i + 4 < lstTemplate.size()) {
                    isSaveWord =
                        isSaveWordInTLXTableORConstes(
                            removeParenthesis(lstTemplate.get(i + 4)), conn);
                    if (!isSaveWord) {
                      field = changePluralSubjectToSingle(lstTemplate.get(i + 4));
                      dataType = GetType.getLabel(field, sentence, true, conn, APPROVE_PRINTING);

                      if (APPROVE_PRINTING) {
                        System.out.println(
                            "----------subject: "
                                + subject
                                + " field: "
                                + field
                                + " type: "
                                + dataType
                                + "----------");
                      }

                      checkFieldAndSubjectInDB(
                          subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
                    }
                  }
                  continue;
                }
              }
            }
          }
        }
      }

      // option11
      if (i + 1 < lstTemplate.size() && (word.equals("ה-") || word.equals("ל-"))) {
        if (word.equals("ל-")) {
          isSaveWord =
              isSaveWordInTLXTableORConstes(removeParenthesis(lstTemplate.get(i - 1)), conn);
          if (!isSaveWord && lstTemplate.get(i - 2).equals("עדכן")) {
            // update values:
            subject = mainSubject;
            field = changePluralSubjectToSingle(lstTemplate.get(i - 1));
            dataType = GetType.getLabel(field, sentence, false, conn, APPROVE_PRINTING);

            if (APPROVE_PRINTING) {
              System.out.println(
                  "----------subject: "
                      + subject
                      + " field: "
                      + field
                      + " type: "
                      + dataType
                      + "----------");
            }
            // *****************/

            checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
            isSaveWord =
                isSaveWordInTLXTableORConstes(removeParenthesis(lstTemplate.get(i + 1)), conn);
            if (isSaveWord) {
              continue;
            }
          }
        }
        if (!isNumericNumber(lstTemplate.get(i + 1))) {
          isSaveWord =
              isSaveWordInTLXTableORConstes(removeParenthesis(lstTemplate.get(i + 1)), conn);
          if (!isSaveWord
              && !lstTemplate.get(i + 1).startsWith("\"")
              && !lstTemplate.get(i + 1).endsWith("\"")) {
            // **update values:*/
            subject = changePluralSubjectToSingle(lstTemplate.get(i + 1));
            field = null;
            dataType = GetType.getLabel(null, sentence, false, conn, APPROVE_PRINTING);

            if (APPROVE_PRINTING) {
              System.out.println(
                  "----------subject: "
                      + subject
                      + " field: "
                      + field
                      + " type: "
                      + dataType
                      + "----------");
            }
            // *****************/

            checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
            continue;
          }
        }
      }

      // option12
      if (i + 1 < lstTemplate.size() && (word.equals("ו-") || word.equals("וגם"))) {
        isSaveWord = isSaveWordInTLXTableORConstes(removeParenthesis(lstTemplate.get(i + 1)), conn);
        if (!isSaveWord) {
          // **update values:*/
          subject = mainSubject;
          field = changePluralSubjectToSingle(lstTemplate.get(i + 1));
          dataType = GetType.getLabel(field, sentence, false, conn, APPROVE_PRINTING);

          if (APPROVE_PRINTING) {
            System.out.println(
                "----------subject: "
                    + subject
                    + " field: "
                    + field
                    + " type: "
                    + dataType
                    + "----------");
          }
          // *****************/

          checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, APPROVE_PRINTING);
        }
      }
    }
  }

  private static boolean isOperator(String s) {
    return operators.contains(s);
  }

  private static boolean isNumericNumber(String s) {

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


  public static String[] createNewArray(String inputString) {
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


  public static String changePluralSubjectToSingle(String pluralSubject) {
    StringBuilder finalWordBuilder = new StringBuilder();
    String[] arrString = createNewArray(pluralSubject);

    int lastIndex = arrString.length - 1;

    for (int j = 0; j < arrString.length; j++) {
      String s = arrString[j];
      StringBuilder wordBuilder = new StringBuilder();

      if (s.equals("ימים")) {
        wordBuilder.append("ימים");
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

    return TranslateWithoutInternet.removeUnderscore(finalWordBuilder.toString());
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

  private static boolean isSaveWordInTLXTableORConstes(String token, Connection conn)
      throws SQLException {

    if (token.startsWith("\"") && token.endsWith("\"")) {
      return true;
    }

    if (isNumericNumber(token)) {
      return true;
    }

    // the tokens in the database is revers
    String reversToken = reverseString(token);

    PreparedStatement statement =
        conn.prepareStatement("SELECT token FROM ZTRLPTLX WHERE token = ?");

    // Set the parameter.
    statement.setString(1, reversToken);

    // Execute the query.
    ResultSet rs = statement.executeQuery();

    // Check if the word is in the table.
    return rs.next();
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

  private static void checkFieldAndSubjectInDB(
      String subject,
      String field,
      String dataType,
      Connection conn,
      Statement stmt,
      ResultSet rs,
      boolean APPROVE_PRINTING) {
    String hebrewField = field;
    String englishField;
    String hebrewSubject = removeParenthesis(subject);
    String englishSubject;
    if (hebrewField == null) {
      englishField = null;
    } else {
      hebrewField = removeParenthesis(field);
      englishField =
          TranslateWithoutInternet.retrieveEnglishValuesFromHebrewValues(
              hebrewField, conn, APPROVE_PRINTING);
    }
    if (subject.equals("mainSubject")) {
      englishSubject = "main_class";
      hebrewSubject = "נושא_ראשי";
    } else {
      englishSubject =
          TranslateWithoutInternet.retrieveEnglishValuesFromHebrewValues(
              hebrewSubject, conn, APPROVE_PRINTING);
    }

    if (APPROVE_PRINTING) {
      System.out.println(
          "**********subject in hebrew: "
              + hebrewSubject
              + " field in hebrew: "
              + hebrewField
              + " subject in english: "
              + englishSubject
              + " field in english: "
              + englishField
              + "----------");
    }

    SaveToDatabase.addSubjectToDatabase(
        hebrewField,
        englishField,
        hebrewSubject,
        englishSubject,
        dataType,
        conn,
        stmt,
            APPROVE_PRINTING);
  }

  public static void main(String[] args) {}
}
