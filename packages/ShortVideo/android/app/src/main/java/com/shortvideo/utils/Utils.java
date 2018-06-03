package com.shortvideo.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;

import com.aliyun.common.logger.Logger;
import com.aliyun.common.utils.StorageUtils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by lichao on 2018/1/24.
 */
public class Utils {
    //    public final static String SD_DIR = Environment.getExternalStorageDirectory().getPath()
//            + "/";

    public static final String VIDEO_STORAGE_DIR = Environment.getExternalStorageDirectory() + "/canyinquan/";
    public static final String CAPTURED_FRAME_FILE_PATH = VIDEO_STORAGE_DIR + "first_frame.jpg";

    public static String SD_DIR ;
    //    public static final String BASE_URL = "http://m.api.inner.alibaba.net";
    public static final String BASE_URL = "https://m-api.qupaicloud.com";   //外网地址（正式环境）TODO:上线前要干掉

    public final static String QU_NAME = "AliyunDemo";
    public static String QU_DIR ;
    static private void copyFileToSD(Context cxt, String src, String dst) throws IOException
    {
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(dst);
        myInput = cxt.getAssets().open(src);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
    }
    static public void copySelf(Context cxt, String root) {
        try {
            String[] files = cxt.getAssets().list(root);
            if(files.length > 0) {
                File subdir = new File(SD_DIR + root);
                if (!subdir.exists())
                    subdir.mkdirs();
                for (String fileName : files) {
                    if (new File(SD_DIR + root + File.separator + fileName).exists()){
                        continue;
                    }
                    copySelf(cxt,root + "/" + fileName);
                }
            }else{
                Logger.getDefaultLogger().d("copy...."+SD_DIR+root);
                OutputStream myOutput = new FileOutputStream(SD_DIR+root);
                InputStream myInput = cxt.getAssets().open(root);
                byte[] buffer = new byte[1024 * 8];
                int length = myInput.read(buffer);
                while(length > 0)
                {
                    myOutput.write(buffer, 0, length);
                    length = myInput.read(buffer);
                }

                myOutput.flush();
                myInput.close();
                myOutput.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public void copyAll(Context cxt) {
        SD_DIR = StorageUtils.getCacheDirectory(cxt).getAbsolutePath() + File.separator;
        QU_DIR = SD_DIR + QU_NAME + File.separator;
        File dir = new File(QU_DIR);
        copySelf(cxt,QU_NAME);
        dir.mkdirs();
        unZip();

    }

    public static void unZip() {
        File[] files = new File(SD_DIR + QU_NAME).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name != null && name.endsWith(".zip")) {
                    return true;
                }
                return false;
            }
        });
        for(final File file : files) {
            int len = file.getAbsolutePath().length();
            if (!new File(file.getAbsolutePath().substring(0, len - 4)).exists()) {
                try {
                    UnZipFolder(file.getAbsolutePath(), SD_DIR + QU_NAME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void UnZipFolder(String zipFileString, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {

                File file = new File(outPathString + File.separator + szName);
                file.createNewFile();
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }

    public static String SavePic(Bitmap bitmap, String imagePath) {
        String path;
        if(imagePath.equals("")){
           path = CAPTURED_FRAME_FILE_PATH;
        }else{
            path = imagePath;
        }
        File file = new File(path);
        FileOutputStream fileOutputStream = null;
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public static Bitmap getFirstFrame(String videoPath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(videoPath);
        Bitmap firstFrame = mmr.getFrameAtTime();
        mmr.release();
        return firstFrame;
    }

    public static String getFirstFramePath(String videoPath) {
        String imagePath="";
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(videoPath);
        Bitmap firstFrame = mmr.getFrameAtTime();
        mmr.release();
        if(!videoPath.contains("canyinquan")){
            imagePath = videoPath.split("[.]")[0].concat(".jpg");
        }
        return Utils.SavePic(firstFrame, imagePath);
    }

}
