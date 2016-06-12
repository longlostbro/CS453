import java.math.BigDecimal;

/**
 * Created by longl on 6/12/2016.
 */
public class Label implements Comparable<Label>
{
    String className;
    BigDecimal probability;
    public Label(String className, BigDecimal probability)
    {
        this.className = className;
        this.probability = probability;
    }

    @Override
    public int compareTo(Label o)
    {
        return probability.compareTo(o.probability);
    }
}
