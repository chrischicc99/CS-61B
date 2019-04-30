package amazons;

import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static amazons.Piece.*;
import static amazons.Utils.error;

/**
 * The input/output and GUI controller for play of Amazons.
 */
public final class Controller {
    /**
     * A Pattern matches comments.
     */
    private final Pattern _comment = Pattern.compile("#.*");
    /**
     * The board.
     */
    private final Board _board = new Board();
    /**
     * The winning side of the current game.
     */
    private Piece _winner;
    /**
     * True while game is still active.
     */
    private boolean _playing;
    /**
     * The object that is displaying the current game.
     */
    private final View _view;
    /**
     * My pseudo-random number generator.
     */
    private final Random _randGen = new Random();
    /**
     * Log file, or null if absent.
     */
    private final PrintStream _logFile;
    /**
     * Input source.
     */
    private final Scanner _input;
    /**
     * A dummy Player used to return commands but not moves when no
     * game is in progress.
     */
    private final Player _nonPlayer;
    /**
     * The current White and Black players, each created from
     * _autoPlayerTemplate or _manualPlayerTemplate.
     */
    private Player _white, _black;
    /**
     * The current templates for manual and automated players.
     */
    private final Player _autoPlayerTemplate;
    private final Player _manualPlayerTemplate;
    /**
     * A list of Commands describing the valid textual commands to the
     * Amazons program and the methods to process them.
     */
    private final Command[] _commands = {
            new Command("quit$", this::doQuit),
            new Command("seed\\s+(\\d+)$", this::doSeed),
            new Command("dump$", this::doDump),
            new Command("new$", this::doNew),
            new Command(Move.MOVE_PATTERN_SRTING, this::doMove),
            new Command("(?i)auto\\s+(black|white)$", this::doAuto),
            new Command("(?i)manual\\s+(black|white)$", this::doManual),
    };
    /**
     * Reporter for messages and errors.
     */
    private final Reporter _reporter;

    /**
     * Controller for one or more games of Amazons, using
     * MANUALPLAYERTEMPLATE as an exemplar for manual players
     * (see the Player.create method) and AUTOPLAYERTEMPLATE
     * as an exemplar for automated players.  Reports
     * board changes to VIEW at appropriate points.  Uses REPORTER
     * to report moves, wins, and errors to user. If LOGFILE is
     * non-null, copies all commands to it. If STRICT, exits the
     * program with non-zero code on receiving an erroneous move from a
     * player.
     */
    Controller(View view, PrintStream logFile, Reporter reporter,
               Player manualPlayerTemplate, Player autoPlayerTemplate) {
        _view = view;
        _playing = false;
        _logFile = logFile;
        _input = new Scanner(System.in);
        _autoPlayerTemplate = autoPlayerTemplate;
        _manualPlayerTemplate = manualPlayerTemplate;
        _nonPlayer = manualPlayerTemplate.create(EMPTY, this);
        _reporter = reporter;
    }

    /**
     * Play Amazons.
     */
    void play() {
        _playing = true;
        _winner = null;
        _board.init();
        _white = _manualPlayerTemplate.create(WHITE, this);
        _black = _autoPlayerTemplate.create(BLACK, this);
        while (_playing) {
            _view.update(_board);
            String command;
            if (_winner == null || _winner == EMPTY) {
                if (_board.turn() == WHITE) {
                    command = _white.myMove();
                } else {
                    command = _black.myMove();
                }
            } else {
                command = _nonPlayer.myMove();
            }
            if (command == null) {
                command = "quit";
            }
            try {
                executeCommand(command);
            } catch (IllegalArgumentException excp) {
                reportError("Error: %s%n", excp.getMessage());
            }
        }
        if (_logFile != null) {
            _logFile.close();
        }
    }

    /**
     * Return the current board.  The value returned should not be
     * modified by the caller.
     */
    Board board() {
        return _board;
    }

    /**
     * Return a random integer in the range 0 inclusive to U, exclusive.
     * Available for use by AIs that use random selections in some cases.
     * Once setRandomSeed is called with a particular value, this method
     * will always return the same sequence of values.
     */
    public int randInt(int U) {
        return _randGen.nextInt(U);
    }

