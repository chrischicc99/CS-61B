package amazons.ai;

// NOTICE:
// This file is a SUGGESTED skeleton.  NOTHING here or in any other source
// file is sacred.  If any of it confuses you, throw it out and do it your way.

import amazons.*;

import java.util.Iterator;

/**
 * A Player that automatically generates moves.
 */
public class AI extends Player {

    /**
     * A position magnitude indicating a win (for white if positive, black
     * if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFINITY = Integer.MAX_VALUE;
    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;

    /**
     * A new AI with no piece or controller (intended to produce
     * a template).
     */
    public AI() {
        this(null, null);
    }

    /**
     * A new AI playing PIECE under control of CONTROLLER.
     */
    private AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    public Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    public String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = board();
        if (_myPiece == Piece.WHITE) {
            // MAX NODE
            findMove(b, maxDepth(b), true, 1, -INFINITY, INFINITY);
        } else {
            // MIN NODE
            findMove(b, maxDepth(b), true, -1, -INFINITY, INFINITY);
        }
        return _lastFoundMove;
    }

    private static final int[] BOUNDS = new int[]{
            20, 65, 72, 76, 80, 81, 82, 83, 84, 85, 100
    };

    /**
     * Return a heuristic value for BOARD.
     */
    public static int staticScore(Board board) {
        Piece winner = board.winner();
        if (winner == Piece.WHITE) {
            return WINNING_VALUE;
        } else if (winner == Piece.BLACK) {
            return -WINNING_VALUE;
        }
        int score = 0;

        Iterator<Square> iterator = Square.iterator();
        while (iterator.hasNext()) {
            Square currentSquare = iterator.next();
            Piece piece = board.get(currentSquare);

            if (piece == Piece.EMPTY || piece == Piece.SPEAR) {
                continue;
            }

            if (piece == Piece.WHITE) {
                // me
                int count = surroundBy(board, currentSquare);
                if (count == 8) {
                    // it cannot move
                    score -= pow(5, count);
                } else if (count == 7) {
                    // only one way out, urgent here
                    score -= pow(4, count);
                } else {
                    score -= pow(3, count);
                }
            } else {
                // opponent
                int count = surroundBy(board, currentSquare);
                if (count == 8) {
                    // it cannot move
                    score += pow(5, count);
                } else if (count == 7) {
                    score += pow(4, count);
                } else {
                    score += pow(2, count);
                }
            }

        }

        return score;
    }

    private static int pow(int base, int exp) {
        int res = 1;
        for (int i = 0; i < exp; i++) {
            res *= base;
        }
        return res;
    }

    private static int surroundBy(Board board, Square currentSquare) {
        int surroundBy = 0;
        for (int i = 0; i < 8; i++) {
            Square surroundSquare = currentSquare.queenMove(i, 1);
            if (surroundSquare == null) {
                surroundBy += 1;
                continue;
            }
            Piece surroundPiece = board.get(surroundSquare);

            if (surroundPiece != Piece.EMPTY) {
                surroundBy += 1;
            }
        }
        return surroundBy;
    }

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     */
    private int findMove(Board board, int depth, boolean saveMove, int sense, int alpha, int beta) {
        if (depth == 0 || board.winner() == Piece.WHITE || board.winner() == Piece.BLACK) {
            return staticScore(board);
        }
        if (sense == 1) {
            // MAX NODE
            int value = -INFINITY;
            Iterator<Move> moveIterator = board.legalMoves(_myPiece);
            while (moveIterator.hasNext()) {
                Move move = moveIterator.next();
                board.makeMove(move);
                _myPiece = _myPiece.opponent();
                int currentMoveValue = findMove(board, depth - 1, false, -sense, alpha, beta);
                _myPiece = _myPiece.opponent();
                board.undo();
                if (currentMoveValue > value) {
                    value = currentMoveValue;
                    if (saveMove) {
                        _lastFoundMove = move;
                    }
                }
                alpha = Math.max(alpha, value);
                if (alpha >= beta) {
                    break;
                }
            }
            return value;
        } else {
            int value = INFINITY;
            Iterator<Move> moveIterator = board.legalMoves(_myPiece);
            while (moveIterator.hasNext()) {
                Move move = moveIterator.next();
                board.makeMove(move);
                _myPiece = _myPiece.opponent();
                int currentMoveValue = findMove(board, depth - 1, false, -sense, alpha, beta);
                _myPiece = _myPiece.opponent();
                board.undo();
                if (currentMoveValue < value) {
                    value = currentMoveValue;
                    if (saveMove) {
                        _lastFoundMove = move;
                    }
                }
                alpha = Math.min(alpha, value);
                if (alpha >= beta) {
                    break;
                }
            }
            return value;
        }
    }

    /**
     * Return a heuristically determined maximum search depth
     * based on characteristics of BOARD.
     */
    private int maxDepth(Board board) {
        int N = board.numMoves();

        int cnt = 1;
        for (int bound : BOUNDS) {
            if (N > bound) {
                cnt++;
            }
        }

        return cnt;
    }


}
