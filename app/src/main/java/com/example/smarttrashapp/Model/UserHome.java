package com.example.smarttrashapp.Model;

public class UserHome {
    private String pid,totalbouns,date,time;

    public UserHome() {
    }

    public UserHome(String pid, String totalbouns, String date, String time) {
        this.pid = pid;
        this.totalbouns = totalbouns;
        this.date = date;
        this.time = time;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTotalbouns() {
        return totalbouns;
    }

    public void setTotalbouns(String totalbouns) {
        this.totalbouns = totalbouns;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
