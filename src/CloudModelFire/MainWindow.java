package CloudModelFire;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow
{
	private class TimeThread extends Thread
	{
		final int ratio = 30;
		long time;
		boolean running;

		@Override
		public void run()
		{
			running = true;
			while (running)
			{
				if (System.currentTimeMillis() - time > 1000 / ratio)
				{
					if (visible)
						canvas.repaint();
					time = System.currentTimeMillis();
				}
				try
				{
					Thread.sleep(1000 / ratio);
				} catch (InterruptedException e)
				{
					running = false;
				}
			}
		}
	}

	private JFrame mainWindow = new JFrame("CloudModelFire");
	private Thread timeThread = new TimeThread();
	private boolean visible = false;
	private JPanel canvas = new FireCanvas();

	public MainWindow()
	{
		mainWindow.setSize(800, 600);
		mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainWindow.setLocationRelativeTo(null);
		mainWindow.add(canvas);
		timeThread.start();
		mainWindow.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				timeThread.interrupt();
			}
		});
	}

	public void setVisible(boolean visible)
	{
		mainWindow.setVisible(visible);
		this.visible = visible;
	}
}
