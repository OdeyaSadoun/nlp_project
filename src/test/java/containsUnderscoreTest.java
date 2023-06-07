//import org.junit.Test;
//import static org.junit.Assert.assertEquals;
//
//public class containsUnderscoreTest {
//    @Test
//    public void testWithUnderScore1() {
//        String word = "hi_";
//        NLPTemplate template = new NLPTemplate("");
//        assertEquals(true, template.containsUnderscore(word));
//    }
//    @Test
//    public void testWithUnderScore2() {
//        String word = "h_i";
//        NLPTemplate template = new NLPTemplate("");
//        assertEquals(true, template.containsUnderscore(word));
//    }
//    @Test
//    public void testWithUnderScore3() {
//        String word = "_hi";
//        NLPTemplate template = new NLPTemplate("");
//        assertEquals(true, template.containsUnderscore(word));
//    }
//    @Test
//    public void testWithOutUnderScore() {
//        String word = "hi";
//        NLPTemplate template = new NLPTemplate("");
//        assertEquals(false, template.containsUnderscore(word));
//    }
//}
