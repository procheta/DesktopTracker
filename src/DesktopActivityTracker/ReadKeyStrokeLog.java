/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DesktopActivityTracker;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.xml.sax.ContentHandler;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Procheta
 */
class CustomButton extends JButton {

    String title;

    public CustomButton(String title) {
        super();
        this.title = title;
    }
}

class wordObject implements Comparable<wordObject> {

    String word;
    int tf;
    TimeStamp timestamp;

    @Override
    public int compareTo(wordObject o) {

        return this.timestamp.getTimeDifference(o.timestamp);
    }
}

class relObject implements Comparable<relObject> {

    String word;
    double tf;
    DesktopActivityTracker.TimeStamp timestamp;

    @Override
    public int compareTo(relObject o) {

        if (this.tf < o.tf) {
            return -1;
        } else if (this.tf > o.tf) {
            return 1;
        } else {
            return 0;
        }
    }
}

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
            // ex.printStackTrace();
            System.out.println("No such method exception occurred in window creation");
        } catch (SecurityException ex) {
            // ex.printStackTrace();
            System.out.println("Security exception occurred in window creation");
        } catch (ClassNotFoundException ex) {
            //ex.printStackTrace();
            System.out.println("Class not found exception occurred in window creation");
        } catch (IllegalAccessException ex) {
            //ex.printStackTrace();
            System.out.println("Illegal access exception occurred in window creation");
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
            System.out.println("Illegal argument exception occurred in window creation");
        } catch (InvocationTargetException ex) {
            // ex.printStackTrace();
            System.out.println("Target invocation exception occurred in window creation");
        }
    }

    public Image resizeImage(String imagePath) throws IOException {
        BufferedImage img = null;
        img = ImageIO.read(new File(imagePath));
        Image dimg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        return dimg;
    }

    public void notificationTrayCreation(ArrayList<ResponseData> resps, int numNotification, String imagePath, String clickLogPath, String closeIconPath) throws IOException {

        final JFrame f = new JFrame();
        f.setUndecorated(true);
        f.setSize(500, 300);
        f.setLocation(860, 400);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setAlwaysOnTop(true);

        int d = 0;
        JPanel p = new JPanel();
        p.setLayout(null);
        p.setBackground(new Color(154, 150, 149));

        Image img = resizeImage(imagePath);
        ImageIcon icon = new ImageIcon(img);
        JLabel l1 = new JLabel(icon);
        l1.setBounds(10, 10, 90, 50);
        JLabel l2 = new JLabel();
        l2.setText("Hi, I am your proactive agent!!");
        l2.setForeground(Color.white);
        l2.setBounds(75, 10, 190, 50);

        JLabel l3 = new JLabel();
        l3.setText("You may find following documents useful for you task..");
        l3.setForeground(Color.white);
        l3.setBounds(10, 50, 350, 50);
        p.add(l1);
        p.add(l2);
        p.add(l3);
        f.add(p);

        int d1 = 30;
        if (numNotification > resps.size()) {
            numNotification = resps.size();
        }
        Image closeImg = resizeImage(closeIconPath);
        ImageIcon closeIcon = new ImageIcon(closeImg);
        JButton b4 = new JButton(closeIcon);
        b4.setBackground(new Color(210, 219, 230));
        b4.setForeground(Color.BLACK);
        b4.setBorderPainted(false);

        //b4.setBorder(BorderFactory.createEmptyBorder());
        b4.setBounds(450, 30, 40, 35);
        p.add(b4);
        final String clickPath = clickLogPath;
        b4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileWriter fw = null;
                try {
                    fw = new FileWriter(new File(clickPath), true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    Calendar c = Calendar.getInstance();
                    bw.write("closed" + " " + c.getTime().toString());
                    bw.newLine();
                    bw.close();
                } catch (Exception ex) {
                    Logger.getLogger(Translucent.class.getName()).log(Level.SEVERE, null, ex);
                }

                f.dispose();
            }
        });
        d1 = 15;
        for (int i = 0; i < numNotification; i++) {
            FontMetrics metrics = getFontMetrics(getFont());
            int width = metrics.stringWidth(resps.get(0).Snippet);
            int width1 = metrics.stringWidth(resps.get(i).title);
            final CustomButton b3 = new CustomButton(resps.get(i).docId);
            b3.setOpaque(false);
            b3.setContentAreaFilled(false);
            b3.setBorder(new LineBorder(Color.BLACK));

            Font ff = new Font("Courier New", Font.BOLD, 14);
            b3.setFont(ff);
            Map attributes = b3.getFont().getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            String title = "";
            String tokens[] = resps.get(i).title.split("\\s+");
            if (tokens.length > 6) {
                for (int j = 0; j < 6; j++) {
                    title += tokens[j] + " ";
                }
                width1 = metrics.stringWidth(title);
                b3.setBounds(20, 100 + d, width1 + 180, 15);
            } else {
                title = resps.get(i).title;
                b3.setBounds(20, 100 + d, width1 + 180, 15);
            }
            b3.setText(title);
            JLabel l = new JLabel();
            l.setText(resps.get(i).Snippet);

            l.setFont(new Font("Courier New", Font.ITALIC, 12));
            l.setForeground(Color.BLACK);
            l.setBackground(Color.red);
            resps.get(i).Snippet = resps.get(i).Snippet.trim();
            System.out.println(resps.get(i).Snippet);
            tokens = resps.get(i).Snippet.split("\\s+");
            String snippet = "<html>";
            String text = "";
            if (tokens.length > 4) {
                System.out.println("here more than 4");
                int size = 8;
                if (tokens.length < size) {
                    size = tokens.length;
                }
                for (int j = 0; j < size; j++) {
                    if (j % 4 == 0 && j > 0) {
                        snippet += tokens[j] + "<br>";
                    } else {
                        snippet += tokens[j] + " ";
                    }
                }
                for (int j = 0; j < 4; j++) {
                    text += tokens[j] + " ";
                }
                snippet += "</html>";
                //System.out.println(snippet);
                width = metrics.stringWidth(text);
            } else {
                System.out.println("less than 8");
                width = metrics.stringWidth(resps.get(i).Snippet);
                snippet = resps.get(i).Snippet;
            }
            //snippet = tokens[0];
            //System.out.println(snippet);
            l.setBounds(20, 100 + d1, 350, 40);
            //snippet = "hello world";
            l.setText(snippet);
            //d1 = d1 + 90;
            b3.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        FileWriter fw = new FileWriter(new File(clickPath), true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        Calendar c = Calendar.getInstance();
                        bw.write(b3.title + " " + c.getTime().toString());
                        bw.newLine();
                        bw.close();
                        Desktop.getDesktop().browse(new URL("http://clueweb.adaptcentre.ie/WebSearcher/view?docid=" + b3.title).toURI());
                    } catch (Exception ex) {
                        Logger.getLogger(Translucent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            p.add(b3);
            p.add(l);
            // d = d + 60;
            d = d + 15 + 40 + 10;
            d1 = d1 + 65;
        }
        f.setVisible(true);
    }
}

