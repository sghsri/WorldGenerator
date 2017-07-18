/**
 * Created by SriramHariharan on 7/18/17.
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import static java.lang.System.out;

/**
 *
 * @author User1 & Sriram Hariharan
 */
public class Anim extends JPanel {

    private static final int SCREENW = 768; //WIDTH OF SCREEN
    private static final int SCREENH = 640; //HEIGHT OF SCREEN
    private static final int MAPW = 200;
    private static final int MAPH = 200;

    public static int[][] map = new int[MAPW][MAPH];
    public static double[][] elevations = new double[MAPW][MAPH];
    public static int[][] moisturemap = new int[MAPW][MAPH];
    public static String[][] politicalmap = new String[MAPW][MAPH];
    public static int[][] citymap = new int[MAPW][MAPH];
    public static int GAMESTATE = 0;

    private Timer timer = new Timer(1000, new TimerListener());
    public ArrayList<BufferedImage> anim = new ArrayList<BufferedImage>();
    public static int counter = 0;
    public ArrayList<String> nations = new ArrayList<String>();

    public Anim() {
        init();
        setBackground(Color.BLACK);
        timer.start();
    }

    public void init() {
        this.generateTerrain();
        this.generateBiomes();
        this.generatePolitical();
        //    out.println(Arrays.deepToString(politicalmap));
        this.repaint();


    }

    public static void main(String[] args) {
        JFrame c = new JFrame();
        c.addMouseListener(new MyAdapter());
        c.addKeyListener(new TAdapter());
        c.add(new Anim());
        c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c.setSize(SCREENW + 16, SCREENH + 38);
        c.setVisible(true);


    }

