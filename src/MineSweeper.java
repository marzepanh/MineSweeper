import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class MineSweeper extends JFrame {

    private JPanel panel;
    private final int cols;
    private final int rows;
    private final int IMAGE_SIZE = 50;
    private final Game game;

    public static void main(String[] args) {
        new MineSweeper(10, 10);
    }

    private MineSweeper(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        this.game = new Game(cols, rows, 20);
        initPanel();
        initFrame();
        initImages();
    }

    private void initFrame() {
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Minesweeper");
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void initPanel() {

        panel = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (int x = 0; x < cols; x++) {
                    for (int y = 0; y < rows; y++) {
                        g.drawImage(game.getVisibleBoard()[x][y].image, IMAGE_SIZE * x, IMAGE_SIZE * y, this);
                    }
                }
            }
        };
        panel.setPreferredSize(new Dimension(cols * IMAGE_SIZE, rows * IMAGE_SIZE));
        add(panel);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Coord coord = new Coord(e.getX() / IMAGE_SIZE, e.getY() / IMAGE_SIZE);
                if (e.getButton() == MouseEvent.BUTTON1)
                    game.onLeftButtonPressed(coord);
                if (e.getButton() == MouseEvent.BUTTON3)
                    game.onRightButtonPressed(coord);
                panel.repaint();
            }
        });
    }

    private void initImages() {
        for (Cell cell: Cell.values()) {
            cell.image = getImage(cell.name());
        }
    }

    private Image getImage(String filename) {
        String imagePath = "res/images/" + filename.toLowerCase() + ".png";
        try {
            return ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
