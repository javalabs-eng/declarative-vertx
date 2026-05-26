package org.javalabs.decl.util;

/**
 *
 * @author schan280
 */
public final class CharUtil {
    
    public static String lowerFirst(String word) {
        return Character.toLowerCase(word.charAt(0)) + word.substring(1);
    }
    
    public static String toCapitalisedCamelCase(String word) {
        return toCamelCase(word, true);
    }
    
    public static String toCamelCase(String word) {
        return toCamelCase(word, false);
    }
    
    public static String toCamelCase(String word, boolean isCapital) {
        final char[] arr = new char[word.length()];
        int idx = 0;
        char ch = '\0';
        
        boolean capitalizedNext = false;
        
        for (int i = 0; i < word.length(); i ++) {
            ch = word.charAt(i);
            if (ch == '_' || ch == '-') {
                capitalizedNext = true;
            }
            else {
                if (i == 0) {
                    if (isCapital) {
                        arr[idx] = Character.toUpperCase(ch);
                    }
                    else {
                        arr[idx] = Character.toLowerCase(ch);
                    }
                }
                else if (capitalizedNext) {
                    arr[idx] = Character.toUpperCase(ch);
                    capitalizedNext = false;
                }
                else {
                    arr[idx] = Character.toLowerCase(ch);
                }
                idx ++;
            }
        }
        return new String(arr, 0, idx);
    }
    
    public static String singular(String name) {
        if (name.toLowerCase().endsWith("info")) {
            return name;
        }
        String ret = CharUtil.toCapitalisedCamelCase(name);
        if (ret.endsWith("ies")) {
            ret = ret.substring(0, ret.length() - 3) + "y";
        }
        else if (ret.endsWith("es")) {
            if (ret.charAt(ret.length() - 3) == 's' || ret.charAt(ret.length() - 3) == 'x') {
                ret = ret.substring(0, ret.length() - 2);
            }
            else if (ret.charAt(ret.length() - 3) == 'v') {
                ret = ret.substring(0, ret.length() - 3) + "fe";
            }
            else if (ret.charAt(ret.length() - 4) == 'c' && ret.charAt(ret.length() - 3) == 'h') {
                ret = ret.substring(0, ret.length() - 2);
            }
            else if (ret.charAt(ret.length() - 4) == 's' && ret.charAt(ret.length() - 3) == 'h') {
                ret = ret.substring(0, ret.length() - 2);
            }
            else {
                ret = ret.substring(0, ret.length() - 1);
            }
        }
        else if (ret.endsWith("s") && ! ret.toLowerCase().endsWith("status")) {
            ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }
    
    public static String plural(String name) {
        String lowerNoun = name.toLowerCase();

        // 1. Handle ending with "y" (consonant + y -> ies)
        if (lowerNoun.endsWith("y") && ! vowel(lowerNoun.charAt(lowerNoun.length() - 2))) {
            return name.substring(0, name.length() - 1) + "ies";
        }
        
        // 2. Handle "hissing" sounds (s, sh, ch, x, z -> es)
        if (lowerNoun.endsWith("s") || lowerNoun.endsWith("sh") || 
            lowerNoun.endsWith("ch") || lowerNoun.endsWith("x") || 
            lowerNoun.endsWith("z")) {
            return name + "es";
        }

        // 3. Default: just add "s"
        return name + "s";
    }
    
    public static boolean vowel(char ch) {
        char chLower = Character.toLowerCase(ch);
        return chLower == 'a' || chLower == 'e' || chLower == 'i' || chLower == 'o' || chLower == 'u';
    }
}
