package com.example.lab3_1;


public class ParserParams {
    public String link;
    public String domain;

    public String  start;
    public String finish;

    public String paternImg;
    public String patternName;

    public ParserParams(String link, String domain, String start, String finish, String paternImg, String patternName) {
        this.link = link;
        this.domain = domain;
        this.start = start;
        this.finish = finish;
        this.paternImg = paternImg;
        this.patternName = patternName;
    }
}
