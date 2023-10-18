import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class ClassifySentenceWithoutInternet {
  static String subject;
  static String pluralSubject;
  static String field;
  static String mainSubject = "mainSubject";


  public static void readTemplate(
          String sentence, Connection conn, Statement stmt, ResultSet rs, boolean printLogs, String englishSubject, String hebrewSubject, boolean withLevinshtainDistance, int levinshtainDistance) {
    String[] template = sentence.split(" ");
    List<String> lstTemplate = Arrays.asList(template);
    try {
      findSubjectAndField(lstTemplate, sentence, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
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
          boolean printLogs,
          String englishSubject,
          String hebrewSubject,
          boolean withLevinshtainDistance,
          int levinshtainDistance)
      throws SQLException {
    String dataType;

    for (int i = 0; i < lstTemplate.size(); i++) {

      String word = lstTemplate.get(i);
      word = Tools.removeParenthesis(word); // הסרת הסוגרים שגורמים לזיהוי לא תקין של מילים שמורות בTLX
      if (printLogs) {
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

      if (Tools.isNumericNumber(word)) {
        continue;
      }
      if (i + 2 < lstTemplate.size()) {
        // תוספת על הענין של אופרטורים 2 מילים לא שמורות ובינהן אופרטור שתיהן שדות
        // בנושא ראשי
        isSaveWord2 =
            isSaveWordInTLXTableORConstes(Tools.removeParenthesis(lstTemplate.get(i + 2)), conn);

        if (!isSaveWord && Tools.isOperator(Tools.removeParenthesis(lstTemplate.get(i + 1)))) {
          if (Tools.isOperator(Tools.removeParenthesis(lstTemplate.get(i + 2)))
              && i + 3 < lstTemplate.size()
              && !isSaveWordInTLXTableORConstes(Tools.removeParenthesis(lstTemplate.get(i + 3)), conn)) {
            // **update values: (second word)*/
            subject = mainSubject;
            field = Tools.changePluralWordToSingle(lstTemplate.get(i + 3));
            dataType = GetType.getLabel(field, sentence, false, conn, printLogs);

            if (printLogs) {
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

            checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
            // **update values: (first word) */
            subject = mainSubject;
            field = Tools.changePluralWordToSingle(word);
            dataType = GetType.getLabel(field, sentence, false, conn, printLogs);

            if (printLogs) {
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

            checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
            continue;
          }
          if (!isSaveWord2) {
            // **update values: (second word)*/
            subject = mainSubject;
            field = Tools.changePluralWordToSingle(lstTemplate.get(i + 2));
            dataType = GetType.getLabel(field, sentence, false, conn, printLogs);

            if (printLogs) {
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

            checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
            // **update values: (first word) */
            subject = mainSubject;
            field = Tools.changePluralWordToSingle(word);
            dataType = GetType.getLabel(field, sentence, false, conn, printLogs);

            if (printLogs) {
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

            checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);

            continue;
          }
        }
      }
      if (i + 1 < lstTemplate.size() && !isSaveWord) {
        // option5-6
        if (Tools.removeParenthesis(lstTemplate.get(i + 1)).equals("קיים")
            || Tools.removeParenthesis(lstTemplate.get(i + 1)).equals("לא")
                && i + 2 < lstTemplate.size()
                && Tools.removeParenthesis(lstTemplate.get(i + 2)).equals("קיים")) {
          subject = Tools.changePluralWordToSingle(word);
          field = null;
          dataType = GetType.getLabel(null, sentence, false, conn, printLogs);

          if (printLogs) {
            System.out.println(
                "----------subject: "
                    + subject
                    + " field: "
                    + field
                    + " type: "
                    + dataType
                    + "----------");
          }
          checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
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
          subject = Tools.changePluralWordToSingle(word);
          field = Tools.changePluralWordToSingle(lstTemplate.get(i + 1));
          dataType = GetType.getLabel(field, sentence, false, conn, printLogs);

          if (printLogs) {
            System.out.println(
                "----------subject: "
                    + subject
                    + " field: "
                    + field
                    + " type: "
                    + dataType
                    + "----------");
          }

          checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
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
              subject = Tools.changePluralWordToSingle(lstTemplate.get(i + 3));
              field = Tools.changePluralWordToSingle(word);
              dataType = GetType.getLabel(field, sentence, false, conn, printLogs);

              if (printLogs) {
                System.out.println(
                    "----------subject: "
                        + subject
                        + " field: "
                        + field
                        + " type: "
                        + dataType
                        + "----------");
              }

              checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
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
        if (isSaveWordInTLXTableORConstes(Tools.removeParenthesis(lstTemplate.get(i - 1)), conn))
          continue;
        field = Tools.changePluralWordToSingle(lstTemplate.get(i - 1));
        if (lstTemplate.get(i + 1).equals("כל") && i + 2 < lstTemplate.size()) {
          if (isSaveWordInTLXTableORConstes(Tools.removeParenthesis(lstTemplate.get(i + 2)), conn))
            continue;
          subject = Tools.changePluralWordToSingle(lstTemplate.get(i + 2));
        } else {
          if (isSaveWordInTLXTableORConstes(lstTemplate.get(i + 1), conn)) continue;
          subject = Tools.changePluralWordToSingle(lstTemplate.get(i + 1));
        }
        dataType = GetType.getLabel(field, sentence, false, conn, printLogs);

        if (printLogs) {
          System.out.println(
              "----------subject: "
                  + subject
                  + " field: "
                  + field
                  + " type: "
                  + dataType
                  + "----------");
        }

        checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
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
        isSaveWord = isSaveWordInTLXTableORConstes(Tools.removeParenthesis(lstTemplate.get(i - 1)), conn);
        if (!isSaveWord && i + 1 < lstTemplate.size()) {
          subject = Tools.changePluralWordToSingle(lstTemplate.get(i - 1));
          isSaveWord =
              isSaveWordInTLXTableORConstes(Tools.removeParenthesis(lstTemplate.get(i + 1)), conn);
          if (!isSaveWord
              && !Tools.isNumericNumber(subject)
              && !Tools.isNumericNumber(lstTemplate.get(i + 1))) {
            field = Tools.changePluralWordToSingle(lstTemplate.get(i + 1));
            dataType = GetType.getLabel(field, sentence, false, conn, printLogs);

            if (printLogs) {
              System.out.println(
                  "----------subject: "
                      + subject
                      + " field: "
                      + field
                      + " type: "
                      + dataType
                      + "----------");
            }

            checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
            continue;
          }
        }
      }

      // option3
      // 3. אם מילהלאשמורה  אינו/הוא  מילהשמורה  -  אזי הראשון הוא שדה  בתוך נושא מרכזי
      // 4.  אם מילהלאשמורה{[הוא] /מילהשמורה}  (למשל אופרטור) – אזי הראשון הוא שדה בתוך נושא מרכזי
      if ((word.equals("אם") || word.equals("וגם") || word.equals("או") || word.equals("עדכן"))
          && i + 2 < lstTemplate.size()) {
        isSaveWord = isSaveWordInTLXTableORConstes(Tools.removeParenthesis(lstTemplate.get(i + 1)), conn);
        isSaveWord2 =
            isSaveWordInTLXTableORConstes(Tools.removeParenthesis(lstTemplate.get(i + 2)), conn);
        if (lstTemplate.size()
            == 3) { // תיקון עבור משפט 3 מילים אם מילה לא שמורה מילה שמורה, הלא שמורה שדה בנושא
          // מרכזי
          if (!isSaveWord && isSaveWord2) {

            // **update values:*/
            subject = mainSubject;
            field = Tools.changePluralWordToSingle(lstTemplate.get(i + 1));
            dataType = GetType.getLabel(field, sentence, false, conn, printLogs);

            if (printLogs) {
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

            checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
            continue;
          }
        } else {
          // עבור התיקון מילה לא שמורה מילה שמורה מילה שמורה, הלא שמורה שדה בנושא מרכזי
          if (!isSaveWord) {
            if (i + 3 < lstTemplate.size()) {
              boolean isSaveWord3 =
                  isSaveWordInTLXTableORConstes(Tools.removeParenthesis(lstTemplate.get(i + 3)), conn);

              if (isSaveWord2 && isSaveWord3) {
                // **update values:*/
                subject = mainSubject;
                field = Tools.changePluralWordToSingle(lstTemplate.get(i + 1));
                dataType = GetType.getLabel(field, sentence, false, conn, printLogs);

                if (printLogs) {
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
                    subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
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
                    isSaveWordInTLXTableORConstes(Tools.removeParenthesis(lstTemplate.get(i + 4)), conn);
                if (!isSaveWord) {
                  // **update values:*/
                  subject = Tools.changePluralWordToSingle(lstTemplate.get(i + 4));
                  field = Tools.changePluralWordToSingle(lstTemplate.get(i + 1));
                  dataType = GetType.getLabel(field, sentence, false, conn, printLogs);

                  if (printLogs) {
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
                      subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
                  continue;
                }
              }
            }

            // option4
            // 4.  אם מילהלאשמורה{[הוא] /מילהשמורה}  (למשל אופרטור) – אזי הראשון הוא שדה בתוך נושא
            // מרכזי

            if (i + 2 < lstTemplate.size()) {
              isSaveWord =
                  isSaveWordInTLXTableORConstes(Tools.removeParenthesis(lstTemplate.get(i + 2)), conn);
              if (isSaveWord) {
                // **update values:*/
                subject = mainSubject;
                field = Tools.changePluralWordToSingle(lstTemplate.get(i + 1));
                dataType = GetType.getLabel(field, sentence, false, conn, printLogs);

                if (printLogs) {
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
                    subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
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
                  subject = Tools.changePluralWordToSingle(pluralSubject);
                  field = Tools.changePluralWordToSingle(lstTemplate.get(i + 1));
                  dataType = GetType.getLabel(field, sentence, true, conn, printLogs);

                  if (printLogs) {
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
                      subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);

                  if (i + 6 < lstTemplate.size()) {
                    isSaveWord =
                        isSaveWordInTLXTableORConstes(
                                Tools.removeParenthesis(lstTemplate.get(i + 6)), conn);
                    if (!isSaveWord) {
                      field = Tools.changePluralWordToSingle(lstTemplate.get(i + 6));
                      dataType = GetType.getLabel(field, sentence, true, conn, printLogs);

                      if (printLogs) {
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
                          subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
                    }
                  }
                  continue;
                }
              } else {
                if (i + 4 < lstTemplate.size()) {
                  // **update values:*/
                  pluralSubject = lstTemplate.get(i + 4);
                  subject = Tools.changePluralWordToSingle(pluralSubject);
                  field = Tools.changePluralWordToSingle(lstTemplate.get(i + 1));
                  dataType = GetType.getLabel(field, sentence, true, conn, printLogs);

                  if (printLogs) {
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
                      subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);

                  if (i + 5 < lstTemplate.size()) {
                    isSaveWord =
                        isSaveWordInTLXTableORConstes(
                                Tools.removeParenthesis(lstTemplate.get(i + 5)), conn);
                    if (!isSaveWord) {
                      field = Tools.changePluralWordToSingle(lstTemplate.get(i + 5));
                      dataType = GetType.getLabel(field, sentence, true, conn, printLogs);

                      if (printLogs) {
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
                          subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
                    }
                  }
                  continue;
                }
              }
            }
            // 10 אם מילהשמורה טקסט שמור [מספר בעברית]   מילהלאשמורה [מילהלאשמורה]   -  הראשון שדה
            // השני נושא ברבים אחריו שדה לוואי ו
            if (i + 2 < lstTemplate.size()
                && Tools.isNumericNumber(Tools.removeParenthesis(lstTemplate.get(i + 2)))) {
              if (i + 3 < lstTemplate.size()) {
                isSaveWord =
                    isSaveWordInTLXTableORConstes(Tools.removeParenthesis(lstTemplate.get(i + 3)), conn);
                if (!isSaveWord) {
                  // **update values:*/
                  pluralSubject = lstTemplate.get(i + 3);
                  subject = Tools.changePluralWordToSingle(Tools.changePluralWordToSingle(pluralSubject));
                  field = Tools.changePluralWordToSingle(lstTemplate.get(i + 1));
                  dataType = GetType.getLabel(field, sentence, true, conn, printLogs);

                  if (printLogs) {
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
                      subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);

                  if (i + 4 < lstTemplate.size()) {
                    isSaveWord =
                        isSaveWordInTLXTableORConstes(
                                Tools.removeParenthesis(lstTemplate.get(i + 4)), conn);
                    if (!isSaveWord) {
                      field = Tools.changePluralWordToSingle(lstTemplate.get(i + 4));
                      dataType = GetType.getLabel(field, sentence, true, conn, printLogs);

                      if (printLogs) {
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
                          subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
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
              isSaveWordInTLXTableORConstes(Tools.removeParenthesis(lstTemplate.get(i - 1)), conn);
          if (!isSaveWord && lstTemplate.get(i - 2).equals("עדכן")) {
            // update values:
            subject = mainSubject;
            field = Tools.changePluralWordToSingle(lstTemplate.get(i - 1));
            dataType = GetType.getLabel(field, sentence, false, conn, printLogs);

            if (printLogs) {
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

            checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
            isSaveWord =
                isSaveWordInTLXTableORConstes(Tools.removeParenthesis(lstTemplate.get(i + 1)), conn);
            if (isSaveWord) {
              continue;
            }
          }
        }
        if (!Tools.isNumericNumber(lstTemplate.get(i + 1))) {
          isSaveWord =
              isSaveWordInTLXTableORConstes(Tools.removeParenthesis(lstTemplate.get(i + 1)), conn);
          if (!isSaveWord
              && !lstTemplate.get(i + 1).startsWith("\"")
              && !lstTemplate.get(i + 1).endsWith("\"")) {
            // **update values:*/
            subject = Tools.changePluralWordToSingle(lstTemplate.get(i + 1));
            field = null;
            dataType = GetType.getLabel(null, sentence, false, conn, printLogs);

            if (printLogs) {
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

            checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
            continue;
          }
        }
      }

      // option12
      if (i + 1 < lstTemplate.size() && (word.equals("ו-") || word.equals("וגם"))) {
        isSaveWord = isSaveWordInTLXTableORConstes(Tools.removeParenthesis(lstTemplate.get(i + 1)), conn);
        if (!isSaveWord) {
          // **update values:*/
          subject = mainSubject;
          field = Tools.changePluralWordToSingle(lstTemplate.get(i + 1));
          dataType = GetType.getLabel(field, sentence, false, conn, printLogs);

          if (printLogs) {
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

          checkFieldAndSubjectInDB(subject, field, dataType, conn, stmt, rs, printLogs, englishSubject,hebrewSubject, withLevinshtainDistance, levinshtainDistance);
        }
      }
    }
  }


  private static boolean isSaveWordInTLXTableORConstes(String token, Connection conn)
      throws SQLException {

    if (token.startsWith("\"") && token.endsWith("\"")) {
      return true;
    }

    if (Tools.isNumericNumber(token)) {
      return true;
    }

    // the tokens in the database is revers
    String reversToken = Tools.reverseString(token);

    PreparedStatement statement =
        conn.prepareStatement("SELECT token FROM ZTRLPTLX WHERE token = ?");

    // Set the parameter.
    statement.setString(1, reversToken);

    // Execute the query.
    ResultSet rs = statement.executeQuery();

    // Check if the word is in the table.
    return rs.next();
  }

  private static void checkFieldAndSubjectInDB(
          String subject,
          String field,
          String dataType,
          Connection conn,
          Statement stmt,
          ResultSet rs,
          boolean printLogs,
          String englishMainSubject,
          String hebrewMainSubject, boolean withLevinshtainDistance, int levinshtainDistance) {
    String hebrewField = field;
    String englishField;
    String hebrewSubject = Tools.removeParenthesis(subject);
    //String englishSubject;
    if (hebrewField == null) {
      englishField = null;
    } else {
      hebrewField = Tools.removeParenthesis(field);
      englishField =
          TranslateWithoutInternet.retrieveEnglishValuesFromHebrewValues(
              hebrewField, conn, printLogs);
    }
    String englishSubject;
    if (subject.equals("mainSubject")) {
      englishSubject = englishMainSubject;
      hebrewSubject = hebrewMainSubject;
    } else {
      englishSubject =
          TranslateWithoutInternet.retrieveEnglishValuesFromHebrewValues(
              hebrewSubject, conn, printLogs);
    }

    if (printLogs) {
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
        printLogs,
        withLevinshtainDistance,
        levinshtainDistance);
  }

  public static void main(String[] args) {}
}
