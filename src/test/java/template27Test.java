import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
public class template27Test {

    //אם  <שדה> של <נושא> <אופרטור_נומרי> <שדה> של <נושא>  הוא להלן {משתנה מקומי}
    @Test
    public void templateTest() {
        NLPTemplate nlpTemplate1 = new NLPTemplate("אם מחיר של בגד + מחיר של מוצר הוא להלן");
        List<Pair<String, String>> subjectsAndFieldsList = new ArrayList<>(){};

        subjectsAndFieldsList.add(new Pair<>("garment", "price"));
        subjectsAndFieldsList.add(new Pair<>("product", "price"));

        try {
            assertEquals(subjectsAndFieldsList, nlpTemplate1.readNLPTemplate());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
