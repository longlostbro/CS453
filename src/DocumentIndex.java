import javafx.util.Pair;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;

/**
 * Created by dtaylor on 5/31/2016.
 */
public class DocumentIndex
{
    public static DocumentIndex _instance;
    HashSet<String> stopWords = new HashSet<>();
    int totalWordCount = 0;
    int number_of_documents;
    TreeMap<String, HashMap<Integer, Integer>> index;
    HashMap<Integer, Integer> maxFrequency;

    public DocumentIndex()
    {
        stopWords = new StopWords().getStopWords();
        index = new TreeMap<>();
        maxFrequency = new HashMap<>();
        number_of_documents = 0;
        _instance = this;
    }

    public void indexDocuments(String path_to_folder)
    {
        File folder = new File(path_to_folder);

        for (final File doc : folder.listFiles())
        {
            indexDocument(doc);
            number_of_documents++;
        }
        PrintWriter printer = null;
        try
        {
            FileWriter fw = new FileWriter("index.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            printer = new PrintWriter(bw);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        for (String stem : index.keySet())
        {
            printer.println(stem);
            for (Map.Entry<Integer, Integer> document : index.get(stem).entrySet())
            {
                printer.println(String.format("%d, %d", document.getKey(), document.getValue()));
            }
            printer.println();
        }
        printer.println();
        printer.flush();
        printer.close();
    }

    private void indexDocument(File file)
    {
        int max_frequency = 0;
        int docNumber = Integer.parseInt(new String(file.getName()).replaceAll("[^0-9]+", ""));
        List<String> doc = new ArrayList<>();
        List<String> words = new ArrayList<>();
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            StringBuilder everything = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null)
            {
                everything.append(line + " ");
            }
            words.addAll(Arrays.asList(everything.toString().replaceAll(System.getProperty("line.separator"), " ").replaceAll("[^a-zA-Z ]", "").toLowerCase().replaceAll("\\p{Punct}+", "").trim().split("\\s+")));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        words.remove("");
        words.removeAll(stopWords);
        for (int i = 0; i < words.size(); i++)
        {
            String stem = words.get(i);
            words.set(i, stem);

            if (!index.containsKey(stem))
            {
                HashMap<Integer, Integer> docs = new HashMap<>();
                docs.put(docNumber, 1);
                if (max_frequency < 1)
                    max_frequency = 1;
                index.put(stem, docs);
            } else
            {
                HashMap<Integer, Integer> docs = index.get(stem);
                if (!docs.containsKey(docNumber))
                {
                    docs.put(docNumber, 1);
                    if (max_frequency < 1)
                        max_frequency = 1;
                } else
                {
                    int frequency = docs.get(docNumber) + 1;
                    docs.put(docNumber, frequency);
                    if (max_frequency < frequency)
                        max_frequency = frequency;
                }
            }
            totalWordCount++;
        }
        maxFrequency.put(docNumber, max_frequency);
        //System.out.println("Document: "+docNumber + " Words: "+words.size());
    }

    public void query(String q)
    {
        ArrayList<String> keywords = new ArrayList<>(Arrays.asList(q.replaceAll(System.getProperty("line.separator"), " ").replaceAll("[^a-zA-Z ]", "").toLowerCase().replaceAll("\\p{Punct}+", "").trim().split("\\s+")));
        TreeMap<Double, Integer> scoredDocuments = new TreeMap<>(Collections.reverseOrder());
        for (int i = 0; i < number_of_documents; i++)
        {

            scoredDocuments.put(score(keywords, i + 1), i + 1);
        }
        printResults(q.trim(), scoredDocuments);
    }

    public double probabilityW(String word)
    {
        if (index.containsKey(word))
        {
            double wordFrequency = 0;
            for (Integer frequency : index.get(word).values())
                wordFrequency += frequency;
            return wordFrequency / totalWordCount;
        }
        return -1;
    }

    private void printResults(String query, TreeMap<Double, Integer> scoredDocuments)
    {
        int i = 1;
        for (Map.Entry<Double, Integer> entry : scoredDocuments.entrySet())
        {
            i++;
            Pair<String,String> snippet = getSnippet(query, entry.getValue());
            System.out.println(String.format("Doc: %d\n%s%s%s", entry.getValue(), snippet.getKey(),"...",snippet.getValue()));
            if (i == 6)
                break;
        }
        System.out.println();
    }

    private Pair<String,String> getSnippet(String query, Integer documentNumber)
    {
        try
        {
            File file = new File(String.format("wiki/To_be_posted/Doc (%d).txt", documentNumber));
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            StringBuilder everything = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null)
            {
                everything.append(line + "\n");
            }
            String[] sentences = everything.toString().split("\\.");
            double maxScore = 0;
            double secondScore = 0;
            int index = -1;
            int secondIndex = -1;
            for (int i = 0; i < sentences.length; i++)
            {
                String sentence = sentences[i];
                double densityMeasure = getDensityMeasure(sentence, documentNumber);
                double longestContiguousRun = getLongestContiguousRun(query, sentence);
                double uniqueTerms = getUniqueTerms(query, sentence);
                double numQueryTerms = getNumQueryTerms(query, sentence);
                double sentencePlacementScore = getSentencePlacementScore(i);
                double score = densityMeasure+longestContiguousRun+uniqueTerms+numQueryTerms+sentencePlacementScore;
                if(score > maxScore)
                {
                    secondScore = maxScore;
                    maxScore = score;
                    secondIndex = index;
                    index = i;
                }
                else if(score > secondScore)
                {
                    secondScore = score;
                    secondIndex = i;
                }
            }
            String sentence1 = "";
            String sentence2 = "";
            if(index != -1)
                sentence1 = sentences[index].trim();
            if(secondIndex != -1)
                sentence2 = sentences[secondIndex].trim();
            return new Pair<>(sentence1,sentence2);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private double getSentencePlacementScore(int i)
    {
        if(i == 0)
            return 3;
        else if(i == 1)
            return 2;
        return 1;
    }

    private double getNumQueryTerms(String query, String sentence)
    {
        String[] splitQuery = query.split("\\s");
        Set<String> uniqueQuery = new HashSet<>();
        int count = 0;
        for(String word : splitQuery)
        {
            uniqueQuery.add(word);
        }
        for(String queryWord : uniqueQuery)
        {
            count += StringUtils.countMatches(sentence, queryWord);
        }
        return count;
    }

    private double getUniqueTerms(String query, String sentence)
    {
        String[] splitQuery = query.split("\\s");
        Set<String> uniqueQuery = new HashSet<>();
        int count = 0;
        for(String word : splitQuery)
        {
            uniqueQuery.add(word);
        }
        for(String word : uniqueQuery)
        {
            if(sentence.contains(word))
                count++;
        }
        return count;
    }

    private double getLongestContiguousRun(String query, String sentence)
    {
        String[] querySplit = query.split("\\s");
        String[] sentenceSplit = sentence.split("\\s");
        int longestRun = 0;
        for(int i = 0; i < sentenceSplit.length; i++)
        {
            String word = sentenceSplit[i];
            for(int j = 0; j <querySplit.length; j++)
            {
                String queryWord = querySplit[j];
                if(queryWord.equals(word))
                {
                    int queryPosition = j;
                    int sentencePosition = i;
                    int run = 0;
                    while(queryPosition+run < querySplit.length && sentencePosition+run < sentenceSplit.length && querySplit[queryPosition+run].equals(sentenceSplit[sentencePosition+run]))
                    {
                        run++;
                    }
                    if(run > longestRun)
                        longestRun = run;
                }
            }
        }
        return longestRun;
    }

    //significance factor
    private double getDensityMeasure(String sentence, int documentNumber)
    {
        String[] words = sentence.split("\\s");
        int startIndex = -1;
        int insignificantWordCount = 0;
        int lastSignificantWord = -1;
        int significantWordCount = 0;
        for(int i = 0; i < words.length; i++)
        {
            //First Significant Word
            if(startIndex == -1 && wordIsSignificant(documentNumber, words[i]))
            {
                startIndex = i;
                lastSignificantWord = i;
                significantWordCount++;
            }
            //Start index set
            else if(startIndex != -1)
            {
                if(!wordIsSignificant(documentNumber, words[i]))
                {
                    insignificantWordCount++;
                    if (insignificantWordCount == 4)
                        break;
                }
                else //word is significant
                {
                    lastSignificantWord = i;
                    significantWordCount++;
                }
            }
        }
        double totalWordCount = lastSignificantWord-startIndex+1;
        return (significantWordCount*significantWordCount)/totalWordCount;
    }

    private boolean wordIsSignificant(int documentNumber, String word)
    {
        int wordFrequency = 0;
        int totalWords = maxFrequency.get(documentNumber);
        wordFrequency = 0;
        if(index.containsKey(word) && index.get(word).containsKey(documentNumber))
        {
            wordFrequency = index.get(word).get(documentNumber);
        }
        double comparisonValue;
        if(totalWords <25)
        {
            comparisonValue = 7-.1*(25-totalWords);
        }
        else if(25 <= totalWords && totalWords <= 40)
        {
            comparisonValue = 7;
        }
        else
        {
            comparisonValue = 7+.1*(totalWords-40);
        }
        return wordFrequency >= comparisonValue;
    }

    /*private void printResultsToFile(String query, TreeMap<Double, Integer> scoredDocuments)
    {
        PrintWriter printer = null;
        try
        {
            FileWriter fw = new FileWriter("results.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            printer = new PrintWriter(bw);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        printer.println(String.format("Query: %s", query));
        printer.println("Ranked Documents\tDocument ID\tFirst Sentence\tRanking Score");
        int i = 1;
        for (Map.Entry<Double, Integer> entry : scoredDocuments.entrySet())
        {
            printer.println(String.format("%d\t%d\t%s\t%f", i++, entry.getValue(), getSnippet(entry.getValue()), entry.getKey()));
            if (i == 11)
                break;
        }
        printer.println();
        printer.flush();
        printer.close();
    }*/

    public double score(ArrayList<String> query, int document_number)
    {
        double score = 0;
        query.removeAll(stopWords);
        for (String word : query)
        {
            score += getTF(word, document_number) * getIDF(word);
        }
        return score;
    }

    public double getTF(String word, int docNumber)
    {
        String stem = word;
        if (index.containsKey(stem) && index.get(stem).containsKey(docNumber))
        {
            double doc = (double) index.get(stem).get(docNumber) / (double) maxFrequency.get(docNumber);
            return doc;
        }
        return 0;
    }

    public double getIDF(String word)
    {
        String stem = word;
        if (index.containsKey(stem))
        {
            double n_sub_w = index.get(stem).keySet().size();
            return (Math.log(number_of_documents / n_sub_w)) / Math.log(2);
        }
        return 0;
    }
}
