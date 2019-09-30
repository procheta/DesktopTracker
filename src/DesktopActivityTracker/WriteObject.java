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
public class WriteObject {

    public String windowTitle;
    public String application;
    public String typed_words;
    public TimeStamp timeStamp;

    public WriteObject(String line) {
        line = line.replaceAll("\\[Window:", "");
        String chunks[] = null;
        String words = line.substring(line.lastIndexOf("]")+1, line.length());
        line = line.substring(0,line.lastIndexOf("]")) +"]" + words.replaceAll("-", "");
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
        typed_words =words.replaceAll("-", "");
        typed_words = typed_words.replaceAll("\\.", " ");
        if (typed_words.length() == 1) {
            typed_words = "NA";
        }
        chunks[chunks.length - 1] = chunks[chunks.length - 1].substring(0, chunks[chunks.length - 1].lastIndexOf("]"));
        try {
            timeStamp = new TimeStamp(chunks[chunks.length - 1]);
            timeStamp.convertTimeStamp();
        } catch (Exception e) {
            System.out.println(line);
            timeStamp = null;

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
    
    public static void main(String[]args){
        WriteObject wob = new WriteObject("[Window: rec_full_form_2018 [Compatibility Mode] - Word - at Thu May 17 16:21:26 2018] ");
    
    }
}
