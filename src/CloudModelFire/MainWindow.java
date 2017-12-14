package CloudModelFire;

import javax.swing.*;
import java.awt.*;
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
	private FireCanvas canvas;
	private CloudModel verticalModel = new CloudModel(1.0, 0.1, 0.01);
	private CloudModel horizontalModel = new CloudModel(0.0, 0.05, 0.01);
	private CloudModel liveModel = new CloudModel(0.0, 0.0, 0.0);
	private JTextField verticalEx = new JTextField();
	private JTextField verticalEn = new JTextField();
	private JTextField verticalHe = new JTextField();
	private JTextField horizontalEx = new JTextField();
	private JTextField horizontalEn = new JTextField();
	private JTextField horizontalHe = new JTextField();
	private JTextField liveEx = new JTextField();
	private JTextField liveEn = new JTextField();
	private JTextField liveHe = new JTextField();

	public MainWindow()
	{
		mainWindow.setSize(800, 750);
		mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainWindow.setLocationRelativeTo(null);
		timeThread.start();
		canvas = new FireCanvas(verticalModel, horizontalModel, liveModel);
		verticalEx.setText(Double.toString(verticalModel.ex));
		verticalEn.setText(Double.toString(verticalModel.en));
		verticalHe.setText(Double.toString(verticalModel.he));
		horizontalEx.setText(Double.toString(horizontalModel.ex));
		horizontalEn.setText(Double.toString(horizontalModel.en));
		horizontalHe.setText(Double.toString(horizontalModel.he));
//		liveEx.setText(Double.toString(liveModel.ex));
//		liveEn.setText(Double.toString(liveModel.en));
//		liveHe.setText(Double.toString(liveModel.he));
		liveEx.setText(Double.toString(0.05));
		liveEn.setText(Double.toString(0.01));
		liveHe.setText(Double.toString(0.01));
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(4, 8, 4, 8);
		constraints.fill = GridBagConstraints.BOTH;
		UITools uiTools = new UITools(constraints);
		controlPanel.add(new Label("Vertical"), uiTools.configConstraints(0, 0, 1, 1, 0.0, 0.0));
		controlPanel.add(new Label("Ex"), uiTools.configConstraints(1, 0, 1, 1, 0.0, 0.0));
		controlPanel.add(verticalEx, uiTools.configConstraints(2, 0, 1, 1, 1.0, 0.0));
		controlPanel.add(new Label("En"), uiTools.configConstraints(3, 0, 1, 1, 0.0, 0.0));
		controlPanel.add(verticalEn, uiTools.configConstraints(4, 0, 1, 1, 1.0, 0.0));
		controlPanel.add(new Label("He"), uiTools.configConstraints(5, 0, 1, 1, 0.0, 0.0));
		controlPanel.add(verticalHe, uiTools.configConstraints(6, 0, 1, 1, 1.0, 0.0));
		controlPanel.add(new Label("Horizontal"), uiTools.configConstraints(0, 1, 1, 1, 0.0, 0.0));
		controlPanel.add(new Label("Ex"), uiTools.configConstraints(1, 1, 1, 1, 0.0, 0.0));
		controlPanel.add(horizontalEx, uiTools.configConstraints(2, 1, 1, 1, 1.0, 0.0));
		controlPanel.add(new Label("En"), uiTools.configConstraints(3, 1, 1, 1, 0.0, 0.0));
		controlPanel.add(horizontalEn, uiTools.configConstraints(4, 1, 1, 1, 1.0, 0.0));
		controlPanel.add(new Label("He"), uiTools.configConstraints(5, 1, 1, 1, 0.0, 0.0));
		controlPanel.add(horizontalHe, uiTools.configConstraints(6, 1, 1, 1, 1.0, 0.0));
		controlPanel.add(new Label("Live"), uiTools.configConstraints(0, 2, 1, 1, 0.0, 0.0));
		controlPanel.add(new Label("Ex"), uiTools.configConstraints(1, 2, 1, 1, 0.0, 0.0));
		controlPanel.add(liveEx, uiTools.configConstraints(2, 2, 1, 1, 1.0, 0.0));
		controlPanel.add(new Label("En"), uiTools.configConstraints(3, 2, 1, 1, 0.0, 0.0));
		controlPanel.add(liveEn, uiTools.configConstraints(4, 2, 1, 1, 1.0, 0.0));
		controlPanel.add(new Label("He"), uiTools.configConstraints(5, 2, 1, 1, 0.0, 0.0));
		controlPanel.add(liveHe, uiTools.configConstraints(6, 2, 1, 1, 1.0, 0.0));
		JButton button = new JButton("Update");
		button.addActionListener(e -> {
			update();
		});
		controlPanel.add(button, uiTools.configConstraints(6, 3, 1, 1, 0.0, 0.0));

		mainWindow.setLayout(new GridBagLayout());
		constraints.fill = GridBagConstraints.HORIZONTAL;
		mainWindow.add(controlPanel, uiTools.configConstraints(0, 0, 1, 1, 1.0, 0.0));
		constraints.fill = GridBagConstraints.BOTH;
		mainWindow.add(canvas, uiTools.configConstraints(0, 1, 1, 1, 1.0, 1.0));
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
		canvas.setLive(true);
	}

	private void update()
	{
		horizontalModel.ex = Double.parseDouble(horizontalEx.getText());
		horizontalModel.en = Double.parseDouble(horizontalEn.getText());
		horizontalModel.he = Double.parseDouble(horizontalHe.getText());
		verticalModel.ex = Double.parseDouble(verticalEx.getText());
		verticalModel.en = Double.parseDouble(verticalEn.getText());
		verticalModel.he = Double.parseDouble(verticalHe.getText());
		liveModel.ex = Double.parseDouble(liveEx.getText());
		liveModel.en = Double.parseDouble(liveEn.getText());
		liveModel.he = Double.parseDouble(liveHe.getText());
		canvas.setLive(true);
	}
}
