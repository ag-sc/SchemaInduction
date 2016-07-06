package de.citec.sc.helper;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class DocumentUtils {

    
    
    public static Set<String> readFile(File file) {
        Set<String> content = null;
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            while ((strLine = br.readLine()) != null) {
                if (content == null) {
                    content = new HashSet<>();
                }
                content.add(strLine);
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error reading the file: " + file.getPath() + "\n" + e.getMessage());
        }

        return content;
    }

    public static void writeListToFile(String fileName, String content, boolean append) {
        try {
            File file = new File(fileName);

            // if file doesnt exists, then create it
            
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file,append);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.print(content);
            
            pw.close();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
