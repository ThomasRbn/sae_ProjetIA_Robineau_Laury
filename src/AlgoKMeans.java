import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AlgoKMeans {

    public static void main(String[] args) {
        try {
            long debut = System.currentTimeMillis();
            if (args.length < 4) {
                System.out.println("Usage: java AlgoKMeans <nbCouleurs> <nbIterations> <fichier> <sortie>");
                System.exit(1);
            }
            int nbCoul = Integer.parseInt(args[0]);
            int nbIteration = Integer.parseInt(args[1]);
            File fichier = new File(args[2]);
            int maxIterations = 100;

            BufferedImage sourceImage = ImageIO.read(fichier);

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
            BufferedImage destination = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            index = 0;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int indiceCentrePlusProche = 0;
                    float minDistance = Float.MAX_VALUE;

                    for (int i = 0; i < nbCoul; i++) {
                        float distance = getDistance(pixels[index], centroides[i]);
                        if (distance < minDistance) {
                            minDistance = distance;
                            indiceCentrePlusProche = i;
                        }
                    }

                    Color newColor = new Color((int) centroides[indiceCentrePlusProche][0],
                            (int) centroides[indiceCentrePlusProche][1], (int) centroides[indiceCentrePlusProche][2]);
                    destination.setRGB(x, y, newColor.getRGB());

                    index++;
                }
            }



            // Enregistrer l'image de sortie
            ImageIO.write(destination, "png", new File(args[3] + "/" + fichier.getName().split("\\.")[0] + "KMeans_" + nbCoul + ".png"));
            long fin = System.currentTimeMillis();

            if (!args[3].equals("tests")){
                System.out.println("Temps d'exécution : " + (fin - debut) + " ms");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getDistance(float[] pixel1, float[] pixel2) {
        int distanceRouge = (int) Math.pow(pixel1[0] - pixel2[0], 2);
        int distanceVert = (int) Math.pow(pixel1[1] - pixel2[1], 2);
        int distanceBleu = (int) Math.pow(pixel1[2] - pixel2[2], 2);

        return distanceRouge + distanceVert + distanceBleu;
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


