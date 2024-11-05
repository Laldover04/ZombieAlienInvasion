import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int tileSize = 32;
        int rows = 16;
        int columns = 16;
        int boardWidth = tileSize * columns;
        int boardHeight = tileSize * rows;

        JFrame frame = new JFrame("ZombieAlienInvasion");
        frame.setSize(boardWidth, boardHeight);

        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ZombieAlienInvasion game = new ZombieAlienInvasion();
        frame.add(game);
        frame.pack();
        game.requestFocus();
        frame.setVisible(true);

    }
}
