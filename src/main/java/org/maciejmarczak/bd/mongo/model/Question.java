package org.maciejmarczak.bd.mongo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document
public class Question {

    @Id
    private String id;
    private String category;

    @Field("air_date")
    private String airDate;
    private String question;
    private String value;
    private String answer;
    private String round;

    @Field("show_number")
    private String showNumber;

    // Getters & Setters generated automatically thanks to Lombok

    @Override
    public String toString() {
        return "Question{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", airDate='" + airDate + '\'' +
                ", question='" + question + '\'' +
                ", value='" + value + '\'' +
                ", answer='" + answer + '\'' +
                ", round='" + round + '\'' +
                ", showNumber='" + showNumber + '\'' +
                '}';
    }
}
