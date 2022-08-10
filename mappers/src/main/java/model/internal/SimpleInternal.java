package model.internal;

import lombok.Data;

import java.util.Date;

@Data
public class SimpleInternal {
    private String name;
    private Date date;
    private int counter;
}
