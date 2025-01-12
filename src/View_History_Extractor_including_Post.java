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
 * Extract youtube links, titles, channel links, channel names, and viewing time from data.
 * 
 * This version excludes music video histories. You can modify this feature in dataCleaning method.
 *
 * @author Haechan Jung
 */
// TODO: Appropriate year and time zone must be defined before execution.
public final class View_History_Extractor_including_Post {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private View_History_Extractor_including_Post() {
    }

    private static final DateFormat oldFormat = new SimpleDateFormat(
        "yyyy. MM. dd. a hh:mm:ss z", Locale.KOREA);
    private static final SimpleDateFormat newFormat = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");

    private static String timeFilter(SimpleReader in) {
        boolean done = false;
        
        String result = "";
        while (!in.atEOS() && !done) {
            String time;
            char chr = in.read();
            if (chr == '2') {
                time = "2" + in.read() + in.read() + in.read() + in.read() + in.read();
                if (time.equals("2024. ") || time.equals("2025. ")) { 
                    while (!time.contains("KST")) {
                        time += in.read();
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
                            date = oldFormat.parse(time);
                            result = newFormat.format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        done = true;
                    }

                }
            }
        }
        if (done) {
            return result;
        }
        return "";
    }

    private static String linkFilter(SimpleReader in) {
        String result = "";
        boolean found = false;
        while (!in.atEOS() && !found) {
            char chr = in.read();
            if (chr == 'h') {
                result = "h";
                for (int i = 0; i < 5 && !in.atEOS(); i++) { // 'href="'
                    result += in.read();
                }
                if (result.equals("href=\"")) {
                    result = "";
                    chr = in.read();
                    while (chr != '"') {
                        result += chr;
                        chr = in.read();
                    }
                    if (result.contains("youtube.com/")) {
                        found = true;
                        
                    }
                }
            }
        }
        if (found) {
            in.read(); // '>'
            return result;
        }
        return "";
    }

    private static String titleFilter(SimpleReader in) {
        String result = "";
        char chr = in.read();
        while (chr != '<') {
            result += chr;
            chr = in.read();
        }
        // In case when title has '<'
        String separator = "<" + in.read() + in.read() + in.read(); // '</a>'
        if (!separator.equals("</a>")) {
            result += separator;
            result += titleFilter(in);
        }
        return result;
    }

    private static void dataCleaning(SimpleReader in, Queue<String> link,
            Queue<String> title, Queue<String> channelLink,
            Queue<String> channelName, Queue<String> time) {

        while (!in.atEOS()) {
            String result = linkFilter(in);
            if (result.contains("music.youtube.com/") ) {   // Exclud music video history
                timeFilter(in);
                continue;
            }
            if (result.length() < 1) {
                break;
            }
            link.enqueue(result);
            result = titleFilter(in);
            if (result.contains("youtube.com/watch")) { // if deleted or undisclosed video 
                title.enqueue("unknown");
                channelLink.enqueue("unknown");
                channelName.enqueue("unknown");
                result = timeFilter(in);
                if (result.length() < 1) {
                    break;
                }
                time.enqueue(result);
                continue;
            }
            if (result.length() < 1) {
                break;
            }
            result = result.replace(',', '_');  // To avoid wrong separation while making csv
            result = result.replace('/', '\\');
            title.enqueue(result);
            result = linkFilter(in);
            if (result.length() < 1) {
                break;
            }
            result = result.replace(',', '_');
            result = result.replace('/', '\\');
            channelLink.enqueue(result);
            result = titleFilter(in);
            if (result.length() < 1) {
                break;
            }
            result = result.replace(',', '_');
            result = result.replace('/', '\\');
            channelName.enqueue(result);
            result = timeFilter(in);
            if (result.length() < 1) {
                break;
            }
            time.enqueue(result);
        }

    }

    private static void dataFraming(SimpleWriter out, Queue<String> link,
            Queue<String> title, Queue<String> channelLink,
            Queue<String> channelName, Queue<String> time) {
        assert link.length() == time
                .length() : "Violation of: two queues have correponding data";

        out.println("link,title,channel_link,channel_name,time"); // column names
        while (link.length() > 0) {
            out.print(link.dequeue() + ",");
            out.print(title.dequeue() + ",");
            out.print(channelLink.dequeue() + ",");
            out.print(channelName.dequeue() + ",");
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
        SimpleReader in = new SimpleReader1L("data\\시청 기록.html");
        Queue<String> link = new Queue1L<>();           // youtube video link
        Queue<String> title = new Queue1L<>();          // youtube video title
        Queue<String> channelLink = new Queue1L<>();    // youtube channel name
        Queue<String> channelName = new Queue1L<>();    // youtube channel name
        Queue<String> time = new Queue1L<>();           // viewing time

        dataCleaning(in, link, title, channelLink, channelName, time); // extract youtube address and view time
        SimpleWriter out = new SimpleWriter1L("output\\YouTube_View_History_with_Post.csv");
        dataFraming(out, link, title, channelLink, channelName, time); // print data into text file
        /*
         * Close input and output streams
         */
        in.close();
        out.close();
    }

}
