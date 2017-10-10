package com.example.kanghyun.moviedownload;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by kanghyun on 2017-03-05.
 */

public class FileFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".torrent");
    }
}
