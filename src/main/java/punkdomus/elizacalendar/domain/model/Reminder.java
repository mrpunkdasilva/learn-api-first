package punkdomus.elizacalendar.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.sql.Date;
import java.util.Objects;

@Entity
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Date date;

    public Reminder() {
    }

    public Reminder(Long id, String title, String description, Date date) {

        Objects.nonNull(title);
        Objects.nonNull(description);
        Objects.nonNull(date);

        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public void update(Reminder reminder) {
        this.date = reminder.date;
        this.title = reminder.title;
        this.description = reminder.description;
    }
}
