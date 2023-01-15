import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
public class template29Test {

    //אם  <שדה> של <נושא> <לוואי> השייך ל-<נושא> <אופרטור_השוואה> <ערך>
    @Test
    public void templateTest() {
        NLPTemplate nlpTemplate1 = new NLPTemplate("אם משכורת של עובד חרוץ השייך ל-מנהל גדול מ 50");
        List<Pair<String, String>> subjectsAndFieldsList = new ArrayList<>(){};

        subjectsAndFieldsList.add(new Pair<>("worker", "salary"));
        subjectsAndFieldsList.add(new Pair<>("worker", "diligent"));
        subjectsAndFieldsList.add(new Pair<>("manager", "worker"));

        try {
            assertEquals(subjectsAndFieldsList, nlpTemplate1.readNLPTemplate());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
