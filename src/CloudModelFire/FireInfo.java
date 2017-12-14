package CloudModelFire;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class FireInfo
{
	/**
	 * 该接口用于产生随机的像素点（粒子），以至于产生动态效果
	 */
	public interface RandomGenerator
	{
		/**
		 * 0<=p<1.0
		 *
		 * @param count
		 * @return
		 */
		List<PixelPoint> random(int count);
	}

	/**
	 * 通过均匀分布形成像素点
	 */
	static class AverageRandom implements RandomGenerator
	{
		Random random = new Random();

		@Override
		public List<PixelPoint> random(int count)
		{
			List<PixelPoint> result = new ArrayList<>(count);
			for (int i = 0; i < count; i++)
			{
				result.add(new PixelPoint(random.nextDouble(), random.nextDouble()));
			}
			return result;
		}
	}

	/**
	 * 部分随机，以减少随机像素点的产生次数
	 */
	static class FixAndRandom implements RandomGenerator
	{
		public double fixRatio;
		private List<PixelPoint> fixPoints;
		private RandomGenerator source;

		public FixAndRandom(RandomGenerator source, double fixRatio)
		{
			if (fixRatio > 0 || fixRatio < 0)
				fixRatio = 0.6;
			this.fixRatio = fixRatio;
			this.source = source;
		}

		@Override
		public List<PixelPoint> random(int count)
		{
			if (fixPoints == null)
			{
				fixPoints = source.random((int) (count * fixRatio));
			}
			List<PixelPoint> result = fixPoints.stream().map(p -> new PixelPoint(p.x, p.y)).collect(Collectors.toList());
			result.addAll(source.random(count - fixPoints.size()));
			return result;
		}
	}

	/**
	 * 删除绘图时重合的像素点，以减少绘图压力l
	 */
	static class RemoveRedundantRandom implements RandomGenerator
	{
		private RandomGenerator source;
		private boolean[][] buffer;
		private int mapX, mapY;
		private int round;

		public RemoveRedundantRandom(RandomGenerator source, int mapX, int mapY, int round)
		{
			this.source = source;
			this.mapX = mapX;
			this.mapY = mapY;
			this.round = round;
			buffer = new boolean[mapY][mapX];
		}

		public void resize(int mapX, int mapY)
		{
			this.mapX = mapX;
			this.mapY = mapY;
			buffer = new boolean[mapY][mapX];
		}

		@Override
		public List<PixelPoint> random(int count)
		{
			List<PixelPoint> points = source.random(count);
			List<PixelPoint> result = new ArrayList<>(count);
			for (int h = 0; h < mapY; h++)
			{
				for (int w = 0; w < mapX; w++)
				{
					buffer[h][w] = false;
				}
			}
			for (PixelPoint point : points)
			{
				int x = (int) (point.x * mapX);
				int y = (int) (point.y * mapY);
				if (round == 0 && !buffer[y][x])
				{
					buffer[y][x] = true;
					result.add(point);
					continue;
				}
				boolean test = false;
				for (int h = y - round; h < y + round; h++)
				{
					if (h < 0 || h >= mapY)
						continue;
					for (int w = x - round; w < x + round; w++)
					{
						if (w < 0 || w >= mapX)
							continue;
						if (buffer[h][w])
							test = true;
					}
				}
				if (!test)
				{
					buffer[y][x] = true;
					result.add(point);
				}
			}
			return result;
		}
	}

	private class RowInfo
	{
		int[] raw;
		List<Integer> colorInfo = new ArrayList<>();
		List<Integer> fractionIndex = new ArrayList<>();
		List<Double> ratio = new ArrayList<>();
	}

	public int height, width;
	public URL fileURL;
	private RowInfo[] map;
	private RandomGenerator randomGenerator;

	public FireInfo(URL fileURL)
	{
		this.fileURL = fileURL;
	}

	public void loadImage() throws IOException
	{
		BufferedImage fireImage = ImageIO.read(fileURL);
		height = fireImage.getHeight();
		width = fireImage.getWidth();
		map = new RowInfo[fireImage.getHeight()];
		for (int h = 0; h < fireImage.getHeight(); h++)
		{
			RowInfo info = new RowInfo();
			info.raw = new int[width];
			map[h] = info;
			int start = 0;
			for (int w = 0; w < fireImage.getWidth(); w++)
			{
				int color = fireImage.getRGB(w, h);
				if ((color & 0xff000000) != 0)
				{
					info.raw[w] = color;
					info.colorInfo.add(color);
					if (start == 0)
						start = 1;
				} else
				{
					if (start == 2)
						start = 0;
				}
				if (start == 1)
				{
					info.fractionIndex.add(w);
					start = 2;
					info.ratio.add(1.0);
				} else if (start == 2)
				{
					info.ratio.set(info.ratio.size() - 1, info.ratio.get(info.ratio.size() - 1) + 1.0);
				}
			}
			info.ratio = info.ratio.stream().map(r -> r / info.colorInfo.size()).collect(Collectors.toList());
		}
		System.out.println("Load completed");
	}

	public RandomGenerator getRandomGenerator()
	{
		return randomGenerator;
	}

	public void setRandomGenerator(RandomGenerator randomGenerator)
	{
		this.randomGenerator = randomGenerator;
	}

	public List<PixelPoint> generateFirePixel(int count)
	{
		List<PixelPoint> points = randomGenerator.random(count);
		for (PixelPoint point : points)
		{
			point.y = point.y * height;
			RowInfo rowInfo = map[point.getIntY()];
			if (rowInfo.fractionIndex.size() == 1)
			{
				point.x = rowInfo.fractionIndex.get(0) + rowInfo.colorInfo.size() * point.x;
			} else
			{
				for (int i = 0; i < rowInfo.fractionIndex.size(); i++)
				{
					double sub = i == 0 ? 0.0 : rowInfo.ratio.get(i - 1);
					if (point.x - sub <= rowInfo.ratio.get(i))
					{
						point.x = rowInfo.fractionIndex.get(i) + rowInfo.colorInfo.size() * (point.x - sub);
					}
				}
			}
			int color = rowInfo.raw[point.getIntX()];
			//数据格式ARGB
			point.a = ((color & 0xff000000) >>> 24) / 256.0f;
			point.r = ((color & 0x00ff0000) >>> 16) / 256.0f;
			point.g = ((color & 0x0000ff00) >>> 8) / 256.0f;
			point.b = (color & 0x000000ff) / 256.0f;
		}
		return points;
	}
}
