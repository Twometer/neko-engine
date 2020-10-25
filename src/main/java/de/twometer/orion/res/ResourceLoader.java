package de.twometer.orion.res;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ResourceLoader {

    public static String loadString(String path) throws IOException {
        BufferedReader reader = new BufferedReader(openReader(path));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
            builder.append(line).append("\n");
        return builder.toString();
    }

    public static ByteBuffer loadPixels(BufferedImage image) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y), true);
                buffer.put((byte) color.getRed());
                buffer.put((byte) color.getGreen());
                buffer.put((byte) color.getBlue());
                buffer.put((byte) color.getAlpha());
            }
        }
        buffer.flip();
        return buffer;
    }

    public static byte[] loadBytes(String path) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream reader = openStream(path);
        byte[] buf = new byte[8192];
        int read;
        while ((read = reader.read(buf)) > 0)
            outputStream.write(buf, 0, read);
        return outputStream.toByteArray();
    }

    public static BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(openStream(path));
    }

    public static InputStreamReader openReader(String path) throws IOException {
        return new InputStreamReader(openStream(path), StandardCharsets.UTF_8);
    }

    private static InputStream openStream(String path) throws IOException {
        return new FileInputStream(path);
    }
}
