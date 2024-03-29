package model.internal;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ComplexInternal {
    private String name;
    private OffsetDateTime timestamp;
    private InnerInternal internal;

    @Data
    public static class InnerInternal {
        private String innerName;
    }

}
