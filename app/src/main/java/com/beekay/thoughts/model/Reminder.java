package com.beekay.thoughts.model;

public class Reminder {

    private Integer id;
    private String reminderText;
    private boolean status;
    private String toBeDoneOn;
    private String createdOn;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReminderText() {
        return reminderText;
    }

    public void setReminderText(String reminderText) {
        this.reminderText = reminderText;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getToBeDoneOn() {
        return toBeDoneOn;
    }

    public void setToBeDoneOn(String toBeDoneOn) {
        this.toBeDoneOn = toBeDoneOn;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}
