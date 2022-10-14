package com.ruoyi.web.controller.tool;

/**
 * @author water
 */

public enum SplitRegex {
    number("(\\d{1,2})", null),
    date("(\\d{1,2}月)|(\\d{1,2}日)|(\\d{1,2}时)|(\\d{1,2}分)|(\\d{1,2}:\\d{1,2})|(\\d{2}年)", null),
    ;
    /**
     * 第一次正则截取
     */
    private String firstRegex;
    /**
     * 第二次正则截取
     */
    private String secondRegex;


    SplitRegex(String firstRegex, String secondRegex) {
        this.firstRegex = firstRegex;
        this.secondRegex = secondRegex;
    }

    public String getFirstRegex() {
        return firstRegex;
    }

    public void setFirstRegex(String firstRegex) {
        this.firstRegex = firstRegex;
    }

    public String getSecondRegex() {
        return secondRegex;
    }

    public void setSecondRegex(String secondRegex) {
        this.secondRegex = secondRegex;
    }

    public static void main(String[] args) {

    }
}
