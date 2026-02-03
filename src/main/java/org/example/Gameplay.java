package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Gameplay extends JPanel implements KeyListener,ActionListener {
    private Timer timer;

    private boolean gameOver = false;

    //Ball_Pos
    private int ballX = 120;
    private int ballY = 350;

    // Ball
    private double ballXdir = -1;
    private double ballYdir = -2;

    // Paddle
    private int playerX = 310;

    // Score
    private int score=0;

    private MapGenerator map;
    private int brickRow=4;
    private int brickCol=12;
    private int totalBricks=brickRow*brickCol;

    //Smooth Collision


    public Gameplay() {
        map=new MapGenerator(brickRow,brickCol);

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();
        addKeyListener(this);

        timer = new Timer(8, this); // game speed
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 700, 600);

        // Ball
        g.setColor(Color.RED);
        g.fillOval(ballX, ballY, 20, 20);

        // Paddle
        g.setColor(Color.GREEN);
        g.fillRect(playerX, 550, 100, 8);


        // Borders
        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, 700, 10);   // top
        g.fillRect(0, 0, 10, 600);   // left
        g.fillRect(690, 0, 10, 600); // right

        // Map
        map.draw(g);

        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("serif", Font.BOLD, 20));
        g.drawString("Score: " + score, 550, 30);

        // Game-Over Score
        if (gameOver && totalBricks > 0) {
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over!", 250, 300);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Score: " + score, 280, 340);
            g.drawString("Press ENTER to Restart", 230, 380);
        }

        // Won
        if (totalBricks == 0) {
            gameOver= true;
            timer.stop();

            g.setColor(Color.GREEN);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You Won!", 270, 300);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Score: " + score, 280, 340);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Score: " + score, 280, 340);
            g.drawString("Press ENTER to Restart", 230, 380);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ballX += ballXdir;
        ballY += ballYdir;

        // Wall collision
        if (ballY <= 10) {
            ballY = 10;                       // snap outside ceiling
            ballYdir = Math.abs(ballYdir);    // force downward
        }
        if (ballX <= 10) {
            ballX = 10;
            ballXdir = Math.abs(ballXdir);   // force right
        }

        if (ballX >= 670) {
            ballX = 670;
            ballXdir = -Math.abs(ballXdir);  // force left
        }

        // Paddle collision
        Rectangle ballRect = new Rectangle(ballX, ballY, 20, 20);
        Rectangle paddleRect = new Rectangle(playerX, 550, 100, 8);

        if (ballRect.intersects(paddleRect)) {

            int paddleWidth = 100;
            int ballRadius = 10;

            int paddleCenterX = playerX + paddleWidth / 2;
            int ballCenterX = ballX + ballRadius;

            // Normalize hit position (-1 to +1)
            double hitPos =
                    (double)(ballCenterX - paddleCenterX) / (paddleWidth / 2);

            hitPos = Math.max(-1, Math.min(1, hitPos));

            double angle = hitPos * Math.toRadians(60);
            int speed = 4;

            ballXdir = speed * Math.sin(angle);
            ballYdir = -speed * Math.cos(angle);

            // Safety: avoid straight vertical
            if (Math.abs(ballXdir) < 0.8) {
                ballXdir = (ballXdir < 0) ? -1 : 1;
            }
        }

        // Brick Collision
        boolean brickHit = false;
        for (int i = 0; i < map.map.length && !brickHit; i++) {
            for (int j = 0; j < map.map[0].length; j++) {
                if (map.map[i][j] > 0) {

                    int brickX = map.sideOffset + j * map.brickWidth;
                    int brickY = map.topOffset + i * map.brickHeight;

                    Rectangle brickRect = new Rectangle(brickX, brickY, map.brickWidth,map.brickHeight);

                    if (ballRect.intersects(brickRect)) {
                        map.setBrickValue(0, i, j);
                        totalBricks--;
                        score += 5;

                        // Direction based on hit side
                        int ballLeft = ballX;
                        int ballRight = ballX + 20;
                        int ballTop = ballY;
                        int ballBottom = ballY + 20;

                        int brickLeft = brickX;
                        int brickRight = brickX + map.brickWidth;
                        int brickTop = brickY;
                        int brickBottom = brickY + map.brickHeight;

                        int overlapLeft = ballRight - brickLeft;
                        int overlapRight = brickRight - ballLeft;
                        int overlapTop = ballBottom - brickTop;
                        int overlapBottom = brickBottom - ballTop;

                        int minOverlapX = Math.min(overlapLeft, overlapRight);
                        int minOverlapY = Math.min(overlapTop, overlapBottom);

                        if (minOverlapX < minOverlapY) {
                            ballXdir = -ballXdir;
                            ballX += ballXdir;
                        } else {
                            ballYdir = -ballYdir;
                            ballY += ballYdir;
                        }
                        // Prevent vertical lock
                        if (Math.abs(ballXdir) < 0.8) {
                            ballXdir = (ballXdir < 0) ? -1 : 1;
                        }
                        brickHit = true;
                        break;
                    }
                }
            }
        }

        if (ballY > 570) {
            gameOver = true;
            timer.stop();
        }

        repaint();

    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600) {
                playerX = 600;
            } else {
                playerX += 20;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX <= 10) {
                playerX = 10;
            } else {
                playerX -= 20;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (gameOver) {
                restartGame();
            }
        }
    }
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void restartGame() {

        gameOver = false;
        score = 0;

        ballX = 120;
        ballY = 350;
        ballXdir = -1;
        ballYdir = -2;

        playerX = 310;

        totalBricks = 21;
        map = new MapGenerator(brickRow,brickCol);

        timer.start();
        repaint();
    }
}
