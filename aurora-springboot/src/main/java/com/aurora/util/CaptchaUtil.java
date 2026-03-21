package com.aurora.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * 图形验证码工具类
 */
public class CaptchaUtil {

    private static final String CHARS = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CHAR_COUNT = 4;

    /**
     * 生成验证码图片和文本
     */
    public static CaptchaResult generateCaptcha() {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 填充背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        // 生成验证码文本
        String captchaText = generateRandomText();
        
        // 绘制验证码字符
        drawText(g, captchaText);
        
        // 添加干扰线
        drawNoiseLine(g);
        
        // 添加噪点
        drawNoisePoint(g);
        
        g.dispose();
        
        // 转换为Base64
        String base64Image = imageToBase64(image);
        
        return new CaptchaResult(captchaText, base64Image);
    }
    
    /**
     * 生成随机文本
     */
    private static String generateRandomText() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CHAR_COUNT; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
    
    /**
     * 绘制文本
     */
    private static void drawText(Graphics2D g, String text) {
        Random random = new Random();
        g.setFont(new Font("Arial", Font.BOLD, 24));
        
        for (int i = 0; i < text.length(); i++) {
            // 随机颜色
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            
            // 随机位置和角度
            int x = 20 + i * 20 + random.nextInt(10);
            int y = 25 + random.nextInt(10);
            
            // 旋转角度
            double angle = (random.nextDouble() - 0.5) * 0.5;
            g.rotate(angle, x, y);
            g.drawString(String.valueOf(text.charAt(i)), x, y);
            g.rotate(-angle, x, y);
        }
    }
    
    /**
     * 绘制干扰线
     */
    private static void drawNoiseLine(Graphics2D g) {
        Random random = new Random();
        g.setStroke(new BasicStroke(1.5f));
        
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }
    }
    
    /**
     * 绘制噪点
     */
    private static void drawNoisePoint(Graphics2D g) {
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g.fillOval(x, y, 2, 2);
        }
    }
    
    /**
     * 图片转Base64
     */
    private static String imageToBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }
    
    /**
     * 验证码结果类
     */
    public static class CaptchaResult {
        private final String text;
        private final String image;
        
        public CaptchaResult(String text, String image) {
            this.text = text;
            this.image = image;
        }
        
        public String getText() {
            return text;
        }
        
        public String getImage() {
            return image;
        }
    }
}