    /**
     * Re-seed the pseudo-random number generator (PRNG) that supplies randInt
     * with the value SEED. Identical seeds produce identical sequences.
     * Initially, the PRNG is randomly seeded.
     */
    private void setSeed(long seed) {
        _randGen.setSeed(seed);
    }

    /**
     * Return the next line of input, or null if there is no more. First
     * prompts for the line.  Trims the returned line (if any) of all
     * leading and trailing whitespace.
     */
    public String readLine() {
        System.out.print("> ");
        System.out.flush();
        if (_input.hasNextLine()) {
            return _input.nextLine().trim();
        } else {
            return null;
        }
    }

    /**
     * Report error by calling reportError(FORMAT, ARGS) on my reporter.
     */
    public void reportError(String format, Object... args) {
        _reporter.reportError(format, args);
    }

    /**
     * Report note by calling reportNote(FORMAT, ARGS) on my reporter.
     */
    public void reportNote(String format, Object... args) {
        _reporter.reportNote(format, args);
    }

    /**
     * Report move by calling reportMove(MOVE) on my reporter.
     */
    public void reportMove(Move move) {
        _reporter.reportMove(move);
    }

    /**
     * Check that CMND is one of the valid Amazons commands and execute it, if
     * so, raising an IllegalArgumentException otherwise.
     */
    private void executeCommand(String cmnd) {
        if (_logFile != null) {
            _logFile.println(cmnd);
            _logFile.flush();
        }

        Matcher commentMatcher = _comment.matcher(cmnd);
        cmnd = commentMatcher.replaceFirst("").trim().toLowerCase();

        if (cmnd.isEmpty()) {
            return;
        }
        for (Command parser : _commands) {
            Matcher matcher = parser._pattern.matcher(cmnd);
            if (matcher.matches()) {
                parser._processor.accept(matcher);
                return;
            }
        }
        throw error("Bad command: %s", cmnd);
    }

    /**
     * Command "new".
     */
    private void doNew(Matcher unused) {
        _board.init();
        _winner = null;
    }

    /**
     * Command "quit".
     */
    private void doQuit(Matcher unused) {
        _playing = false;
        System.exit(0);
    }

    /**
     * Command "seed N" where N is the first group of MAT.
     */
    private void doSeed(Matcher mat) {
        try {
            setSeed(Long.parseLong(mat.group(1)));
        } catch (NumberFormatException excp) {
            throw error("number too large");
        }
    }

    /**
     * Dump the contents of the board on standard output.
     */
    private void doDump(Matcher unused) {
        System.out.printf("===%n%s===%n", _board);
    }

    private void doMove(Matcher matcher) {
        _board.makeMove(Move.mv(matcher.group()));

        _winner = _board.winner();

        if (_winner == WHITE) {
            _reporter.reportNote("White wins.");
        }
        if (_winner == BLACK) {
            _reporter.reportNote("Black wins.");
        }
    }

    private void doAuto(Matcher matcher) {
        String player = matcher.group(1);

        if ("black".equalsIgnoreCase(player)) {
            _black = _autoPlayerTemplate.create(BLACK, this);
        }
        if ("white".equalsIgnoreCase(player)) {
            _white = _autoPlayerTemplate.create(WHITE, this);
        }
    }

    private void doManual(Matcher matcher) {
        String player = matcher.group(1);

        if ("black".equalsIgnoreCase(player)) {
            _black = _manualPlayerTemplate.create(BLACK, this);
        }
        if ("white".equalsIgnoreCase(player)) {
            _white = _manualPlayerTemplate.create(WHITE, this);
        }
    }

    /**
     * A Command is pair (<pattern>, <processor>), where <pattern> is a
     * Matcher that matches instances of a particular command, and
     * <processor> is a functional object whose .accept method takes a
     * successfully matched Matcher and performs some operation.
     */
    private static class Command {
        /**
         * A Matcher matching my pattern.
         */
        final Pattern _pattern;
        /**
         * The function object that implements my command.
         */
        final Consumer<Matcher> _processor;

        /**
         * A new Command that matches PATN (a regular expression) and uses
         * PROCESSOR to process commands that match the pattern.
         */
        Command(String patn, Consumer<Matcher> processor) {
            _pattern = Pattern.compile(patn);
            _processor = processor;
        }
    }

}
