import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;;

/**
 * Put a short phrase describing the program here.
 *
 * @author Haechan Jung
 */
public final class 기본 {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private 기본() {
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        /*
         * Put your main program code here; it may call myMethod as shown
         */
        String x = null;
        out.println(x.length());
        /*
         * Close input and output streams
         */
        in.close();
        out.close();
    }

}
