package amazons.cli;

import amazons.Board;
import amazons.View;

/**
 * A View that does nothing.
 *
 * @author P. N. Hilfinger
 */
public class NullView implements View {

    @Override
    public void update(Board board) {
    }
}
