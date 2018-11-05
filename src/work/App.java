package work;

import java.util.*;
import java.io.*;

public class App {

    public ArrayList<Expression> GetRealExp(String file) {
        ArrayList<Expression> exprList = new ArrayList<Expression>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = br.readLine())!= null) {
                if (line==null||line.length() <= 0) {
                    break;
                }
                exprList.add(new Expression(line));
            }
            br.close();
            return exprList;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {

        App app = new App();
        String name = "30-26";
        //loci
        ArrayList<Expression> realExprList = app.GetRealExp("file\\logic\\Slogic" + name + ".txt");
        //phenotype
        //ArrayList<Expression> realExprList = app.GetRealExp("file\\logic\\Plogic" + name + ".txt");

        //loci
        File folder = new File("E:\\PSOlab\\" + name + "\\res\\finals");
        //phenotype
        //File folder = new File("E:\\PSOlab\\" + name + "\\res\\finalp");
        ArrayList<Float> presFile_smList = new ArrayList<Float>();
        for (File file : folder.listFiles()) {

            ArrayList<Expression> exprList = app.GetRealExp(file.getPath());

            float sum_sm = 0;
            for (Expression exp : exprList) {
                ArrayList<Float> smList = new ArrayList<Float>();
                for (Expression realexp : realExprList) {
                    smList.add(realexp.calSimilarity(exp.pairList));
                }
                sum_sm += Collections.max(smList);
            }
            sum_sm /= exprList.size();

            presFile_smList.add(sum_sm);
        }
        System.out.println("");

        try {
        	//loci
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("file\\res\\S" + name + ".txt")));
            //phenotype
            //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("file\\res\\P" + name + ".txt")));
            for (float f : presFile_smList) {
                String str = String.format("%.2f%%\r\n", f * 100);
                bw.append(str);
            }

            bw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        System.out.println("END");

    }

}

class Expression {

    int[] datArray; 
    int[] posArray; 
    public ArrayList<String> pairList; 

    public Expression(String line) {
        String[] lineSplit = line.trim().split(" ");
        ArrayList<Integer> numList = new ArrayList<Integer>();
        for (String ls : lineSplit) {
            numList.add(Integer.valueOf(ls));
        }
       
        ArrayList<Integer> posList = new ArrayList<Integer>();
        while (true) {
            int pos = numList.indexOf(-2);
            if (pos == -1) {
                break;
            }
            posList.add(pos - 1);
            numList.remove(pos);
        }

        datArray = new int[numList.size()];
        for (int i = 0; i < numList.size(); i++) {
            datArray[i] = numList.get(i);
        }
        
        posArray = new int[posList.size()];
        for (int i = 0; i < posList.size(); i++) {
            posArray[i] = posList.get(i);
        }
        calPairList();
    }

    public void calPairList() {
        pairList = new ArrayList<String>();
        for (int i = 0; i < datArray.length; i++) {
            for (int j = i + 1; j < datArray.length; j++) {
                String oper = "AND";
                for (int pos : posArray) {
                    if (i <= pos && pos < j) {
                        oper = "OR";
                        break;
                    }
                }
                
                int max, min;
                if (datArray[i] > datArray[j]) {
                    max = datArray[i];
                    min = datArray[j];
                } else {
                    min = datArray[i];
                    max = datArray[j];
                }
                String pair = String.format("%d%s%d", min, oper, max);
                pairList.add(pair);
                
            }
        }        
        HashSet<String> hs=new HashSet<String>(pairList);
        pairList= new ArrayList<String>(hs);
    }

    public float calSimilarity(ArrayList<String> pairList2) {
        int matching_num = 0;
        for (String s : pairList2) {
            if (pairList.indexOf(s) != -1) {
                matching_num++;
            }
        }
        return ((float) matching_num) / pairList.size();
    }
}
