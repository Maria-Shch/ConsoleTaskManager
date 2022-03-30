package ru.shcherbatykh.manager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {

    private String title;
    private String description;
    private Date date;
    private String contactDetails;

    public Task() {
    }

    public Task(String title, String description, Date date, String contactDetails) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.contactDetails = contactDetails;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public String getDateForPrint() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy 'в' HH:mm");
        return dateFormat.format(date);
    }

    public String getContactDetails() {
        return contactDetails;
    }

    @Override
    public String toString() {
        return "{\"title\":\"" + title + "\", \"description\":\"" + description + "\", \"date\":\"" + date.getTime() + "\", \"contactDetails\":\"" + contactDetails + "\"}";
    }
}
