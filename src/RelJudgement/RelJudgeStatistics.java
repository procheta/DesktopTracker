/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RelJudgement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Procheta
 */
public class RelJudgeStatistics {

    public HashMap<String, ArrayList<String>> getRelJudgeStat() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(new File("C:\\Users\\Procheta\\Desktop/2016-qrels-docs.txt"));
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        HashMap<String, ArrayList<String>> relDocMap = new HashMap<>();

        while (line != null) {
            String st[] = line.split(" ");
            if (st[3].equals("1") || st[4].equals("1")) {
                if (relDocMap.containsKey(st[0])) {
                    ArrayList<String> dList = relDocMap.get(st[0]);
                    if(!dList.contains(st[2]))
                    dList.add(st[2]);
                    relDocMap.put(st[0], dList);
                } else {
                    ArrayList<String> dList = new ArrayList<>();
                    dList.add(st[2]);
                    relDocMap.put(st[0], dList);
                }
            }
            //System.out.println(st[0]);
            line = br.readLine();
            
        }

        //System.out.println(relDocMap.size());
        Iterator it = relDocMap.keySet().iterator();
        
       /* while(it.hasNext()){
            String st = (String) it.next();
            ArrayList<String> ar = relDocMap.get(st);
            System.out.println(ar.size());
        }*/
        
        System.out.println(relDocMap);
        
        return relDocMap;      
    }
    
    public void printCount(HashMap<String, ArrayList<String>> relDocMap){
    
        Iterator it = relDocMap.keySet().iterator();
        
        while(it.hasNext()){
            String st = (String) it.next();
            System.out.println("Task number "+ st);
            ArrayList<String> ar = relDocMap.get(st);
            int count = 0;
            for(String s: ar){
                 try{
                //gethtmlformdocId(null,s);
                count++;
                 }catch(Exception e){
                 
                 }
            }
            System.out.println("Number of rel docs found "+ count);
        }
    
    }

    public static void main(String[] args) throws IOException {
     RelJudgeStatistics rjs = new RelJudgeStatistics();
     rjs.getRelJudgeStat();
        
    }
}
