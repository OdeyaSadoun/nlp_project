import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
public class template13Test {

    //אזי <נושא> הוא <לוואי>
    @Test
    public void templateTest() {
        NLPTemplate nlpTemplate1 = new NLPTemplate("אזי קבע ל- רווח של חנות התראת מכירה בינוני הודע \"בעיה חמורה\" המלץ לפתוח קריאה עם (בונוס של עובד ל- 0, משכורת של עובד ל- 30)");
        List<Pair<String, String>> subjectsAndFieldsList = new ArrayList<>(){};

        subjectsAndFieldsList.add(new Pair<>("store", "profit"));
        subjectsAndFieldsList.add(new Pair<>("call", null));
        subjectsAndFieldsList.add(new Pair<>("employee", "bonus"));
        subjectsAndFieldsList.add(new Pair<>("employee", "salary"));
        try {
            assertEquals(subjectsAndFieldsList, nlpTemplate1.readNLPTemplate());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
