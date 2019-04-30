package enigma;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/** Class that represents a complete enigma machine.
 *  @author Chris Chi
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;
        _allRotors = allRotors;
        _rotors = new Rotor[_numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (rotors.length != _numRotors) {
            throw EnigmaException.error("wrong robots length");
        }
        Set<String> rotorNames = new HashSet<>();
        for (int i = 0; i < rotors.length; i++) {
            String rotorName = rotors[i];
            if (rotorNames.add(rotorName)) {
                Rotor rotor = findRotorByName(rotorName);
                if (i == 0) {
                    if(!rotor.reflecting()) {
                        throw EnigmaException.error("index 0 must be Reflector");
                    }
                }else if (i < _numRotors - _numPawls) {
                    if(rotor.rotates()) {
                        throw EnigmaException.error("index " + i + " must be MovingRotor");
                    }
                }else{
                    if (!rotor.rotates()) {
                        throw EnigmaException.error("index " + i + " must be MovingRotor");
                    }
                }
                _rotors[i] = rotor;
            }else{
                throw EnigmaException.error("duplicate rotor name");
            }

        }
    }

    private Rotor findRotorByName(String rotorName) {
        for (Rotor rotor : _allRotors) {
            if (rotor.name().equalsIgnoreCase(rotorName)) {
                return rotor;
            }
        }
        throw EnigmaException.error("not found rotorName:" + rotorName);
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw EnigmaException.error("wrong setting length");
        }
        _setting = setting;
        for (int i = 0; i < _setting.length(); i++) {
            char c = _setting.charAt(i);
            if(_alphabet.contains(c)) {
                _rotors[i+1].set(c);
            }else{
                throw EnigmaException.error("alphabet not contain setting letter");
            }
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        //advance
        boolean[] canMove = new boolean[_numRotors];
        canMove[_numRotors - 1] = true;
        for (int i = _rotors.length - 1; i >= 0; i--) {
            //Double Stepping
            if (_rotors[i].atNotch() && i > 0) {
                if (_rotors[i].rotates() && _rotors[i - 1].rotates()) {
                    canMove[i] = true;
                    canMove[i - 1] = true;
                }
            }
        }
        for (int i = 0; i < canMove.length; i++) {
            if (canMove[i]) {
                _rotors[i].advance();
            }
        }
        //
        int currIndex = _plugboard.permute(c);
        for (int i = _rotors.length - 1; i >= 0; i--) {
            currIndex = _rotors[i].convertForward(currIndex);
        }
        for (int i = 1; i < _rotors.length; i++) {
            currIndex = _rotors[i].convertBackward(currIndex);
        }
        return _plugboard.invert(currIndex);
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        msg = msg.replaceAll("\\s+", "").toUpperCase();
        char[] chars = new char[msg.length()];
        for (int i = 0; i < msg.length(); i++) {
            chars[i] = _alphabet.toChar(convert(_alphabet.toInt(msg.charAt(i))));
        }
        return new String(chars);
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    //ADDITIONAL FIELDS HERE, IF NEEDED.
    private int _numRotors;

    private int _numPawls;

    private Collection<Rotor> _allRotors;

    private Rotor[] _rotors;

    private Permutation _plugboard;

    private String _setting;
}
