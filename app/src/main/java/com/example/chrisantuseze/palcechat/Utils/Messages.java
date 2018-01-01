package com.example.chrisantuseze.palcechat.Utils;

/**
 * Created by CHRISANTUS EZE on 28/11/2017.
 */

public class Messages {

    private String message, type;
    private boolean seen;
    private String time;
    private String from;
    private String to;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Messages() {
    }

    public Messages(String message, String type, boolean seen, String time, String from, String to, String image) {
        this.message = message;
        this.type = type;
        this.seen = seen;
        this.time = time;
        this.from = from;
        this.to = to;
        this.image = image;
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

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
