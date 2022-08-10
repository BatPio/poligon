package model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SimpleDTO {
    private String name;
    private Date date;
    private int counter;
}
