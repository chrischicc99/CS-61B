package enigma;

import java.util.HashMap;
import java.util.Map;

import static enigma.EnigmaException.error;

/**
 * @since 2018/10/13.
 */
public class CharAlphabet extends Alphabet {

    public CharAlphabet(String string) {
        _chars = string.toCharArray();
        _charMap = new HashMap<>();
        for (int i = 0; i < _chars.length; i++) {
            char c = _chars[i];
            if (_charMap.put(c, i) != null) {
                throw EnigmaException.error("duplicate alphabet:" + c);
            }
        }
    }

    @Override
    int size() {
        return _chars.length;
    }

    @Override
    boolean contains(char ch) {
        return _charMap.containsKey(ch);
    }

    @Override
    char toChar(int index) {
        if (index > size()) {
            throw error("character index out of range");
        }
        return _chars[index];
    }

    @Override
    int toInt(char ch) {
        if (!contains(ch)) {
            throw error("character out of range");
        }
        return _charMap.get(ch);
    }

    final char[] _chars;
    Map<Character, Integer> _charMap;
}
