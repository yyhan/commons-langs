package com.cloudin.commons.langs;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 图形验证码工具类
 *
 * @author 小天
 * @version 1.0.0, 2017/12/8 0008 14:24
 */
public class CaptchaTool {
	
	/**
	 * 干扰线条数量
	 */
	private int   interferenceLineSize  = 5;
	/**
	 * 干扰线条颜色
	 */
	private Color interferenceLineColor = Color.RED;
	/**
	 * 生成的图片的宽度
	 */
	private int   width                 = 120;
	/**
	 * 生成的图片的高度
	 */
	private int   height                = 30;
	
	private Color fontColor = Color.RED;
	
	private Font font;
	
	public CaptchaTool() {
		
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.startsWith("windows")) {
			font = new Font("宋体", Font.BOLD, 30);
		} else if (osName.startsWith("linux")) {
			font = new Font("cmr10", Font.BOLD, 30);
		} else if (osName.startsWith("freebsd")) {
			font = new Font(null, Font.BOLD, 30);
		} else if (osName.startsWith("mac")) {
			font = new Font(null, Font.BOLD, 30);
		}
	}
	
	public CaptchaTool(int width, int height, String fontFamily, int fontStyle, int fontSize, Color fontColor) {
		this.width = width;
		this.height = height;
		this.fontColor = fontColor;
		this.interferenceLineColor = new Color(fontColor.getRGB(), fontColor.getGreen(), fontColor.getBlue(), 200);
		font = new Font(fontFamily, fontStyle, fontSize);
	}
	
	/**
	 * 根据指定的验证码字符串创建图片
	 *
	 * @param imageVerifyCode 验证码字符串
	 *
	 * @return {@link java.awt.image.RenderedImage}
	 */
	public RenderedImage generateImage(String imageVerifyCode) {
		// 1.在内存中创建一张图片
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 2.得到绘图板
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		// 3.设置背景颜色
		graphics.setColor(Color.WHITE);
		// 4.填充区域
		graphics.fillRect(0, 0, width, height);
		
		// 5.绘制验证码
		drawString(graphics, imageVerifyCode);
		
		// 6.在图片上画干扰线
		drawInterferenceLine(graphics);
		
		return image;
	}
	
	/**
	 * 在图片上画随机线条
	 *
	 * @param graphics 绘图板
	 */
	private void drawInterferenceLine(Graphics2D graphics) {
		// 设置颜色
		graphics.setColor(interferenceLineColor);
		graphics.setStroke(new BasicStroke(2));
		// 设置线条个数并画线
		Random random = ThreadLocalRandom.current();
		for (int i = 0; i < interferenceLineSize; i++) {
			int x1 = random.nextInt(width);
			int y1 = random.nextInt(height);
			int x2 = random.nextInt(width);
			int y2 = random.nextInt(height);
			graphics.drawLine(x1, y1, x2, y2);
		}
	}
	
	/**
	 * 将验证码添加到绘图板上
	 *
	 * @param graphics  绘图板
	 * @param imageCode 图片验证码
	 */
	private void drawString(Graphics2D graphics, String imageCode) {
		
		int charSpacing = (int) (this.width / imageCode.length() * 0.75);
		
		// 设置颜色
		graphics.setColor(fontColor);
		// 设置字体
		graphics.setFont(font);
		
		Random random = ThreadLocalRandom.current();
		
		for (int i = 0, x = charSpacing; i < imageCode.length(); i++, x += charSpacing) {
			// 设置字体旋转角度
			int degree = random.nextInt() % 30;
			// 正向角度
			graphics.rotate(degree * Math.PI / 180, x, height / 2);
			
			// 绘制字符
			graphics.drawString(imageCode.substring(i, i + 1), x, (int) (height * 0.75));
			
			// 反向角度
			graphics.rotate(-degree * Math.PI / 180, x, height / 2);
		}
	}
}
