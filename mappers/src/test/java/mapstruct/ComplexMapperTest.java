package mapstruct;

import model.dto.ComplexDTO;
import model.dto.SimpleDTO;
import model.internal.ComplexInternal;
import model.internal.SimpleInternal;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComplexMapperTest {

    private ComplexMapper mapper = Mappers.getMapper(ComplexMapper.class);

    @Test
    public void shouldMapSimpleModel() {
        ComplexInternal.InnerInternal innerInternal = new ComplexInternal.InnerInternal();
        innerInternal.setInnerName("Inner name");

        ComplexInternal internal = new ComplexInternal();
        internal.setName("SourceName");
        internal.setTimestamp(OffsetDateTime.now());
        internal.setInternal(innerInternal);

        ComplexDTO dto = mapper.toDto(internal);

        assertEquals(internal.getName(), dto.getName());
        assertEquals(internal.getTimestamp().toString(), dto.getTimestamp());
        assertEquals(internal.getInternal().getInnerName(), dto.getInternal().getInnerName());
    }

}
