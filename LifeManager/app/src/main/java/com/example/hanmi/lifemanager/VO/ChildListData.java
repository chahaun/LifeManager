package com.example.hanmi.lifemanager.VO;

public class ChildListData {

    private long id;
    private String group_data;
    private String title;
    private String contents;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private String work_and_life;
    private String scope;

    private int probability;
    private int success_count=0;
    private int fail_count=0;
    private int continue_bit=0;


    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getGroup_data() {
        return group_data;
    }

    public void setGroup_data(String group_data) {
        this.group_data = group_data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {

        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getWork_and_life() {
        return work_and_life;
    }

    public void setWork_and_life(String work_and_life) {
        this.work_and_life = work_and_life;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }


    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }


    public int getSuccess_count() {
        return success_count;
    }

    public void setSuccess_count(int success_count) {
        this.success_count = success_count;
    }

    public int getFail_count() {
        return fail_count;
    }

    public void setFail_count(int fail_count) {
        this.fail_count = fail_count;
    }

    public int getContinue_bit() {
        return continue_bit;
    }

    public void setContinue_bit(int continue_bit) {
        this.continue_bit = continue_bit;
    }
}


