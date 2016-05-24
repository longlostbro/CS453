import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by longl on 5/24/2016.
 */
public class QueryLogParser {
    private String folderPath;
    private SimpleDateFormat dateFormat;
    private Trie trie;

    public QueryLogParser(String folderPath)
    {
        this.folderPath = folderPath;
        this.trie = new Trie();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
    }

    public void parse()
    {
        File directory = new File(folderPath);
        if(directory.exists() && directory.isDirectory())
        {
            for(File file : directory.listFiles())
            {
                parseDocument(file);
                System.out.println(String.format("%s complete",file.getName()));
                break;//TODO remove this
            }
        }
        System.out.println("COMPLETE!");
    }

    public void parseDocument(File file)
    {
        try
        {
            if (file.exists()) {
                Scanner scanner = new Scanner(new FileInputStream(file));
                String previousUserId="";
                Date previousDate=null;
                TrieNode previousQueryNode = null;
                scanner.nextLine();
                while(scanner.hasNextLine())
                {
                    String line = scanner.nextLine();
                    String[] lineArray = line.split("\\t");
                    String currentId = lineArray[0];
                    String currentQuery = lineArray[1];
                    Date currentDate = dateFormat.parse(lineArray[2]);

                    TrieNode currentQueryNode = trie.insertQuery(currentQuery);
                    if(previousDate != null)
                    {
                        double minutes = (previousDate.getTime() - currentDate.getTime()) / 60;
                        if (minutes <= 10 && previousUserId.equals(currentId)) {
                            previousQueryNode.addModification(currentQuery);
                        }
                    }
                    previousDate = currentDate;
                    previousUserId = lineArray[0];
                    previousQueryNode = currentQueryNode;
                }
            }
            else
            {
                System.err.println(String.format("File does not exist in path: %s",file.getAbsolutePath()));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public Trie getTrie() {
        return trie;
    }
}
