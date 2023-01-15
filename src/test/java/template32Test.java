import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
public class template32Test {

    //אם  <שדה> של <נושא> החל מתו <קבוע_מספרי> הוא  "ערך"
    @Test
    public void templateTest() {
        NLPTemplate nlpTemplate1 = new NLPTemplate("אם כרטיס של מנהל החל מתו 7 הוא 20");
        List<Pair<String, String>> subjectsAndFieldsList = new ArrayList<>(){};

        subjectsAndFieldsList.add(new Pair<>("manager", "card"));

        try {
            assertEquals(subjectsAndFieldsList, nlpTemplate1.readNLPTemplate());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
