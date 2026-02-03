package org.example;

import java.awt.*;

public class MapGenerator {
    public int[][] map;

    public int sideOffset;
    public final int topOffset = 50;

    public int brickWidth ;
    public final int brickHeight = 25;



    public MapGenerator(int row,int col){
        map = new int[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                map[i][j]=1;
            }
        }

        int playAreaWidth = 700 - 160; // leave margins
        brickWidth = playAreaWidth / col;

        int totalWidth = col * brickWidth;
        sideOffset = (700 - totalWidth) / 2;
    }

    public void draw(Graphics g) {
        Graphics2D g2d= (Graphics2D) g;

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {

                    int x = sideOffset + j * brickWidth;
                    int y = topOffset + i * brickHeight;

                    g.setColor(Color.WHITE);
                    g.fillRect(j * brickWidth + 80,
                            i * brickHeight + 50,
                            brickWidth,
                            brickHeight);

                    g2d.setStroke(new BasicStroke(2));
                    g.setColor(Color.BLACK);
                    g.drawRect(j * brickWidth + 80,
                            i * brickHeight + 50,
                            brickWidth,
                            brickHeight);
                }
            }
        }
    }
    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }
}
