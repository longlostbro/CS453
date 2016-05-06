/**
 * Created by longl on 5/6/2016.
 */
public class DocumentQueryExecutor {
    public static void main(String[] args) {
        DocumentIndex index = new DocumentIndex();
        index.indexDocuments("wiki");
        index.query("killing incident");
        index.query("suspect charged with murder\n");
        index.query("court");
        index.query("jury sentenced murderer to prison\n");
        index.query("movie");
        index.query("entertainment films\n");
        index.query("court appeal");
        index.query("action film producer");
        index.query("drunk driving accusations");
        index.query("actor appeared in movie premiere");
    }
}
