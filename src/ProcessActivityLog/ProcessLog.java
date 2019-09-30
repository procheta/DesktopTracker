/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProcessActivityLog;

import DesktopActivityTracker.WriteObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 *
 * @author Procheta
 */
public class ProcessLog {

    String activityLog;
    ArrayList<String> wordList;
    String processedLogFile;
    
    public ProcessLog(Properties prop){
        activityLog = prop.getProperty("KeyLogFile");
        wordList = new ArrayList<>();
        processedLogFile= prop.getProperty("processedLogFile");
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

    public void processActivityLog() throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(new File(activityLog));
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();

        FileWriter fw = new FileWriter(new File(processedLogFile));
        BufferedWriter bw = new BufferedWriter(fw);

        String prevLine = "";
        WriteObject wob = null;
        int count = 0;

        bw.write("Window Title\tApplication Name\tTyped Words\tTimestamp\n");
        while (line != null) {
            for (String s : wordList) {
                line = line.replaceAll(s, "");
            }
            line = line.replaceAll("\\p{C}", "");
            line = line.replaceAll("[^\\x00-\\x7F]", "");
            if (line.startsWith("[Window:")) {
                if (wob != null) {
                    wob.typed_words += prevLine;
                    bw.write(wob.windowTitle + "\t" + wob.application + "\t" + wob.typed_words + "\t"+wob.timeStamp);
                    bw.newLine();
                }
                try {
                    wob = new WriteObject(line);
                    wob.typed_words = wob.typed_words.replaceAll("\\.", " ");
                    wob.typed_words = wob.typed_words.toLowerCase();
                    wob.typed_words = wob.typed_words.replaceAll("[0-9]", " ");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(line);
                }
                prevLine = "";

            }
            String st[] = null;
            if (wob != null) {
                if (wob.windowTitle.equals("WriteTopic") || wob.windowTitle.equals(" Clueweb Search Interface")) {
                    wob.windowTitle = "NA";
                }
            } else {
                prevLine += line + " ";
            }
            line = br.readLine();
            count++;
        }
        bw.close();
    }
    
    public void processAccessLog() throws FileNotFoundException, IOException{
        
        FileReader fr = new FileReader(new File(""));
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        
        HashMap<String,String> folderAccess = new HashMap<>();
        while(line != null){
            String st[] = line.split(" ");
            folderAccess.put(st[1], st[0]);                
            line = br.readLine();
        }        
    }
    
    public static void main(String[] args) throws IOException {
        Properties prop = new Properties();
        ProcessLog pl = new ProcessLog(prop);
        pl.addKeyword();
        pl.activityLog = "C:\\Users\\Procheta\\Desktop/System32Log.txt";
        pl.processedLogFile = "C:\\Users\\Procheta\\Desktop/Log.txt";
        pl.processActivityLog();

    }
}
