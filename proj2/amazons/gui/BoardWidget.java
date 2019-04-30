package amazons.gui;

import amazons.*;
import ucb.gui2.Pad;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

// This skeleton provides a bit of suggested structure.   It's up to you to
// discover what needs to be added.  You are NOT required to reproduce the
// GUI of the staff program.

/**
 * A widget that displays an Amazons game.
 */
class BoardWidget extends Pad {

    /* Parameters controlling sizes, speeds, colors, and fonts. */

    /**
     * Colors of empty squares and grid lines.
     */
    static final Color
            SPEAR_COLOR = new Color(64, 64, 64),
            LIGHT_SQUARE_COLOR = Color.WHITE,
            DARK_SQUARE_COLOR = Color.LIGHT_GRAY;

    /**
     * Locations of images of white and black queens.
     */
    private static final String
            WHITE_QUEEN_IMAGE = Paths.get("assets").resolve("wq4.png").toString(),
            BLACK_QUEEN_IMAGE = Paths.get("assets").resolve("bq4.png").toString(),
            ARROW_IMAGE = Paths.get("assets").resolve("arrow.png").toString();

    /**
     * Size parameters.
     */
    private static final int
            SQUARE_SIDE = 30,
            BOARD_SIDE = SQUARE_SIDE * 10,
            WIDTH = 600;
    /**
     * Board being displayed.
     */
    private final Board _board = new Board();
    /**
     * Queue on which to post move commands (from mouse clicks).
     */
    private ArrayBlockingQueue<String> _commands;
    /**
     * Image of white queen.
     */
    private BufferedImage _whiteQueen;
    /**
     * Image of black queen.
     */
    private BufferedImage _blackQueen;

    private static final Font TEXT_FONT = new Font(Font.MONOSPACED, Font.BOLD, 16);
    /**
     * True iff accepting moves from user.
     */
    private boolean _acceptingMoves;
    private BufferedImage _arrow;
    private String whiteMode = "manual";
    private String blackMode = "auto";
    private Square from;
    private Square to;

    /**
     * A graphical representation of an Amazons board that sends commands
     * derived from mouse clicks to COMMANDS.
     */
    BoardWidget(ArrayBlockingQueue<String> commands) {
        _commands = commands;
        setMouseHandler("click", this::mouseClicked);
        setPreferredSize(WIDTH, BOARD_SIDE);

        try {
            _whiteQueen = ImageIO.read(Utils.getResource(WHITE_QUEEN_IMAGE));
            _blackQueen = ImageIO.read(Utils.getResource(BLACK_QUEEN_IMAGE));
            _arrow = ImageIO.read(Utils.getResource(ARROW_IMAGE));
        } catch (IOException excp) {
            System.err.println("Could not read queen images.");
            System.exit(1);
        }
        _acceptingMoves = false;
    }

    /**
     * Draw the bare board G.
     */
    private void drawGrid(Graphics2D g) {
        g.setColor(LIGHT_SQUARE_COLOR);
        g.fillRect(0, 0, BOARD_SIDE, BOARD_SIDE);

        Iterator<Square> square = Square.iterator();
        while (square.hasNext()) {
            Square sq = square.next();
            int cx = cx(sq);
            int cy = cy(sq);
            Piece piece = _board.get(sq);

            if ((sq.row() + sq.col()) % 2 == 0) {
                g.setColor(LIGHT_SQUARE_COLOR);
            } else {
                g.setColor(DARK_SQUARE_COLOR);
            }
            if (sq == to) {
                g.setColor(Color.BLUE);
            }

            g.fillRect(cx, cy, SQUARE_SIDE, SQUARE_SIDE);

            drawQueen(g, sq, piece);


        }
    }

    private void drawInformation(Graphics2D g) {
        g.setFont(TEXT_FONT);
        g.setColor(Color.BLACK);
        String turn = String.format("Waiting for %s to move.", _board.getTurn().toName());
        g.drawString(turn, 320, 40);

        String wMode = String.format("White: %s", whiteMode);
        g.drawString(wMode, 350, 80);

        String bMode = String.format("Black: %s", blackMode);
        g.drawString(bMode, 350, 120);

        List<Move> moves = _board.lastMoves(5);
        Collections.reverse(moves);
        for (int i = 0, movesSize = moves.size(); i < movesSize; i++) {
            Move move = moves.get(i);
            g.drawString(move.toString(), 350, 150 + 30 * i);
        }
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        super.paintComponent(g);

        drawGrid(g);
        drawInformation(g);
    }

    /**
     * Draw a queen for side PIECE at square S on G.
     */
    private void drawQueen(Graphics2D g, Square s, Piece piece) {
        if (piece == Piece.BLACK) {
            g.drawImage(_blackQueen, cx(s.col()) + 2, cy(s.row()) + 4, null);
        }
        if (piece == Piece.WHITE) {
            g.drawImage(_whiteQueen, cx(s.col()) + 2, cy(s.row()) + 4, null);
        }
        if (piece == Piece.SPEAR) {
            g.drawImage(_arrow, cx(s.col()) - 4, cy(s.row()) - 4, null);
        }
    }

    /**
     * Handle a click on S.
     */
    private void click(Square s) {
        Piece piece = _board.get(s);


        if (from == null) {
            if (piece != _board.getTurn()) {
                return;
            }
            from = s;
            return;
        }

        if (to == null) {
            to = s;
        } else {
            Move mv = Move.mv(from, to, s);

            if (_board.isLegal(mv)) {
                _commands.add(mv.toString());
            }

            from = null;
            to = null;
        }

        repaint();
    }

    /**
     * Handle mouse click event E.
     */
    private synchronized void mouseClicked(String unused, MouseEvent e) {
        int xpos = e.getX(), ypos = e.getY();
        int x = xpos / SQUARE_SIDE,
                y = (BOARD_SIDE - ypos) / SQUARE_SIDE;
        if (_acceptingMoves
                && x >= 0 && x < Board.SIZE && y >= 0 && y < Board.SIZE) {
            click(Square.sq(x, y));
        }
    }

    /**
     * Revise the displayed board according to BOARD.
     */
    synchronized void update(Board board) {
        _board.copy(board);
        repaint();
    }

    synchronized void updateInformation(String white, String black) {
        if (white != null) {
            whiteMode = white;
        }
        if (black != null) {
            blackMode = black;
        }
        repaint();
    }

    /**
     * Turn on move collection iff COLLECTING, and clear any current
     * partial selection.   When move collection is off, ignore clicks on
     * the board.
     */
    void setMoveCollection(boolean collecting) {
        _acceptingMoves = collecting;
        repaint();
    }

    /**
     * Return x-pixel coordinate of the left corners of column X
     * relative to the upper-left corner of the board.
     */
    private int cx(int x) {
        return x * SQUARE_SIDE;
    }

    /**
     * Return y-pixel coordinate of the upper corners of row Y
     * relative to the upper-left corner of the board.
     */
    private int cy(int y) {
        return (Board.SIZE - y - 1) * SQUARE_SIDE;
    }

    /**
     * Return x-pixel coordinate of the left corner of S
     * relative to the upper-left corner of the board.
     */
    private int cx(Square s) {
        return cx(s.col());
    }

    /**
     * Return y-pixel coordinate of the upper corner of S
     * relative to the upper-left corner of the board.
     */
    private int cy(Square s) {
        return cy(s.row());
    }
}
