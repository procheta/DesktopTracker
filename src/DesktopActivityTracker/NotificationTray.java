/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DesktopActivityTracker;

/**
 *
 * @author Procheta
 */
import AdditionalComponents.FileAccess;
import ProcessActivityLog.ProcessLog;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class NotificationTray {

    public static void main(String[] args) throws InterruptedException, FileNotFoundException, IOException, Exception {
        /* Use an appropriate Look and Feel */

        Properties prop = new Properties();
        prop.load(new FileReader(new File("init.properties")));

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        //Schedule a job for the event-dispatching thread:
        //adding TrayIcon.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
        /* ProcessTrigger pr = new ProcessTrigger();
        String processPath = prop.getProperty("process");
        pr.loadProcess(processPath);
        System.out.println("Process Loaded");*/

        FileAccess fa = new FileAccess();
        int numDoc = Integer.parseInt(prop.getProperty("numDoc"));
        int num = Integer.parseInt(prop.getProperty("relLogQueryWord"));
        int numR = Integer.parseInt(prop.getProperty("readQueryWord"));
        int interval = Integer.parseInt(prop.getProperty("interval"));
        String os = prop.getProperty("OS");

        System.out.println("Initializing user's prior knowledge state...");
        ReadKeyStrokeLog rkl = new ReadKeyStrokeLog();
        rkl.addKeyword();
        
        File dir = new File(prop.getProperty("relFolder"));
        File[] directoryListing = dir.listFiles();
        ArrayList<String> existingFile = new ArrayList<>();
        for (File f : directoryListing) {
            String s = f.getAbsolutePath() + "/" + f.getName();
            existingFile.add(s);
        }
        ArrayList<relObject> relWords = rkl.readRelDocsForFolder(prop.getProperty("relFolder"), prop.getProperty("stop"));
        String prevQuery = "";

        while (true) {
            ArrayList<String> filesAccessed = fa.check(prop.getProperty("accessFolder"), prop.getProperty("AccessLog"));
            ArrayList<relObject> readWords = new ArrayList<>();
            if (filesAccessed.size() > 0) {
                for (String s : filesAccessed) {
                    if (!existingFile.contains(s)) {
                        ArrayList<relObject> accessWords = rkl.readRelDocs(s, prop.getProperty("stop"));
                        readWords.addAll(accessWords);
                    }
                }
            }
            System.out.println(filesAccessed);
            System.out.println("Access Folder checked");
            ArrayList<wordObject> words = null;
            try {
                if (os.equals("Windows")) {
                    words = (ArrayList<wordObject>) rkl.reverseKeyStrokeFileRead();
                } else if (os.equals("Ubuntu")) {
                    words = rkl.reverseKeyLogFileForUbuntu();
                }
                System.out.println("Activity log Read Complete");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception in reverse activity log reading");
            }
            String currQuery = rkl.throwNotification(words, relWords,readWords, num, numDoc, prevQuery,numR);
            prevQuery = currQuery;
            Thread.sleep(interval);
        }
    }

    private static void createAndShowGUI() {
        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon
                = new TrayIcon(createImage("images/bulb.gif", "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a popup menu components
        MenuItem exitItem = new MenuItem("Exit");

        //Add components to popup menu
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }

        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "This dialog box is run from System Tray");
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                Properties prop = new Properties();
                try {
                    prop.load(new FileReader(new File("init.properties")));
                    ProcessLog pl = new ProcessLog(prop);
                    pl.processActivityLog();
                } catch (Exception ex) {
                    Logger.getLogger(NotificationTray.class.getName()).log(Level.SEVERE, null, ex);
                }
                /*
                String processNum = prop.getProperty("processNum");
                ProcessTrigger pr = new ProcessTrigger();
                pr.stopProcess(processNum);*/

                System.exit(0);
            }
        });
    }

    //Obtain the image URL
    protected static Image createImage(String path, String description) {
        URL imageURL = NotificationTray.class.getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}
