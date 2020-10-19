package de.twometer.orion.res;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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
