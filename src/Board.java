import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public class Board {
    private final Coord size;
    private final int bombsAmount;
    private int flagsAmount;
    private Cell [] [] visibleBoardState;
    private Cell [] [] hiddenBoardState;
    private final List<Coord> bombs = new ArrayList<>();

    public Board(int cols, int rows,  int bombsAmount) {
        this.size = new Coord(cols, rows);
        this.bombsAmount = (bombsAmount > cols * rows / 2) ? bombsAmount / 2 : bombsAmount;
        initHiddenBoard();
        initVisibleBoard();
    }

    public Cell[][] getHiddenBoardState() {
        return hiddenBoardState;
    }

    public Cell[][] getVisibleBoardState() {
        return visibleBoardState;
    }

    private void initHiddenBoard() {
        initBombs();
        hiddenBoardState = new Cell[size.getX()][size.getY()];
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                hiddenBoardState[x][y] = (bombs.contains(new Coord(x, y))) ? Cell.BOMB : Cell.NUM0;
            }
        }

        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                if (!hiddenBoardState[x][y].equals(Cell.BOMB)) {
                    hiddenBoardState[x][y] = cellByInt(around(x, y, Cell.BOMB, hiddenBoardState));
                }
            }
        }
    }

    private void initVisibleBoard() {
        visibleBoardState = new Cell[size.getX()][size.getY()];
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                visibleBoardState[x][y] = Cell.CLOSED;
            }
        }
    }
    //bombs and flags around
    public int around(int x, int y, Cell cell, Cell[][] board) {
        int k = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (!inRange(new Coord(i, j))) continue;
                if (board[i][j].equals(cell))
                    k++;
            }
        }
        return k;
    }

    private Cell cellByInt(int k) {
        for (Cell cell : Cell.values()) {
            if (cell.ordinal() == k) return cell;
        }
        throw new NoSuchElementException("Cell not found");
    }

    public boolean inRange(Coord coord) {
        int x = coord.getX();
        int y = coord.getY();
        return x >= 0 && x < size.getX() && y >= 0 && y < size.getY();
    }

    private void initBombs () {
        Random r = new Random();
        for (int i = 0; i < bombsAmount; i++) {
            Coord bomb = new Coord(r.nextInt(size.getX()), r.nextInt(size.getY()));
            while (bombs.contains(bomb)) {
                bomb = new Coord(r.nextInt(size.getX()), r.nextInt(size.getY()));
            }
            bombs.add(bomb);
        }
    }
}
