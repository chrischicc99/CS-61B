package enigma;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Chris Chi
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        this._notches = notches;
    }

    @Override
    void advance() {
        set(setting() + 1);
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        return _notches.indexOf(alphabet().toChar(setting())) > -1;
    }

    String notches () { return _notches; }

    /** String representation of notches. */
    private String _notches;
}
