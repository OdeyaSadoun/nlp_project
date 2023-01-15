import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
public class template30Test {

    //אם  <שדה> של <נושא> הוא בתחום <קבוע_מספרי> עד <קבוע_מספרי>
    @Test
    public void templateTest() {
        NLPTemplate nlpTemplate1 = new NLPTemplate("אם גיל של ילד הוא בתחום 5 עד 15");
        List<Pair<String, String>> subjectsAndFieldsList = new ArrayList<>(){};

        subjectsAndFieldsList.add(new Pair<>("child", "age"));

        try {
            assertEquals(subjectsAndFieldsList, nlpTemplate1.readNLPTemplate());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
