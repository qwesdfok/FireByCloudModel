package CloudModelFire;

public class DropPoint implements Comparable<DropPoint>
{
	public double value;
	public double belong;

	public DropPoint()
	{
	}

	public DropPoint(double value, double belong)
	{
		this.value = value;
		this.belong = belong;
	}

	@Override
	public int compareTo(DropPoint o)
	{
		return Double.compare(value, o.value);
	}

	@Override
	public String toString()
	{
		return value + ", " + belong;
	}
}
