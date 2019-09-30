/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AdditionalComponents;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Procheta
 */
public class FileAccess {

    public int getTimeDifference(String currTime, String accessTime) {
        try {
            String currentHHMM = currTime.split(" ")[3];
            String fileHHMM = accessTime.substring(accessTime.indexOf("T") + 1, accessTime.indexOf(".") - 1);
            String currTimeSplit[] = currentHHMM.split(":");
            String fileTimeSplit[] = fileHHMM.split(":");
            if (Integer.parseInt(currTimeSplit[0]) < (Integer.parseInt(fileTimeSplit[0]) + 1)) {
                return -1;
            } else if ((Integer.parseInt(currTimeSplit[1]) - Integer.parseInt(fileTimeSplit[1])) >= 0 && (Integer.parseInt(currTimeSplit[1]) - Integer.parseInt(fileTimeSplit[1])) < 3) {
                int thisTime = Integer.parseInt(currTimeSplit[0]) * 3600 + Integer.parseInt(currTimeSplit[1]) * 60 + Integer.parseInt(currTimeSplit[2]);
                int thatTime = (Integer.parseInt(fileTimeSplit[0]) + 1) * 3600 + Integer.parseInt(fileTimeSplit[1]) * 60 + Integer.parseInt(fileTimeSplit[2]);
                int diff = thisTime - thatTime;
                if (diff <= (5 * 60 * 60)) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } catch (Exception e) {
            System.out.println("Exception occurred while computing time difference");
            return -1;
        }
    }

    public ArrayList<String> check(String folderpath, String writeFile) throws IOException, InterruptedException {
        Calendar c = Calendar.getInstance();
        File dir = new File(folderpath);
        File[] directoryListing = dir.listFiles();
        ArrayList<String> filesAccessed = new ArrayList<>();
        String currTime = c.getTime().toString();
        FileWriter fw = new FileWriter(new File(writeFile), true);
        BufferedWriter bw = new BufferedWriter(fw);
        for (File f : directoryListing) {
            File myfile = new File(dir.getPath() + "/" + f.getName());
            Path path = myfile.toPath();
            BasicFileAttributes fatr = Files.readAttributes(path,
                    BasicFileAttributes.class);
            int flag = getTimeDifference(currTime, fatr.lastAccessTime().toString());
            if (flag == 1) {
                bw.write(f.getAbsolutePath() + "/" + f.getName() + " " + fatr.lastAccessTime().toString());
                bw.newLine();
                filesAccessed.add(f.getAbsolutePath() + "/" + f.getName());
            }
        }
        bw.close();
        return filesAccessed;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

    }
}
