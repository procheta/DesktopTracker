/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DesktopActivityTracker;

import com.alee.laf.window.WebFrame;
import com.alee.managers.style.StyleId;
import com.google.gson.Gson;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;

class GoogleResults {

    private ResponseData responseData;

    public ResponseData getResponseData() {
        return responseData;
    }

    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }

    public String toString() {
        return "ResponseData[" + responseData + "]";
    }

    static class ResponseData {

        private List<Result> results;

        public List<Result> getResults() {
            return results;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }

        public String toString() {
            return "Results[" + results + "]";
        }
    }

    static class Result {

        private String url;
        private String title;

        public String getUrl() {
            return url;
        }

        public String getTitle() {
            return title;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String toString() {
            return "Result[url:" + url + ",title:" + title + "]";
        }
    }

}

/**
 *
 * @author Procheta
 */
class Translucent extends JPanel implements ActionListener {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private final Date now = new Date();
    private final Timer timer = new Timer(1000, this);
    private final JLabel text = new JLabel();

    public Translucent() {

        super(true);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        now.setTime(System.currentTimeMillis());
        text.setText(String.format("<html><body><font size='50'>%s</font></body></html>", sdf.format(now)));
    }

    // taken from: http://java.sun.com/developer/technicalArticles/GUI/translucent_shaped_windows/
    public static void setTranslucency(Window window) {
        try {
            Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
            Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
            if (!mSetWindowOpacity.isAccessible()) {
                mSetWindowOpacity.setAccessible(true);
            }
            mSetWindowOpacity.invoke(null, window, Float.valueOf(0.75f));
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    public void notificationTrayCreation(ArrayList<String> docIdList, ArrayList<String> summaryList, int numNotification) {       
        FontMetrics metrics = getFontMetrics(getFont());
        int width = metrics.stringWidth(docIdList.get(0));
        final JFrame f = new JFrame();
        f.setUndecorated(true);
        f.setShape(new RoundRectangle2D.Double(100, 50, 400, 200, 150, 150));
        f.setSize(500, 300);
        f.setLocation(800, 300);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setAlwaysOnTop(true);       
        
       
        int d = 0;
        JPanel p = new JPanel();
        p.setLayout(null);       
        p.setBackground(Color.darkGray);
        f.add(p);
        
        int d1 = 20;
        if (numNotification > docIdList.size()) {
            numNotification = docIdList.size();
        }
        JButton b4 = new JButton("X");
        b4.setBackground(Color.darkGray);
        b4.setForeground(Color.white);
        b4.setBorderPainted(false);
        b4.setBorder(BorderFactory.createEmptyBorder());
        b4.setBounds(400, 70, 30, 30);
        p.add(b4);
        
         b4.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    f.dispose();
                }
            });
        
        for (int i = 0; i < numNotification; i++) {
            JButton b3 = new JButton("X");
            b3.setBackground(Color.lightGray);
            b3.setBorderPainted(false);
            b3.setBorder(BorderFactory.createEmptyBorder());
            b3.setBounds(150, 100 + d, width + 50, 20);
            b3.setText(docIdList.get(i));
            JLabel l = new JLabel();
            l.setText(summaryList.get(i));
            l.setForeground(Color.white);
            l.setBounds(150, 100 + d1, 300, 20);
            d1 = d1 + 40;
            b3.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        System.out.println(e.getActionCommand());
                        Desktop.getDesktop().browse(new URL("http://clueweb.adaptcentre.ie/WebSearcher/view?docid=" + e.getActionCommand()).toURI());
                    } catch (Exception ex) {
                        Logger.getLogger(Translucent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            p.add(b3);
            p.add(l);
            d = d + 40;
        }
        f.setVisible(true);
    }
}

class TimeStamp {

    String weekday;
    String month;
    String day;
    String hour;
    String minute;
    String second;
    String year;

    public TimeStamp(String s) {
        String chunks[] = s.split("\\s+");
        weekday = chunks[2];
        month = chunks[3];
        day = chunks[4];
        year = chunks[6];
        String time[] = chunks[5].split(":");
        hour = time[0];
        minute = time[1];
        second = time[2];
    }

    public String toString() {
        return String.format("Date: " + day + " " + month + " " + year + "  Time: " + hour + ":" + minute + ":" + second);
    }

}

class WriteObject {

    String windowTitle;
    String application;
    String typed_words;
    TimeStamp timeStamp;

    public WriteObject(String line) {
        line = line.replaceAll("\\[Window:", "");
        String chunks[] = line.split("]");
        line = chunks[0] + "] " + chunks[1].replaceAll("-", "");
        chunks = line.split("-");
        windowTitle = chunks[0];
        application = "";
        if (chunks.length > 3) {
            for (int i = 1; i < chunks.length - 1; i++) {
                application += chunks[i] + " ";
            }
        } else {
            application = chunks[1];
        }
        typed_words = "NA";
        typed_words = chunks[chunks.length - 1].substring(chunks[chunks.length - 1].lastIndexOf("]") + 1, chunks[chunks.length - 1].length());
        typed_words = typed_words.replaceAll("\\.", " ");
        if (typed_words.length() == 1) {
            typed_words = "NA";
        }
        chunks[chunks.length - 1] = chunks[chunks.length - 1].substring(0, chunks[chunks.length - 1].lastIndexOf("]"));
        timeStamp = new TimeStamp(chunks[chunks.length - 1]);
    }

    public WriteObject(String line, TimeStamp t, String windowTitle, String application) {
        typed_words = line;
        timeStamp = t;
        this.windowTitle = windowTitle;
        this.application = application;
    }

