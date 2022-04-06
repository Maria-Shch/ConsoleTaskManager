package ru.shcherbatykh.models;

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

    //Геттеры полей нужны для работы библиотеки json-simple
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public Date getNotificationDate() {
        return notificationDate;
    }
    public String getContactDetails() {
        return contactDetails;
    }

    @Override
    public String toString() {
        return "{\"title\":\"" + title + "\", \"description\":\"" + description + "\", \"notificationDate\":\"" + notificationDate.getTime() + "\", \"contactDetails\":\"" + contactDetails + "\"}";
    }
}
