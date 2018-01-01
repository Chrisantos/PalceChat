package com.example.chrisantuseze.palcechat.Utils;

/**
 * Created by CHRISANTUS EZE on 08/12/2017.
 */

public class GroupMessages {
    public String message, type, from, time;

    public GroupMessages() {
    }

    public GroupMessages(String message, String type, String from, String time) {
        this.message = message;
        this.type = type;
        this.from = from;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
