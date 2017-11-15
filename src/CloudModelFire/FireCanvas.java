package CloudModelFire;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.List;

public class FireCanvas extends JPanel
{
	private double ex = 1.0, en = 0.05, he = 0.001;
	private CloudModel cloudModel = new CloudModel(ex, en, he);
	private static final int moveY = 90;
	private int x0, y0, width, height;
	private FireInfo fireInfo;
	private ImageIcon candleStatic;

	public FireCanvas()
	{
		Dimension dimension = this.getSize();
		x0 = dimension.width / 2;
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
				y0 = height * 2 / 3;
				if (fireInfo != null && fireInfo.getRandomGenerator() instanceof FireInfo.RemoveRedundantRandom)
				{
					((FireInfo.RemoveRedundantRandom) fireInfo.getRandomGenerator()).resize(width, height);
				}
			}
		});
		try
		{
			fireInfo = new FireInfo(this.getClass().getResource("/fire.png"));
			fireInfo.loadImage();
			FireInfo.RandomGenerator generator = new FireInfo.AverageRandom();
			FireInfo.FixAndRandom fix = new FireInfo.FixAndRandom(generator, 0.5);
			FireInfo.RemoveRedundantRandom remove = new FireInfo.RemoveRedundantRandom(fix, width, height, 4);
			fireInfo.setRandomGenerator(remove);
			candleStatic = new ImageIcon(this.getClass().getResource("/static.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("加载文件失败，请确保资源文件在正确的目录中");
		}
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		List<PixelPoint> points = fireInfo.generateFirePixel(5000);
		DropPoint dropPoints = cloudModel.generateCloudDrop(1)[0];
		for (PixelPoint point : points)
		{
			g.setColor(new Color(point.r, point.g, point.b, point.a));
			point.move(-fireInfo.width / 2.0, -fireInfo.height)
					.zoom(1.0, dropPoints.value)
					.move(fireInfo.width / 2.0, fireInfo.height)
					.move(x0 - fireInfo.width / 2, height - fireInfo.height - moveY);
			g.fillOval(point.getIntX(), point.getIntY(), 5, 5);
		}
		g.drawImage(candleStatic.getImage(), x0 - candleStatic.getIconWidth() / 2, height - candleStatic.getIconHeight(), candleStatic.getImageObserver());
	}
}
