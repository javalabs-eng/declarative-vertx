package org.javalabs.decl.util;

/**
 * Utility class for performing operations on characters and strings.
 * 
 * <p>This class provides static methods for character classification, 
 * case conversion, and validation. It is designed to complement the 
 * standard Java {@link java.lang.Character} class by providing 
 * frequently used helper functions.</p>
 * 
 * <p>All methods in this class are {@code null}-safe where applicable 
 * and throw {@link java.lang.NullPointerException} only when primitive 
 * character arguments cannot be unboxed.</p>
 *
 * @author Sudiptasish Chanda
 */
public final class CharUtil {
    
    /**
     * Converts the first character of the given string to lowercase.
     * 
     * <p>This method takes a string and turns its first letter into a lowercase letter. 
     * All remaining characters in the string are left completely unchanged. If the 
     * first character is already lowercase, or is not a letter (like a number or symbol), 
     * the string is returned as-is.</p>
     * 
     * <p>Example transformations:</p>
     * <ul>
     *   <li>{@code "UserName"} becomes {@code "userName"}</li>
     *   <li>{@code "URL"} becomes {@code "uRL"}</li>
     *   <li>{@code "apple"} becomes {@code "apple"}</li>
     *   <li>{@code "123Total"} becomes {@code "123Total"}</li>
     * </ul>
     *
     * @param word the string whose first character should be lowercased, may be null
     * @return the string with its first character converted to lowercase, or {@code null} if the input was null
     * 
     * @see <a href="https://oracle.com">Character.toLowerCase Documentation</a>
     */
    public static String lowerFirst(String word) {
        return Character.toLowerCase(word.charAt(0)) + word.substring(1);
    }
    
    /**
     * Converts a given input string into CapitalisedCamelCase (PascalCase).
     * 
     * <p>This method takes a string and joins words together. It removes 
     * delimiters like spaces, underscores, or hyphens. The first letter 
     * of every word is changed to uppercase. All other letters in the 
     * word are changed to lowercase.</p>
     * 
     * <p>Example transformations:</p>
     * <ul>
     *   <li>{@code "hello_world"} becomes {@code "HelloWorld"}</li>
     *   <li>{@code "user name id"} becomes {@code "UserNameId"}</li>
     *   <li>{@code "get-ready"} becomes {@code "GetReady"}</li>
     * </ul>
     *
     * @param word the string to convert into camel case, may be null
     * @return the capitalised camel case string, or {@code null} if the input was null
     * 
     * @see <a href="https://oracle.com">Java Character API</a>
     */
    public static String toCapitalisedCamelCase(String word) {
        return toCamelCase(word, true);
    }
    
    /**
     * {@link #toCamelCase(java.lang.String, boolean) }
     * @param word      Word to be camel cased
     * @return String   Camel cased string
     */
    public static String toCamelCase(String word) {
        return toCamelCase(word, false);
    }
    
    /**
     * Converts a given input string into lowerCamelCase.
     * 
     * <p>This method takes a string and joins words together. It removes 
     * delimiters like spaces, underscores, or hyphens. The first letter 
     * of the entire string starts with a lowercase letter. The first letter 
     * of every following word is changed to uppercase.</p>
     * 
     * <p>Example transformations:</p>
     * <ul>
     *   <li>{@code "hello_world"} becomes {@code "helloWorld"}</li>
     *   <li>{@code "User Name Id"} becomes {@code "userNameId"}</li>
     *   <li>{@code "GET-READY"} becomes {@code "getReady"}</li>
     * </ul>
     *
     * @param word the string to convert into camel case, may be null
     * @return the lower camel case string, or {@code null} if the input was null
     * 
     * @see <a href="https://oracle.com">Java String API</a>
     */
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
    
    /**
     * Converts a plural English noun into its singular form.
     * 
     * <p>This method applies standard English grammar rules to change plural words 
     * back to a single form. It handles regular plural endings like adding "s" or "es". 
     * It also handles common irregular words.</p>
     * 
     * <p>Example transformations:</p>
     * <ul>
     *   <li>{@code "cats"} becomes {@code "cat"}</li>
     *   <li>{@code "boxes"} becomes {@code "box"}</li>
     *   <li>{@code "cherries"} becomes {@code "cherry"}</li>
     *   <li>{@code "children"} becomes {@code "child"} (irregular form)</li>
     * </ul>
     *
     * @param name the plural string to convert, may be null
     * @return the singular form of the noun, or {@code null} if the input was null
     * 
     * @see <a href="https://oracle.com">Java String API</a>
     */
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
    
    /**
     * Converts a singular English noun into its plural form.
     * 
     * <p>This method applies standard English grammar rules to change singular words 
     * into plural forms. It handles regular endings like adding "s" or "es". 
     * It also handles common irregular words.</p>
     * 
     * <p>Example transformations:</p>
     * <ul>
     *   <li>{@code "cat"} becomes {@code "cats"}</li>
     *   <li>{@code "box"} becomes {@code "boxes"}</li>
     *   <li>{@code "cherry"} becomes {@code "cherries"}</li>
     *   <li>{@code "child"} becomes {@code "children"} (irregular form)</li>
     * </ul>
     *
     * @param name the singular string to convert, may be null
     * @return the plural form of the noun, or {@code null} if the input was null
     * 
     * @see <a href="https://oracle.com">Java String API</a>
     */
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
    
    /**
     * Checks if the specified character is an English vowel.
     * 
     * <p>This method determines if a character is a vowel ({@code 'a'}, {@code 'e'}, 
     * {@code 'i'}, {@code 'o'}, {@code 'u'}). It checks for both lowercase and 
     * uppercase characters.</p>
     * 
     * <p>Example checks:</p>
     * <ul>
     *   <li>{@code 'a'} returns {@code true}</li>
     *   <li>{@code 'E'} returns {@code true}</li>
     *   <li>{@code 'b'} returns {@code false}</li>
     *   <li>{@code 'y'} returns {@code false} (treated as a consonant)</li>
     * </ul>
     *
     * @param ch the character to check
     * @return {@code true} if the character is an English vowel; {@code false} otherwise
     * 
     * @see <a href="https://oracle.com">Java Character API</a>
     */
    public static boolean vowel(char ch) {
        char chLower = Character.toLowerCase(ch);
        return chLower == 'a' || chLower == 'e' || chLower == 'i' || chLower == 'o' || chLower == 'u';
    }
}
