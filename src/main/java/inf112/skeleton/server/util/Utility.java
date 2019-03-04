package inf112.skeleton.server.util;

public class Utility {
    public static String ucFirst(String str) {
        str = str.toLowerCase();
        if (str.length() > 1) {
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
        } else {
            return str.toUpperCase();
        }
        return str;
    }

    public static String formatPlayerName(String str) {
        String str1 = ucFirst(str);
        str1.replace("_", " ");
        return str1;
    }

    public static boolean isStringInt(String s)
    {
        try
        {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
    }
}
