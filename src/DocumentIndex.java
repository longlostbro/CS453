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
    HashMap<Integer, Integer> maxFrequency;
    HashMap<Integer,String> docSentences = new HashMap<>();
    public DocumentIndex()
    {
        stopWords = new StopWords().getStopWords();
        stemmer = new PorterStemmer();
        index = new TreeMap<>();
        maxFrequency = new HashMap<>();
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
            //printer.println(stem);
            int frequency = 0;
            for(Map.Entry<Integer, Integer> document : index.get(stem).entrySet())
            {
                frequency += document.getValue();
            }
            printer.println(String.format("%s\t%d",stem,frequency));
            //printer.println();
        }
        printer.println();
        printer.flush();
        printer.close();
    }

    int wordfreq = 0;
    int uniquecount = 0;
    private void indexDocument(File file) {
        int max_frequency = 0;
        int docNumber = Integer.parseInt(new String(file.getName()).replaceAll("[^0-9]+", ""));
        if(docNumber == 91)
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
        PrintWriter printer = null;
        try {
            FileWriter fw = new FileWriter("unique.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            printer = new PrintWriter(bw);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //words.removeAll(stopWords);
        for(int i = 0; i < words.size(); i++)
        {
            String stem = words.get(i);
            wordfreq++;
            if(!index.containsKey(stem))
            {
                uniquecount++;
                HashMap<Integer, Integer> docs = new HashMap<>();
                docs.put(docNumber, 1);
                if(max_frequency < 1)
                    max_frequency = 1;
                index.put(stem, docs);
            }
            else
            {
                HashMap<Integer, Integer> docs = index.get(stem);
                if(!docs.containsKey(docNumber))
                {
                    docs.put(docNumber, 1);
                    if(max_frequency < 1)
                        max_frequency = 1;
                }
                else
                {
                    int frequency = docs.get(docNumber)+1;
                    docs.put(docNumber,frequency);
                    if(max_frequency < frequency)
                        max_frequency = frequency;
                }
            }
            printer.println(String.format("%s\t%s",wordfreq, uniquecount));
        }
        printer.flush();
        printer.close();
        maxFrequency.put(docNumber,max_frequency);
        //System.out.println("Document: "+docNumber + " Words: "+words.size());
    }

    public void query(String q) {
        ArrayList<String> keywords = new ArrayList<>(Arrays.asList( q.replaceAll("-"," ").replaceAll(System.getProperty("line.separator"), " ").replaceAll("[^a-zA-Z ]", "").toLowerCase().replaceAll("\\p{Punct}+", "").trim().split("\\s+")));
        Map<Integer, Double> scoredDocuments = new TreeMap<>();
        for(int i = 0; i < number_of_documents; i++)
        {

            scoredDocuments.put(i+1, score(keywords,i+1));
        }
        SortedSet<Map.Entry<Integer,Double>> sorted = entriesSortedByValues(scoredDocuments);
        printResults(q.trim(), sorted);
    }
    static <K,V extends Comparable<? super V>>
    SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
                new Comparator<Map.Entry<K,V>>() {
                    @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        int res = -1*e1.getValue().compareTo(e2.getValue());
                        return res != 0 ? res : 1;
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
    private void printResults(String query, SortedSet<Map.Entry<Integer, Double>> scoredDocuments) {
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
        for(Map.Entry<Integer,Double> entry : scoredDocuments)
        {
            printer.println(String.format("%d\t%d\t%s\t%f", i++, entry.getKey(), docSentences.get(entry.getKey()), entry.getValue()));
            if(i == 11)
                break;
        }
        printer.println();
        printer.flush();
        printer.close();
    }

    public double score(ArrayList<String> query, int document_number)
    {
        double score = 0;
        query.removeAll(stopWords);
        for(String word : query)
        {
            score += getTF(word, document_number) * getIDF(word);
        }
        return score;
    }

    public double getTF(String word, int docNumber)
    {
        if(docNumber == 33)
            System.out.print("");
        String stem = stemmer.stem(word);
        if(index.containsKey(stem) && index.get(stem).containsKey(docNumber))
        {
            double doc = (double)index.get(stem).get(docNumber)/(double)maxFrequency.get(docNumber);
            return doc;
        }
        return 0;
    }

    public double getIDF(String word)
    {
        String stem = stemmer.stem(word);
        if(index.containsKey(stem))
        {
            double n_sub_w = index.get(stem).keySet().size();
            return (Math.log(number_of_documents / n_sub_w))/Math.log(2);
        }
        return 0;
    }
}
