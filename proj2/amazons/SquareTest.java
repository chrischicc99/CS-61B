package amazons;

import org.junit.Test;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

public class SquareTest {

    @Test
    public void exists() {
        assertTrue(Square.exists(0, 0));
        assertTrue(Square.exists(9, 9));
        assertFalse(Square.exists(10, 10));
        assertFalse(Square.exists(-1, 0));
    }

    @Test
    public void sq() {
        assertSame(Square.sq(0), Square.sq(0));
        assertSame(Square.sq(99), Square.sq(99));
        assertEquals(0, Square.sq(0).col());
        assertEquals(0, Square.sq(0).row());
        assertEquals(9, Square.sq(99).col());
        assertEquals(9, Square.sq(99).row());
    }

    @Test
    public void sq1() {
        assertSame(Square.sq("a", "1"), Square.sq("a", "1"));
        assertSame(Square.sq("j", "10"), Square.sq("j", "10"));
        assertEquals(0, Square.sq("a", "1").col());
        assertEquals(0, Square.sq("a", "1").row());
        assertEquals(9, Square.sq("j", "10").col());
        assertEquals(9, Square.sq("j", "10").row());
    }

    @Test
    public void sq2() {
        assertSame(Square.sq("a1"), Square.sq("a1"));
        assertSame(Square.sq("j10"), Square.sq("j10"));
        assertEquals(0, Square.sq("a1").col());
        assertEquals(0, Square.sq("a1").row());
        assertEquals(9, Square.sq("j10").col());
        assertEquals(9, Square.sq("j10").row());
    }

    @Test
    public void sq3() {
        Square a1 = Square.sq("a1");

        Square next = a1.queenMove(7, 1);

        assertNull(next);
    }

    @Test
    public void iterator() {
        Iterator<Square> iterator = Square.iterator();

        long count = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                .count();

        assertEquals(Board.SIZE * Board.SIZE, count);
    }

    @Test
    public void isQueenMove() {
        assertTrue(Square.sq("a1").isQueenMove(Square.sq("a5")));
        assertTrue(Square.sq("a1").isQueenMove(Square.sq("j1")));
        assertTrue(Square.sq("a1").isQueenMove(Square.sq("c3")));
        assertTrue(Square.sq("c5").isQueenMove(Square.sq("e7")));
        assertTrue(Square.sq("c9").isQueenMove(Square.sq("g5")));

        assertFalse(Square.sq("a1").isQueenMove(Square.sq("a1")));
        assertFalse(Square.sq("a1").isQueenMove(Square.sq("b4")));
        assertFalse(Square.sq("a4").isQueenMove(Square.sq("c1")));
        assertFalse(Square.sq("a1").isQueenMove(Square.sq("g3")));
        assertFalse(Square.sq("c4").isQueenMove(Square.sq("e7")));
        assertFalse(Square.sq("c9").isQueenMove(Square.sq("a5")));
    }

    @Test
    public void queenMove() {
        assertSame(Square.sq("a2"), Square.sq("a1").queenMove(0, 1));
        assertSame(Square.sq("d4"), Square.sq("b2").queenMove(1, 2));
        assertSame(Square.sq("f3"), Square.sq("c3").queenMove(2, 3));
        assertSame(Square.sq("h1"), Square.sq("d5").queenMove(3, 4));
        assertSame(Square.sq("e3"), Square.sq("e8").queenMove(4, 5));
        assertSame(Square.sq("d4"), Square.sq("j10").queenMove(5, 6));
        assertSame(Square.sq("c10"), Square.sq("j10").queenMove(6, 7));
        assertSame(Square.sq("b9"), Square.sq("j1").queenMove(7, 8));
    }

    @Test
    public void direction() {
        assertEquals(0, Square.sq("a1").direction(Square.sq("a2")));
        assertEquals(1, Square.sq("b2").direction(Square.sq("d4")));
        assertEquals(2, Square.sq("c3").direction(Square.sq("f3")));
        assertEquals(3, Square.sq("d5").direction(Square.sq("h1")));
        assertEquals(4, Square.sq("e8").direction(Square.sq("e3")));
        assertEquals(5, Square.sq("j10").direction(Square.sq("d4")));
        assertEquals(6, Square.sq("j10").direction(Square.sq("c10")));
        assertEquals(7, Square.sq("j1").direction(Square.sq("b9")));
    }
}