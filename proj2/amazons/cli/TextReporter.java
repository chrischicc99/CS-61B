package amazons.cli;

import amazons.Move;
import amazons.Reporter;

/**
 * A Reporter that uses the standard output for messages.
 *
 * @author P. N. Hilfinger
 */
public class TextReporter implements Reporter {

    @Override
    public void reportError(String fmt, Object... args) {
        System.out.printf(fmt, args);
        System.out.println();
    }

    @Override
    public void reportNote(String fmt, Object... args) {
        System.out.printf("* " + fmt, args);
        System.out.println();
    }

    @Override
    public void reportMove(Move move) {
        System.out.printf("* %s%n", move);
    }
}
