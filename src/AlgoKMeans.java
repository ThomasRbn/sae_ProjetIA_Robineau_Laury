import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AlgoKMeans {

    public static void main(String[] args) {
        try {
            long debut = System.currentTimeMillis();

            BufferedImage sourceImage = ImageIO.read(new File("images/copie.png"));

            //Conversion de l'image en tableau de pixels avec leurs composantes RGB
            int width = sourceImage.getWidth();
            int height = sourceImage.getHeight();
            float[][] pixels = new float[width * height][3];
            int index = 0;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Color color = new Color(sourceImage.getRGB(x, y));
                    pixels[index][0] = color.getRed();
                    pixels[index][1] = color.getGreen();
                    pixels[index][2] = color.getBlue();
                    index++;
                }
            }

            int nbCoul = 100;
            int nbIteration = 0;
            int maxIterations = 100;

            // Appliquer l'algorithme de regroupement k-means
            float[][] centroides = new float[nbCoul][3];
            int[] associations = new int[nbCoul];
            float[][] anciensCentroides = new float[nbCoul][3];

            // Initialisation des couleurs aléatoires
            for (int i = 0; i < nbCoul; i++) {
                int randomIndex = (int) (Math.random() * (width * height));
                centroides[i] = pixels[randomIndex];
            }

            while (!hasConverged(anciensCentroides, centroides) && nbIteration < maxIterations) {
                System.out.println("Iteration " + nbIteration);
                // Réinitialiser les clusters et les centres
                for (int i = 0; i < nbCoul; i++) {
                    anciensCentroides[i] = centroides[i];
                    centroides[i] = new float[3];
                    associations[i] = 0;
                }

                // Attribution des pixels aux clusters
                for (int i = 0; i < width * height; i++) {
                    int indiceCentreProche = 0;
                    float minDistance = Float.MAX_VALUE;

                    for (int j = 0; j < nbCoul; j++) {
                        float distance = getDistance(pixels[i], anciensCentroides[j]);
                        if (distance < minDistance) {
                            minDistance = distance;
                            indiceCentreProche = j;
                        }
                    }

                    // Ajout du pixel au cluster correspondant
                    centroides[indiceCentreProche][0] += pixels[i][0];
                    centroides[indiceCentreProche][1] += pixels[i][1];
                    centroides[indiceCentreProche][2] += pixels[i][2];
                    associations[indiceCentreProche]++;
                }

                // Ajustement de la couleur du centroide
                for (int i = 0; i < nbCoul; i++) {
                    if (associations[i] > 0) {
                        centroides[i][0] /= associations[i];
                        centroides[i][1] /= associations[i];
                        centroides[i][2] /= associations[i];
                    }
                }

                nbIteration++;
            }

            // Remplacement des pixels par les couleurs des centres de cluster
            BufferedImage quantizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            index = 0;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int closestCenterIndex = 0;
                    float minDistance = Float.MAX_VALUE;

                    for (int i = 0; i < nbCoul; i++) {
                        float distance = getDistance(pixels[index], centroides[i]);
                        if (distance < minDistance) {
                            minDistance = distance;
                            closestCenterIndex = i;
                        }
                    }

                    Color newColor = new Color((int) centroides[closestCenterIndex][0],
                            (int) centroides[closestCenterIndex][1], (int) centroides[closestCenterIndex][2]);
                    quantizedImage.setRGB(x, y, newColor.getRGB());

                    index++;
                }
            }

            // Enregistrer l'image de sortie
            ImageIO.write(quantizedImage, "png", new File("images/copieKMeans_" + nbCoul + ".png"));
            long fin = System.currentTimeMillis();
            System.out.println("Temps d'exécution : " + (fin - debut) + " ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static float getDistance(float[] pixel1, float[] pixel2) {
        float sum = 0.0f;
        for (int i = 0; i < pixel1.length; i++) {
            float diff = pixel1[i] - pixel2[i];
            sum += diff * diff;
        }
        return (float) Math.sqrt(sum);
    }

    private static boolean hasConverged(float[][] vCentroides, float[][] nCentroides) {
        for (int i = 0; i < vCentroides.length; i++) {
            if (getDistance(vCentroides[i], nCentroides[i]) != 0) {
                return false;
            }
        }
        return true;
    }
}
