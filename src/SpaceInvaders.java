import java.awt.*;
import java.awt.event.*;
import javax.swing.*;// all 3 used for game 
import java.util.ArrayList;// to store all the alines and bullets
import java.util.Random;// for color of alines

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true;// used for aliens
        boolean used = false;// used for bullets

        Block (int x, int y, int width , int height, Image img ) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }

    }

    // board
    int tileSize = 32;
    int rows = 16;
    int columns = 16;
    int boardWidth = tileSize * columns; // 32*16=512
    int boardHeight = tileSize * rows; // 32*16=512

    Image gunImg;
    Image blueminialienImg;
    Image crazyalienImg;
    Image purplealienImg;
    Image redalienImg;

    ArrayList<Image> alienImgArray;// to store all the alien images so i can pick one random

    // ship 
int shipwidth =  tileSize*2;//32*2=64
int shipheight = tileSize;// 32 
int shipX = tileSize*columns/2 - tileSize;// 512/2-32=224
int shipY = boardHeight -tileSize*2;// 512-32*2=448
Block ship;
int shipvelocityx = tileSize;

//aliens
ArrayList<Block> alienArray;
int alienwidth = tileSize*2;
;int alienHeight = tileSize;
int alienX = tileSize;
int alienY = tileSize;
int alienRows = 2;
int alienColumns = 3;
int alienCount= 0; //  number alines to defeat
int alienVelocityX = 1; // aliens moving speed

//BULLETS 
ArrayList<Block> bulletArray;
int bulletWidth = tileSize/8;
int bulletHeight = tileSize/2;
int bulletVelocityY = -10; // bullets moving speed



Timer gameLoop;
int score = 0;
boolean gameOver = false;


    SpaceInvaders() {// constructor
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // to load images
        gunImg = new ImageIcon(getClass().getResource("./gun.png")).getImage();

        blueminialienImg = new ImageIcon(getClass().getResource("./blueminialien.png")).getImage();

        crazyalienImg = new ImageIcon(getClass().getResource("./crazyalien.png")).getImage();

        purplealienImg = new ImageIcon(getClass().getResource("./purplealien.png")).getImage();

        redalienImg = new ImageIcon(getClass().getResource("./redalien.png")).getImage();

        alienImgArray = new ArrayList<Image>();
        alienImgArray.add(blueminialienImg);
        alienImgArray.add(crazyalienImg);
        alienImgArray.add(purplealienImg);
        alienImgArray.add(redalienImg);

        ship = new Block(shipX, shipY, shipwidth, shipheight, gunImg);
        alienArray = new ArrayList<Block>();
        bulletArray = new ArrayList<Block>();

        // game loop / timer 
        gameLoop= new Timer(1000/60, this);// 1000/60=16.7ms ~ 60fps
        createAliens();
        gameLoop.start();
    }
    public  void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g) {
        // ship
        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);

        //aliens 
        for (int i=0 ; i<alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }

        // bullets
        g.setColor(Color.white);
        for (int i=0; i<bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            if (!bullet.used) {
                g.drawRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }
        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
if (gameOver) {
            g.drawString("Game Over" + String.valueOf(score), 10, 35);
        } else {
            g.drawString(String.valueOf(score),10 ,35);
        }
    }
    public void update() {
        // update aliens position
        for (int i=0; i<alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (alien.alive) {
                alien.x += alienVelocityX;
                // check if alien hits the wall
                if (alien.x + alien.width >= boardWidth || alien.x <= 0) {
                    alienVelocityX *= -1; // reverse direction
                    // move all aliens down
                    for (int j=0; j<alienArray.size(); j++) {
                        Block a = alienArray.get(j);
                        a.y += tileSize; // move down by one tile
                    }
                    break; // only need to check one alien for wall collision
                }
            }
        }
    }
    public void move() {
        // aliens
        for (int i =0; i < alienArray.size();i++) {
            Block alien = alienArray.get(i);
            if (alien.alive){
                alien.x += alienVelocityX;

                // if alien touch the wall
                if ( alien.x + alien.width >= boardWidth || alien.x <=0) {
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX*2;
                    
                    // move all aliens down
                    for (int j=0; j<alienArray.size(); j++) {
                        alienArray.get(j).y += alienHeight; 
                    }

                }
                if (alien.y >= ship.y){
                    gameOver = true;
                }
            }
        }
        // bullets
        for (int i=0; i<bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocityY;

            // bullet collision
            for (int j=0; j<alienArray.size(); j++) {
                Block alien = alienArray.get(j);
                if (alien.alive && !bullet.used && detectCollision(bullet, alien)) {
                    alien.alive = false;
                    bullet.used = true;
                    alienCount--;
                }
            }
        }

        // clean up bullets
        while (bulletArray.size() > 0 && (bulletArray.get(0).used || bulletArray.get(0).y < 0)) {
            bulletArray.remove(0);
        }
        // next level

        if (alienCount == 0) {
            score += alienColumns * alienRows * 100; // increase score based on number of aliens defeated
            alienColumns = Math.min(alienColumns + 1, columns / 2 - 2); // increase columns but not exceed half of the board
            alienRows = Math.min(alienRows + 1, rows - 6 ); // increase rows but not exceed half of the board
            alienArray.clear();
            bulletArray.clear();
            alienVelocityX = 1; // increase speed of aliens
            createAliens();
        }

    }


public void createAliens() {
        Random random = new Random();
        for (int r = 0; r < alienRows; r++) {
            for (int c = 0; c < alienColumns; c++) {
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                    alienX + c * alienwidth, 
                    alienY + r * alienHeight, 
                    alienwidth,
                    alienHeight, 
                    alienImgArray.get(randomImgIndex)
                );
                alienArray.add(alien);  
            }
        }
        alienCount = alienArray.size();
    }

    public boolean detectCollision(Block a, Block b) {// collision detection formula

        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();// repaint the panel every time the timer ticks
        if (gameOver) {
            gameLoop.stop();
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {
        
    }
    @Override
    public void keyPressed(KeyEvent e) {
        
    }
    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
         ship.x = shipX;
         alienArray.clear();
         bulletArray.clear();
        score = 0;
    alienVelocityX = 1;
    alienColumns = 3;
    alienRows = 2;
    gameOver = false;
    createAliens();
    gameLoop.start();
}

        if (e.getKeyCode() == KeyEvent.VK_LEFT && ship.x - shipvelocityx >= 0) {
            ship.x -= shipvelocityx;
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + ship.width + shipvelocityx <= boardWidth) {
            ship.x += shipvelocityx;
        }
        else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // create a bullet
            Block bullet = new Block(ship.x + ship.width*15/32, ship.y, bulletWidth, bulletHeight, null);
            bulletArray.add(bullet);
        }
    }
    
    
}