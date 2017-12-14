package CloudModelFire;

import static CloudModelFire.FunctionLib.gaussKernel;
import static CloudModelFire.FunctionLib.gaussRandom;

public class CloudModel
{
	public double ex;
	public double en;
	public double he;

	public CloudModel()
	{
	}

	public CloudModel(double ex, double en, double he)
	{
		this.ex = ex;
		this.en = en;
		this.he = he;
	}

	public DropPoint[] generateCloudDrop(int count)
	{
		DropPoint[] result = new DropPoint[count];
		for (int i = 0; i < count; i++)
		{
			double gaussRandom1 = gaussRandom();
			double gaussRandom2 = gaussRandom();
			double iEn = gaussRandom1 * he + en;
			double iX = gaussRandom2 * iEn + ex;
			double iBelong = gaussKernel(iX, ex, iEn);
			DropPoint point = new DropPoint();
			point.value = iX;
			point.belong = iBelong;
			result[i] = point;
		}
		return result;
	}

	public void validate()
	{
		if (ex < 0)
			ex = 0;
		if (en < 0)
			en = 0;
		if (he < 0)
			he = 0;
	}
}
