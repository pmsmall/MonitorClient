package com.monitor.core;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.Kernel;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.monitor.pack.util.LZ4JNIManager;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import net.coobird.thumbnailator.Thumbnails;

public class PrintScreen {
	private Robot robot;
	public final static Dimension screenDimension;
	public static final int screenWidth;
	public static final int screenHeight;
	
	private int imageQuality;
	private double scale = 2;
	static {
		screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		screenHeight = screenDimension.height;
		screenWidth = screenDimension.width;
	}

	public PrintScreen() throws AWTException {
		robot = new Robot();

	}

	public BufferedImage getFullScreenShot() {
		BufferedImage bfImage = robot.createScreenCapture(new Rectangle(0, 0, screenWidth, screenHeight));
		return bfImage;
	}

	public BufferedImage getScreenShot(int x, int y, int width, int height) {
		BufferedImage bfImage = robot.createScreenCapture(new Rectangle(x, y, width, height));
		return bfImage;
	}

	public BufferedImage cutBufferedImage(BufferedImage srcBfImg, int x, int y, int width, int height) {
		CropImageFilter cropFilter = new CropImageFilter(x, y, width, height);
		Image img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(srcBfImg.getSource(), cropFilter));
		BufferedImage cutedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = cutedImage.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return cutedImage;
	}

	public static void main(String[] args) {
		try {
			new PrintScreen().work();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void work() {
		try {
			PrintScreen print = new PrintScreen();
			JFrame frame = new JFrame();
			frame.setSize(500, 500);
			JPanel panel = new JPanel();
			panel.setPreferredSize(new Dimension(500, 500));
			frame.add(panel);

			frame.setVisible(true);
			new Thread(new Runnable() {

				@Override
				public void run() {
					final Graphics g = panel.getGraphics();
					for (int i = 0;; i++) {
						BufferedImage image = print.cutBufferedImage(
								print.getScreenShot(0, 0, PrintScreen.screenWidth, PrintScreen.screenHeight), 0, 0,
								PrintScreen.screenWidth, PrintScreen.screenHeight);
						try {
							long time = System.currentTimeMillis();
							byte[] b = writeImage(image);
							// System.out.println(b.length);
							image = readImage(b);
							long time2 = System.currentTimeMillis();
							System.out.println(b.length / 1024 + "kB");
							System.out.println((time2 - time) / 1000.0);
							System.out.println((b.length / 1024.0) / (time2 - time) * 1000 + "kB/s");
							if (isLZ4Mode())
								System.out.println((preLen - b.length) / (double) preLen * 100.0 + "%");
							double scale;
							if (scaleOn)
								scale = PrintScreen.this.scale;
							else
								scale = 1;
							g.drawImage(image, 0, 0, (int) (image.getWidth() * scale),
									(int) (image.getHeight() * scale), 0, 0, image.getWidth(), image.getHeight(), null);
						} catch (IOException | ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						// try {
						// // ByteArrayOutputStream buff = new
						// // ByteArrayOutputStream();
						// // //
						// //
						// ����ѡ����JPG��ʽ,������ʽҲ�����ص��ǽ�ͼƬת����ĳ�ָ�ʽ���ֽ���
						// // // һ�������"PNG","GIF","JPG"��.
						// // ImageIO.write((RenderedImage) image, "PNG",
						// // buff);
						//
						// // System.out.println(buff.toByteArray().length);
						// Thread.sleep(10);
						// } catch (InterruptedException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }
					}
				}
			}).start();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	byte[] b1, b2;
	boolean scaleOn = true;

	private BufferedImage readImage(byte[] imageBytes) throws IOException, ClassNotFoundException {
		if (imageBytes == null)
			return null;
		InputStream imageReadBuff;

		if (isLZ4Mode()) {
			imageBytes = LZ4JNIManager.decompress(imageBytes, preLen);
			imageReadBuff = new ByteArrayInputStream(imageBytes);
			// JPEGImageDecoder dec =
			// JPEGCodec.createJPEGDecoder(imageReadBuff);
			// return dec.decodeAsBufferedImage();
			// return ImageIO.read(imageReadBuff);
		} else if (isZipMode()) {
			imageReadBuff = new ByteArrayInputStream(imageBytes);
			ZipInputStream zip = new ZipInputStream(imageReadBuff);
			zip.getNextEntry();
			imageReadBuff = zip;
		} else {
			imageReadBuff = new ByteArrayInputStream(imageBytes);
		}

		if (scaleOn)
			return sharpen(Thumbnails.of(imageReadBuff).scale(1.0f).outputQuality(1f).asBufferedImage());
		return Thumbnails.of(imageReadBuff).scale(1.0f).outputQuality(1f).asBufferedImage();
	}

	int preLen;

	// 序列化
	private byte[] writeImage(BufferedImage image) throws IOException {

		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		// 这里选用了JPG格式,其他格式也可以重点是将图片转换成某种格式的字节码
		// 一般可以用"PNG","GIF","JPG"吧.
		if (image == null) {
			return null;
		}

		byte[] result;
		String format = "bmp";
		if (isLZ4Mode()) {
			// JPEGImageEncoder enc = JPEGCodec.createJPEGEncoder(buff);
			// ImageIO.write((RenderedImage) image, "jpg", buff);
			float scale;
			if (scaleOn)
				scale = (float) (1 / PrintScreen.this.scale);
			else
				scale = 1.0f;
			Thumbnails.of(image).scale(scale).outputQuality(0.5f).outputFormat(format).toOutputStream(buff);
			// enc.encode(image);
			byte[] tmp = buff.toByteArray();
			preLen = tmp.length;
			result = LZ4JNIManager.compress(tmp);
		} else if (isZipMode()) {
			ZipEntry entry = new ZipEntry("image");
			// ImageIO.write((RenderedImage) image, "jpg", buff);
			ZipOutputStream zip = new ZipOutputStream(buff);
			zip.putNextEntry(entry);
			Thumbnails.of(image).scale(1f).outputQuality(0.5f).outputFormat(format).toOutputStream(zip);
			zip.close();
			result = buff.toByteArray();
		} else {
			Thumbnails.of(image).scale(1f).outputQuality(1f).outputFormat(format).toOutputStream(buff);
			result = buff.toByteArray();
		}
		return result;
	}

	private boolean isZipMode() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isLZ4Mode() {
		// TODO Auto-generated method stub
		return true;
	}

	private BufferedImage applyFilter(BufferedImage bufImage, float[] data) {
		if (bufImage == null)
			return null; // 如果bufImage为空则直接返回
		Kernel kernel = new Kernel(3, 3, data);
		ConvolveOp imageOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null); // 创建卷积变换操作对象
		BufferedImage filteredBufImage = new BufferedImage(bufImage.getWidth(), bufImage.getHeight(),
				BufferedImage.TYPE_INT_ARGB); // 过滤后的缓冲区图像
		imageOp.filter(bufImage, filteredBufImage);// 过滤图像，目标图像在filteredBufImage
		bufImage = filteredBufImage; // 让用于显示的缓冲区图像指向过滤后的图像
		return bufImage;
	}

	public BufferedImage sharpen(BufferedImage bufImage) {
		if (bufImage == null)
			return null;
		float a = 1f;
		float[] data = { //
				-1.0f / a, -1.0f / a, -1.0f / a, //
				-1.0f / a, +9.0f / a, -1.0f / a, //
				-1.0f / a, -1.0f / a, -1.0f / a };
		return applyFilter(bufImage, data);
	}

}