    public String toString() {

        return String.format("Window Title: " + windowTitle + " Application: " + application + " TimeStamp: " + timeStamp + " Typed Words: " + typed_words);
    }
}

public class ReadKeyStrokeLog {

    ArrayList<String> wordList;
    String keyLogFile;


    public ReadKeyStrokeLog() throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        prop.load(new FileReader(new File("init.properties")));
        keyLogFile = prop.getProperty("KeyLogFile");
        wordList = new ArrayList<>();
    }

    public void addKeyword() {
        wordList = new ArrayList<>();
        wordList.add("\\[CONTROL]");
        wordList.add("\\[BACKSPACE]");
        wordList.add("\\[SHIFT]");
        wordList.add("\\[DOWN]");
        wordList.add("\\[RIGHT]");
        wordList.add("\\[LEFT]");
        wordList.add("\\[TAB]");
        wordList.add("\\[UP]");
    }

    public HashSet<String> reverseKeyStrokeFileRead() {
        ReversedLinesFileReader object = null;
        int count = 0;
        HashSet<String> words = new HashSet<>();
        ArrayList<TimeStamp> times = new ArrayList<>();
        ArrayList<String> windowTitle = new ArrayList<>();
        ArrayList<String> app = new ArrayList<>();
        String prevLine = "";
        try {
            object = new ReversedLinesFileReader(new File(keyLogFile));
            WriteObject wob = null;
            while (object != null) {
                String line = object.readLine();
                for (String s : wordList) {
                    line = line.replaceAll(s, "");
                }
                line = line.replaceAll("\\p{C}", "");
                line = line.replaceAll("[^\\x00-\\x7F]", "");

                if (line.startsWith("[Window:")) {
                    wob = new WriteObject(line);
                    wob.typed_words += prevLine;
                    wob.typed_words = wob.typed_words.replaceAll("\\.", " ");
                    wob.typed_words = wob.typed_words.toLowerCase();
                    wob.typed_words = wob.typed_words.replaceAll("[0-9]", " ");
                    prevLine = "";
                }
                String st[] = null;
                if (wob != null) {
                    st = wob.typed_words.split("\\s+");
                    for (String s : st) {
                        if (s.length() > 2) {
                            words.add(s);
                        }
                    }
                } else {
                    prevLine += line + " ";
                }

                count++;
                if (count == 200) {
                    break;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                object.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return words;
    }

    public void throwNotification(HashSet<String> words, int num) throws MalformedURLException, IOException {

        String notitficationLine = "";
        Iterator it = words.iterator();
        int count = 0;
        while (it.hasNext()) {
            String word = (String) it.next();
            notitficationLine += word + " ";
            count++;
            if (count == num) {
                break;
            }
        }
        ArrayList<String> summaryList = new ArrayList<>();
        ArrayList<String> docIdList = createRankedListUsingClueweb(notitficationLine, 3);
        for (int i = 0; i < docIdList.size(); i++) {
            summaryList.add(notitficationLine);
        }
        // notitficationLine = "<html>" + notitficationLine + "<br/>" + notitficationLine + "<br/>" + "<a href='https://google.com'>urlllllllllllllllllllllllllllllllllllll</a>" + "</html>";
        Translucent t = new Translucent();
        t.notificationTrayCreation(docIdList, summaryList, 3);
    }

    public ArrayList<String> createRankedListUsingClueweb(String s, int num) throws UnsupportedEncodingException, MalformedURLException, IOException {
        String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
        String param1 = s;
        ArrayList<String> docIdList = new ArrayList<>();
        URL url = new URL("http://clueweb.adaptcentre.ie/WebSearcher/search?query=" + URLEncoder.encode(param1, charset));
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String all = "";
            String line;
            while ((line = in.readLine()) != null) {
                all += line;
            }
            JSONObject jobJSONObject = new JSONObject();
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(all);
            ArrayList arr = new ArrayList<String>();

            int count = 0;
            for (int j1 = 0; j1 < jsonArray.size() - 1; j1++) {
                String st1 = ((Object) jsonArray.get(j1)).toString();
                String st2 = st1.replace("[", "");
                st2 = st2.replace("]", "");
                char d = '"';
                String st4 = st2.substring(st2.indexOf("id" + d), st2.length());

                String st7 = st4;
                try {
                    st4 = st4.substring(0, st4.indexOf(","));
                    String st3[] = st2.split(",");
                    String st5[] = st4.split(":");
                    st5[1] = st5[1].replace("" + d, "");
                    docIdList.add(st5[1]);
                    arr.add(st4);
                } catch (Exception e) {
                    st7 = st7.substring(st7.indexOf(",") + 1, st7.length());
                    String st8 = st7.substring(0, st7.indexOf(","));
                    String st5[] = st8.split(":");
                    //System.out.println(st8);
                    st5[1] = st5[1].replace("" + d, "");
                    docIdList.add(st5[1]);
                    arr.add(st4);
                }
            }
        } catch (Exception e) {
            System.out.println("entered" + e);
        }
        // System.out.println(docIdList);
        return docIdList;
    }

    public void createRankedListUsingGoogle(String s, int num) throws IOException {
        String GOOGLE_SEARCH_URL = "https://www.google.com";
        String searchURL = GOOGLE_SEARCH_URL + "?q=" + s + "&num=" + num;
        Document doc = (Document) Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        
        
        ReadKeyStrokeLog rkl = new ReadKeyStrokeLog();
        rkl.addKeyword();
        HashSet<String> words = rkl.reverseKeyStrokeFileRead();
        rkl.throwNotification(words, 5);
        //  rkl.createRankedListUsingClueweb("dog", 5);
    }
}
