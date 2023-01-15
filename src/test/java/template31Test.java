import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
public class template31Test {

    //אם  <שדה> של <נושא> הוא ה-<שדה> <אופרטור_ערך>  מבין כל ה-<נושא_ברבים>
    @Test
    public void templateTest() {
        NLPTemplate nlpTemplate1 = new NLPTemplate("אם משכורת של עובד הוא ה-סכום גבוה מבין כל ה-משכורות");
        List<Pair<String, String>> subjectsAndFieldsList = new ArrayList<>(){};

        subjectsAndFieldsList.add(new Pair<>("employee", "salary"));
        subjectsAndFieldsList.add(new Pair<>("sum", "salary"));

        try {
            assertEquals(subjectsAndFieldsList, nlpTemplate1.readNLPTemplate());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
