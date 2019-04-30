package amazons;

// NOTICE:
// This file is a SUGGESTED skeleton.  NOTHING here or in any other source
// file is sacred.  If any of it confuses you, throw it out and do it your way.

import java.util.*;

import static amazons.Piece.*;


/**
 * The state of an Amazons Game.
 */
public class Board {

    /**
     * The number of squares on a side of the board.
     */
    public static final int SIZE = 10;
    /**
     * An empty iterator for initialization.
     */
    private static final Iterator<Square> NO_SQUARES =
            Collections.emptyIterator();

    /**
     * Initial state of board, from top to bottom, left to right.
     */
    private static final Piece[][] INIT_STATE = {
            {EMPTY, EMPTY, EMPTY, BLACK, EMPTY, EMPTY, BLACK, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {BLACK, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, BLACK},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {WHITE, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WHITE},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, WHITE, EMPTY, EMPTY, WHITE, EMPTY, EMPTY, EMPTY},

    };

    /**
     * Current state of board.
     */
    private Piece[][] _state = new Piece[SIZE][SIZE];

    /**
     * Piece whose turn it is (BLACK or WHITE).
     */
    private Piece _turn;
    /**
     * Cached value of winner on this board, or EMPTY if it has not been
     * computed.
     */
    private Piece _winner;

    private Deque<Move> _moveStack = new LinkedList<>();

    /**
     * Initializes a game board with SIZE squares on a side in the
     * initial position.
     */
    public Board() {
        init();
    }

    /**
     * Initializes a copy of MODEL.
     */
    public Board(Board model) {
        copy(model);
    }

    /**
     * Initialize a board by the string representation
     */
    public Board(String content) {
        String[] split = content.split("\n");
        for (int i = 0, splitLength = split.length; i < splitLength; i++) {
            String line = split[i];
            int jIndex = 0;
            for (int j = 0, length = line.length(); j < length; j++) {
                char c = line.charAt(j);
                if (c == ' ') {
                    continue;
                }

                Piece p = null;
                if (c == '-') {
                    p = EMPTY;
                }
                if (c == 'S') {
                    p = SPEAR;
                }
                if (c == 'W') {
                    p = WHITE;
                }
                if (c == 'B') {
                    p = BLACK;
                }
                _state[SIZE - i - 1][jIndex++] = p;
            }
        }
        _turn = WHITE;
        _winner = EMPTY;
    }


    /**
     * Copies MODEL into me.
     */
    public void copy(Board model) {
        for (int i = 0; i < SIZE; i++) {
            _state[i] = Arrays.copyOf(model._state[i], SIZE);
        }
        _turn = model._turn;
        _winner = model._winner;
        _moveStack = new LinkedList<>(model._moveStack);
    }

    /**
     * Clears the board to the initial position.
     */
    void init() {
        for (int i = 0; i < SIZE; i++) {
            _state[i] = Arrays.copyOf(INIT_STATE[SIZE - i - 1], SIZE);
        }

        _turn = WHITE;
        _winner = EMPTY;
        _moveStack.clear();
    }

    /**
     * Return the Piece whose move it is (WHITE or BLACK).
     */
    Piece turn() {
        return _turn;
    }

    /**
     * Return the number of moves (that have not been undone) for this
     * board.
     */
    public int numMoves() {
        return _moveStack.size();
    }

    /**
     * Return the winner in the current position, or null if the game is
     * not yet finished.
     */
    public Piece winner() {
        return _winner;
    }

    /**
     * Return the contents the square at S.
     */
    public Piece get(Square s) {
        Objects.requireNonNull(s);

        int col = s.col();
        int row = s.row();

        return get(col, row);
    }

    /**
     * Return the contents of the square at (COL, ROW), where
     * 0 <= COL, ROW < 9.
     */
    Piece get(int col, int row) {
        return _state[row][col];
    }

    /**
     * Return the contents of the square at COL ROW.
     */
    private Piece get(char col, int row) {
        return get(col - 'a', row - 1);
    }

    /**
     * Set square S to P.
     */
    void put(Piece p, Square s) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(s);

