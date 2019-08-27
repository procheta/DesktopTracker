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
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.apache.commons.io.input.ReversedLinesFileReader;

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

    public void notificationTrayCreation(String s, int numNotification) {

        FontMetrics metrics = getFontMetrics(getFont());
        int width = metrics.stringWidth(s);
        final JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setAlwaysOnTop(true);
        f.setSize(500, 500);
        f.setLocation(100, 100);
        f.setVisible(true);
        int d = 0;
        JPanel p = new JPanel();
        p.setLayout(null);
        f.add(p);
        
        for (int i = 0; i < numNotification; i++) {
            JButton b3 = new JButton("X");
            b3.setBackground(Color.CYAN);
            b3.setBorderPainted(false);
            b3.setBorder(BorderFactory.createEmptyBorder());           
            b3.setBounds(100, 100 + d, width+50, 20);
            b3.setText(s);
            b3.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URL("http://www.google.com").toURI());
                    } catch (Exception ex) {
                        Logger.getLogger(Translucent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            p.add(b3);
            d = d + 80;
        }
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
        String chunks[] = line.split("-");
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
        try {
            chunks[chunks.length - 1] = chunks[chunks.length - 1].substring(0, chunks[chunks.length - 1].lastIndexOf("]"));
            timeStamp = new TimeStamp(chunks[chunks.length - 1]);
        } catch (Exception e) {
            System.out.println(line);
        }
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

    public HashSet<String> reverseKeyStrokeFileRead(File file) {
        ReversedLinesFileReader object = null;
        int count = 0;
        HashSet<String> words = new HashSet<>();
        ArrayList<TimeStamp> times = new ArrayList<>();
        ArrayList<String> windowTitle = new ArrayList<>();
        ArrayList<String> app = new ArrayList<>();
        String prevLine = "";
        try {
            object = new ReversedLinesFileReader(file);
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
                    //System.out.println(wob);
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
                // }
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
        System.out.println(words);
        // System.out.println(times);
        // System.out.println(windowTitle);
        // System.out.println(app);
        return words;
    }

    public void throwNotification(HashSet<String> words) {

        String notitficationLine = "";
        Iterator it = words.iterator();
        int count = 0;
        while (it.hasNext()) {
            String word = (String) it.next();
            notitficationLine += word + " ";
            count++;
            if (count == 5) {
                break;
            }
        }
       // notitficationLine = "<html>" + notitficationLine + "<br/>" + notitficationLine + "<br/>" + "<a href='https://google.com'>urlllllllllllllllllllllllllllllllllllll</a>" + "</html>";
        System.out.println(notitficationLine);
        Translucent t = new Translucent();
        t.notificationTrayCreation(notitficationLine,3);
    }

    public void runMethod() throws InterruptedException {
        while (true) {
            ReadKeyStrokeLog rkl = new ReadKeyStrokeLog();
            HashSet<String> words = rkl.reverseKeyStrokeFileRead(new File("C:/Users/Procheta/Desktop/System32Log.txt"));
            rkl.throwNotification(words);
            Thread.sleep(20000);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // while (true) {
        ReadKeyStrokeLog rkl = new ReadKeyStrokeLog();
        rkl.addKeyword();
        HashSet<String> words = rkl.reverseKeyStrokeFileRead(new File("C:/Users/Procheta/Desktop/System32Log.txt"));
        rkl.throwNotification(words);
        //   Thread.sleep(20000);
        //}
        //rkl.readKeyLogFile();
    }
}
