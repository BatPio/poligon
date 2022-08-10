package mapstruct;

import model.dto.SimpleDTO;
import model.internal.SimpleInternal;
import org.mapstruct.Mapper;

@Mapper
public interface SimpleMapper {
    SimpleDTO toDto(SimpleInternal source);
    SimpleInternal toInternal(SimpleDTO destination);
}