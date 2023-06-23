package TP3;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class AlgoV1 {

    public static Map<Color, Integer> couleurs;
    private int pas;

    AlgoV1(int pas ){
        couleurs = new HashMap<>();
        this.pas = pas;
    }

    public void getAllColors(BufferedImage image){
        for(int x = 0; x<image.getWidth(); x++){
            for(int y = 0; y< image.getHeight(); y++){
                Color color = new Color(image.getRGB(x,y));
                addInMap(color);
            }
        }
    }

    public void addInMap(Color color){
        Iterator<Color> iter = couleurs.keySet().iterator();
        boolean isIn = false;
        while(iter.hasNext() && !isIn){
            Color colorMap = iter.next();
            if(isInPlage(colorMap, color)){
                int nb = couleurs.get(colorMap);
                nb++;
                couleurs.replace(colorMap, nb);
                isIn = true;
            }
        }
        if(!isIn) {
            couleurs.put(color, 1);
        }
    }

    public boolean isInPlage(Color couleurMap, Color couleur){
        int rgbmap = couleurMap.getRGB();
        int rgbcoul = couleur.getRGB();
       // System.out.printf("map : %d -- coul : %d%n", rgbmap, rgbcoul);
        if((rgbcoul>rgbmap-pas) && (rgbcoul<rgbmap+pas)) return true;
        return false;
    }

    public void afficherMap(){
        Iterator<Color> iter = couleurs.keySet().iterator();
        while (iter.hasNext()){
            Color color = iter.next();
            System.out.println(" " + color.getRGB() + " ==> " + couleurs.get(color));
        }
    }

    public static Map<Color, Integer> sortMapDescending() {
        List<Map.Entry<Color, Integer>> entryList = new ArrayList(couleurs.entrySet());

        // Trie la liste des entrées de la Map en utilisant un Comparator personnalisé
        Collections.sort(entryList, new Comparator<>() {
            @Override
            public int compare(Map.Entry<Color, Integer> entry1, Map.Entry<Color, Integer> entry2) {
                // Tri par ordre décroissant en comparant les valeurs
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });

        // Crée une nouvelle Map triée
        Map<Color, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Color, Integer> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public ArrayList<Color> getTopColor(int nbCol){

        Set entry = sortMapDescending().keySet();
        List entryList = entry.stream().toList();

        ArrayList<Color> topFiveStrings = new ArrayList<>();
        int count = 0;
        for (Object color : entryList) {
            topFiveStrings.add((Color) color);
            count++;
            if (count >= nbCol) {
                break;
            }
        }

        return topFiveStrings;
    }


    public static void main(String[] args) throws IOException {

        //nb color
        int nbColor = 5;
        int pas = 1000;
        if(args.length>=1){
            nbColor = Integer.parseInt(args[0]);
            pas = Integer.parseInt(args[1]);
        }

        BufferedImage image = ImageIO.read(new File("images/originale.jpg"));
        BufferedImage destination = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

        AlgoV1 sae = new AlgoV1(pas);
        sae.getAllColors(image);
        sae.afficherMap();
        ArrayList<Color> topColor = sae.getTopColor(nbColor);

        for (int x = 0; x<image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                ArrayList<Double> distances = new ArrayList();
                Color couleurmin = null;
                for (Color couleur : topColor) {
                    double distance = DistanceSAE.calculerDistance(couleur, new Color(image.getRGB(x, y)));
                    if (DistanceSAE.isMinimum(distances, distance)) {
                        couleurmin = couleur;
                    }
                    distances.add(distance);
                }

                assert couleurmin != null;
                int cRB = couleurmin.getRGB();

                destination.setRGB(x, y, cRB);
            }
        }
        ImageIO.write(destination, "PNG", new File("images/AlgoV1"+ nbColor + "_" + pas + ".png"));

    }
}

class DistanceSAE {

    public static long distance(BufferedImage image1, BufferedImage image2){
        long distanceTotale = 0;
        for (int x = 0; x<image1.getWidth(); x++){
            for(int y = 0; y<image1.getHeight(); y++){
                distanceTotale += calculerDistance(new Color(image1.getRGB(x, y)), new Color(image2.getRGB(x, y)));
            }
        }
        return distanceTotale;
    }

    public static long calculerDistance(Color couleur1, Color couleurPixel){
        return (long) (Math.pow( (couleur1.getRed() - couleurPixel.getRed()) , 2)
                +   Math.pow( ( couleur1.getGreen() - couleurPixel.getGreen()) , 2)
                +  Math.pow( ( couleur1.getBlue() - couleurPixel.getBlue()) , 2));
    }

    public static boolean isMinimum(ArrayList<Double> numbers, double minimum){
        for (double number : numbers) {
            if (number < minimum) {
                return false;
            }
        }

        return true;
    }
}
