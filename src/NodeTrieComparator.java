import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Comparator;

/**
 * Created by longl on 5/24/2016.
 */
public class NodeTrieComparator implements Comparator<TrieNode>
{
    private final PorterStemmer stemmer;
    private final String query;
    private final Trie trie;
    private final TrieNode queryNode;

    public NodeTrieComparator(String query, Trie trie)
    {
        stemmer = new PorterStemmer();
        this.query = query;
        this.trie = trie;
        this.queryNode = trie.getNode(query);
    }
    @Override
    public int compare(TrieNode o1, TrieNode o2)
    {
        double suggestedRank1 = getSuggestedRank(o1);
        o1.setRankScore(suggestedRank1);
        double suggestedRank2 = getSuggestedRank(o2);
        o2.setRankScore(suggestedRank2);
        if(suggestedRank1 > suggestedRank2)
            return -1;
        else if(suggestedRank1 < suggestedRank2)
            return 1;
        return 0;
    }

    public double getSuggestedRank(TrieNode node)
    {
        double freqSQ = Math.log(node.getQueryFrequency());
        double wcfQSQ = Math.log(wcfScore(getLastWord(query),getSecondWord(node.toString())));
        double modQSQ = 0;
        //System.out.println(String.format("%f %f %f",freqSQ, wcfQSQ, modQSQ));
        if(queryNode != null && queryNode.getModifiedTo().containsKey(node.toString()))
        {
            modQSQ = Math.log(queryNode.getModifiedTo().get(node.toString()));
        }
        return (freqSQ+wcfQSQ+modQSQ)/(1-Math.min(freqSQ, Math.min(wcfQSQ,modQSQ)));
    }

    public Double wcfScore(String word1, String word2)
    {
        try{

            String w1;
            if(word1.length()<=1)
                w1=word1;
            else
                w1=stemmer.stem(word1);
            String w2;
            if(word2.length()<=1)
                w2 = word2;
            else
                w2=stemmer.stem(word2);


            String myURL = "http://peacock.cs.byu.edu/CS453Proj2/?word1="+w1+"&word2="+w2;

            //System.out.println("Fetching content: "+myURL);

            Document pageDoc = Jsoup.connect(myURL).get();
            String htmlContent = pageDoc.html();
            Document contentDoc = Jsoup.parse(htmlContent);
            String contentVal = contentDoc.body().text();

            //System.out.println(contentVal);

            Double val= Double.parseDouble(contentVal);
            if(val == -1)
                return Double.valueOf(1);
            return val;

        }
        catch (Exception e) {
            e.printStackTrace();
            //System.out.println(String.format("%s %s\n",word1,word2));
            return Double.valueOf(1);
        }
    }

    public String getLastWord(String query)
    {
        if(query.contains(" "))
        {
            String[] splitQuery = query.split(" ");
            return splitQuery[splitQuery.length-1];
        }
        else
        {
            return query;
        }
    }

    public String getSecondWord(String query)
    {
        if(query.contains(" "))
        {
            String[] splitQuery = query.split(" ");
            return splitQuery[1];
        }
        else
        {
            return query;
        }
    }
}
