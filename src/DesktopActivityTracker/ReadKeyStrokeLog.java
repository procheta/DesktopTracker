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

    @Override
    public int compareTo(wordObject o) {
        return (this.tf - o.tf);
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

    public void notificationTrayCreation(ArrayList<ResponseData> resps, int numNotification, String imagePath, String clickLogPath) throws IOException {
        FontMetrics metrics = getFontMetrics(getFont());
        int width = metrics.stringWidth(resps.get(0).Snippet);
        int width1 = metrics.stringWidth(resps.get(0).docId);
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
        if (numNotification > resps.size()) {
            numNotification = resps.size();
        }
        JButton b4 = new JButton("X");
        b4.setBackground(new Color(210, 219, 230));
        b4.setForeground(Color.BLACK);
        b4.setBorderPainted(false);
        b4.setText("CLOSE");

        b4.setBorder(BorderFactory.createEmptyBorder());
        b4.setBounds(350, 30, 120, 30);
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

        for (int i = 0; i < numNotification; i++) {
            final CustomButton b3 = new CustomButton(resps.get(i).docId);
            b3.setOpaque(false);
            b3.setContentAreaFilled(false);
            b3.setBorder(new LineBorder(Color.BLACK));
            b3.setBounds(20, 100 + d, width1 + 180, 20);

            Font ff = new Font("Courier New", Font.BOLD, 14);
            b3.setFont(ff);
            Map attributes = b3.getFont().getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            b3.setText(resps.get(i).title);
            JLabel l = new JLabel();
            l.setText(resps.get(i).Snippet);
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
    String imagePath;
    String clickPath;

    public ReadKeyStrokeLog() throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        prop.load(new FileReader(new File("init.properties")));
        keyLogFile = prop.getProperty("KeyLogFile");
        imagePath= prop.getProperty("img");
        clickPath = prop.getProperty("click");
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
        wordList.add("\\[ESCAPE]");
        wordList.add("\\[END]");
        wordList.add("\\[HOME]");
        wordList.add("\\[CAPSLOCK]");
    }

    public List<wordObject> reverseKeyStrokeFileRead() throws IOException {
        ReversedLinesFileReader object = null;
        int count = 0;
        HashSet<String> words = new HashSet<>();
        HashMap<String, wordObject> wordMap = new HashMap<>();
        ArrayList<TimeStamp> times = new ArrayList<>();
        ArrayList<String> windowTitle = new ArrayList<>();
        ArrayList<String> app = new ArrayList<>();
        String prevLine = "";
        try {
            object = new ReversedLinesFileReader(new File(keyLogFile));
            WriteObject wob = null;
            while (object != null) {
                String line = object.readLine();
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
                        st = wob.typed_words.split("\\s+");
                        for (String s : st) {
                            if (s.length() > 2) {
                                if (!wordMap.containsKey(s)) {
                                    wordObject wobb = new wordObject();
                                    wobb.word = s;
                                    wobb.tf = 1;
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

                    count++;
                }
                if (count == 200) {
                    break;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            System.out.println("Exception occurred while reading keyLog file");
            return null;
        }
        object.close();

        Iterator it = wordMap.keySet().iterator();
        ArrayList<wordObject> values = new ArrayList<>();
        while (it.hasNext()) {
            String st = (String) it.next();
            values.add(wordMap.get(st));
        }

        return values;
    }

    public void throwNotification(ArrayList<wordObject> words, int num, int numDoc) throws MalformedURLException, IOException, Exception {

        String notitficationLine = "";
        Collections.sort(words,Collections.reverseOrder());
        int count = 0;
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i).word;
            ArrayList<ResponseData> r = createRankedListUsingClueweb(word, 5);
            if (r.size() > 0) {
                notitficationLine += " " + words.get(i).word;
                count++;
            }
            if (count == num) {
                break;
            }
        }
        System.out.println("Proactive Query: "+notitficationLine);
        ArrayList<ResponseData> resps = createRankedListUsingClueweb(notitficationLine, numDoc);
        ArrayList<ResponseData> notifyList = new ArrayList<>();
        if (resps.size() > 0) {
            if(numDoc > resps.size()){
                numDoc = resps.size();
            }
            for (int i = 0; i < numDoc; i++) {
                notifyList.add(resps.get(i));
            }
            Translucent t = new Translucent();
            t.notificationTrayCreation(notifyList, numDoc, imagePath, clickPath);
        }
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
            int count = 0;
            if (num > jsonArray.size() - 1) {
                num = jsonArray.size() - 1;
            }
            for (int j1 = 0; j1 < num; j1++) {
                ResponseData rpd = new ResponseData();
                JSONArray job = (JSONArray) (jsonArray.get(j1));
                JSONObject job1 = (JSONObject) job.get(0);
                String Snippet = (String) job1.get("snippet");
                String title = (String) job1.get("title");
                Snippet = Snippet.replaceAll("n", "");
                Snippet = Snippet.replaceAll("t", "");
                Snippet = Snippet.replaceAll("\\\\", "");
                Snippet = Snippet.replaceAll("<B>", "");
                Snippet = Snippet.replaceAll("</B>", "");
                rpd.Snippet = Snippet;
                rpd.title = title;

                rpd.docId = (String) job1.get("id");
                resps.add(rpd);
            }
        } catch (Exception e) {
            System.out.println("Exception occurred while calling Clueweb Search API");
            //e.printStackTrace();
        }
        return resps;
    }

    public void createRankedListUsingGoogle(String s, int num) throws IOException {
        String GOOGLE_SEARCH_URL = "https://www.google.com";
        String searchURL = GOOGLE_SEARCH_URL + "?q=" + s + "&num=" + num;
        Document doc = (Document) Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();

    }

    public String processHtml(String html, String stopFile) throws IOException, SAXException, TikaException, Exception {
        InputStream input = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
        ContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        new HtmlParser().parse(input, handler, metadata, new ParseContext());
        String title = metadata.get("title");
        String text = preprocessText(html, false, stopFile);
        text = title + "######sssss" + text;
        System.out.println(title);
        return text;
    }

    String preprocessText(String html, boolean title, String stopFile) throws IOException, Exception {

        int freqCutoffThreshold = title ? 1 : -1;
        HashMap<String, Integer> tfMap = new HashMap<>();
        StringBuffer buff = new StringBuffer();
        CharArraySet stopList = StopFilter.makeStopSet(buildStopwordList(stopFile));

        TokenStream stream = constructAnalyzer(stopFile).tokenStream("field", new StringReader(html));
        CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);

        stream.reset();
        while (stream.incrementToken()) {
            String token = termAtt.toString();
            Integer tf = tfMap.get(token);
            if (tf == null) {
                tf = new Integer(0);
            }
            tf++;
            tfMap.put(token, tf);
        }
        stream.end();
        stream.close();

        for (Map.Entry<String, Integer> e : tfMap.entrySet()) {
            String word = e.getKey();
            int tf = e.getValue();
            if (tf >= freqCutoffThreshold) {
                for (int i = 0; i < tf; i++) { // print this word tf times... word order doesn't matter!
                    buff.append(word).append(" ");
                }
            }
        }
        return buff.toString();
    }

    public ArrayList<wordObject> readRelDocs(String relDocPath, String stopFile) throws FileNotFoundException, IOException, TikaException, Exception {
        File dir = new File(relDocPath);
        File[] directoryListing = dir.listFiles();
        HashMap<String, wordObject> wordMap = new HashMap<>();
        for (File f : directoryListing) {
            if (f.getName().endsWith(".html")) {
                String line = "";
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                line = br.readLine();
                String htmlText = "";
                while (line != null) {
                    htmlText += line;
                    line = br.readLine();
                }
                String text = processHtml(htmlText, stopFile);
                String body[] = text.split("######sssss");
                String[] words = body[1].split("\\s+");
                int maxFreq = 0;
                for (String s : words) {
                    if (!s.contains("_") && !s.contains(".")) {
                        if (wordMap.containsKey(s)) {
                            wordObject wob = wordMap.get(s);
                            wob.tf++;
                            if (maxFreq < wob.tf) {
                                maxFreq = wob.tf;
                            }
                            wordMap.put(s, wob);
                        } else {
                            wordObject wob = new wordObject();
                            wob.word = s;
                            wob.tf = 1;
                            if (maxFreq < wob.tf) {
                                maxFreq = wob.tf;
                            }
                            wordMap.put(s, wob);
                        }
                    }
                }
                words = body[0].split("\\s+");
                for (String s : words) {
                    wordObject wob = new wordObject();
                    wob.word = s;
                    wob.tf = maxFreq + 50;
                    wordMap.put(s, wob);
                }
            }
        }

        Iterator it = wordMap.keySet().iterator();
        ArrayList<wordObject> values = new ArrayList<>();
        while (it.hasNext()) {
            String st = (String) it.next();
            values.add(wordMap.get(st));
        }
        return values;
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
        // rkl.addKeyword();
        //HashSet<String> words = rkl.reverseKeyStrokeFileRead();
    }
}
