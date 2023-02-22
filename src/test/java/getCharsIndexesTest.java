import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class getCharsIndexesTest {
    @Test
    public void testWithIndexes() {
        String word = "hii$i$i";
        NLPTemplate template = new NLPTemplate("");
        List<Integer> indexes = new ArrayList<>();
        indexes.add(3);
        indexes.add(5);
        assertEquals(indexes, template.getCharsIndexes(word, '$'));
    }

    @Test
    public void testWithoutIndexes() {
        String word = "hiiii";
        NLPTemplate template = new NLPTemplate("");
        List<Integer> indexes = new ArrayList<>();
        assertEquals(indexes, template.getCharsIndexes(word, '$'));
    }
}
