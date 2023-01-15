import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
public class template34Test {

    //אם  <שדה> של <נושא> השייך ל-<נושא> הוא <שדה> <אופרטור_ערך> מבין כל ה-<נושא_ברבים>
    @Test
    public void templateTest() {
        NLPTemplate nlpTemplate1 = new NLPTemplate("אם בגד של ילד השייך ל-משפחה הוא בד יקר מבין כל ה-בגדים");
        List<Pair<String, String>> subjectsAndFieldsList = new ArrayList<>(){};

        subjectsAndFieldsList.add(new Pair<>("child", "garment"));
        subjectsAndFieldsList.add(new Pair<>("family", "child"));
        subjectsAndFieldsList.add(new Pair<>("garment", "cloth"));


        try {
            assertEquals(subjectsAndFieldsList, nlpTemplate1.readNLPTemplate());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
