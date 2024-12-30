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
public final class ViewTime_Separator {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private ViewTime_Separator() {
    }

    private static String timeFilter(SimpleReader in) {
        boolean done = false;
        DateFormat oldFormat = new SimpleDateFormat(
                "yyyy. MM. dd. a hh:mm:ss z", Locale.KOREA);
        SimpleDateFormat newFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String result = "";
        while (!in.atEOS() && !done) {
            String time;
            char chr = in.read();
            if (chr == '2') {
                time = "2" + in.read() + in.read() + in.read() + in.read()
                        + in.read();
                if (time.equals("2024. ")) {
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
                    if (result.contains("youtube.com")
                            && !result.contains("youtube.com/post/")) {
                        found = true;
                    }
                }
            }
        }
        if (result.contains("music.youtube.com/")) {
            return null;
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
        // If title has '<'
        String separator = "<" + in.read() + in.read() + in.read(); // '</a>'
        if (!separator.equals("</a>")) {
            result += separator;
            result += titleFilter(in);
        }
        if (result.contains("https://www.youtube.com/watch?v")) {
            return null;
        }
        return result;
    }

    private static void dataCleaning(SimpleReader in, Queue<String> link,
            Queue<String> title, Queue<String> channelLink,
            Queue<String> channelName, Queue<String> time) {

        while (!in.atEOS()) {
            String result = linkFilter(in);
            if (result == null) {
                linkFilter(in);
                continue;
            }
            if (result.length() < 1) {
                break;
            }
            link.enqueue(result);
            result = titleFilter(in);

            if (result == null) { // if deleted
                link.rotate(-1);
                link.dequeue();
                link.rotate(1);
                continue;
            }
            if (result.length() < 1) {
                break;
            }
            result = result.replace(',', '_');
            result = result.replace('/', '\\');
            title.enqueue(result);

            result = linkFilter(in);
            
            /*
             * Should be deleted eventually=======================================================
             */ 
            if (result == null) {
                title.rotate(-1);
                result = title.dequeue();
                title.rotate(1);
                System.out.println(result);
            }
            /*
             * Should be deleted eventually=======================================================
             */ 


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

        out.println("link,title,channel_link,channel_name,time");
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
        SimpleReader in = new SimpleReader1L(
                "data\\ViewData.txt");
        Queue<String> link = new Queue1L<>();           // youtube video link
        Queue<String> title = new Queue1L<>();          // youtube video title
        Queue<String> channelLink = new Queue1L<>();    // youtube channel name
        Queue<String> channelName = new Queue1L<>();    // youtube channel name
        Queue<String> time = new Queue1L<>();           // viewing time

        dataCleaning(in, link, title, channelLink, channelName, time); // extract youtube address and view time

        /*
         * Should be deleted eventually=======================================================
         */ 
        SimpleWriter o = new SimpleWriter1L();
        o.println("link=====" + link.length()); // Should be deleted eventually
        o.println("title=====" + title.length());
        o.println("Channel_Link=====" + channelLink.length());
        o.println("Channel_Name=====" + channelName.length());
        /*
         * Should be deleted eventually=======================================================
         */ 


        SimpleWriter out = new SimpleWriter1L(
                "output\\YouTube_View_History.csv");
        dataFraming(out, link, title, channelLink, channelName, time); // print data into text file
        /*
         * Close input and output streams
         */
        in.close();
        out.close();
    }

}
