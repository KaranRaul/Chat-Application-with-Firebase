package com.example.chatapplication;

public class ModelClass {
    String messege;
    String from;

    public ModelClass() {
    }
    public ModelClass(String messege , String from) {
        this.messege = messege;
        this.from = from;
    }
    public String getMessege() {
        return messege;
    }

    public void setMsg(String messege) {
        this.messege = messege;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


}
