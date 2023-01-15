import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
public class template10Test {

    //אזי <נושא> הוא <לוואי>
    @Test
    public void templateTest() {
        NLPTemplate nlpTemplate1 = new NLPTemplate("אזי עדכן משכורת של עובד ל- סכום כולל של כל ה- הוצאות השייכים ל-עובד");
        List<Pair<String, String>> subjectsAndFieldsList = new ArrayList<>(){};

        subjectsAndFieldsList.add(new Pair<>("employee", "salary"));
        subjectsAndFieldsList.add(new Pair<>("expenditure", "amount"));
        subjectsAndFieldsList.add(new Pair<>("employee", null));
        try {
            assertEquals(subjectsAndFieldsList, nlpTemplate1.readNLPTemplate());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
