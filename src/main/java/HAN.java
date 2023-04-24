//import edu.stanford.nlp.pipeline.*;
//import edu.stanford.nlp.ling.*;
//import edu.stanford.nlp.semgraph.*;
//import edu.stanford.nlp.util.CoreMap;
//import java.util.List;
//import java.util.Properties;
//
public class HAN {
//
//    public static void main(String[] args) {
//        String text = "The boy is a student. The dog is white.";
//
//        // set up pipeline properties
//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse");
//
//        // set up pipeline
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//
//        // create a document object
//        CoreDocument document = new CoreDocument(text);
//
//        // annotate the document
//        pipeline.annotate(document);
//
//        // loop over the sentences in the document
//        for (CoreMap sentence : sentences) {
//            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
//            for (IndexedWord root : dependencies.getRoots()) {
//                if (root.tag().startsWith("VB")) { // check if the root word is a verb
//                    for (SemanticGraphEdge edge : dependencies.outgoingEdgeList(root)) {
//                        if (edge.getRelation().getShortName().equals("nsubj")) { // check if the edge is a subject relation
//                            IndexedWord subject = edge.getTarget(); // get the subject
//                            String subjectWord = subject.word(); // get the text of the subject
//                            String currentField = null;
//                            for (SemanticGraphEdge subjectEdge : dependencies.outgoingEdgeList(subject)) {
//                                if (subjectEdge.getRelation().getShortName().equals("dobj") || subjectEdge.getRelation().getShortName().equals("nmod")) {
//                                    IndexedWord field = subjectEdge.getTarget();
//                                    currentField = field.word();
//                                }
//                            }
//                            System.out.println("The main subject of the sentence is: " + subjectWord);
//                            System.out.println("The current field of the subject is: " + currentField);
//                        }
//                    }
//                }
//            }
//        }
//
//
    }
//}