        int col = s.col();
        int row = s.row();

        put(p, col, row);
    }

    /**
     * Set square (COL, ROW) to P.
     */
    private void put(Piece p, int col, int row) {
        _state[row][col] = p;
    }

    /**
     * Set square COL ROW to P.
     */
    public void put(Piece p, char col, int row) {
        put(p, col - 'a', row - 1);
    }

    public Piece getTurn() {
        return _turn;
    }

    /**
     * Return true iff FROM - TO is an unblocked queen move on the current
     * board, ignoring the contents of ASEMPTY, if it is encountered.
     * For this to be true, FROM-TO must be a queen move and the
     * squares along it, other than FROM and ASEMPTY, must be
     * empty. ASEMPTY may be null, in which case it has no effect.
     */
    public boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if (!(get(to) == EMPTY) && to != asEmpty) {
            return false;
        }

        if (!from.isQueenMove(to)) {
            return false;
        }

        int direction = from.direction(to);

        for (int i = 1; i < SIZE; i++) {
            Square currentPos = from.queenMove(direction, i);
            if (currentPos == to) {
                return true;
            }

            if (get(currentPos) != EMPTY && currentPos != asEmpty) {
                return false;
            }
        }

        throw new RuntimeException("Shall not reach here");
    }

    /**
     * Return true iff FROM is a valid starting square for a move.
     */
    private boolean isLegal(Square from) {
        Piece curState = get(from);
        if (curState == EMPTY || curState == SPEAR) {
            return false;
        }

        // Once find a move, it's a legal starting square. (from_to(from))
        for (int i = 0; i < 8; i++) {
            Square next = from.queenMove(i, 1);
            if (next == null) {
                continue;
            }
            Piece nextPiece = get(next);
            if (nextPiece == EMPTY) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return true iff FROM-TO is a valid first part of move, ignoring
     * spear throwing.
     */
    public boolean isLegal(Square from, Square to) {
        return isUnblockedMove(from, to, null);
    }

    /**
     * Return true iff FROM-TO(SPEAR) is a legal move in the current
     * position.
     */
    boolean isLegal(Square from, Square to, Square spear) {
        if (get(from) != _turn) {
            return false;
        }

        return isUnblockedMove(from, to, null)
                && isUnblockedMove(to, spear, from);
    }

    /**
     * Return true iff MOVE is a legal move in the current
     * position.
     */
    public boolean isLegal(Move move) {
        return isLegal(move.from(), move.to(), move.spear());
    }

    /**
     * Move FROM-TO(SPEAR), assuming this is a legal move.
     */
    public void makeMove(Square from, Square to, Square spear) {
        makeMove(Move.mv(from, to, spear));
    }

    /**
     * Move according to MOVE, assuming it is a legal move.
     */
    public void makeMove(Move move) {
        assert isLegal(move);

        assert get(move.from()) == _turn;

        put(EMPTY, move.from());
        put(_turn, move.to());
        put(SPEAR, move.spear());

        _turn = _turn.opponent();
        _moveStack.push(move);

        updateWinner();
    }

    private void updateWinner() {
        int valid = 0;

        for (int i = 0; i < SIZE * SIZE; i++) {
            if (isLegal(Square.sq(i))) {
                if (get(Square.sq(i)) == _turn) {
                    valid++;
                }
            }
        }

        if (valid == 0) {
            _winner = _turn.opponent();
        } else {
            _winner = EMPTY;
        }
    }

    /**
     * Undo one move.  Has no effect on the initial board.
     */
    public void undo() {
        if (numMoves() <= 0 || _moveStack.size() <= 0) {
            return;
        }

        Move lastMove = _moveStack.poll();

        _turn = _turn.opponent();
        put(EMPTY, lastMove.spear());
        put(EMPTY, lastMove.to());
        put(_turn, lastMove.from());

        updateWinner();
    }

    /**
     * Return an Iterator over the Squares that are reachable by an
     * unblocked queen move from FROM. Does not pay attention to what
     * piece (if any) is on FROM, nor to whether the game is finished.
     * Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     * feature is useful when looking for Moves, because after moving a
     * piece, one wants to treat the Square it came from as empty for
     * purposes of spear throwing.)
     */
    public Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /**
     * Return an Iterator over all legal moves on the current board.
     */
    public Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /**
     * Return an Iterator over all legal moves on the current board for
     * SIDE (regardless of whose turn it is).
     */
    public Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    public int countChess(Piece p) {
        Iterator<Square> iterator = Square.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            Square square = iterator.next();
            if (get(square) == p) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 10; i >= 1; i--) {
            sb.append("   ");
            boolean first = true;
            for (char j = 'a'; j <= 'j'; j++) {
                if (!first) {
                    sb.append(" ");
                }
                first = false;

                sb.append(get(j, i));
            }
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    /**
     * An iterator used by reachableFrom.
     */
    private class ReachableFromIterator implements Iterator<Square> {

        /**
         * Starting square.
         */
        private final Square _from;
        /**
         * Current direction.
         */
        private int _dir;
        /**
         * Current distance.
         */
        private int _steps;
        /**
         * Square treated as empty.
         */
        private final Square _asEmpty;

        private Square _next;

        /**
         * Iterator of all squares reachable by queen move from FROM,
         * treating ASEMPTY as empty.
         */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = 0;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            Square next = _from.queenMove(_dir, _steps);
            toNext();
            return next;
        }

        /**
         * Advance _dir and _steps, so that the next valid Square is
         * _steps steps in direction _dir from _from.
         */
        private void toNext() {
            if (!hasNext()) {
                return;
            }

            _steps += 1;
            if (_dir < 0 || _dir >= 8 || _steps < 0 || _steps >= 10) {
                _dir += 1;
                _steps = 0;
                toNext();
                return;
            }

            _next = _from.queenMove(_dir, _steps);
            if (_next == null || !isUnblockedMove(_from, _next, _asEmpty)) {
                _dir += 1;
                _steps = 0;
                toNext();
            }
        }
    }

    /**
     * An iterator used by legalMoves.
     */
    private class LegalMoveIterator implements Iterator<Move> {

        /**
         * Color of side whose moves we are iterating.
         */
        private final Piece _fromPiece;
        /**
         * Current starting square.
         */
        private Square _start;
        /**
         * Remaining starting squares to consider.
         */
        private final Iterator<Square> _startingSquares;
        /**
         * Current piece's new position.
         */
        private Square _nextSquare;
        /**
         * Remaining moves from _start to consider.
         */
        private Iterator<Square> _pieceMoves;
        /**
         * Remaining spear throws from _piece to consider.
         */
        private Iterator<Square> _spearThrows;

        /**
         * All legal moves for SIDE (WHITE or BLACK).
         */
        LegalMoveIterator(Piece side) {
            _startingSquares = Square.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _spearThrows.hasNext();
        }

        @Override
        public Move next() {
            Move mv = Move.mv(_start, _nextSquare, _spearThrows.next());
            toNext();
            return mv;
        }

        /**
         * Advance so that the next valid Move is
         * _start-_nextSquare(sp), where sp is the next value of
         * _spearThrows.
         */
        private void toNext() {
            if (hasNext()) {
                return;
            }

            if (_pieceMoves.hasNext()) {
                _nextSquare = _pieceMoves.next();
                _spearThrows = reachableFrom(_nextSquare, _start);
                return;
            }

            while (_startingSquares.hasNext()) {
                _start = _startingSquares.next();
                if (get(_start) == _fromPiece && isLegal(_start)) {
                    _pieceMoves = reachableFrom(_start, null);
                    // Since it's a legal starting square, toNext() won't fail
                    toNext();
                    return;
                }
            }

            // END ITERATING
            _spearThrows = NO_SQUARES;
        }
    }

    public List<Move> lastMoves(int count) {
        List<Move> moves = new ArrayList<>(count);
        for (Move move : _moveStack) {
            if (count <= 0) {
                break;
            }

            moves.add(move);
            count--;
        }
        return moves;
    }
}
