import TP3.Distance;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class BatterieTests {

    public static void main(String[] args) {

        //Photo fleur
        String[] chemins = {"images/copie.png", "images/venise.jpg"};
        String[] nbCouleurs = {"20", "50", "100"};
        String nbIterations = "100";
        String decomposition = "48";

        try {
            for (String chemin : chemins) {


                for (String nbCouleur : nbCouleurs) {
                    long debut = System.currentTimeMillis();
                    String[] argsFleur = {nbCouleur, nbIterations, chemin, "tests"};
                    AlgoKMeans.main(argsFleur);
                    long fin = System.currentTimeMillis();
                    long temps = fin - debut;
                    System.out.println("Temps d'exécution KMeans: " + temps + " ms");

                    debut = System.currentTimeMillis();
                    argsFleur = new String[]{nbCouleur, decomposition, chemin, "tests"};
                    TP3.AlgoV2.main(argsFleur);
                    fin = System.currentTimeMillis();
                    System.out.println("Temps d'exécution AlgoV2: " + (fin - debut) + " ms");

                    if (temps < fin - debut) {
                        System.out.println("KMeans est plus rapide pour " + nbCouleur + " couleurs");
                    } else {
                        System.out.println("AlgoV2 est plus rapide pour " + nbCouleur + " couleurs");
                    }

                    File fichierKMeans = new File("tests/copieKMeans_" + nbCouleur + ".png");
                    File fichierAlgoV2 = new File("tests/copieAlgoV2_" + decomposition + "_" + nbCouleur + ".png");
                    File fichierSource = new File(chemin);

                    BufferedImage iKMeans = ImageIO.read(fichierKMeans);
                    BufferedImage iAlgoV2 = ImageIO.read(fichierAlgoV2);
                    BufferedImage iSource = ImageIO.read(fichierSource);
                    long[] distances = getAlgorithmePlusProche(iKMeans, iAlgoV2, iSource);

                    System.out.println("Distance Source - KMeans : " + distances[0]);
                    System.out.println("Distance Source - AlgoV2 : " + distances[1]);
                    System.out.println("-----");
                }
                System.out.println("----- IMAGE SUIVANTE -----");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static long[] getAlgorithmePlusProche(BufferedImage iKMeans, BufferedImage iAlgoV2, BufferedImage source) {
        long distanceKMeans = Distance.getDistanceBetweenImages(iKMeans, source);
        long distanceAlgoV2 = Distance.getDistanceBetweenImages(iAlgoV2, source);
        return new long[]{distanceKMeans, distanceAlgoV2};
    }
}
