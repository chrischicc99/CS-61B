package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.error;

/**
 * Enigma simulator.
 *
 * @author Chris Chi
 */
public final class Main {

    /**
     * Alphabet used in this machine.
     */
    private Alphabet _alphabet;
    /**
     * Source of input messages.
     */
    private Scanner _input;
    /**
     * Source of machine configuration.
     */
    private Scanner _config;
    /**
     * File for encoded/decoded messages.
     */
    private PrintStream _output;

    /**
     * Check ARGS and open the necessary files (see comment on main).
     */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /**
     * Process a sequence of encryptions and decryptions, as
     * specified by ARGS, where 1 <= ARGS.length <= 3.
     * ARGS[0] is the name of a configuration file.
     * ARGS[1] is optional; when present, it names an input file
     * containing messages.  Otherwise, input comes from the standard
     * input.  ARGS[2] is optional; when present, it names an output
     * file for processed messages.  Otherwise, output goes to the
     * standard output. Exits normally if there are no errors in the input;
     * otherwise with code 1.
     */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /**
     * Return a Scanner reading from the file named NAME.
     */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Return a PrintStream writing to the file named NAME.
     */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Configure an Enigma machine from the contents of configuration
     * file _config and apply it to the messages in _input, sending the
     * results to _output.
     */
    private void process() {
        Machine machine = readConfig();
        String setting = null;
        if (_input.hasNext()) {
            setting = _input.nextLine();
            if (setting.startsWith("*")) {
                setUp(machine, setting);
            } else {
                setting = null;
            }
        }
        if (setting == null) {
            throw error("Input must begin with settings");
        }
        while (_input.hasNextLine()) {
            String next = _input.nextLine();
            if (next.startsWith("*")) {
                setUp(machine, next);
            } else {
                String convert = machine.convert(next);
                printMessageLine(convert);
            }
        }
    }

    /**
     * Return an Enigma machine configured from the contents of configuration
     * file _config.
     */
    private Machine readConfig() {
        try {
            checkConfigNext("alphabet");
            String[] alphabet = _config.nextLine().toUpperCase().split("-");
            if (alphabet.length < 2) {
                throw error("wrong alphabet format");
                /**
                 * support AZERF alphabet format
                 */
//                _alphabet = new CharAlphabet(alphabet[0]);

            }else{
                _alphabet = new CharacterRange(alphabet[0].charAt(0), alphabet[1].charAt(0));
            }

            checkConfigNext("rotor number");
            int rotorNum = _config.nextInt();
            checkConfigNext("pawl number");
            int pawlNum = _config.nextInt();

            List<Rotor> rotors = new ArrayList<>();
            while (_config.hasNext()) {
                rotors.add(readRotor());
            }
            return new Machine(_alphabet, rotorNum, pawlNum, rotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    private void checkConfigNext(String args) {
        if (!_config.hasNext()) {
            throw error("config file error:miss %s", args);
        }
    }

    /**
     * Return a rotor, reading its description from _config.
     */
    private Rotor readRotor() {
        try {
            String rotorName = _config.next();
            checkConfigNext(rotorName + "'s rotorType");
            String rotorType = _config.next();
            checkConfigNext(rotorName + "'s cycle permutation");
            StringBuilder cycleBuilder = new StringBuilder();
            while (_config.hasNext("\\(.*\\)")) {
                cycleBuilder.append(_config.next());
            }

            Permutation perm = new Permutation(cycleBuilder.toString(), _alphabet);

            if (rotorType.startsWith("M")) {
                return new MovingRotor(rotorName, perm, rotorType.substring(1));
            } else if (rotorType.startsWith("N")) {
                return new FixedRotor(rotorName, perm);
            } else if (rotorType.startsWith("R")) {
                return new Reflector(rotorName, perm);
            }
            throw error("wrong rotor type");
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /**
     * Set M according to the specification given on SETTINGS,
     * which must have the format specified in the assignment.
     */
    private void setUp(Machine M, String settings) {
        String[] arr = settings.split(" ");
        if (arr.length < M.numRotors() + 2) {
            throw error("wrong settings format");
        }
        String[] rotors = new String[M.numRotors()];
        System.arraycopy(arr, 1, rotors, 0, M.numRotors());
        M.insertRotors(rotors);
        M.setRotors(arr[M.numRotors() + 1]);
        StringBuilder cycleBuilder = new StringBuilder();
        for (int i = M.numRotors() + 2; i < arr.length; i++) {
            cycleBuilder.append(arr[i]);
        }
        M.setPlugboard(new Permutation(cycleBuilder.toString(), _alphabet));
    }

    /**
     * Print MSG in groups of five (except that the last group may
     * have fewer letters).
     */
    private void printMessageLine(String msg) {
        if (msg.length() == 0) {
            _output.println();
        }
        for (int i = 0; i < msg.length(); i += 5) {
            if (i + 5 >= msg.length()) {
                _output.println(msg.substring(i));
            } else {
                _output.print(msg.substring(i, i + 5) +" ");
            }
        }
    }
}