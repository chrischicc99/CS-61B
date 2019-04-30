package amazons;

import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BoardTest {

    private static final String STATE_STRING =
            "   - - - B - - B - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   B - - - - - - - - B\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   W - - - - - - - - W\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - W - - W - - -\n";

    private static final String STATE_STRING2 =
            "   - - - B - - B - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   B - - - - - - - - B\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   W - - - - - - - - W\n" +
                    "   - S - - - - - - W -\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - W - - - - - -\n";

    private static final String STATE_STRING3 =
            "   - - - - - - B - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   B - - - - - - - - B\n" +
                    "   - - - B - - - S - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   W - - - - - - - - W\n" +
                    "   - S - - - - - - W -\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - W - - - - - -\n";

    @Test
    public void testGame() {
        Board board = new Board();

        assertEquals(STATE_STRING, board.toString());

        board.makeMove(Move.mv("g1 i3 b3"));

        assertEquals(STATE_STRING2, board.toString());

        board.makeMove(Move.mv("d10 d6 h6"));

        assertEquals(STATE_STRING3, board.toString());
    }

    @Test
    public void testIsUnblockedMove() {
        Board board = new Board();

        assertTrue(board.isUnblockedMove(Square.sq("d1"), Square.sq("j1"), Square.sq("g1")));
    }

    @Test
    public void testReachableIterator() {
        Board board = new Board();

        assertEquals(20, reachCount(board, Square.sq("d1")));
        assertEquals(20, reachCount(board, Square.sq("a4")));
        assertEquals(20, reachCount(board, Square.sq("a7")));
        assertEquals(20, reachCount(board, Square.sq("d10")));

        board.makeMove(Move.mv("j4 e4 j4"));

        assertEquals(22, reachCount(board, Square.sq("d4")));
    }

    @Test
    public void testAsEmptyReachableIterator() {
        Board board = new Board();

        assertEquals(24, reachCount(board, Square.sq("d1"), Square.sq("g1")));
        assertEquals(20, reachCount(board, Square.sq("a4")));
        assertEquals(20, reachCount(board, Square.sq("a7")));
        assertEquals(20, reachCount(board, Square.sq("d10")));

        board.makeMove(Move.mv("j4 e4 j4"));

        assertEquals(22, reachCount(board, Square.sq("d4")));
    }


    private int reachCount(Board board, Square square) {
        return reachCount(board, square, null);
    }

    private int reachCount(Board board, Square square, Square asEmpty) {
        Iterator<Square> moveIterator = board.reachableFrom(square, asEmpty);

        int count = 0;
        while (moveIterator.hasNext()) {
            moveIterator.next();
            count++;
        }
        return count;
    }


    @Test
    public void testLegalIterator() {
        Board board = new Board();

        assertEquals(manualLegalCount(board, Piece.WHITE), legalCount(board, Piece.WHITE));

        assertEquals(manualLegalCount(board, Piece.BLACK), legalCount(board, Piece.BLACK));
    }

    private Set<Move> manualLegalCount(Board board, Piece piece) {
        Set<Move> sets = new HashSet<>();
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                Square start = Square.sq(i, j);
                if (board.get(start) != piece) {
                    continue;
                }

                for (int x = 0; x < Board.SIZE; x++) {
                    for (int y = 0; y < Board.SIZE; y++) {
                        Square to = Square.sq(x, y);
                        if (!board.isLegal(start, to)) {
                            continue;
                        }
                        for (int a = 0; a < Board.SIZE; a++) {
                            for (int b = 0; b < Board.SIZE; b++) {
                                Square spear = Square.sq(a, b);
                                Move mv = Move.mv(start, to, spear);
                                if (board.isUnblockedMove(start, to, null)
                                        && board.isUnblockedMove(to, spear, start)) {
                                    sets.add(mv);
                                }
                            }
                        }
                    }
                }
            }
        }
        return sets;
    }

    private Set<Move> legalCount(Board board, Piece piece) {
        Iterator<Move> moveIterator = board.legalMoves(piece);

        Set<Move> sets = new HashSet<>();
        while (moveIterator.hasNext()) {
            Move move = moveIterator.next();
            sets.add(move);
        }
        return sets;
    }


    @Test
    public void test1() {
        String content =
                "   S S S S S S S - - -\n" +
                        "   S S S S - S S - - -\n" +
                        "   S S S S S S S - - -\n" +
                        "   S - S S S S W - - S\n" +
                        "   S S S S S S - - - -\n" +
                        "   S W S S S S - S S S\n" +
                        "   S S S S S S S S S S\n" +
                        "   S S S S - S S S B S\n" +
                        "   S S B S S S S S S S\n" +
                        "   B S S W B S W S S -\n";
        Board b = new Board(content);

        assertEquals(manualLegalCount(b, Piece.WHITE), legalCount(b, Piece.WHITE));
    }

    @Test
    public void test2() {
        String content =
                "   S S S S S S S - - -\n" +
                        "   S S S S - S S - - -\n" +
                        "   S S S S S S S - - -\n" +
                        "   S - S S S S W - - S\n" +
                        "   S S S S S S - - - -\n" +
                        "   S W S S S S - S S S\n" +
                        "   S S S S S S S S S S\n" +
                        "   S S S S - S S S B S\n" +
                        "   S S B S S S S S S S\n" +
                        "   B S S W B S W S S -\n";

        Board b = new Board(content);

        b.makeMove(Move.mv("g7-h8(h9)"));


        assertEquals(Piece.WHITE, b.winner());
    }

    @Test
    public void test3() {
        Board board = new Board();

        Iterator<Move> moves = board.legalMoves();
        boolean found = false;

        Move target = Move.mv("a4-b5(b7)");
        while (moves.hasNext()) {
            Move next = moves.next();
            if (next == target) {
                found = true;
                break;
            }
        }

        assertTrue(found);
    }
}