/**
 * Created by longl on 6/11/2016.
 */
public class Word implements Comparable<Word>
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
    public int compareTo(Word o)
    {
        return Double.compare(o.score,score);
    }
}
