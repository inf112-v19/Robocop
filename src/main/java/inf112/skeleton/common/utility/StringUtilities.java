package inf112.skeleton.common.utility;

public class StringUtilities {
    /**
     * Uppercase first letter of a string
     * @param string
     * @return string with first letter uppercase
     */
    public static String upperCaseFirst(String string) {
        string = string.toLowerCase();
        if (string.length() > 1) {
            string = string.substring(0, 1).toUpperCase() + string.substring(1);
        } else {
            return string.toUpperCase();
        }
        return string;
    }

    /**
     * Format a username to a cleaner format.
     * @param username
     * @return formatted username
     */
    public static String formatPlayerName(String username) {
        String str1 = upperCaseFirst(username);
        str1.replace("_", " ");
        return str1;
    }

    /**
     * check if a string is an int.
     * @param string
     * @return true if string is an int, false if not.
     */
    public static boolean isStringInt(String string)
    {
        try
        {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
    }
}
