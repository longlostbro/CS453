import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by longl on 5/6/2016.
 */
public class DocumentIndex {
    HashSet<String> stopWords = new HashSet<>();
    PorterStemmer stemmer;
    int number_of_documents;
    TreeMap<String,HashMap<Integer,Integer>> index;
    HashMap<Integer,String> docSentences = new HashMap<>();
    public DocumentIndex()
    {
        stopWords = new StopWords().getStopWords();
        stemmer = new PorterStemmer();
        index = new TreeMap<>();
        number_of_documents = 0;
    }

    public void indexDocuments(String path_to_folder)
    {
        File folder = new File(path_to_folder);

        for (final File doc : folder.listFiles()) {
            indexDocument(doc);
            number_of_documents++;
        }
        PrintWriter printer = null;
        try {
            FileWriter fw = new FileWriter("index.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            printer = new PrintWriter(bw);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String stem : index.keySet())
        {
            printer.println(stem);
            for(Map.Entry<Integer, Integer> document : index.get(stem).entrySet())
            {
                printer.println(String.format("%d, %d",document.getKey(),document.getValue()));
            }
            printer.println();
        }
        printer.println();
        printer.flush();
        printer.close();
    }

    private void indexDocument(File file) {
        int docNumber = Integer.parseInt(new String(file.getName()).replaceAll("[^0-9]+", ""));
        if(docNumber == 122)
            System.out.println();
        List<String> doc = new ArrayList<>();
        List<String> words = new ArrayList<>();
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            StringBuilder everything = new StringBuilder();
            String line;
            while( (line = in.readLine()) != null) {
                everything.append(line+" ");
            }
            docSentences.put(docNumber, everything.toString().substring(0,everything.toString().indexOf('.')+1));
            words.addAll(Arrays.asList(everything.toString().replaceAll("-"," ").replaceAll(System.getProperty("line.separator"), " ").replaceAll("[^a-zA-Z ]", "").toLowerCase().replaceAll("\\p{Punct}+", "").trim().split("\\s+")));
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
        //System.out.println("Document: "+docNumber + " Words: "+words.size());
    }

    public void query(String q) {
        List<String> keywords = Arrays.asList(q.replaceAll("-"," ").replaceAll(System.getProperty("line.separator"), " ").replaceAll("[^a-zA-Z ]", "").toLowerCase().replaceAll("\\p{Punct}+", "").trim().split("\\s+"));
        TreeMap<Double, Integer> scoredDocuments = new TreeMap<>(Collections.reverseOrder());
        for(int i = 0; i < number_of_documents; i++)
        {
            scoredDocuments.put(score(keywords,i),i);
        }
        printResults(q.trim(), scoredDocuments);
    }

    private void printResults(String query, TreeMap<Double, Integer> scoredDocuments) {
        PrintWriter printer = null;
        try {
            FileWriter fw = new FileWriter("results.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            printer = new PrintWriter(bw);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        printer.println(String.format("Query: %s",query));
        printer.println("Ranked Documents\tDocument ID\tFirst Sentence\tRanking Score");
        int i = 1;
        for(Map.Entry<Double,Integer> entry : scoredDocuments.entrySet())
        {
            printer.println(String.format("%d\t%d\t%s\t%f", i++, entry.getValue(), docSentences.get(entry.getValue()), entry.getKey()));
            if(i == 11)
                break;
        }
        printer.println();
        printer.flush();
        printer.close();
    }

    public double score(List<String> query, int document_number)
    {
        double score = 0;
        for(String word : query)
        {
            score += getTF(word, document_number) * getIDF(word);
        }
        return score;
    }

    public double getTF(String word, int docNumber)
    {
        String stem = stemmer.stem(word);
        if(index.containsKey(stem) && index.get(stem).containsKey(docNumber))
        {
            return index.get(stem).get(docNumber);
        }
        return 0;
    }

    public double getIDF(String word)
    {
        String stem = stemmer.stem(word);
        if(index.containsKey(stem))
        {
            double n_sub_w = index.get(stem).keySet().size();
            return Math.log(number_of_documents / n_sub_w);
        }
        return 0;
    }
}
