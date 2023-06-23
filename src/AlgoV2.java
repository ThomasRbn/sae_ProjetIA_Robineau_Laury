import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Stocker les couleurs par plages pour éviter la multiplication des verts (AlgoV1)
 */
public class AlgoV2 {

    public static void main(String[] args) throws IOException {
        long debut = System.currentTimeMillis();
        BufferedImage source = ImageIO.read(new File("images/copie.png"));

        int decomposition = 64;
        int nbCouleurs = 20;
        HashMap<Color, Integer> composition = new HashMap<>();

        for (int i = 0; i < 256; i += decomposition) {
            for (int j = 0; j < 256; j += decomposition) {
                for (int k = 0; k < 256; k += decomposition) {
                    composition.put(new Color(i, j, k), 0);
                }
            }
        }

//        System.out.println(composition);

        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                Color couleurPixelCourant = new Color(source.getRGB(x, y));
                Color couleurPlusProche = getColorRange(couleurPixelCourant, composition, decomposition);

                composition.put(couleurPlusProche, composition.get(couleurPlusProche) + 1);

            }
        }

        Color[] selection = getMostUsedColors(nbCouleurs, composition);

        BufferedImage destination = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {

                Color pixel = new Color(source.getRGB(x, y));
                int distanceMin = Distance.getDistanceCouleur(pixel, selection[0]);
                Color colorMin = selection[0];
                for (int i = 1; i < selection.length; i++) {
                    int distance = Distance.getDistanceCouleur(pixel, selection[i]);
                    if (distance < distanceMin) {
                        distanceMin = distance;
                        colorMin = selection[i];
                    }
                }
                destination.setRGB(x, y, colorMin.getRGB());
            }
        }

        ImageIO.write(destination, "png", new File("images/copieAlgoV2_" + decomposition + "_" + nbCouleurs + ".png"));
        long fin = System.currentTimeMillis();
        System.out.println("Temps d'exécution : " + (fin - debut) + " ms");
    }

    public static Color getColorRange(Color c, HashMap<Color, Integer> range, int decomposition) {
        int red = c.getRed();
        int green = c.getGreen();
        int blue = c.getBlue();

        Color[] keys = range.keySet().toArray(new Color[0]);
        for (Color key : keys) {

            if ((red >= key.getRed() && red < key.getRed() + decomposition) &&
                    (green >= key.getGreen() && green < key.getGreen() + decomposition) &&
                    (blue >= key.getBlue() && blue < key.getBlue() + decomposition)) {
                return key;
            }
        }
        return null;
    }

    public static Color[] getMostUsedColors(int nbColors, HashMap<Color, Integer> compo) {
        Color[] colors = new Color[nbColors];
        int[] values = new int[nbColors];
        int i = 0;
        for (Color color : compo.keySet()) {
            int value = compo.get(color);
            if (i < nbColors) {
                colors[i] = color;
                values[i] = value;
                i++;
            } else {
                int min = 0;
                for (int j = 0; j < nbColors; j++) {
                    if (values[j] < values[min]) {
                        min = j;
                    }
                }
                if (value > values[min]) {
                    colors[min] = color;
                    values[min] = value;
                }
            }
        }

        for (int t = 0; t < colors.length; t++) {
            System.out.println(colors[t] + " : " + values[t]);
        }
        return colors;
    }
}
