package CloudModelFire;

import java.util.Random;

public class FunctionLib
{
	private static Random random = new Random();

	public static double gaussKernel(double x, double y, double sigma)
	{
		return Math.exp(-((x - y) * (x - y)) / (2.0 * sigma * sigma));
	}

	public static double gaussRandom()
	{
		return random.nextGaussian();
	}

	public static CloudModel reverseCloudModel(double[] data)
	{
		double average = 0, variance = 0;
		CloudModel model = new CloudModel();
		for (int i = 0; i < data.length; i++)
		{
			average += data[i];
		}
		average = average / data.length;
		for (int i = 0; i < data.length; i++)
		{
			variance += (data[i] - average) * (data[i] - average);
		}
		variance = variance / (data.length - 1);
		model.ex = average;
		double en = 0;
		for (int i = 0; i < data.length; i++)
		{
			en += Math.abs(data[i] - average);
		}
		en = en * Math.sqrt(Math.PI / 2) / data.length;
		model.en = en;
		model.he = Math.sqrt(variance * variance - en * en);
		return model;
	}

	public static DropPoint[] GenerateCloudDrop(CloudModel model, int count)
	{
		DropPoint[] result = new DropPoint[count];
		for (int i = 0; i < count; i++)
		{
			double gaussRandom1 = gaussRandom();
			double gaussRandom2 = gaussRandom();
			double iEn = gaussRandom1 * model.he + model.en;
			double iX = gaussRandom2 * iEn + model.ex;
			double iBelong = gaussKernel(iX, model.ex, iEn);
			DropPoint point = new DropPoint();
			point.value=iX;
			point.belong = iBelong;
			result[i] = point;
		}
		return result;
	}
}
