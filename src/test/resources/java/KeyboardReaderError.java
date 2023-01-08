import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * Causes a compilation error due to an unhandled Exception
 */
public class KeyboardReaderError {

    public static void main(String[] args) throws java.io.IOException { // throws java.io.IOException

        String s1;
        String s2;

        double num1, num2, product;

        // set up the buffered reader to read from the keyboard
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter a line of input");
    
    /* Following line triggers the error.  Error will show the type of
       unhandled exception and where the call occurs */
        s1 = br.readLine();

        System.out.println("The line has " + s1.length() + " characters");

        System.out.println();
        System.out.println("Breaking the line into tokens we get:");

        int numTokens = 0;
        StringTokenizer st = new StringTokenizer(s1);

        while (st.hasMoreTokens()) {
            s2 = st.nextToken();
            numTokens++;
            System.out.println("    Token " + numTokens + " is: " + s2);
        }
    }
}
