import javax.microedition.lcdui.*;

public class Loading extends Canvas {

	private Slovnik_WEB2CZ midlet = null;

	private Image iLoading = null;

	private static final int ILOADING_WIDTH = 119;
	private static final int ILOADING_HEIGHT = 76;
	private static final int ILOADING_100PERCENT = 48;

	public Loading(Slovnik_WEB2CZ midlet) {
		this.midlet = midlet;

		try {
			iLoading = Image.createImage("/loading.png");
		} catch(Exception e) {
		}
	}

	public void paint(Graphics g) {
		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(iLoading, ((getWidth() - ILOADING_WIDTH) / 2), ((getHeight() - ILOADING_HEIGHT) / 2), g.TOP | g.LEFT);

		increase(g);
	}

	private void increase(Graphics g) {
		g.setColor(255, 255, 255);
		g.drawLine((((getWidth() - ILOADING_WIDTH) / 2) + 35), ((((getHeight() - ILOADING_HEIGHT) / 2) + ILOADING_HEIGHT) - 5), ((((getWidth() - ILOADING_WIDTH) / 2) + 35) + ((ILOADING_100PERCENT * midlet.loadingPercent) / 100)), ((((getHeight() - ILOADING_HEIGHT) / 2) + ILOADING_HEIGHT) - 5));
		g.setColor(152, 163, 202);
		g.drawLine((((getWidth() - ILOADING_WIDTH) / 2) + 35), ((((getHeight() - ILOADING_HEIGHT) / 2) + ILOADING_HEIGHT) - 4), ((((getWidth() - ILOADING_WIDTH) / 2) + 35) + ((ILOADING_100PERCENT * midlet.loadingPercent) / 100)), ((((getHeight() - ILOADING_HEIGHT) / 2) + ILOADING_HEIGHT) - 4));
	}
}