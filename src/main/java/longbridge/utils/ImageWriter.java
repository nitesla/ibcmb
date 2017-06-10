package longbridge.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
 * Created by Showboy on 07/06/2017.
 */
public class ImageWriter {

    public String writeImage(String imagePath, String caption) {

        String newImage = "";
        BufferedImage image = null;
        try {

            ByteArrayOutputStream baout = new ByteArrayOutputStream();
            File f = new File(imagePath);
            image = ImageIO.read(f);

            Graphics graphics = image.getGraphics();
            int y = image.getHeight() / 3;
            int x = image.getWidth();
            graphics.setColor(Color.RED);
            graphics.fillRect(0, 0, 200, 50);
            graphics.setColor(Color.BLACK);
            graphics.setFont(new Font("Arial Black", Font.BOLD, 20));
            graphics.drawString(caption, x, y);

            ImageIO.write(image, "jpg", baout);

            return Base64.getEncoder().encodeToString(baout.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newImage;
    }

//    String getImage(String imagePath, String caption) throws IOException {
//
//        ByteArrayOutputStream baout = new ByteArrayOutputStream();
//        File f = new File(imagePath);
//        //BufferedImage bufferedImage = new BufferedImage(170, 30, BufferedImage.TYPE_INT_RGB);
//        BufferedImage bufferedImage = ImageIO.read(f);
//        Graphics graphics = bufferedImage.getGraphics();
//        graphics.setColor(Color.RED);
//        graphics.fillRect(0, 0, 200, 50);
//        graphics.setColor(Color.BLACK);
//        graphics.setFont(new Font("Arial Black", Font.BOLD, 20));
//        graphics.drawString(caption, 10, 25);
//        ImageIO.write(bufferedImage, "jpg", baout);
//        return Base64.getEncoder().encodeToString(baout.toByteArray());
//    }

}