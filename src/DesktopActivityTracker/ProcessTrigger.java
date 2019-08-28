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

    String OS;

    public void loadProcess(String processPath) {

        Runtime r = Runtime.getRuntime();
        Process p = null;
        try {
            p = r.exec(processPath);
        } catch (Exception e) {
            System.out.println("error===" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopProcess(String processNum) {

        Runtime r = Runtime.getRuntime();
        Process p = null;
        try {
            String s = "C";
           // if (OS.equals("Windows")) {
                Process process = Runtime.getRuntime().exec(processNum);
                p = r.exec("TASKKILL /F /IM " + processNum);
           // } else  if (OS.equals("Linux")){
             //   
             //   process.destroy();
           // }

        } catch (Exception e) {
            System.out.println("error===" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ProcessTrigger pr = new ProcessTrigger();
        pr.loadProcess(args[0]);
        Thread.sleep(10000);
        pr.stopProcess(args[1]);
    }

}
