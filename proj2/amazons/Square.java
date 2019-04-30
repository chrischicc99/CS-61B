package amazons;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a position on an Amazons board.  Positions are numbered
 * from 0 (lower-left corner) to 99 (upper-right corner).  Squares
 * are immutable and unique: there is precisely one square created for
 * each distinct position.  Clients create squares using the factory method
 * sq, not the constructor.  Because there is a unique Square object for each
 * position, you can freely use the cheap == operator (rather than the
 * .equals method) to compare Squares, and the program does not waste time
 * creating the same square over and over again.
 */
public final class Square {

    /**
     * The regular expression for a square designation (e.g.,
     * a3). For convenience, it is in parentheses to make it a
     * group.  This subpattern is intended to be incorporated into
     * other pattern that contain square designations (such as
     * patterns for moves).
     */
    public static final String SQ = "([a-j](?:[1-9]|10))";
    /**
     * Definitions of direction for queenMove.  DIR[k] = (dcol, drow)
     * means that to going one step from (col, row) in direction k,
     * brings us to (col + dcol, row + drow).
     */
    private static final int[][] DIR = {
            {0, 1}, {1, 1}, {1, 0}, {1, -1},
            {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}
    };
    /**
     * The cache of all created squares, by index.
     */
    private static final Square[] SQUARES =
            new Square[Board.SIZE * Board.SIZE];
    /**
     * SQUARES viewed as a List.
     */
    private static final List<Square> SQUARE_LIST = Arrays.asList(SQUARES);

    static {
        for (int i = Board.SIZE * Board.SIZE - 1; i >= 0; i -= 1) {
            SQUARES[i] = new Square(i);
        }
    }

    /**
     * My index position.
     */
    private final int _index;
    /**
     * My row and column (redundant, since these are determined by _index).
     */
    private final int _row, _col;
    /**
     * My String denotation.
     */
    private final String _str;

    /**
     * Return the Square with index INDEX.
     */
    private Square(int index) {
        _index = index;
        _row = index / 10;
        _col = index % 10;
        _str = String.format("%c%d", col() + 'a', row() + 1);
    }

    /**
     * Return true iff COL ROW is a legal square.
     */
    static boolean exists(int col, int row) {
        return row >= 0 && col >= 0 && row < Board.SIZE && col < Board.SIZE;
    }

    /**
     * Return the (unique) Square denoting COL ROW.
     */
    public static Square sq(int col, int row) {
        if (!exists(row, col)) {
            return null;
        }
        return sq(row * 10 + col);
    }

    /**
     * Return the (unique) Square denoting the position with index INDEX.
     */
    public static Square sq(int index) {
        if (index < 0 || index >= Board.SIZE * Board.SIZE) {
            return null;
        }
        return SQUARES[index];
    }

    /**
     * Return the (unique) Square denoting the position COL ROW, where
     * COL ROW is the standard cli format for a square (e.g., a4).
     */
    public static Square sq(String col, String row) {
        return sq(col.charAt(0) - 'a', Integer.parseInt(row) - 1);
    }

    /**
     * Return the (unique) Square denoting the position in POSN, in the
     * standard cli format for a square (e.g. a4). POSN must be a
     * valid square designation.
     */
    public static Square sq(String posn) {
        assert posn.matches(SQ);
        return sq(posn.charAt(0) - 'a', Integer.parseInt(posn.substring(1)) - 1);
    }

    /**
     * Return an iterator over all Squares.
     */
    public static Iterator<Square> iterator() {
        return SQUARE_LIST.iterator();
    }

    /**
     * Return my row position, where 0 is the bottom row.
     */
    public int row() {
        return _row;
    }

    /**
     * Return my column position, where 0 is the leftmost column.
     */
    public int col() {
        return _col;
    }

    /**
     * Return my index position (0-99).  0 represents square a1, and 99
     * is square j10.
     */
    public int index() {
        return _index;
    }

    /**
     * Return true iff THIS - TO is a valid queen move.
     */
    boolean isQueenMove(Square to) {
        if (this == to) {
            return false;
        }

        if (to.row() == this.row()) {
            return true;
        }
        if (to.col() == this.col()) {
            return true;
        }
        return to.col() + to.row() == col() + row() || to.col() - to.row() == col() - row();

    }

    /**
     * Return the Square that is STEPS>0 squares away from me in direction
     * DIR, or null if there is no such square.
     * DIR = 0 for north, 1 for northeast, 2 for east, etc., up to 7 for west.
     * If DIR has another value, return null. Thus, unless the result
     * is null the resulting square is a queen move away rom me.
     */
    public Square queenMove(int dir, int steps) {
        assert dir >= 0 && dir < 8;
        assert steps >= 0 && steps < 10;
        int[] direction = DIR[dir];
        return sq(col() + direction[0] * steps, row() + direction[1] * steps);
    }

    /**
     * Return the direction (an int as defined in the documentation
     * for queenMove) of the queen move THIS-TO.
     */
    public int direction(Square to) {
        assert isQueenMove(to);

        int rowDiff = to.row() - row();
        int colDiff = to.col() - col();

        int gcd = Math.abs(Utils.GCD(rowDiff, colDiff));

        for (int i = 0, dirLength = DIR.length; i < dirLength; i++) {
            int[] ints = DIR[i];
            if (ints[0] == colDiff / gcd && ints[1] == rowDiff / gcd) {
                return i;
            }
        }

        return -1;
    }

    int distance(Square to, int direction) {
        for (int i = 0; i < Board.SIZE; i++) {
            if (this.queenMove(direction, i) == to) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return _str;
    }

}
