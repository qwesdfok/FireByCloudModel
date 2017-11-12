package CloudModelFire;

public class PixelPoint
{
	public double x, y;

	public PixelPoint()
	{
	}

	public PixelPoint(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public static PixelPoint parsePoint(DropPoint dropPoint)
	{
		return new PixelPoint(dropPoint.value, dropPoint.belong);
	}

	public PixelPoint zoom(double xZoom, double yZoom)
	{
		x *= xZoom;
		y *= yZoom;
		return this;
	}

	public PixelPoint move(double xDelta, double yDelta)
	{
		x += xDelta;
		y += yDelta;
		return this;
	}

	public PixelPoint rotate(double rad)
	{
		double tx = x, ty = y;
		x = tx * Math.cos(rad) - ty * Math.sin(rad);
		y = tx * Math.sin(rad) + ty * Math.cos(rad);
		return this;
	}

	public PixelPoint minBorder(double xBorder, double yBorder)
	{
		if (x < xBorder)
			x = xBorder;
		if (y < yBorder)
			y = yBorder;
		return this;
	}

	public PixelPoint maxBorder(double xBorder, double yBorder)
	{
		if (x > xBorder)
			x = xBorder;
		if (y > yBorder)
			y = yBorder;
		return this;
	}

	public int getIntX()
	{
		return (int) x;
	}

	public int getIntY()
	{
		return (int) y;
	}

	@Override
	public String toString()
	{
		return x + ", " + y;
	}
}
