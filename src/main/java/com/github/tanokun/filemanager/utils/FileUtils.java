package com.github.tanokun.filemanager.utils;

import com.github.tanokun.filemanager.FileManager;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class FileUtils {
    private FileUtils(){}
    
    public static void DownloadFile(String url_string, Path path) throws Exception {
        URL url = new URL(url_string);
        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();
        conn.setAllowUserInteraction(false);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestMethod("GET");
        conn.connect();
        int httpStatusCode = conn.getResponseCode();
        if (httpStatusCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("HTTP Status " + httpStatusCode);
        }

        DataInputStream dataInStream
                = new DataInputStream(conn.getInputStream());

        DataOutputStream dataOutStream
                = new DataOutputStream(new BufferedOutputStream(
                        new FileOutputStream(path.toFile())));

        byte[] b = new byte[4096];
        int readByte = 0;
        while (-1 != (readByte = dataInStream.read(b))) {
            dataOutStream.write(b, 0, readByte);
        }

        dataInStream.close();
        dataOutStream.close();
    }

    public static String getDefaultPath(String pth) {
        String dp = Paths.get("plugins" + File.separator).normalize().toAbsolutePath().toString();
        if (pth.contains(dp)) {
            return (pth + File.separator);
        } else {
            return (dp + File.separator + pth);
        }
    }

    public static String changeSeparator_Slash(String path){
        return path.replace(File.separator, "/");
    }

    public static String changeSeparator_BackSlash(String path){
        return path.replace("/", "\\");
    }

    public static String getBasePath(){
        return StringUtils.replace(changeSeparator_BackSlash(FileManager.getPlugin().getDataFolder().getAbsolutePath()),
                "plugins\\FileManager", "");
    }
}