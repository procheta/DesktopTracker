/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DesktopActivityTracker;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.extended.window.TestFrame;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.separator.WebSeparator;
import com.alee.laf.text.WebTextArea;
import com.alee.laf.window.WebFrame;
import com.alee.managers.style.StyleId;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 *
 * @author Procheta
 */
public class TrayIconDemo {

    public static void main(String[] args) throws AWTException {
        // You should work with UI (including installing L&F) inside Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Initializing WebLaF managers
                // Your application L&F will still be the same as before
                WebLookAndFeel.initializeManagers();
                
                /*final WebFrame frame = new WebFrame(StyleId.frameDecorated, "Dark frame");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setSize(400, 300);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);*/
                final WebPanel panel = new WebPanel ( StyleId.of ( "shadow" ), new VerticalFlowLayout ( true, true ) );
                final WebLabel title = new WebLabel ( StyleId.of ( "title" ), "Panel Title" );
                panel.add ( title );

                final WebSeparator separator = new WebSeparator ( StyleId.of ( "line" ) );
                panel.add ( separator );

                final WebTextArea textArea = new WebTextArea ( StyleId.of ( "text" ), 3, 20 );
                final WebScrollPane scrollPane = new WebScrollPane ( StyleId.of ( "scroll" ), textArea );
                scrollPane.setHorizontalScrollBarPolicy ( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
                panel.add ( scrollPane );
                
                TestFrame.show ( panel );
                // Use custom WebLaF components here
                // WebButton button = ...
            }
        });
    }

    public void displayTray() throws AWTException {

    }
}