class ResponseData {

    String title;
    String Snippet;
    String docId;
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

    public int getTimeDifference(TimeStamp t) {
        if (!this.year.equals(t.year) || !this.day.equals(t.day) || !this.month.equals(t.month)) {
            return 24 * 60 * 60;
        } else {

            int thisTime = Integer.parseInt(this.hour) * 3600 + Integer.parseInt(this.minute) * 60 + Integer.parseInt(this.second);

            int thatTime = Integer.parseInt(t.hour) * 3600 + Integer.parseInt(t.minute) * 60 + Integer.parseInt(t.second);
            int diff = thisTime - thatTime;
            return diff;
        }
    }

    public String toString() {
        return String.format("Date: " + day + " " + month + " " + year + "  Time: " + hour + ":" + minute + ":" + second);
    }

    public void convertTimeStamp() {
        if (month.equals("Jan")) {
            month = "01";
        } else if (month.equals("Feb")) {
            month = "02";
        } else if (month.equals("Mar")) {
            month = "03";
        } else if (month.equals("Apr")) {
            month = "04";
        } else if (month.equals("May")) {
            month = "05";
        } else if (month.equals("Jun")) {
            month = "06";
        } else if (month.equals("Jul")) {
            month = "07";
        } else if (month.equals("Aug")) {
            month = "08";
        } else if (month.equals("Sep")) {
            month = "09";
        } else if (month.equals("Oct")) {
            month = "10";
        } else if (month.equals("Nov")) {
            month = "11";
        } else if (month.equals("Dec")) {
            month = "12";
        }

    }
}

