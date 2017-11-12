package CloudModelFire;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FireCanvas extends JPanel
{
	private double ex = 0.0, en = 0.5, he = 0.05;
	private CloudModel cloudModel = new CloudModel(ex, en, he);
	private final double scale = 10.0;
	private final int moveY = 100;
	private int x0, y0, width, height;
	private int size = 100;

	public FireCanvas()
	{
		Dimension dimension = this.getSize();
		x0 = dimension.width / 2;
		y0 = dimension.height - 100;
		if (y0 < 0)
			y0 = dimension.height * 2 / 3;
		width = dimension.width;
		height = dimension.height;
		this.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				width = e.getComponent().getWidth();
				height = e.getComponent().getHeight();
				x0 = width / 2;
				y0 = height - 100;
				if (y0 < 0)
					y0 = height * 2 / 3;
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.drawLine(0, y0, width, y0);
		g.drawLine(x0, 0, x0, height);
		DropPoint[] dropPoints = FunctionLib.GenerateCloudDrop(cloudModel, 5000);
		List<DropPoint> dropList = Arrays.asList(dropPoints);
		Collections.sort(dropList);
		List<DropPoint> reverseList = dropList.stream().map(dropPoint -> {
			DropPoint point = new DropPoint();
			point.belong = -dropPoint.belong;
			point.value = dropPoint.value;
			return point;
		}).collect(Collectors.toList());
		int[] min = transformDropToPoint(new DropPoint(ex - 3 * en, 0.0));
		int minx = min[0] + x0;
		int miny = min[1] + y0;
		g.setColor(Color.BLACK);
		g.drawOval(minx, miny, 50, 50);
		for (int i = 0; i < dropList.size(); i++)
		{
			int[] point = transformDropToPoint(dropList.get(i));
			int[] reverse = transformDropToPoint(reverseList.get(i));
			int mx1 = point[0] + x0;
			int my1 = point[1] + y0;
			int mx2 = reverse[0] + x0;
			int my2 = reverse[1] + y0;
			g.setColor(generateFireColor(my1, y0, miny));
			g.drawOval(mx1, my1, 5, 5);
			g.drawOval(mx2, my2, 5, 5);
		}
	}

	private int[] transformDropToPoint(DropPoint dropPoint)
	{
		int[] point = new int[2];
		double xZoom = (width * 1.0 / scale);
		double yZoom = (height - y0) * 4.0 / 5;
		int y = (int) (dropPoint.value * xZoom);
		int x = (int) (dropPoint.belong * yZoom);
		y -= moveY;
		if (x > width)
			x = width;
		if (y > height)
			y = height;
		point[0] = x;
		point[1] = y;
		return point;
	}

	private static Color generateFireColor(int height, int y0, int minHeight)
	{
		if (minHeight <= 0)
			minHeight = 1;
		if (height >= y0)
			height = y0 - 1;
		int delta = height - minHeight;
		if (delta < 0)
			delta = 0;
		int changeToBlue = (y0 - minHeight) * 9 / 10;
		int changeToOrigin = (y0 - minHeight) * 7 / 10;
		float r, g, b;
		if (delta > changeToBlue)
		{
			r = (delta - changeToBlue) * 1.0f / (y0 - changeToBlue + 1.0f);
			g = r;
			b = 1.0f;
		} else if (delta > changeToOrigin && delta <= changeToBlue)
		{
			r = 1.0f - (delta - changeToOrigin) * 1.0f / (changeToBlue - changeToOrigin + 1.0f);
			g = r;
			b = 1.0f;
		} else
		{
			r = 1.0f;
			g = 0.5f + (delta) * 0.5f / (changeToOrigin);
			b = (delta) * 1.0f / (changeToOrigin);
		}
		if (r > 1.0f || b > 1.0f || g > 1.0f || r < 0.0f || b < 0.0f || g < 0.0f)
			System.out.println("error");
		Color color = new Color(r, g, b);
		return color;
	}

}
