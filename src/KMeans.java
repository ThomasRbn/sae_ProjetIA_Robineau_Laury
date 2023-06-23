import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class KMeans {

    private final float[][] centroides;
    private final int[] associations;
    private final float[][] anciensCentroides;
    private float[][] pixels;
    private final int nbCoul;
    private final BufferedImage sourceImage;
    private int nbIteration;

    public KMeans(BufferedImage sourceImage, int nbCoul, int nbIteration){
        this.nbCoul = nbCoul;
        this.nbIteration = nbIteration ;
        this.centroides = new float[nbCoul][3];
        this.associations = new int[nbCoul];
        this.anciensCentroides = new float[nbCoul][3];
        this.sourceImage = sourceImage;
        convertirImage();
    }

    private void convertirImage(){
        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();
        this.pixels = new float[width * height][3];
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
        initialiserCouleursAleatoires();
    }

    private void initialiserCouleursAleatoires(){
        for (int i = 0; i < nbCoul; i++) {
            int randomIndex = (int) (Math.random() * (sourceImage.getWidth() * sourceImage.getHeight()));
            centroides[i] = pixels[randomIndex];
        }
    }


    public void calculerCouleurs(){
        int maxIterations = 100;
        while (!hasConverged(anciensCentroides, centroides) && nbIteration < maxIterations) {
            System.out.println("Iteration " + nbIteration);
            // Réinitialiser les clusters et les centres
           initialiserClustersCentres();
            // Attribution des pixels aux clusters
            attribuerPixelsClusters();
            // Ajustement de la couleur du centroide
            ajusterCouleurCentroides();
            nbIteration++;
        }
        remplacerPixelsParCentre();
    }

    private void initialiserClustersCentres(){
        for (int i = 0; i < nbCoul; i++) {
            anciensCentroides[i] = centroides[i];
            centroides[i] = new float[3];
            associations[i] = 0;
        }
    }

    private void attribuerPixelsClusters(){
        for (int i = 0; i < sourceImage.getWidth() * sourceImage.getHeight(); i++) {
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
    }

    private void ajusterCouleurCentroides(){
        for (int i = 0; i < nbCoul; i++) {
            if (associations[i] > 0) {
                centroides[i][0] /= associations[i];
                centroides[i][1] /= associations[i];
                centroides[i][2] /= associations[i];
            }
        }
    }



    private void remplacerPixelsParCentre(){
        BufferedImage destination = new BufferedImage(sourceImage.getWidth(),
                sourceImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        int index = 0;
        for (int x = 0; x < sourceImage.getWidth(); x++) {
            for (int y = 0; y < sourceImage.getHeight(); y++) {
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
        enregistrerImage(destination);
    }

    private void enregistrerImage(BufferedImage destination)  {
        try {
            ImageIO.write(destination, "png", new File("images/copieKMeans_" + nbCoul + ".png"));
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

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: java AlgoKMeans <nbCouleurs> <nbIterations>");
            System.exit(1);
        }
        int nbCoul = Integer.parseInt(args[0]);
        int nbIteration = Integer.parseInt(args[1]);
        int maxIterations = 100;

        long debut = System.currentTimeMillis();

        BufferedImage sourceImage = ImageIO.read(new File("images/colorfull.jpg"));

        KMeans kMeans = new KMeans(sourceImage, nbCoul, nbIteration);
        kMeans.calculerCouleurs();
        long fin = System.currentTimeMillis();
        System.out.println("Temps d'exécution : " + (fin - debut) + " ms");
        System.out.println("Image sauvegardée dans le fichier images/copieKMeans_" + nbCoul + ".png");


    }

}
