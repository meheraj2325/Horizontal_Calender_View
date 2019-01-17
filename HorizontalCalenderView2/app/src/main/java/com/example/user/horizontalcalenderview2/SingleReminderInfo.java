package com.example.user.horizontalcalenderview2;

public class SingleReminderInfo {
    private int id;
    private String reminderStatus;
    private String reminderTime;
    private String reminderMedName;
    private String reminderMedDoseInfo;

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
