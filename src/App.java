import javax.swing.*;//for window - 16r&16c, ech tile 32px by 32px , whole window 512px by 512px

public class App {
    public static void main(String[] args) throws Exception {
        // window
        int tileSize = 32;
        int rows = 16;
        int columns = 16;
        int boardWidth = tileSize * columns; // 32*16=512
        int boardHeight = tileSize * rows; // 32*16=512

        JFrame frame = new JFrame("Space Invaders");
        frame.setSize(boardWidth, boardHeight);

        frame.setLocationRelativeTo(null);// center of the screen
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// TO TERMINATE

        SpaceInvaders spaceInvaders = new SpaceInvaders();// Instance of jpanel class
        frame.add(spaceInvaders);
        frame.pack();// pack the frame to fit the preferred size of the panel
        spaceInvaders.requestFocus();
        frame.setVisible(true);

    }
}
