// Part 3: characters, bytes and encoding. Non-ASCII is written with \ u escapes so the
// source file stays plain ASCII, and results are printed as numbers so the console
// encoding cannot lie to us.
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

public class StringDemo3 {
    public static void main(String[] args) throws Exception {
        String accented = "caf\u00e9";       // c a f e-acute  -> 4 chars
        String emoji = "A\uD83D\uDE00";      // 'A' + grinning face, stored as TWO chars

        System.out.println("accented.length()     : " + accented.length());
        System.out.println("emoji.length()        : " + emoji.length());
        System.out.println("emoji code points     : " + emoji.codePointCount(0, emoji.length()));
        System.out.println("emoji.charAt(1) value : " + (int) emoji.charAt(1) + " (half a character)");
        System.out.println("emoji.codePointAt(1)  : " + emoji.codePointAt(1));

        byte[] utf8 = accented.getBytes("UTF-8");
        byte[] latin1 = accented.getBytes("ISO-8859-1");
        System.out.println("UTF-8 byte count      : " + utf8.length + " " + Arrays.toString(utf8));
        System.out.println("ISO-8859-1 byte count : " + latin1.length + " " + Arrays.toString(latin1));

        String wrong = new String(utf8, "ISO-8859-1");   // decoded with the wrong rulebook
        String right = new String(utf8, "UTF-8");
        System.out.println("wrong decode length   : " + wrong.length());
        System.out.println("right decode length   : " + right.length());
        System.out.println("right decode matches  : " + right.equals(accented));
        System.out.println("platform default      : " + Charset.defaultCharset());

        System.out.println("equals ignoring case  : " + "HELLO".equalsIgnoreCase("hello"));
        System.out.println("'i' upper in Turkish  : " + (int) "i".toUpperCase(new Locale("tr")).charAt(0));
        System.out.println("'i' upper with ROOT   : " + (int) "i".toUpperCase(Locale.ROOT).charAt(0));

        // A password in a char[] can be erased; a String cannot.
        char[] password = {'s', '3', 'c', 'r', 'e', 't'};
        Arrays.fill(password, '\0');
        System.out.println("wiped password        : " + Arrays.toString(password));
    }
}
