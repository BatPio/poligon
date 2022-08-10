package mapstruct;

import mapstruct.SimpleMapper;
import model.dto.SimpleDTO;
import model.internal.SimpleInternal;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleMapperTest {

    private SimpleMapper mapper = Mappers.getMapper(SimpleMapper.class);

    @Test
    public void shouldMapSimpleModel() {
        SimpleInternal internal = new SimpleInternal();
        internal.setName("SourceName");
        internal.setDate(new Date());
        internal.setCounter(100);

        SimpleDTO dto = mapper.toDto(internal);

        assertEquals(internal.getName(), dto.getName());
        assertEquals(internal.getDate(), dto.getDate());
        assertEquals(internal.getCounter(), dto.getCounter());
    }

}
