import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class ZombieAlienInvasion extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true; // used for zombies
        boolean used = false; // used for peas

        Block(int x, int y, int width, int height, Image img) {
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
    int boardWidth = tileSize * columns;
    int boardHeight = tileSize * rows;

    Image peaShooterImg;
    Image brownCoatImg;
    Image coneHeadImg;
    Image bucketHeadImg;
    ArrayList<Image> zombieImgArray;

    // peashooter
    int peaShooterWidth = tileSize; // 64px
    int peaShooterHeight = tileSize; // 32 px
    int peaShooterX = tileSize * columns / 2 - tileSize;
    int peaShooterY = boardHeight - tileSize * 3;
    int peaShooterVelocityX = tileSize;
    Block peaShooter;

    // zombies
    ArrayList<Block> zombieArray;
    int zombieWidth = tileSize; // 32px
    int zombieHeight = tileSize * 2; // 64px
    int zombieX = tileSize;
    int zombieY = tileSize;

    int zombieRows = 2;
    int zombieColumns = 3;
    int zombieCount = 0;
    int zombieVelocityX = 1;
    Block zombie;

    Timer gameLoop;

    ZombieAlienInvasion() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(new Color(94, 144, 75));
        setFocusable(true);
        addKeyListener(this);

        // load images
        peaShooterImg = new ImageIcon(getClass().getResource("./Peashooter.png")).getImage();
        brownCoatImg = new ImageIcon(getClass().getResource("./zombie1.png")).getImage();
        coneHeadImg = new ImageIcon(getClass().getResource("./Conehead_Zombie.png")).getImage();
        bucketHeadImg = new ImageIcon(getClass().getResource("./HD_Buckethead_Zombie-_1_.png")).getImage();

        zombieImgArray = new ArrayList<Image>();
        zombieImgArray.add(brownCoatImg);
        zombieImgArray.add(coneHeadImg);
        zombieImgArray.add(bucketHeadImg);

        peaShooter = new Block(peaShooterX, peaShooterY, peaShooterWidth, peaShooterHeight, peaShooterImg);
        zombieArray = new ArrayList<Block>();

        // game timer
        gameLoop = new Timer(1000 / 60, this); // 60 fps
        createZombies();
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // peashooter
        g.drawImage(peaShooter.img, peaShooter.x, peaShooter.y, peaShooter.width, peaShooter.height, null);

        // zombies
        for (int i = 0; i < zombieArray.size(); i++) {
            Block zombie = zombieArray.get(i);
            if (zombie.alive) {
                g.drawImage(zombie.img, zombie.x, zombie.y, zombie.width, zombie.height, null);
            }
        }

    }

    public void createZombies() {
        Random random = new Random();
        for (int r = 0; r < zombieRows; r++) {
            for (int c = 0; c < zombieColumns; c++) {
                int randomImgIndex = random.nextInt(zombieImgArray.size());
                Block zombie = new Block(
                        zombieX + c * zombieWidth,
                        zombieY + r * zombieHeight,
                        zombieWidth,
                        zombieHeight,
                        zombieImgArray.get(randomImgIndex));
                zombieArray.add(zombie);
            }
        }
        zombieCount = zombieArray.size();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT && peaShooter.x - peaShooterVelocityX >= 0) {
            peaShooter.x -= peaShooterVelocityX; // Move left one tile
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT
                && peaShooter.x + peaShooter.width + peaShooterVelocityX <= boardWidth) {
            peaShooter.x += peaShooterVelocityX; // Move right one tile
        }
    }
}