    private class TimerListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
       /*  generateTerrain();
            generateBiomes(); */
            repaint();
        }
    }

    public void generateBiomes() {
        int equator = map.length / 2;
        int chunk = 2;//ThreadLocalRandom.current().nextInt(1, 5);
        for (int j = 0; j < map.length; j++) {
            for (int i = 0; i < map[0].length; i++) {
                if(map[i][j] != 0){
                    moisturemap[i][j] = distanceFromWater(j,i);
                    if( moisturemap[i][j]> 3 && (i > 40 && i < map.length - 40)) {
                        createDesert(i, j, equator);
                    }
                    if(moisturemap[i][j] > 0/* && moisturemap[i][j] <=4  &&dist(equator,i)< ThreadLocalRandom.current().nextInt(20, 40) */ ){
                        createJungle(i, j, equator + ThreadLocalRandom.current().nextInt(-30, 30));
                    }
                }
                    /*

                    */

            }

        }
      /*  for(int[] x : moisturemap){
            out.println(Arrays.toString(x));
        } */

    }

    public int getDistance(int x1, int x2, int y1, int y2){
        return (int)(Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)));

    }


    public int distanceFromWater(int x, int y){


        int distance = Integer.MAX_VALUE;
        int xmax, xmin,  ymax, ymin;
        xmax = x+4;
        xmin = x-4;
        ymax = y+4;
        ymin = y-4;
        if(xmax > map.length-1){
            xmax = map.length-1;
        }
        if(ymax > map.length-1){
            ymax = map.length-1;
        }
        if(ymin < 0){
            ymin = 0;
        }
        if(xmin < 0){
            xmin = 0;
        }

        for(int j = ymin; j<=ymax;j++) {
            for (int i = xmin; i <= xmax; i++) {
                if (map[i][j] == 0) {
                    int temp = getDistance(x,i,y,j);
                  /*  if(temp == 1){
                        return 1;
                    } */
                    if (temp < distance) {
                        distance = temp;
                    }
                }
            }
        }



        return distance;
    }

    public void createDesert(int i, int j, int equator){
        if ( (elevations[i][j] >= .25 && elevations[i][j] < .6)) {
            int rand = ThreadLocalRandom.current().nextInt(1, 10);
            map[i][j] = 5;
            if (rand < 5) {
                if(i != 199) {
                    map[i + 1][j] = 5;
                }
                if(i != 0) {
                    map[i - 1][j] = 5;
                }
            }
        }
    }
    public void createJungle(int i, int j, int bounds){
        if (map[i][j] !=5 && (elevations[i][j] >= .2 && elevations[i][j] < .5)) {
            int rand = ThreadLocalRandom.current().nextInt(1, 10);
            map[i][j] = 7;
            if (rand < 4) {
                if(i != 199) {
                    map[i + 1][j] = 7;
                }
                if(i != 0) {
                    map[i - 1][j] = 7;
                }
            }
            if (rand < 4) {
                if(j != 199) {
                    map[i][j+1] = 7;
                }
                if(j != 0) {
                    map[i][j-1] = 7;
                }
            }
        }
    }


    public void generatePolitical(){
        int x = ThreadLocalRandom.current().nextInt(0, map.length);
        int y = ThreadLocalRandom.current().nextInt(0, map[0].length);

        do{
            x = ThreadLocalRandom.current().nextInt(0, map.length);
            y = ThreadLocalRandom.current().nextInt(0, map[0].length);
        }
        while(map[x][y] == 0);
        citymap[x][y] = 1;
    }



    public void generateTerrain(){
        int seed = ThreadLocalRandom.current().nextInt(1, 1001);
        // seed = 50;
        OpenSimplexNoise noise = new OpenSimplexNoise(seed); //SEED

        for (int j = 0; j < map.length; j++) {
            for (int i = 0; i < map[j].length; i++) {

                // double value = noise.eval(i/32.0,j/32.0,1);
                double value = noise.eval(i / 32.0, j / 32.0, 1) + (.5 * noise.eval(2 * (i / 32.0), 2 * (j / 32.0), 1)) + (.25 * noise.eval(4 * (i / 32.0), 4 * (j / 32.0), 1));
                elevations[i][j] = value; //Math.pow(value,1.5);



                if(elevations[i][j] >= .25){
                    map[i][j] = 3;
                }

                if(elevations[i][j] >= .2 && elevations[i][j] < .25){ //sand
                    map[i][j] = 5;
                }
                if(elevations[i][j] < .2){
                    map[i][j] = 0;
                }

                if(elevations[i][j] > .56){
                    map[i][j] = 2;
                }
                if(elevations[i][j] > .73){
                    map[i][j] = 8;
                }
                if(elevations[i][j] > .87){
                    map[i][j] = 4;
                }
               /* if(value > 0 && value <= .4){
                    map[i][j] = 5;
                } */
                /*
                if(isTouching(i,j,0)){
                    map[i][j] = 4;
                } */






            }
        }

        /*
        int numcities = ThreadLocalRandom.current().nextInt(5, 40);
        for(int k = 0; k<numcities;k++){
            int x = ThreadLocalRandom.current().nextInt(0, map.length);
            int y = ThreadLocalRandom.current().nextInt(0,map.length);
            if(map[y][x] != 0 && map[y][x] != 4){
                map[y][x] = 6;
            }
        } */
        out.println(seed);
        out.println(Arrays.deepToString(elevations));
    }

    public int dist(int x, int y){
        return Math.abs(x-y);
    }

    public double randRange(double min, double max) {
        double random = ThreadLocalRandom.current().nextDouble(min, max);
        return random;
    }

    public boolean isTouching(int i, int j, int val){
        if (j > 0 && map[j - 1][i] == val) {
            return true;
        }
        if (j < map.length - 1 && map[j + 1][i] == val) {
            return true;
        }
        if (i > 0 && map[j][i - 1] == val) {
            return true;
        }
        if (i < map[j].length - 1 && map[j][i + 1] == val) {
            return true;
        }
        return false;
    }

    public void update() {
        for (int j = 0; j < map.length; j++) {
            for (int i = 0; i < map[j].length; i++) {


                if (map[i][j] == 1) { //magma
                    int rand = ThreadLocalRandom.current().nextInt(1, 101);
                    if (rand < 2) {
                        if (j > 0 && map[j - 1][i] == 0) {
                            map[j - 1][i] = 2;
                        }
                        if (j < map.length - 1 && map[j + 1][i] == 0) {
                            map[j + 1][i] = 2;
                        }
                        if (i > 0 && map[j][i - 1] == 0) {
                            map[j][i - 1] = 2;
                        }
                        if (i < map[j].length - 1 && map[j][i + 1] == 0) {
                            map[j][i + 1] = 2;
                        }


                    }
                }

                if (map[i][j] == 0) { //water
                    int rand = ThreadLocalRandom.current().nextInt(1, 101);
                    if (rand < 10) {
                        int c = 0;
                        if (j > 0 && map[j - 1][i] == 2) {
                            map[j - 1][i] = 3;
                        }
                        if (j < map.length - 1 && map[j + 1][i] == 2) {
                            map[j + 1][i] = 3;
                        }
                        if (i > 0 && map[j][i - 1] == 2) {
                            map[j][i - 1] = 3;
                        }
                        if (i < map[j].length - 1 && map[j][i + 1] == 2) {
                            map[j][i + 1] = 3;
                        }


                    }
                }


/*
                if (map[i][j] == 3) { //stone
                    int rand = ThreadLocalRandom.current().nextInt(1, 101);
                    if (rand < 5) {
                        map[i][j] = 0;
                    }
                }
               */


                //map[i][j]=ThreadLocalRandom.current().nextInt(0,3);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (GAMESTATE == 0) {
                    if (map[y][x] == 0) { //Water
                    /*
                    int r = (int)(Math.random() * 2);
                    if(r == 0){
                        g2d.setColor(new Color(0, 105, 148));
                    }
                    else{
                        g2d.setColor(new Color(15,94,156));
                    } */
                        g2d.setColor(new Color(35, 137, 218));

                        g2d.fillRect(x * (SCREENW / map[y].length), y * (SCREENH / map.length), SCREENW / map[y].length, SCREENH / map.length);
                        //g2d.fillRect(x, y, x+115, y+115);
                    }

                    if (map[y][x] == 1) { //Magma
                        g2d.setColor(Color.red);
                        g2d.fillRect(x * (SCREENW / map[y].length), y * (SCREENH / map.length), SCREENW / map[y].length, SCREENH / map.length);
                        //g2d.fillRect(x, y, x+115, y+115);
                    }
                    if (map[y][x] == 5) { //Sand
                        g2d.setColor(new Color(239, 221, 111));
                        g2d.fillRect(x * (SCREENW / map[y].length), y * (SCREENH / map.length), SCREENW / map[y].length, SCREENH / map.length);
                        //g2d.fillRect(x, y, x+115, y+115);
                    }
                    if (map[y][x] == 2) { //Stone
                        g2d.setColor(Color.gray);
                        g2d.fillRect(x * (SCREENW / map[y].length), y * (SCREENH / map.length), SCREENW / map[y].length, SCREENH / map.length);
                        //g2d.fillRect(x, y, x+115, y+115);
                    }
                    if (map[y][x] == 3) { //Grass
                        g2d.setColor(new Color(34, 139, 34));
                        g2d.fillRect(x * (SCREENW / map[y].length), y * (SCREENH / map.length), SCREENW / map[y].length, SCREENH / map.length);
                        //g2d.fillRect(x, y, x+115, y+115);
                    }
                    if (map[y][x] == 4) { //SNOW
                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(x * (SCREENW / map[y].length), y * (SCREENH / map.length), SCREENW / map[y].length, SCREENH / map.length);
                        //g2d.fillRect(x, y, x+115, y+115);
                    }
                    if (map[y][x] == 6) { //City
                        g2d.setColor(Color.black);
                        g2d.fillRect(x * (SCREENW / map[y].length), y * (SCREENH / map.length), 1 + SCREENW / map[y].length, 1 + SCREENH / map.length);
                        //g2d.fillRect(x, y, x+115, y+115);
                    }
                    if (map[y][x] == 7) { //JUNGLE
                        g2d.setColor(new Color(41, 171, 135));
                        g2d.fillRect(x * (SCREENW / map[y].length), y * (SCREENH / map.length), 1 + SCREENW / map[y].length, 1 + SCREENH / map.length);
                        //g2d.fillRect(x, y, x+115, y+115);
                    }
                    if (map[y][x] == 8) { //Mountain
                        g2d.setColor(Color.lightGray);
                        g2d.fillRect(x * (SCREENW / map[y].length), y * (SCREENH / map.length), 1 + SCREENW / map[y].length, 1 + SCREENH / map.length);
                        //g2d.fillRect(x, y, x+115, y+115);
                    }


                }
                if(GAMESTATE == 1){
                    if(map[y][x] == 0){
                        g2d.setColor(new Color(35, 137, 218));
                    }
                    if(citymap[y][x] == 1){
                        g2d.setColor(Color.BLACK);
                    }
                    else{
                        g2d.setColor(Color.RED);
                    }
                    g2d.fillRect(x * (SCREENW / map[y].length), y * (SCREENH / map.length), SCREENW / map[y].length, SCREENH / map.length);

                }
            }
        }



    }

    private static class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            switch( keyCode ) {
                case KeyEvent.VK_UP:
                    // handle up
                    break;
                case KeyEvent.VK_DOWN:
                    // handle down
                    break;
                case KeyEvent.VK_LEFT:
                    // handle left
                    GAMESTATE = 0;
                    break;
                case KeyEvent.VK_RIGHT :
                    GAMESTATE = 1;
                    // handle right
                    break;
            }

        }
    }

    private static class MyAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            out.println("" + e.getX() + " " + e.getY());

        }
    }

}