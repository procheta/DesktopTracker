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
public class ProcessTrigger {

    public void loadProcess() {

        Runtime r = Runtime.getRuntime();
        Process p = null;
        try {
            String s = "C:\\Windows\\notepad.exe";
            p = r.exec(s);
        } catch (Exception e) {
            System.out.println("error===" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void stopProcess() {

        Runtime r = Runtime.getRuntime();
        Process p = null;
        try {
            String s = "C";
            p = r.exec("TASKKILL /F /IM notepad.exe");
           
        } catch (Exception e) {
            System.out.println("error===" + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws InterruptedException{
        ProcessTrigger pr = new ProcessTrigger();
        pr.loadProcess();
        System.out.println("hereee");
        Thread.sleep(10000);
        pr.stopProcess();
         System.out.println("thereereee");
    }

}
