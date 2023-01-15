import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
public class template39Test {

    //אם  נדרס <שדה> של <נושא>
    @Test
    public void templateTest() {
        NLPTemplate nlpTemplate1 = new NLPTemplate("אם נדרס כדור של ילד");
        List<Pair<String, String>> subjectsAndFieldsList = new ArrayList<>(){};

        subjectsAndFieldsList.add(new Pair<>("child", "ball"));


        try {
            assertEquals(subjectsAndFieldsList, nlpTemplate1.readNLPTemplate());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
