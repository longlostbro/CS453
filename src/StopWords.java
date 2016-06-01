import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * Created by dtaylor on 5/31/2016.
 */
public class StopWords {

    static HashSet<String> StopWords = new HashSet<String>();
    static HashSet<String> validWords = new HashSet<String>();

    public StopWords(){
        try {
            // Read the unorder file in
            BufferedReader in = new BufferedReader(new FileReader("stopwords.txt")); //THIS IS THE FILE THAT CONTAINS THE STOPWORDS
            StringBuffer str = new StringBuffer();
            String nextLine = "";
            while ((nextLine = in.readLine()) != null)
                str.append(nextLine+"\n");
            in.close();
            //save it to a bin tree.
            StringTokenizer st = new StringTokenizer(str.toString());//create a string
            while(st.hasMoreTokens()){
                nextLine = st.nextToken();
                if(nextLine.matches("[a-zA-Z'.]*"))
                    StopWords.add(nextLine.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public HashSet<String> getStopWords(){return StopWords;}
    public void setStopWord(HashSet<String> words){StopWords=words;}

    public boolean contains(String word){
        return StopWords.contains(word.toLowerCase().trim());
    }
}

