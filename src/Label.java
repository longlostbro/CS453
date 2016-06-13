import java.math.BigDecimal;

/**
 * Created by longl on 6/12/2016.
 */
public class Label implements Comparable<Label>
{
    String className;
    double probability;
    public Label(String className, double probability)
    {
        this.className = className;
        this.probability = probability;
    }

    @Override
    public int compareTo(Label o)
    {
        return Double.compare(o.probability,probability);
    }
}
