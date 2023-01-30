package com.fedor.cs34.discord.bot.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Images {
    public static byte[] toByteArray(BufferedImage image, String format) throws IOException {
        var buffer = new ByteArrayOutputStream();
        ImageIO.write(image, format, buffer);
        return buffer.toByteArray();
    }
}
