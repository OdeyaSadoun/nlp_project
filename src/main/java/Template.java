import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Template {

    String sentence;
    String subject;
    String field;
    boolean success;
    String subSubject;
    String subField;
    int indexSub;
    int indexFil;

    FileWriter f = new FileWriter("./localDB.txt");

    public Template(String sen) throws IOException {
        sentence = sen;
        subject = "";
        subSubject = "";
        subField = "";
        field = "";
        success = false;
        indexSub = -1;
        indexFil = -1;
    }

    public void readTemplate(){
        String[] template = sentence.split(" ");
        List<String> lstTemplate = Arrays.asList(template);

        if (template[0].equals("אם"))
            ifSentence(lstTemplate);
        else //אזי
            thanSentence(lstTemplate);
    }

    private void thanSentence(List<String> lstTemplate) {

        try {
            //טמפלטים שאין להם שדות ונושאים להכניס לדטהבייס:
            //14.	אזי קבע שגיאה, הודע "מלל הודעה"
            if (lstTemplate.get(2).equals("שגיאה,")) {
                System.out.println("There is no subjects and fields");
                return;
            }

            // V 1.	אזי <נושא> הוא <לוואי>
            else if (lstTemplate.get(2).equals("הוא")) {
                indexSub = 1;
                indexFil = 3;
                subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
            }

            // V 2.	אזי בטל ל-<נושא> ש-"מלל מסקנה"
            // V 4.	אזי הסק ל-<נושא> ש-"מלל מסקנה"
            else if (lstTemplate.get(1).equals("בטל") || lstTemplate.get(1).equals("הסק")) {
                indexSub = 2;
                indexFil = -1;
                subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
            }

            // V 3.	אזי הוסף <קבוע_מספרי> ל-<שדה> של <נושא>
            else if (lstTemplate.get(1).equals("הוסף")) {
                indexSub = 5;
                indexFil = 3;
                subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
            }

            // V 13.	אזי קבע ל- <שדה> של <נושא> התראת <סוג> <דרגת_חומרה>, הודע "מלל הודעה", המלץ לפתוח <נושא> עם (<שדה> של <נושא> ל- <ערך>, <שדה> של <נושא> ל- <ערך>)
            else if (lstTemplate.get(1).equals("קבע") && lstTemplate.get(6).equals("התראת")) {
                int i = lstTemplate.indexOf("עם");

                //we split the string to 2 substrings
                List<String> str1 = new ArrayList<>(lstTemplate.subList(0, i + 1));
                List<String> str2 = new ArrayList<>(lstTemplate.subList(i + 1, lstTemplate.size()));

                //we need to remove the ()
                String subString = str2.get(0).substring(1);
                str2.remove(0);
                str2.add(0, subString);

                //str1:
                //check the subjects and fields exist in this sentence:
                findFieldOfSubject(str1);

                //subject2 to str1
                indexSub = str1.size() - 2;
                indexFil = -1;
                subjectAndFieldInEnglish(indexSub, indexFil, str1);

                //str2:
                //check the subjects and fields exist in this sentence:
                findFieldOfSubject(str2);
            }

            // V 5.	אזי הצג "מלל", ברר <שדה> של <נושא> במטרה "מלל"
            // V 6.	אזי עדכן <שדה> של <נושא> ל- <אופרטור_ערך> מבין (<קבוע_מספרי>, <קבוע_מספרי>)
            // V 7.	אזי עדכן <שדה> של <נושא> ל- <ערך>
            //8.	אזי עדכן <שדה> של <נושא> ל- <קבוע_מספרי>  <אופרטור_נומרי> מספר ה-<נושא_ברבים>
            //9.	אזי עדכן <שדה> של <נושא> ל- <שדה> כולל של כל ה- <נושא_ברבים>
            //10.	אזי עדכן <שדה> של <נושא> ל- <שדה> כולל של כל ה- <נושא_ברבים> השייכים ל-<נושא>
            // V 11.	אזי עדכן <שדה> של <נושא> ל- ערך בטבלת <שם_טבלה> לפי <שדה> של <נושא>
            // V 12.	אזי קבע ל- <שדה> של <נושא> בעיית <סוג> <דרגת_חומרה>, הודע "מלל הודעה", המלץ "מלל המלצה "
            else {
                //check the subjects and fields exist in this sentence:
                findFieldOfSubject(lstTemplate);

                //8.	אזי עדכן <שדה> של <נושא> ל- <קבוע_מספרי>  <אופרטור_נומרי> מספר ה-<נושא_ברבים>
                if (lstTemplate.size() >= 8 && lstTemplate.get(8).equals("מספר")) {
                    //subject2:
                    indexSub = 9;
                    indexFil = -1;
                    subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
                }

                //9.	אזי עדכן <שדה> של <נושא> ל- <שדה> כולל של כל ה- <נושא_ברבים>
                //10.	אזי עדכן <שדה> של <נושא> ל- <שדה> כולל של כל ה- <נושא_ברבים> השייכים ל-<נושא>
                else if (lstTemplate.size() > 7 && lstTemplate.get(7).equals("כולל")) {
                    indexSub = 11;
                    indexFil = 6;
                    subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);

                    if (lstTemplate.size() > 12 && lstTemplate.get(12).equals("השייכים")) {
                        // subject3
                        indexSub = 13;
                        indexFil = -1;
                        subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ifSentence(List<String> lstTemplate) {

        //טמפלטים שאין להם שדות ונושאים להכניס לדטהבייס:
        //15.	אם  <אופרטור_ערך>  מבין (<קבוע_מספרי>, <קבוע_מספרי> >) <אופרטור_השוואה> <קבוע_מספרי>
        //37.	אם  מכיוון ש-"מלל חופשי"
        //40.	אם  עברו <אופרטור_תאריך> מ- <קבוע_מספרי> <ביטוי_זמן> מאז <קבוע_תאריך>
        if(lstTemplate.get(1).equals("עברו") || lstTemplate.get(1).equals("מכיוון") || lstTemplate.get(2).equals("מבין")){
            System.out.println("There is no subjects and fields");
            return;
        }

        // V 16.	אם  <נושא> <לוואי> השייך ל-<נושא> קיים
        else if(lstTemplate.size() == 6 && lstTemplate.get(3).equals("השייך") && lstTemplate.get(5).equals("קיים")){
            indexSub = 1; indexFil = 2;
            subjectAndFieldInEnglish(indexSub,indexFil,lstTemplate);

            //we want the subject without preconditions:
            // subject
            indexSub = 4; indexFil = -1;
            subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
        }

        // V 17.	אם  <נושא> אינו <לוואי>
        // V 18.	אם  <נושא>  הוא <לוואי>
        else if(lstTemplate.get(2).equals("אינו") || lstTemplate.get(2).equals("הוא")){
            indexSub = 1; indexFil = 3;
            subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
        }

        //19.	אם  <נושא> קיים
        else if(lstTemplate.get(2).equals("קיים") && lstTemplate.size() == 3){
            // subject
            indexSub = 1; indexFil = -1;
            subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
        }

        //20.	אם  <נושא> שייך ל-<נושא>
        else if(lstTemplate.get(2).equals("שייך")){
            // subject1
            indexSub = 1; indexFil = -1;
            subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);

            // subject2
            indexSub = 3; indexFil = -1;
            subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
        }

        //21.	אם  <שדה> <אופרטור_סכימה> של כל ה- <נושא_ברבים> <אופרטור_השוואה>  <קבוע_מספרי>
        //22.	אם  <שדה> כולל של כל ה-<נושא_ברבים> השייכים ל-<נושא> <אופרטור_השוואה>  <קבוע_מספרי>
        else if(lstTemplate.get(3).equals("של") && lstTemplate.get(4).equals("כל")){

            indexFil = 1;

            if(lstTemplate.get(5).equals("ה-")) {
                // subjects
                indexSub = 6;
                subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
            }
            else{
                // subjects
                indexSub = 5;
                subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);

                //subject2
                indexSub = 7; indexFil = -1;
                subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
            }
        }

        //23.	אם  <שדה> של <אופרטור_קצה> <קבוע_מספרי> <נושא_ברבים> <אופרטור_השוואה> <ערך>
        //מקרה הקצה של שדה של נושא- נשים אותו לפני השליחה לאופציה של שדה של נושא
        else if(lstTemplate.get(2).equals("של") && (lstTemplate.get(3).equals("לפחות") ||
                (lstTemplate.get(3).equals("לכל") && lstTemplate.get(4).equals("היותר")))){
            indexSub = 1; indexFil = 5;
            subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
        }

        //36.	אם  הוסק ל-<נושא> ש-"מלל מסקנה"
        else if(lstTemplate.get(1).equals("הוסק")){
            indexSub = 2; indexFil = -1;
            subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
        }

        //38.	אם  מספר ה-<נושא_ברבים> <אופרטור_השוואה>  <קבוע_מספרי>
        else if(lstTemplate.get(1).equals("מספר")){
            indexSub = 2; indexFil = -1;
            subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
        }

        //24.	אם  <שדה> של <נושא> הוא אחד מתוך (<ערך>,<ערך>)
        //25.	אם  <שדה> של <נושא> <אופרטור_השוואה> <ערך>
        //26.	אם  <שדה> של <נושא> <אופרטור_נומרי> <קבוע_מספרי> <אופרטור_השוואה> <ערך>
        //27.	אם  <שדה> של <נושא> <אופרטור_נומרי> <שדה> של <נושא>  הוא להלן {משתנה מקומי}
        //28.	אם  <שדה> של <נושא> <אופרטור_קיום>
        //30.	אם  <שדה> של <נושא> הוא בתחום <קבוע_מספרי> עד <קבוע_מספרי>
        //32.	אם  <שדה> של <נושא> החל מתו <קבוע_מספרי> הוא  "ערך"
        //35.	אם  <שדה> של <נושא> מתחיל ב-"ערך"
        //39.	אם  נדרס <שדה> של <נושא>
        else{
            //check the subjects and fields exist in this sentence:
            findFieldOfSubject(lstTemplate);

            //29.	אם  <שדה> של <נושא> <לוואי> השייך ל-<נושא> <אופרטור_השוואה> <ערך>
            if((lstTemplate.size() >= 8) && lstTemplate.get(5).equals("השייך")){
                //הלוואי של משפט 29 הוא בעצם שייך לנושא הראשון שלו יש כבר שדה- כלומר יהיה 2 שדות לנושא הראשון
                //לצורך כך, נשלח שוב את הנושא לבדיקה של השדה הנוכחי בדטהבייס, כדי שנדע להצמיד את השדה לנושא המתאים
                // subject1
                indexSub = 3; indexFil = 4;
                subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);

                // subject2
                indexSub = 6; indexFil = -1;
                subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
            }

            //31.	אם  <שדה> של <נושא> הוא ה-<שדה> <אופרטור_ערך>  מבין כל ה-<נושא_ברבים>
            else if(lstTemplate.size() == 10 && lstTemplate.get(4).equals("הוא") && !(lstTemplate.get(5).equals("בתחום") || lstTemplate.get(5).equals("אחד"))) {
                //הבנתי כי השדה השני שייך לנושא ברבים - אם זה לא נכון, אז נצטרך לשנות את זה ולשלוח את הנושא הראשון שבמקום 3
                indexSub = 9; indexFil = 5;
                subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
            }

            //33.	אם  <שדה> של <נושא> השייך ל-<נושא> <אופרטור_השוואה> <ערך>
            //34.	אם  <שדה> של <נושא> השייך ל-<נושא> הוא <שדה> <אופרטור_ערך> מבין כל ה-<נושא_ברבים>
            else if(lstTemplate.get(4).equals("השייך")){
                indexSub = 5; indexFil = -1;
                subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);

                //משפט 34
                if(lstTemplate.size() == 12){
                    indexSub = 11; indexFil = 7;
                    subjectAndFieldInEnglish(indexSub, indexFil, lstTemplate);
                }
            }
        }
    }

    private boolean checkIfExistSubjectAndFieldInDB(String subject, String field){

        boolean isExistSubject = checkIfExistSubjectInDB(subject,field);
        if(!isExistSubject){
            return false;
        }
        boolean isExistField = checkIfExistFieldInDB(subject,field);
        if(!isExistField){
            return false;
        }
        
        return true;
    }

    private boolean checkIfExistSubjectInDB(String subject, String field){

        writeToFile(f, subject, field);

        //check if subject is exist in db
        boolean isExistInDB= false;

        //we need to use query to change this flag to true if is exists
        //we need to check the subject in "רבים"

        if(!isExistInDB){
            if(field == ""){
                addSubjectToDB(subject);
            }
            else {
                addSubjectAndFieldToDB(subject, field);
            }
            return false;
        }

        return true;
    }

    private boolean checkIfExistFieldInDB(String subject, String field){
        //check if field is exist in db
        boolean isExistInDB= false;
        //we need to use query to change this flag to true if is exists
        if(!isExistInDB){
            addFieldToExistSubjectInDB(subject,field);
            return false;
        }

        return true;
    }

    private void findFieldOfSubject(List<String> lstTemplate){

        /*
        * We decided to treat the case where there is twice the conjunction "subject field" in this form of Indexof and lastindexof
        *  for reasons of efficiency.
        * Of course, if there are more than 2 combinations,
        *  we will have to go through a while loop and each time download the first element in the list.
        */

        int i = lstTemplate.indexOf("של");
        if (!lstTemplate.get(i + 1).equals("כל")) {
            subjectAndFieldInEnglish(i + 1, i - 1 , lstTemplate);
        }
        int j = lstTemplate.lastIndexOf("של");
        if (j != i && !lstTemplate.get(j + 1).equals("כל")) {
            subjectAndFieldInEnglish(j + 1, j - 1 , lstTemplate);
        }
    }

    private void addSubjectAndFieldToDB(String subject, String field){
        //add subject to db and field to this subject
    }

    private void addFieldToExistSubjectInDB(String subject, String field){
        //add field to the subject that exist in db
    }

    private void addSubjectToDB(String subject){
        //add subject without a field
    }

    private void subjectAndFieldInEnglish(int iSubject, int iField, List<String> lstTemplate){
        try {
            subject = lstTemplate.get(iSubject);   // subject
            subSubject = subject.substring(subject.indexOf("-") + 1).trim();
            Translator tSubject = new Translator(subSubject);
            Translator tField;

            if(iField == -1){
                subField = "";
                success = checkIfExistSubjectInDB(tSubject.translate("he", "en", tSubject.getWord()), subField);
                return;
            }
            else{
                field = lstTemplate.get(iField);     // field
                subField = field.substring(field.indexOf("-") + 1).trim();
                tField = new Translator(subField);
                success = checkIfExistSubjectAndFieldInDB(tSubject.translate("he", "en", tSubject.getWord()), tField.translate("he", "en", tField.getWord()));
            }
            return;

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void writeToFile(FileWriter f, String sub, String fil){
        String text="";
        if(fil == ""){
            text="Subject is: " +sub;
        }
        else{
            text="Subject is: " +sub+ ", Field is: "+ fil;
        }
        try {
            FileWriter fWriter = new FileWriter("localDB.txt", true);
            fWriter.write(text+"\n");
            fWriter.close();
            System.out.println(text);
            System.out.println("File is created successfully with the content.");
        }
        catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    public static List<String> readFileAsListString(String fileName)throws Exception
    {
        Path path = Paths.get(fileName);
        byte[] bytes = Files.readAllBytes(path);
        List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
        return allLines;
    }

    public static void main(String [] args){
//        try {
//            Template t = new Template("אם בקבוק תכלת השייך ל-ילד קיים");
//            t.readTemplate();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        List<String> data = new ArrayList<String>();
        try {
            data = readFileAsListString("sentences.txt");
            System.out.println(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Template t;
        for( int i = 0; i < data.size(); i++){
            try {
                t = new Template(data.get(i));
                System.out.println(data.get(i));
                t.readTemplate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
