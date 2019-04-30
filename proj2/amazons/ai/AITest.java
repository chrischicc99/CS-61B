package amazons.ai;

import amazons.Board;
import amazons.Move;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertTrue;

public class AITest {

    @Test
    @Ignore
    public void staticScore() {
        Board board = new Board();

        System.out.println(board);

        Iterator<Move> moves = board.legalMoves();
        System.out.println(AI.staticScore(board));

        Move chosen = null;
        int score = Integer.MIN_VALUE;
        while (moves.hasNext()) {
            Move next = moves.next();
            board.makeMove(next);
            int c = AI.staticScore(board);
            if (c > score) {
                chosen = next;
                score = c;
                System.out.println(score);

            }
            board.undo();
        }

        System.out.println(chosen);
        board.makeMove(chosen);
        System.out.println(board);
    }

    @Test
    public void test1() {
        String content =
                "   - - - B B - - - - -\n" +
                        "   - - - S - - - - - -\n" +
                        "   - - - - - - - - - -\n" +
                        "   B - - - - - - - - B\n" +
                        "   - - - - - - - - - -\n" +
                        "   - - - - - - - - - -\n" +
                        "   W - - W - - - - - W\n" +
                        "   - - - - - - - - - -\n" +
                        "   - - - - - - - - - -\n" +
                        "   - - - - S - W - - -\n";
        Board b = new Board(content);

        int aScore = AI.staticScore(b);

        content =
                "   - - - B - - - - - -\n" +
                        "   - - - S - - - - - -\n" +
                        "   - - - - - - - - - -\n" +
                        "   B - - - - - B - - B\n" +
                        "   - - - - - - - - - -\n" +
                        "   - - - - S - - - - -\n" +
                        "   W - - W - - - - - W\n" +
                        "   - - - - - - - - - -\n" +
                        "   - - - - - - - - - -\n" +
                        "   - - - - - - W - - -\n";
        b = new Board(content);

        int bScore = AI.staticScore(b);

        assertTrue(bScore < aScore);
    }

    @Test
    @Ignore
    public void test3() {
        Board board = new Board();

        board.makeMove(Move.mv("d1 d4 d9"));

        Iterator<Move> moves = board.legalMoves();
        System.out.println(AI.staticScore(board));

        Move chosen = null;
        int score = Integer.MAX_VALUE;
        while (moves.hasNext()) {
            Move next = moves.next();
            board.makeMove(next);
            int c = AI.staticScore(board);
            if (c < score) {
                chosen = next;
                score = c;
                System.out.println(score);

            }
            board.undo();
        }

        System.out.println(chosen);
        board.makeMove(chosen);
        System.out.println(board);
    }
}