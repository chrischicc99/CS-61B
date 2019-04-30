package enigma;

import java.util.HashMap;
import java.util.Map;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Chris Chi
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = new HashMap<>();
        _inverse = new HashMap<>();
        for (String str : cycles.replaceAll("\\s+", "").split("\\)\\(")) {
            addCycle(str.replace("(", "").replace(")", ""));
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        if (cycle.length() == 1) {
            return;
        }
        for (int i = 0; i < cycle.length(); i++) {
            char ch1 = cycle.charAt(i);
            char ch2 = cycle.charAt((i + 1) % cycle.length());
            if (_cycles.put(ch1, ch2) != null) {
                throw EnigmaException.error("duplicate cycle letter:"+cycle.charAt(i));
            }
            _inverse.put(ch2, ch1);
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        return alphabet().toInt(permute(_alphabet.toChar(wrap(p))));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        return alphabet().toInt(invert(alphabet().toChar(wrap(c))));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _cycles.getOrDefault(p, p);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return _inverse.getOrDefault(c, c);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return _cycles.size() == size();
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    // ADDITIONAL FIELDS HERE, AS NEEDED
    Map<Character, Character> _cycles;
    Map<Character, Character> _inverse;
}
