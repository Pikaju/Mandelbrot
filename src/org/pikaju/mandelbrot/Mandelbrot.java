package org.pikaju.mandelbrot;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

public class Mandelbrot extends Canvas {
	private static final long serialVersionUID = 1L;

	public static Mandelbrot i;
	
	public float scale = 1;
	private float newScale = scale;
	
	private int[] pixels;
	private BufferedImage image;
	
	public int width;
	public int height;
	
	public boolean edown = false;
	public boolean ddown = false;
	public boolean updown = false;
	public boolean downdown = false;
	public boolean leftdown = false;
	public boolean rightdown = false;

	public Mandelbrot() {
		setPreferredSize(new Dimension(640, 480));
		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == 'q') iterations++;
				if (e.getKeyChar() == 'a') iterations--;
				if (iterations < 2) iterations = 2;
				
				if (e.getKeyChar() == 'w') newScale = scale / 2;
				if (e.getKeyChar() == 's') newScale = scale * 2;
			}
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_E) i.edown = false;
				if (e.getKeyCode() == KeyEvent.VK_D) i.ddown = false;
				
				if (e.getKeyCode() == KeyEvent.VK_UP) i.updown = false;
				if (e.getKeyCode() == KeyEvent.VK_DOWN) i.downdown = false;
				if (e.getKeyCode() == KeyEvent.VK_LEFT) i.leftdown = false;
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) i.rightdown = false;	
			}
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_E) i.edown = true;
				if (e.getKeyCode() == KeyEvent.VK_D) i.ddown = true;				

				if (e.getKeyCode() == KeyEvent.VK_UP) i.updown = true;
				if (e.getKeyCode() == KeyEvent.VK_DOWN) i.downdown = true;
				if (e.getKeyCode() == KeyEvent.VK_LEFT) i.leftdown = true;
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) i.rightdown = true;
			}
		});
	}
	
	private void createFrameBuffers(float scale) {
		this.scale = scale;
		width = (int) (getWidth() / this.scale);
		height = (int) (getHeight() / this.scale);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}

	public static void main(String[] args) {
		i = new Mandelbrot();
		JFrame frame = new JFrame("Metroid");
		frame.add(i);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		i.createFrameBuffers(0.5f);
		i.run();
	}

	private void run() {
		long currentTime = 0;
		long lastTime = System.currentTimeMillis();
		
		double delta = 0;
		final double ns = 1000.0 / 60.0;
		while (true) {
			currentTime = System.currentTimeMillis();
			delta = (currentTime - lastTime) / ns;
			lastTime = currentTime;
			
			update(delta);
			render();
		}
	}
	
	private static final double speed = 0.01;
	private static final double zoomSpeed = 0.01;
	
	private void update(double delta) {
		if (newScale != scale) createFrameBuffers(newScale);
		
		if (edown) zoom -= delta * (zoom) * zoomSpeed;
		if (ddown) zoom += delta * (zoom) * zoomSpeed;
		if (updown) yOff -= delta * zoom * speed;
		if (downdown) yOff += delta * zoom * speed;
		if (leftdown) xOff -= delta * zoom * speed;
		if (rightdown) xOff += delta * zoom * speed;
	}

	private int iterations = 32;
	private double zoom = 5.0f;
	private double xOff = 0.0f;
	private double yOff = 0.0f;
	
	private void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			requestFocus();
			return;
		}
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setColor(Color.WHITE);
		
		for (int x = 0; x < width; x += 1) {
			for (int y = 0; y < height; y += 1) {
				double cx = ((x / (double) width) - 0.5) * zoom - 0.3 + xOff;
				double cy = ((y / (double) height) - 0.5) * zoom + yOff;
				double zx = cx;
				double zy = cy;

				int i;
				for (i = 0; i < iterations; i++) {
					double xx = (zx * zx - zy * zy) + cx;
					double yy = (zy * zx + zx * zy) + cy;

			        if((xx * xx + yy * yy) > 4.0) break;
			        zx = xx;
			        zy = yy;
				}
				float col = (i == iterations ? 0.0f : i) / 100.0f;
				drawPixel(x, y, col, col, col);
			}
		}
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		
		g.drawString("Iterations: " + iterations, 2, 10);
		g.drawString("Resolution: " + scale, 2, 22);
		g.dispose();
		bs.show();
	}

	private void drawPixel(int x, int y, float red, float green, float blue) {
		int c = ((int) (red * 255.0f) << 16) | ((int) (green * 255.0f) << 8) | ((int) (blue * 255.0f));
		pixels[x + y * width] = c;
	}
}
