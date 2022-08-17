package model.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ComplexDTO {
    private String name;
    private String timestamp;
    private InnerInternal internal;

    @Data
    public static class InnerInternal {
        private String innerName;
    }

}
