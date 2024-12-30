import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import components.queue.Queue;
import components.queue.Queue1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Extract youtube links and viewing time from data.
 *
 * @author Haechan Jung
 */
public final class COPY {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private COPY() {
    }

    private static void timeFilter(SimpleReader in, Queue<String> time) {
        boolean done = false;
        DateFormat oldFormat = new SimpleDateFormat(
                "yyyy. MM. dd. a hh:mm:ss z", Locale.KOREA);
        SimpleDateFormat newFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        final int date_length = 27;

        while (!in.atEOS() && !done) {
            String word;
            char chr = in.read();
            if (chr == '2') {
                word = "2" + in.read() + in.read() + in.read() + in.read()
                        + in.read();
                if (word.equals("2024. ")) {
                    while (!word.contains("KST")) {
                        word += in.read();
                    }
                    /*
                     * Check if following is </div>
                     */
                    String div = "";
                    final int six = 6;
                    for (int i = 0; i < six; i++) {
                        div += in.read();
                    }
                    if (div.equals("</div>")) {
                        Date date;
                        try {
                            date = oldFormat.parse(word);
                            String formatted = newFormat.format(date);
                            time.enqueue(formatted);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        done = true;
                    }

                }
            }
        }

    }

    private static String linkFilter(SimpleReader in, Queue<String> link) {
        in.read(); // '='
        in.read(); // '\"'
        String result = "";
        char chr = in.read();
        while (chr != '"') {
            result += chr;
            chr = in.read();
        }
        return result;
    }

    private static void dataCleaning(SimpleReader in, Queue<String> link,
            Queue<String> title, Queue<String> channelLink, Queue<String> name,
            Queue<String> time) {

        while (!in.atEOS()) {
            String address;
            char chr = in.read();
            if (chr == 'h') {
                address = "h" + in.read() + in.read() + in.read(); // 'r' + 'e' + 'f'
                if (address.equals("href")) {

                    if (address.contains("https://www.youtube.com/watch?")) {
                        link.enqueue(address);
                        timeFilter(in, time);
                    }
                }
            }
        }

    }

    private static void dataFraming(SimpleWriter out, Queue<String> link,
            Queue<String> title, Queue<String> channelLink, Queue<String> name,
            Queue<String> time) {
        assert link.length() == time
                .length() : "Violation of: two queues have correponding data";

        while (link.length() > 0) {
            out.print(link.dequeue());
            out.print(",");
            out.println(time.dequeue());
        }

    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L(
                "z_PORTFOLIO_Youtube_Data\\00-WorkSpace\\data\\ViewData.txt");
        Queue<String> link = new Queue1L<>(); // youtube video link
        Queue<String> title = new Queue1L<>(); // youtube video title
        Queue<String> channelLink = new Queue1L<>(); // youtube channel name
        Queue<String> name = new Queue1L<>(); // youtube channel name
        Queue<String> time = new Queue1L<>(); // viewing time

        dataCleaning(in, link, title, channelLink, name, time); // extract youtube address and view time
        SimpleWriter out = new SimpleWriter1L(
                "z_PORTFOLIO_Youtube_Data\\00-WorkSpace\\src\\Youtube_View_History.csv");
        dataFraming(out, link, title, channelLink, name, time); // print data into text file
        /*
         * Close input and output streams
         */
        in.close();
        out.close();
    }

}
