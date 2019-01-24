package com.example.user.horizontalcalenderview2;

import java.io.Serializable;

public class SingleReminderInfo implements Serializable {
    private int id;
    private String reminderStatus;
    private String reminderTime;
    private String reminderMedName;
    private String reminderMedDoseInfo;

    public void setId(int id) {
        this.id = id;
    }

    public void setReminderStatus(String reminderStatus) {
        this.reminderStatus = reminderStatus;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public void setReminderMedName(String reminderMedName) {
        this.reminderMedName = reminderMedName;
    }

    public void setReminderMedDoseInfo(String reminderMedDoseInfo) {
        this.reminderMedDoseInfo = reminderMedDoseInfo;
    }

    public SingleReminderInfo(int id, String reminderStatus, String reminderTime, String reminderMedName, String reminderMedDoseInfo) {
        this.id = id;
        this.reminderStatus = reminderStatus;

        this.reminderTime = reminderTime;
        this.reminderMedName = reminderMedName;
        this.reminderMedDoseInfo = reminderMedDoseInfo;
    }

    public int getId() {
        return id;
    }

    public String getReminderStatus() {
        return reminderStatus;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public String getReminderMedName() {
        return reminderMedName;
    }

    public String getReminderMedDoseInfo() {
        return reminderMedDoseInfo;
    }
}
