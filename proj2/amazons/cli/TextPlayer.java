package amazons.cli;

import amazons.Controller;
import amazons.Move;
import amazons.Piece;
import amazons.Player;

/**
 * A Player that takes input as cli commands from the standard input.
 */
public class TextPlayer extends Player {
    /**
     * A new TextPlayer with no piece or controller (intended to produce
     * a template).
     */
    public TextPlayer() {
        this(null, null);
    }

    /**
     * A new TextPlayer playing PIECE under control of CONTROLLER.
     */
    private TextPlayer(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    public Player create(Piece piece, Controller controller) {
        return new TextPlayer(piece, controller);
    }

    @Override
    public String myMove() {
        while (true) {
            String line = _controller.readLine();
            if (line == null) {
                return "quit";
            }

            if (!Move.isGrammaticalMove(line)) {
                return line;
            }

            Move mv = Move.mv(line);
            if (mv != null && board().isLegal(mv) && _myPiece != Piece.EMPTY) {
                return line;
            } else {
                _controller.reportError("Invalid move. Please try again.");
            }
        }
    }
}
