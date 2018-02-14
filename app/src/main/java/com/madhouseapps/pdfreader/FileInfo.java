package com.madhouseapps.pdfreader;

/**
 * Created by Akshansh on 13-02-2018.
 */

public class FileInfo {

    private String name;
    private String uri;

    private FileInfo() {
    }

    public FileInfo(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }
}
