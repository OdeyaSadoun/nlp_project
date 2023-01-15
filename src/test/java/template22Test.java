import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
public class template22Test {

    //אם <שדה> כולל של כל ה-<נושא_ברבים> השייכים ל-<נושא> <אופרטור_השוואה>  <קבוע_מספרי>
    @Test
    public void templateTest() {
        NLPTemplate nlpTemplate1 = new NLPTemplate("אם מחיר כולל של כל ה-חולצות השייכים ל-חנות גדול מ 50");
        List<Pair<String, String>> subjectsAndFieldsList = new ArrayList<>(){};

        subjectsAndFieldsList.add(new Pair<>("shirt", "price"));
        subjectsAndFieldsList.add(new Pair<>("store", "shirt"));

        try {
            assertEquals(subjectsAndFieldsList, nlpTemplate1.readNLPTemplate());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
