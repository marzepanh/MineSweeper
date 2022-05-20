import java.util.Random;

public class Game {
    private final Board board;
    private int flags = 0;
    private final Cell[][] visibleBoard;
    private final Cell[][] hiddenBoard;
    Random r = new Random();
    private int closedCellsAmount;
    private GameState state;

    public Game(int cols, int rows, int bombsAmount) {
        this.board = new Board(cols, rows, bombsAmount);
        this.visibleBoard = board.getVisibleBoardState();
        this.hiddenBoard = board.getHiddenBoardState();
        this.closedCellsAmount = cols * rows;
        this.state = GameState.PLAYING;
        initFirstMove();
    }

    public Cell[][] getVisibleBoard() { return this.visibleBoard; }

    private void initFirstMove() {
        Coord size = board.getSize();
        int x = r.nextInt(size.getX());
        int y = r.nextInt(size.getY());
        while (hiddenBoard[x][y] != Cell.NUM0) {
            x = r.nextInt(size.getX());
            y = r.nextInt(size.getY());
        }
        reveal(x, y);
    }

    public void onLeftButtonPressed(Coord coord) {
        if (flagged(coord)) return;
        if (!closed(coord)) {
            revealAround(coord);
        } else {
            Cell cell = hiddenBoard[coord.getX()][coord.getY()];
            if (cell == Cell.BOMB) {
                loseGame(coord);
            } else {
                reveal(coord);
            }
        }
    }

    public void onRightButtonPressed(Coord coord) {
        switch (visibleBoard[coord.getX()][coord.getY()]) {
            case CLOSED -> {
                visibleBoard[coord.getX()][coord.getY()] = Cell.FLAGGED;
                flags++;
            }
            case FLAGGED -> {
                visibleBoard[coord.getX()][coord.getY()] = Cell.CLOSED;
                flags--;
            }
        }
    }

    //reveal if we press on num

    private void revealAround(Coord coord) {
        int x = coord.getX();
        int y = coord.getY();
        System.out.println("Bombs around: " + board.around(x, y, Cell.BOMB, hiddenBoard));
        System.out.println("Flags amount: " + board.around(x, y, Cell.FLAGGED, visibleBoard));
        if (board.around(x, y, Cell.BOMB, hiddenBoard) == board.around(x, y, Cell.FLAGGED, visibleBoard)) {
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (!board.inRange(new Coord(i, j))) continue;
                    if (visibleBoard[i][j] != Cell.CLOSED) continue;
                    onLeftButtonPressed(new Coord(i, j));
                }
            }
        }
    }

    public void reveal(Coord coord) {
        int x = coord.getX();
        int y = coord.getY();
        if (!board.inRange(coord)) return;
        if (!closed(coord)) return;
        visibleBoard[x][y] = hiddenBoard[x][y];
        closedCellsAmount--;
        checkWinner();

        if (board.around(x, y, Cell.BOMB, hiddenBoard) != 0) return;
        reveal(x-1,y-1);
        reveal(x-1,y+1);
        reveal(x+1,y-1);
        reveal(x+1,y+1);
        reveal(x-1,y);
        reveal(x+1,y);
        reveal(x,y-1);
        reveal(x,y+1);
    }

    private void reveal(int x, int y) {
        reveal(new Coord(x, y));
    }

    private boolean closed(Coord coord) {
        return visibleBoard[coord.getX()][coord.getY()] == Cell.CLOSED;
    }

    private boolean flagged(Coord coord) {
        return visibleBoard[coord.getX()][coord.getY()] == Cell.FLAGGED;
    }

    private void loseGame(Coord coord) {
        Coord size = board.getSize();
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                if (hiddenBoard[x][y] == Cell.BOMB && visibleBoard[x][y] != Cell.FLAGGED) {
                    visibleBoard[x][y] = Cell.BOMB;
                }
                if (visibleBoard[x][y] == Cell.FLAGGED && hiddenBoard[x][y] != Cell.BOMB) {
                    visibleBoard[x][y] = Cell.NOBOMB;
                }
            }
        }
        visibleBoard[coord.getX()][coord.getY()] = Cell.BOMBED;
        state = GameState.LOSE;
    }

    private void checkWinner() {
        if (state == GameState.PLAYING) {
            if (closedCellsAmount == board.getBombsAmount()) {
                state = GameState.WIN;
            }
        }
    }

    public GameState getState() {
        return state;
    }
}
