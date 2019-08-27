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
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class NotificationTray {

    public static void main(String[] args) throws InterruptedException {
        /* Use an appropriate Look and Feel */

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
        ProcessTrigger pr = new ProcessTrigger();
        pr.loadProcess();

        while (true) {
            ReadKeyStrokeLog rkl = new ReadKeyStrokeLog();
            rkl.addKeyword();
            HashSet<String> words = rkl.reverseKeyStrokeFileRead(new File("C:/Users/Procheta/Desktop/System32Log.txt"));
            rkl.throwNotification(words);
            Thread.sleep(10000);
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
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                ProcessTrigger pr = new ProcessTrigger();
                pr.stopProcess();
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
