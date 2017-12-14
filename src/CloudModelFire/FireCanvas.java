package CloudModelFire;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.List;

public class FireCanvas extends JPanel
{
	private CloudModel xCloudModel;
	private CloudModel yCloudModel;
	private CloudModel liveModel;
	private static final int moveY = 90;
	private int x0, y0, width, height;
	private FireInfo fireInfo;
	private ImageIcon candleStatic;
	private boolean live = true;
	private long liveTime = 0;

	public FireCanvas(CloudModel xCloudModel, CloudModel yCloudModel, CloudModel liveModel)
	{
		this.xCloudModel = xCloudModel;
		this.yCloudModel = yCloudModel;
		this.liveModel = liveModel;
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
		if (live)
		{
			List<PixelPoint> points = fireInfo.generateFirePixel(5000);
			DropPoint xDropPoint = xCloudModel.generateCloudDrop(1)[0];
			DropPoint yDropPoint = yCloudModel.generateCloudDrop(1)[0];
			if (System.currentTimeMillis() - liveTime >= 1000)
			{
				DropPoint lDropPoint = liveModel.generateCloudDrop(1)[0];
				xCloudModel.ex -= lDropPoint.value;
				xCloudModel.en -= lDropPoint.value / 5;
//				xCloudModel.he -= lDropPoint.value / 10;
				yCloudModel.he += lDropPoint.value / 10;
				xCloudModel.validate();
				yCloudModel.validate();
				liveTime = System.currentTimeMillis();
			}
			if (xDropPoint.value <= 0.45)
				live = false;
			int extend = 3;
			for (PixelPoint point : points)
			{
				g.setColor(new Color(point.r, point.g, point.b, point.a));
				//像素位置变换
				point.move(-fireInfo.width / 2.0, -fireInfo.height)
						.rotate(yDropPoint.value * Math.PI / 50)
						.zoom(xDropPoint.value, xDropPoint.value)
						.move(1.0, -18 / xDropPoint.value)
						.move(fireInfo.width / 2.0, fireInfo.height)
						.move(x0 - fireInfo.width / 2, height - fireInfo.height - moveY)
						.minBorder(extend, extend)
						.maxBorder(width - extend, height - extend);
				g.fillRect(point.getIntX() - extend, point.getIntY() - extend, 2 * extend, 2 * extend);
			}
		}
		g.drawImage(candleStatic.getImage(), x0 - candleStatic.getIconWidth() / 2, height - candleStatic.getIconHeight(), candleStatic.getImageObserver());
	}

	public boolean isLive()
	{
		return live;
	}

	public void setLive(boolean live)
	{
		this.live = live;
	}
}
