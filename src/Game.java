public class Game {
    private final Board board;
    private int flags = 0;
    private final Cell[][] visibleBoard;
    private final Cell[][] hiddenBoard;

    public Game(int cols, int rows, int bombsAmount) {
        this.board = new Board(cols, rows, bombsAmount);
        this.visibleBoard = board.getVisibleBoardState();
        this.hiddenBoard = board.getHiddenBoardState();
    }

    public Cell[][] getVisibleBoard() { return this.visibleBoard; }

    public void onLeftButtonPressed(Coord coord) {
        if (flagged(coord)) return;
        if (!closed(coord) ) {
            revealAround(coord);
        } else {
            Cell cell = hiddenBoard[coord.getX()][coord.getY()];
            if (cell == Cell.BOMB) {
                hiddenBoard[coord.getX()][coord.getY()] = Cell.BOMBED;
                endGame();
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

    //ривил если бомб нет
    private void revealAround(Coord coord) {
        int x = coord.getX();
        int y = coord.getY();
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

    private void reveal(Coord coord) {
        int x = coord.getX();
        int y = coord.getY();
        if (!board.inRange(coord)) return;
        if (!closed(coord)) return;
        visibleBoard[x][y] = hiddenBoard[x][y];
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

    private void endGame() {
        //
    }
}
