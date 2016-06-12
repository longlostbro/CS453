/**
 * Created by longl on 6/11/2016.
 */
public class Word implements Comparable
{
    private String word;
    private double score;
    public Word(String word, double score)
    {
        this.word = word;
        this.score = score;
    }

    public double getScore()
    {
        return score;
    }

    public String getWord()
    {
        return word;
    }

    @Override
    public int compareTo(Object o)
    {
        if(o instanceof Word)
            return Double.compare(score,((Word)o).getScore());
        return -1;
    }
}
