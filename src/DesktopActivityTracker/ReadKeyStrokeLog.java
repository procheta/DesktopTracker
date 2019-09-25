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
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
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
import java.io.Reader;
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
import java.util.Date;
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
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.xml.sax.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;

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

    public Image resizeImage(String imagePath) throws IOException {
        BufferedImage img = null;
        img = ImageIO.read(new File(imagePath));
        Image dimg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        return dimg;
    }

    public void notificationTrayCreation(ArrayList<String> docIdList, ArrayList<String> summaryList, ArrayList<String> titleList, int numNotification,String imagePath, String clickLogPath) throws IOException {
        FontMetrics metrics = getFontMetrics(getFont());
        int width = metrics.stringWidth(summaryList.get(0));
        int width1 = metrics.stringWidth(docIdList.get(0));
        final JFrame f = new JFrame();
        f.setUndecorated(true);
        f.setSize(500, 300);
        f.setLocation(800, 300);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setAlwaysOnTop(true);

        int d = 0;
        JPanel p = new JPanel();
        p.setLayout(null);
        p.setBackground(new Color(210, 219, 230));

        Image img = resizeImage(imagePath);
        //Image img = resizeImage("agent.jpg");
        ImageIcon icon = new ImageIcon(img);
        JLabel l1 = new JLabel(icon);
        l1.setBounds(20, 30, 90, 50);
        JLabel l2 = new JLabel();
        l2.setText("Proactive Agent");
        l2.setBounds(100, 30, 90, 50);
        p.add(l1);
        p.add(l2);
        f.add(p);

        int d1 = 20;
        if (numNotification > docIdList.size()) {
            numNotification = docIdList.size();
        }
        JButton b4 = new JButton("X");
        b4.setBackground(new Color(210, 219, 230));
        b4.setForeground(Color.BLACK);
        b4.setBorderPainted(false);
        b4.setText("CLOSE");

        b4.setBorder(BorderFactory.createEmptyBorder());
        b4.setBounds(350, 30, 120, 30);
        p.add(b4);
        final String  clickPath = clickLogPath;
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

        for (int i = 0; i < numNotification; i++) {
            final CustomButton b3 = new CustomButton(docIdList.get(i));
            b3.setOpaque(false);
            b3.setContentAreaFilled(false);
            b3.setBorder(new LineBorder(Color.BLACK));
            b3.setBounds(20, 100 + d, width1 + 180, 20);

            Font ff = new Font("Courier New", Font.BOLD, 14);
            b3.setFont(ff);
            Map attributes = b3.getFont().getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            b3.setText(titleList.get(i));
            JLabel l = new JLabel();
            l.setText(summaryList.get(i));
            l.setFont(new Font("Courier New", Font.ITALIC, 12));
            l.setForeground(Color.BLACK);
            l.setBounds(20, 100 + d1, width + 50, 30);
            d1 = d1 + 40;
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
            d = d + 40;
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

    public HashSet<String> reverseKeyStrokeFileRead() throws IOException {
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
            return words;
        } 
        object.close();
        return words;
    }

    public void throwNotification(HashSet<String> words, int num,String imagePath, String clickPath) throws MalformedURLException, IOException, Exception {

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
        ArrayList<String> docIdList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<ResponseData> resps = createRankedListUsingClueweb("java program", 3);

        for (int i = 0; i < 3; i++) {
            String docId = resps.get(i).docId;
            docIdList.add(docId);
            //summaryList.add(resps.get(i).Snippet);
            summaryList.add("Hello World. It is a proactive software. ");
            titleList.add(resps.get(i).title);
        }
        // notitficationLine = "<html>" + notitficationLine + "<br/>" + notitficationLine + "<br/>" + "<a href='https://google.com'>urlllllllllllllllllllllllllllllllllllll</a>" + "</html>";
        Translucent t = new Translucent();
        t.notificationTrayCreation(docIdList, summaryList, titleList, 3,imagePath,clickPath);
    }

    public ArrayList<ResponseData> createRankedListUsingClueweb(String s, int num) throws UnsupportedEncodingException, MalformedURLException, IOException {
        String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
        String param1 = s;
        ArrayList<String> docIdList = new ArrayList<>();
        ArrayList<ResponseData> resps = new ArrayList<ResponseData>();
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
            if (num > jsonArray.size() - 1) {
                num = jsonArray.size() - 1;
            }
            for (int j1 = 0; j1 < num; j1++) {
                ResponseData rpd = new ResponseData();
                String st1 = ((Object) jsonArray.get(j1)).toString();
                String st2 = st1.replace("[", "");
                st2 = st2.replace("]", "");
                char d = '"';
                String st4 = st2.substring(st2.indexOf("id" + d), st2.length());
                String title = st2.substring(st2.indexOf("title") + 8, st2.indexOf("url") - 3);
                String Snippet = st2.substring(st2.indexOf("snippet") + 10, st2.indexOf("id") - 3);
                Snippet = Snippet.replaceAll("n", "");
                Snippet = Snippet.replaceAll("t", "");
                Snippet = Snippet.replaceAll("\\\\", "");
                Snippet = Snippet.replaceAll("<B>", "");
                Snippet = Snippet.replaceAll("</B>", "");
                rpd.Snippet = Snippet;
                rpd.title = title;

                String st7 = st4;
                try {
                    st4 = st4.substring(0, st4.indexOf(","));
                    String st3[] = st2.split(",");
                    String st5[] = st4.split(":");
                    st5[1] = st5[1].replace("" + d, "");
                    docIdList.add(st5[1]);
                    rpd.docId = st5[1];
                } catch (Exception e) {
                    st7 = st7.substring(st7.indexOf(",") + 1, st7.length());
                    String st8 = st7.substring(0, st7.indexOf(","));
                    String st5[] = st8.split(":");
                    st5[1] = st5[1].replace("" + d, "");
                    docIdList.add(st5[1]);
                    rpd.docId = st5[1];
                }
                resps.add(rpd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(resps.size());
        return resps;
    }

    public void createRankedListUsingGoogle(String s, int num) throws IOException {
        String GOOGLE_SEARCH_URL = "https://www.google.com";
        String searchURL = GOOGLE_SEARCH_URL + "?q=" + s + "&num=" + num;
        Document doc = (Document) Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();

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
        HashSet<String> words = rkl.reverseKeyStrokeFileRead();
    }
}
