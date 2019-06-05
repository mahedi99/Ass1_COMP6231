package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {
    public static boolean writeToFile(String serverName, String data){
        File file = new File(serverName);
        if (file.exists() && !file.isDirectory()){
            try {
                FileWriter fw = new FileWriter(file, true);
                fw.write(getSystemTime());
                fw.write(System.lineSeparator());
                fw.write(data);
                fw.write(System.lineSeparator());
                fw.write(System.lineSeparator());
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            writeToFile(serverName, data);
        }
        return true;
    }

    private static String getSystemTime(){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        return dateFormat.format(date) + " : " + timeFormat.format(date);
    }
}
