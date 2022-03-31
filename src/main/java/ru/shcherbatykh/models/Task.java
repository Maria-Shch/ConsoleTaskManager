package ru.shcherbatykh.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {

    private String title;
    private String description;
    private Date notificationDate;
    private String contactDetails;

    public Task() {
    }

    public Task(String title, String description, Date notificationDate, String contactDetails) {
        this.title = title;
        this.description = description;
        this.notificationDate = notificationDate;
        this.contactDetails = contactDetails;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return notificationDate;
    }

    public String getDateForPrint() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy 'в' HH:mm");
        return dateFormat.format(notificationDate);
    }

    public String getContactDetails() {
        return contactDetails;
    }

    @Override
    public String toString() {
        return "{\"title\":\"" + title + "\", \"description\":\"" + description + "\", \"date\":\"" + notificationDate.getTime() + "\", \"contactDetails\":\"" + contactDetails + "\"}";
    }
}
