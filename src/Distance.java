import java.awt.*;
import java.awt.image.BufferedImage;

public class Distance {

    public static int getDistanceCouleur(Color c1, Color c2) {
        int distanceRouge = (int) Math.pow(c1.getRed() - c2.getRed(), 2);
        int distanceVert = (int) Math.pow(c1.getGreen() - c2.getGreen(), 2);
        int distanceBleu = (int) Math.pow(c1.getBlue() - c2.getBlue(), 2);

        return distanceRouge + distanceVert + distanceBleu;
    }

    public static long getDistanceBetweenImages(BufferedImage img1, BufferedImage i2g) {

        long distance = 0;

        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {

                long pixelImg1 = img1.getRGB(x, y);
                long pixelImg2 = i2g.getRGB(x, y);

                distance += getDistanceCouleur(new Color((int) pixelImg1), new Color((int) pixelImg2));

            }
        }

        return distance;
    }
}
