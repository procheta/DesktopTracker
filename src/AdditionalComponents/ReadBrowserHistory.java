/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AdditionalComponents;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author Procheta
 */
public class ReadBrowserHistory {

    Connection conn;
    String downloadFileLog;
    String urlVisitLog;
    long lastVisitTime;

    public ReadBrowserHistory() {
        lastVisitTime = 0;
    }

    public void initDatabase() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Procheta\\Pictures/hist.db");

    }

    public void processDownloadHistory() throws ClassNotFoundException, SQLException, IOException {

        Statement stat = conn.createStatement();
        ResultSet rs = stat.executeQuery("select * from downloads;");

        FileWriter fw = new FileWriter(new File(downloadFileLog), true);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("current_path\tstart_time\tend_time\ttab_url\tlast_access_time");
        bw.newLine();
        while (rs.next()) {
            bw.write(rs.getString("current_path") + "\t" + rs.getString("start_time") + "\t" + rs.getString("end_time") + "\t" + rs.getString("tab_url") + "\t" + rs.getString("last_access_time"));
            bw.newLine();
        }
        bw.close();
        rs.close();
    }

    public void processSearchLog() throws ClassNotFoundException, SQLException, IOException, ParseException {
        Statement stat = conn.createStatement();
        File f = new File(urlVisitLog);
        FileWriter fw = new FileWriter(f, true);
        BufferedWriter bw = new BufferedWriter(fw);
        ResultSet rs = stat.executeQuery("select * from urls;");

        if (!f.exists()) {
            
            bw.write("url\ttitle\tvisi_count\tlast_visit_time");
            bw.newLine();
        }
        int count = 0;
        while (rs.next()) {
            long currTime = Long.parseLong(rs.getString("last_visit_time"));
            if (currTime > lastVisitTime) {
                count++;
                if (count >= 45) {
                    bw.write(rs.getString("url") + "\t" + rs.getString("title") + "\t" + rs.getString("visit_count") + "\t" + extractTime(rs.getString("last_visit_time")));
                    bw.newLine();
                    lastVisitTime = Long.parseLong(rs.getString("last_visit_time"));
                }
            } else {
                break;
            }
        }
        bw.close();
        System.out.println(count);
        rs.close();
    }

    public String extractTime(String time) throws ParseException {

        String logDate = "";
        long l1 = Long.parseLong(time);
        l1 /= 1000;
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        format.setTimeZone(TimeZone.getTimeZone("Ireland/Dublin"));

        String sDate1 = "01/01/1601 00:00:00";
        Date date1 = format.parse(sDate1);
        long l = date1.getTime();
        l1 = l1 + l;
        Date date = new Date(l1);
        String formatted = format.format(date);
        return formatted;
    }

    public void copyBrowserHistoryFile() {
        FileInputStream instream = null;
        FileOutputStream outstream = null;
        try {
            File infile = new File("C:\\Users\\Procheta\\AppData\\Local\\Google\\Chrome\\User Data\\Default/History");
            File outfile = new File("C:\\Users\\Procheta\\Pictures/hist.db");

            instream = new FileInputStream(infile);
            outstream = new FileOutputStream(outfile);

            byte[] buffer = new byte[1024];

            int length;
            while ((length = instream.read(buffer)) > 0) {
                outstream.write(buffer, 0, length);
            }

            //Closing the input/output file streams
            instream.close();
            outstream.close();
            //System.out.println("File copied successfully!!");

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void writeLg() throws ClassNotFoundException, SQLException, IOException, ParseException, InterruptedException {
        while (true) {
            System.out.println("here");
            copyBrowserHistoryFile();
            processSearchLog();
           // Thread.sleep(5 * 60 * 60 * 1000);
           break;
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException, ParseException, InterruptedException {
        ReadBrowserHistory rdb = new ReadBrowserHistory();
        rdb.initDatabase();
        rdb.urlVisitLog = "C:\\Users\\Procheta\\Desktop/searchlog.txt";
        rdb.writeLg();
    }

}
