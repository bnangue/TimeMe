package com.example.bricenangue.timeme;


import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class offers all folder-paths
 */
public class FileHelper {

    //Klasse stellt sätmliche Ordnerpfade bereit
    Context newcontext;

    public FileHelper(Context context)
    {
        newcontext = context;
    }


    //Data/Data/timeme/Files
    String getFilesDirectory ()
    {
        return newcontext.getFilesDir().getPath();
    }

    String getFileFromFilesDirectory (String fileName)
    {
        return  getFilesDirectory() + "/" + fileName;
    }

    //Data/Data/timeme/Files/Users
    String getWorkbooksFolder()
    {
        return  (getFilesDirectory() + "/Workbooks");
    }

    //z.B. Data/Data/timeme/Files/Users/Max Mustermann
    String getExcelfile (String excelFileName)
    {
        return (getWorkbooksFolder() + "/" + excelFileName);
    }



    //helper um die entpackten .config Dateien zu finden
    public String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

//        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
//        }
        return ext;
    }

// delete user data  to allow new sign up
    public void clearApplicationDataforRelaunch() {
        File cache = newcontext.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();

            for (String s : children) {

                if (s.equals("lib") || s.startsWith("app")) {

                }else {
                    File f= new File(appDir, s);
                    deleteDir(f);
                    Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");


                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }


    public String getLineFromFile(String filename, String stringToLookFor) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            filename = "config.e-Sign.cfg";
            InputStream is = newcontext.openFileInput(filename);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ( (str = in.readLine()) != null ) {


                if (str.contains(stringToLookFor))
                {
                    return str;
                }
            }
            return buf.toString();
        } catch (IOException e) {

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.w("awesome Error", e.getMessage());
                }
            }
        }

        return null;
    }
    public String getreadFile(String filename) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            filename = "config.e-Sign.cfg";
            InputStream is = newcontext.openFileInput(filename);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ( (str = in.readLine()) != null ) {
                buf.append(str).append(" ");
            }
            return buf.toString();
        } catch (IOException e) {

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.w("awesome Error", e.getMessage());
                }
            }
        }

        return null;
    }
}
