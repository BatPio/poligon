package common;

import java.time.OffsetDateTime;

public class OffsetDateTimeMapper {

    public String map(OffsetDateTime dateTime) {
        return dateTime.toString();
    }

    public OffsetDateTime map(String dateTime) {
        return OffsetDateTime.parse(dateTime);
    }

}
