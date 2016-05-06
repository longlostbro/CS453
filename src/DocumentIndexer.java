import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by longl on 5/6/2016.
 */
public class DocumentIndexer {
    HashSet<String> stopWords = new HashSet<>();
    PorterStemmer stemmer;
    TreeMap<String,HashMap<Integer,Integer>> index;
    public DocumentIndexer()
    {
        stopWords = new StopWords().getStopWords();
        stemmer = new PorterStemmer();
        index = new TreeMap<>();
    }

    public void indexDocuments(String path_to_folder)
    {
        File folder = new File(path_to_folder);

        for (final File doc : folder.listFiles()) {
            indexDocument(doc);
        }
    }

    private void indexDocument(File file) {
        int docNumber = Integer.parseInt(new String(file.getName()).replaceAll("[^0-9]+", ""));
        List<String> doc = new ArrayList<>();
        List<String> words = new ArrayList<>();
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            StringBuilder everything = new StringBuilder();
            String line;
            while( (line = in.readLine()) != null) {
                everything.append(line);
            }
            words.addAll(Arrays.asList(everything.toString().replaceAll("[^a-zA-Z ]", "").toLowerCase().replaceAll("-"," ").replaceAll("\\p{Punct}+", "").replaceAll(System.getProperty("line.separator"), "").trim().split("\\s+")));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        words.remove("");
        words.removeAll(stopWords);
        for(int i = 0; i < words.size(); i++)
        {
            String stem = stemmer.stem(words.get(i));
            words.set(i,stem);

            if(!index.containsKey(stem))
            {
                HashMap<Integer, Integer> docs = new HashMap<>();
                docs.put(docNumber, 1);
                index.put(stem, docs);
            }
            else
            {
                HashMap<Integer, Integer> docs = index.get(stem);
                if(!docs.containsKey(docNumber))
                {
                    docs.put(docNumber, 1);
                }
                else
                {
                    docs.put(docNumber,docs.get(docNumber)+1);
                }
            }
        }
        System.out.println("Document: "+docNumber + " Words: "+words.size());
    }
}
