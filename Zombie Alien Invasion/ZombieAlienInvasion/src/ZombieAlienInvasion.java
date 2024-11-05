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
    int zombieVelocity = 1;

    // peas
    ArrayList<Block> peaArray;
    int peaWidth = tileSize / 2;
    int peaHeight = tileSize / 2;
    int peaVelocityY = -10;

    Timer gameLoop;
    int score = 0;
    boolean gameOver = false;

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
        peaArray = new ArrayList<Block>();

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
            if (zombie.alive && zombie.y > 0) {
                g.drawImage(zombie.img, zombie.x, zombie.y, zombie.width, zombie.height, null);
            }
        }

        // peas
        g.setColor(Color.green);
        for (int i = 0; i < peaArray.size(); i++) {
            Block pea = peaArray.get(i);
            if (!pea.used) {
                g.fillRect(pea.x, pea.y, pea.width, pea.height);
            }
        }

        // score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over:" + String.valueOf(score), 10, 35);
        } else {
            g.drawString("Score:" + String.valueOf(score), 10, 35);
        }
    }

    public boolean detectCollision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void move() {
        // zombies

        Random random = new Random();
        for (int i = 0; i < zombieArray.size(); i++) {
            Block zombie = zombieArray.get(i);
            if (zombie.alive) {
                if (random.nextInt(3) == 0) {
                    zombie.y += zombieVelocity;
                    if (zombie.x - 2 * zombieVelocity > 0)
                        zombie.x -= 2 * zombieVelocity;
                } else if (zombie.x + zombieVelocity + zombie.width < boardWidth) {
                    zombie.x += zombieVelocity;
                }

                zombie.y += zombieVelocity;

                // game over
                if (zombie.y + peaShooter.height >= peaShooter.y) {
                    gameOver = true;
                }
            }
        }

        // peas
        for (int i = 0; i < peaArray.size(); i++) {
            Block pea = peaArray.get(i);
            pea.y += peaVelocityY;

            // pea collisions with zombies
            for (int j = 0; j < zombieArray.size(); j++) {
                Block zombie = zombieArray.get(j);
                if (!pea.used && zombie.alive && detectCollision(pea, zombie)) {
                    pea.used = true;
                    zombie.alive = false;
                    zombieCount--;
                    score += 100;
                }
            }
        }

        // clear peas
        while (peaArray.size() > 0 && (peaArray.get(0).used || peaArray.get(0).y < 0)) {
            peaArray.remove(0);
        }

        // next level
        if (zombieCount == 0) {
            // increase number of zombies in columns and rows by 1
            score += zombieColumns * zombieRows * 100;
            zombieColumns = Math.min(zombieColumns + 1, columns - 6);
            zombieRows = Math.min(zombieRows + 1, rows / 2 - 2);
            zombieArray.clear();
            peaArray.clear();
            zombieVelocity = 1;
            createZombies();
        }

    }

    public void createZombies() {
        Random random = new Random();
        for (int r = 0; r < zombieRows; r++) {
            for (int c = 0; c < zombieColumns; c++) {
                int randomZombieIndex = random.nextInt(zombieImgArray.size() + 1); // + 1 for the next line.
                if (randomZombieIndex < zombieImgArray.size()) {// Adds a random chance to not spawn the zombie at all
                    Block zombie = new Block(
                            zombieX + c * zombieWidth,
                            zombieY - (r + 1) * zombieHeight,
                            zombieWidth,
                            zombieHeight,
                            zombieImgArray.get(randomZombieIndex));
                    zombieArray.add(zombie);
                }
            }
        }
        zombieCount = zombieArray.size();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
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
            peaShooter.x = peaShooterX;
            zombieArray.clear();
            peaArray.clear();
            score = 0;
            zombieVelocity = 1;
            zombieColumns = 3;
            zombieRows = 2;
            gameOver = false;
            createZombies();
            gameLoop.start();
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT && peaShooter.x - peaShooterVelocityX >= 0) {
            peaShooter.x -= peaShooterVelocityX; // Move left one tile
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT
                && peaShooter.x + peaShooter.width + peaShooterVelocityX <= boardWidth) {
            peaShooter.x += peaShooterVelocityX; // Move right one tile
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            Block pea = new Block(peaShooter.x + peaShooterWidth * 15 / 32, peaShooter.y, peaWidth, peaHeight, null);
            peaArray.add(pea);
        }
    }

}
