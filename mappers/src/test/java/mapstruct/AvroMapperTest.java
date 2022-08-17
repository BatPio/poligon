package mapstruct;

import model.avro.ComplexAvro;
import model.dto.ComplexDTO;
import model.internal.ComplexInternal;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AvroMapperTest {

    private AvroMapper mapper = Mappers.getMapper(AvroMapper.class);

    @Test
    public void shouldMapSimpleModel() {
        ComplexInternal.InnerInternal innerInternal = new ComplexInternal.InnerInternal();
        innerInternal.setInnerName("Inner name");

        ComplexInternal internal = new ComplexInternal();
        internal.setName("SourceName");
        internal.setTimestamp(OffsetDateTime.now());
        internal.setInternal(innerInternal);

        ComplexAvro avro = mapper.toAvro(internal);

        assertEquals(internal.getName(), avro.getName());
        assertEquals(internal.getTimestamp().toString(), avro.getTimestamp());
        assertEquals(internal.getInternal().getInnerName(), avro.getInternal().getInnerName());
    }

}