public class ReadKeyStrokeLog {

    ArrayList<String> wordList;
    String keyLogFile;
    String imagePath;
    String closeIconpath;
    String clickPath;
    double freqThreshold;
    int activityLogThreshold;
    int activityLogQueryWord;

    public ReadKeyStrokeLog() throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        prop.load(new FileReader(new File("init.properties")));
        keyLogFile = prop.getProperty("KeyLogFile");
        imagePath = prop.getProperty("img");
        clickPath = prop.getProperty("click");
        wordList = new ArrayList<>();
        freqThreshold = Double.parseDouble(prop.getProperty("relThresh"));
        closeIconpath = prop.getProperty("close");
        activityLogThreshold = Integer.parseInt(prop.getProperty("activityLogThreshold"));
        activityLogQueryWord = Integer.parseInt(prop.getProperty("activityLogQueryWord"));
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
        wordList.add("\\[ESCAPE]");
        wordList.add("\\[END]");
        wordList.add("\\[HOME]");
        wordList.add("\\[CAPSLOCK]");
    }

    public TimeStamp createTimeStampForUbuntu(int diff) {
        Calendar c = Calendar.getInstance();
        String curTime = c.getTime().toString();
        String st[] = curTime.split("\\s+");
        String s = " at ";
        s += st[0] + " " + st[1] + " " + st[2] + " " + st[3] + " " + st[5];
        TimeStamp t = new TimeStamp(s);
        int minute = Integer.parseInt(st[3].split(":")[1]);
        if (minute > diff) {
            minute -= diff;
            t.minute = String.valueOf(minute);
        }
        return t;
    }

    public ArrayList<wordObject> reverseKeyLogFileForUbuntu() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(new File(keyLogFile));
        BufferedReader br = new BufferedReader(fr);

        HashMap<String, wordObject> wordMap = new HashMap<>();
        String line = br.readLine();
        String typed_words = "";
        ArrayList<String> words = new ArrayList<>();
        while (line != null) {
            if (line.equals("Return")) {
                words.add(typed_words);
                typed_words = "";
            } else {
                typed_words += line;
            }

            line = br.readLine();
        }
        words.add(typed_words);
        ArrayList<String> modifiedWords = new ArrayList<>();
        int diff = 50;
        for (String s : words) {
            s = s.replaceAll("Up", "");
            s = s.replaceAll("Down", "");
            s = s.replaceAll("Control_L", "");
            s = s.replaceAll("Shift_L", "");
            s = s.replaceAll("space", " ");
            s = s.replaceAll("BackSpace", "");
            s = s.replaceAll("_", "");
            s = s.replaceAll("period", " ");
            s = s.replaceAll("minus", " ");
            s = s.replaceAll("slash", " ");
            s = s.replaceAll("Rquestion", " ");
            s = s.replaceAll("Rcolon", " ");
            s = s.replaceAll("equal", " ");
            s = s.replaceAll("Tab", "");
            System.out.println(s);
            modifiedWords.add(s);
            wordObject wob = new wordObject();
            if (!s.equals("")) {
                String st[] = s.split("\\s+");
                for (String word : st) {
                    if (wordMap.containsKey(word)) {
                        wob = wordMap.get(word);
                        wob.tf++;
                        wordMap.put(word, wob);
                    } else {
                        wob.word = word;
                        wob.tf = 1;
                        wob.timestamp = createTimeStampForUbuntu(diff);
                        diff--;
                        wordMap.put(word, wob);
                    }
                }
            }
        }
        ArrayList<wordObject> wList = new ArrayList<>();
        Iterator it = wordMap.keySet().iterator();
        while (it.hasNext()) {
            String st = (String) it.next();
            wList.add(wordMap.get(st));
        }
        return wList;
    }

    public List<wordObject> reverseKeyStrokeFileRead() throws IOException {
        ReversedLinesFileReader object = null;
        int count = 0;
        HashSet<String> words = new HashSet<>();
        HashMap<String, wordObject> wordMap = new HashMap<>();
        String prevLine = "";
        try {
            object = new ReversedLinesFileReader(new File(keyLogFile));
            WriteObject wob = null;
            String line = object.readLine();
            while (object != null) {
                if (line != null) {
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
                        if (wob.typed_words.trim().length() > 5) {

                            st = wob.typed_words.split("\\s+");
                            for (String s : st) {
                                if (s.length() > 2 && s.length() <= 15) {
                                    if (!wordMap.containsKey(s)) {
                                        wordObject wobb = new wordObject();
                                        wobb.word = s;
                                        wobb.tf = 1;
                                        wobb.timestamp = wob.timeStamp;
                                        wordMap.put(s, wobb);
                                    } else {
                                        wordObject wobb = wordMap.get(s);
                                        wobb.tf++;
                                        wordMap.put(s, wobb);
                                    }
                                }
                            }
                        }
                        if (wob.windowTitle.equals("WriteTopic") || wob.windowTitle.equals(" Clueweb Search Interface")) {
                            wob.windowTitle = "NA";
                        }
                        st = wob.windowTitle.split("\\s+");

                        for (String s : st) {
                            if (s.length() > 2 && s.length() <= 16) {
                                if (!wordMap.containsKey(s)) {
                                    wordObject wobb = new wordObject();
                                    wobb.word = s;
                                    wobb.tf = 1;
                                    wobb.timestamp = wob.timeStamp;
                                    wordMap.put(s, wobb);
                                } else {
                                    wordObject wobb = wordMap.get(s);
                                    wobb.tf++;
                                    wordMap.put(s, wobb);
                                }
                            }
                        }
                    } else {
                        prevLine += line + " ";
                    }
                    wob = null;
                    count++;
                }
                if (count == activityLogThreshold) {
                    break;
                }
                line = object.readLine();
            }
        } catch (Exception e) {
            System.out.println("Exception occurred while reading keyLog file");
            if (wordMap.size() == 0) {
                return null;
            }

        }
        object.close();

        Iterator it = wordMap.keySet().iterator();
        ArrayList<wordObject> values = new ArrayList<>();
        while (it.hasNext()) {
            String st = (String) it.next();
            //System.out.println(st + " " + wordMap.get(st).timestamp);
            double d = computeIdf(st);
            if (d > 0) {
                values.add(wordMap.get(st));
            }
        }
        Collections.sort(values, Collections.reverseOrder());
        return values;
    }

    public double computeIdf(String word) throws MalformedURLException, IOException {

        double idf = 0;
        URL url = new URL("http://clueweb.adaptcentre.ie/CluwebDocFreq/?word=" + URLEncoder.encode(word, "UTF-8"));
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String all = "";
            String line;
            while ((line = in.readLine()) != null) {
                all += line;
            }
            idf = Double.parseDouble(all.substring(all.indexOf("</title>") + 8, all.indexOf("</head>")).trim());
            if (idf != 0) {
                idf = Math.log(13719681 / idf);
            }
        } catch (Exception e) {
        }

        return idf;
    }

    public String throwNotification(ArrayList<wordObject> words, ArrayList<relObject> relWords, ArrayList<relObject> readWords, int num, int numDoc, String prevQuery, int numR) throws MalformedURLException, IOException, Exception {

        String notitficationLine = "";
        Collections.sort(relWords, Collections.reverseOrder());
        int count = 0;
        for (int i = 0; i < relWords.size(); i++) {
            String word = relWords.get(i).word;
            notitficationLine += " " + relWords.get(i).word + ":2";
            count++;
            if (count == num) {
                break;
            }

        }
        if (readWords.size() > 0) {
            count = 0;
            for (int i = 0; i < readWords.size(); i++) {
                if (!notitficationLine.contains(readWords.get(i).word + ":2")) {
                    notitficationLine += " " + readWords.get(i).word + ":3";
                    count++;
                    if (count == numR) {
                        break;
                    }
                }
            }

        }
        if (words != null) {
            int size = activityLogQueryWord;
            if (size > words.size()) {
                size = words.size();
            }
            for (int i = 0; i < size; i++) {
                notitficationLine += " " + words.get(i).word + ":5";
            }
        }

        System.out.println("Proactive Query: " + notitficationLine);
        if (prevQuery.equals(notitficationLine)) {
            System.out.println("Same query formulated...");
            return notitficationLine;
        }
        ArrayList<ResponseData> resps = createRankedListUsingClueweb(notitficationLine, numDoc);
        // ArrayList<ResponseData> resps = createRankedListUsingClueweb("oversea:2", numDoc);
        System.out.println("Clueweb results returned for proactive query!!");
        if (resps.size() == 0) {
            System.out.println("Nothing found from the proactive query!!");
            return notitficationLine;
        }
        //ArrayList<ResponseData> resps = createRankedListUsingClueweb("oversea:6 india:5 govern:4 param:1", numDoc);
        ArrayList<ResponseData> notifyList = new ArrayList<>();
        if (resps.size() > 0) {
            if (numDoc > resps.size()) {
                numDoc = resps.size();
            }
            for (int i = 0; i < numDoc; i++) {
                notifyList.add(resps.get(i));
            }
            Translucent t = new Translucent();
            t.notificationTrayCreation(notifyList, numDoc, imagePath, clickPath, closeIconpath);
        }
        return notitficationLine;
    }

    public ArrayList<ResponseData> createRankedListUsingClueweb(String s, int num) throws UnsupportedEncodingException, MalformedURLException, IOException {
        String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
        String param1 = s;
        ArrayList<String> docIdList = new ArrayList<>();
        ArrayList<ResponseData> resps = new ArrayList<ResponseData>();
        //URL url = new URL("http://clueweb.adaptcentre.ie/WebSearcher/search?query=" + URLEncoder.encode(param1, charset));
        URL url = new URL("http://clueweb.adaptcentre.ie/CluwebDocFreq/Query.jsp?query=" + URLEncoder.encode(param1, charset));

        String all = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                all += line;
            }
            all = all.substring(all.indexOf("["), all.indexOf("</body>")).trim();
            JSONObject jobJSONObject = new JSONObject();
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(all);
            int count = 0;
            if (num > jsonArray.size()) {
                num = jsonArray.size();
            }
            for (int j1 = 0; j1 < num; j1++) {
                ResponseData rpd = new ResponseData();
                JSONArray job = (JSONArray) (jsonArray.get(j1));
                JSONObject job1 = (JSONObject) job.get(0);
                String Snippet = (String) job1.get("snippet");
                String title = (String) job1.get("title");
                Snippet = Snippet.replaceAll("\n", "");
                Snippet = Snippet.replaceAll("\t", "");
                Snippet = Snippet.replaceAll("\\\\", "");
                Snippet = Snippet.replaceAll("<B>", "");
                Snippet = Snippet.replaceAll("</B>", "");
                String tokens[] = Snippet.split("\\s+");
                String text = "";
                if (tokens.length > 16) {
                    for (int i = 0; i < 16; i++) {
                        text += tokens[i] + " ";
                    }
                    rpd.Snippet = Snippet;
                } else {
                    rpd.Snippet = Snippet;
                }
                rpd.title = title;

                rpd.docId = (String) job1.get("id");
                resps.add(rpd);
            }
        } catch (Exception e) {
            System.out.println("Exception occurred while calling Clueweb Search API");
            System.out.println(param1);
            e.printStackTrace();
        }
        return resps;
    }

    public void createRankedListUsingGoogle(String s, int num) throws IOException {
        String GOOGLE_SEARCH_URL = "https://www.google.com";
        String searchURL = GOOGLE_SEARCH_URL + "?q=" + s + "&num=" + num;
        Document doc = (Document) Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();

    }

    public HashMap<String, relObject> processHtml(String html, String stopFile) throws IOException, SAXException, TikaException, Exception {
        InputStream input = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
        ContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        new HtmlParser().parse(input, handler, metadata, new ParseContext());
        String title = metadata.get("title");
        System.out.println("Doc Read " + title);
        HashMap<String, relObject> docStat = preprocessText(html, stopFile);

        CharArraySet stopList = StopFilter.makeStopSet(buildStopwordList(stopFile));
        TokenStream stream = constructAnalyzer(stopFile).tokenStream("field", new StringReader(title));
        CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
        stream.reset();
        while (stream.incrementToken()) {
            String token = termAtt.toString();
            token = processToken(token);
            relObject wob = new relObject();
            wob.word = token;
            wob.tf = 1.0;
            docStat.put(token, wob);
        }
        return docStat;
    }

    public String processToken(String word) {

        word = word.replaceAll(";", "");
        word = word.replaceAll(",", "");
        word = word.replaceAll("style", "");
        word = word.replaceAll("div", "");
        word = word.replaceAll("span", "");
        word = word.replaceAll("script", "");
        word = word.replaceAll("html", "");
        word = word.replaceAll("[0-9]*", "");
        word = word.replaceAll(":", "");
        word = word.replaceAll("\\.", " ");
        word = word.replaceAll("_", " ");
        word = word.replaceAll("href", "");
        word = word.replaceAll("http", "");
        word = word.replaceAll("class", "");
        word = word.replaceAll("ww*", "");
        word = word.trim();
        if (word.length() <= 2 || word.length() > 20) {
            word = "";
        }
        try {
            Double number = Double.parseDouble(word);
            word = "";
        } catch (Exception e1) {

        }
        return word;
    }

    HashMap<String, relObject> preprocessText(String html, String stopFile) throws IOException, Exception {

        int freqCutoffThreshold = -1;
        HashMap<String, Integer> tfMap = new HashMap<>();
        StringBuffer buff = new StringBuffer();
        CharArraySet stopList = StopFilter.makeStopSet(buildStopwordList(stopFile));
        TokenStream stream = constructAnalyzer(stopFile).tokenStream("field", new StringReader(html));
        CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);

        int maxFreq = 0;
        stream.reset();
        double docLength = 0;
        while (stream.incrementToken()) {
            String token = termAtt.toString();
            token = processToken(token);
            docLength++;
            if (token.equals("")) {
                continue;
            }
            Integer tf = tfMap.get(token);
            if (tf == null) {
                tf = new Integer(0);
            }
            tf++;
            if (maxFreq < tf) {
                maxFreq = tf;
            }
            tfMap.put(token, tf);
        }
        stream.end();
        stream.close();

        HashMap<String, relObject> relWords = new HashMap<>();
        for (Map.Entry<String, Integer> e : tfMap.entrySet()) {
            String word = e.getKey();
            relObject rob = new relObject();
            rob.word = word;
            rob.tf = e.getValue() / docLength;
            if (rob.tf > freqThreshold) {
                relWords.put(word, rob);
            }
        }
        return relWords;
    }

    public HashMap<String, relObject> getContent(File f, String stopFile) throws FileNotFoundException, IOException, TikaException, Exception {
        String line = "";
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        line = br.readLine();
        String htmlText = "";
        while (line != null) {
            htmlText += line;
            line = br.readLine();
        }
        String title = "";
        return processHtml(htmlText, stopFile);
    }

    public ArrayList<relObject> getValues(HashMap<String, relObject> relMap) throws IOException {
        Iterator it = relMap.keySet().iterator();
        ArrayList<relObject> values = new ArrayList<>();

        while (it.hasNext()) {
            String st = (String) it.next();
            values.add(relMap.get(st));

        }
        Collections.sort(values, Collections.reverseOrder());
        for (int i = 0; i < values.size(); i++) {
            values.get(i).tf *= computeIdf(values.get(i).word);
        }
        Collections.sort(values, Collections.reverseOrder());
        return values;
    }

    public ArrayList<relObject> readRelDocsForFolder(String relDocPath, String stopFile) throws FileNotFoundException, IOException, TikaException, Exception {

        File dir = new File(relDocPath);
        File[] directoryListing = dir.listFiles();

        HashMap<String, relObject> relMap = new HashMap<>();
        for (File f : directoryListing) {
            if (f.getName().endsWith(".html")) {
                relMap.putAll(getContent(f, stopFile));
            }
        }
        return getValues(relMap);
    }

    public ArrayList<relObject> readRelDocs(String relDocPath, String stopFile) throws FileNotFoundException, IOException, TikaException, Exception {

        File f = new File(relDocPath);

        HashMap<String, relObject> relMap = new HashMap<>();
        if (f.getName().endsWith(".html")) {
            relMap.putAll(getContent(f, stopFile));
        }
        return getValues(relMap);
    }

    public String getHtmlString(String docId) throws UnsupportedEncodingException, MalformedURLException, IOException {
        String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
        String param1 = docId;
        URL url = new URL("http://clueweb.adaptcentre.ie/WebSearcher/view?docid=" + URLEncoder.encode(param1, charset));
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String all = "";
        String line;
        while ((line = in.readLine()) != null) {
            all += line;
        }
        return all;

    }

    Analyzer constructAnalyzer(String stopFile) throws Exception {
        Analyzer eanalyzer = new EnglishAnalyzer(StopFilter.makeStopSet(buildStopwordList(stopFile))); // default analyzer
        return eanalyzer;
    }

    public List<String> buildStopwordList(String stopwordFileName) {
        List<String> stopwords = new ArrayList<>();
        String stopFile = stopwordFileName;
        String line;

        try (FileReader fr = new FileReader(stopFile);
                BufferedReader br = new BufferedReader(fr)) {
            while ((line = br.readLine()) != null) {
                stopwords.add(line.trim());
            }
            br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stopwords;
    }

    Query buildQuery(String queryStr) throws Exception {
        BooleanQuery q = new BooleanQuery();
        Term thisTerm = null;
        Query tq = null;
        String[] queryWords = queryStr.split("\\s+");

        // search in title and content...
        for (String term : queryWords) {
            thisTerm = new Term("words", term);
            tq = new TermQuery(thisTerm);
            q.add(tq, BooleanClause.Occur.SHOULD);
        }
        return q;
    }

    String getSnippet(String query, String docid) throws Exception {
        StringBuffer buff = new StringBuffer();
        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
        Query q = buildQuery("dog");
        String html = getHtmlString(docid);
        InputStream input = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
        ContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        new HtmlParser().parse(input, handler, metadata, new ParseContext());
        String text = handler.toString();
        Analyzer analyzer = new EnglishAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream("dummy", new StringReader(text));
        String snippet = text.substring(0, 200);
        String modifiedText = snippet;

        String pattern = "<(\\s*)[a-zA-Z0-9]+[^>]+$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(snippet);
        if (m.find()) {
            modifiedText = m.replaceAll("");
        }
        snippet = modifiedText;
        return snippet;
    }

    public static void main(String[] args) throws IOException, InterruptedException, Exception {

        ReadKeyStrokeLog rkl = new ReadKeyStrokeLog();
        rkl.addKeyword();
        rkl.readRelDocs("C:/Users/Procheta/Desktop/relFolder/", "C:/Users/Procheta/Documents/NetBeansProjects/DesktopTracker/stop.txt");
    }
}
