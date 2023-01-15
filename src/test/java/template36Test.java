import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
public class template36Test {

    //אם  הוסק ל-<נושא> ש-"מלל מסקנה"
    @Test
    public void templateTest() {
        NLPTemplate nlpTemplate1 = new NLPTemplate("אם הוסק ל-עובד ש-\"כלום כלום\"");
        List<Pair<String, String>> subjectsAndFieldsList = new ArrayList<>(){};

        subjectsAndFieldsList.add(new Pair<>("employee", null));


        try {
            assertEquals(subjectsAndFieldsList, nlpTemplate1.readNLPTemplate());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
