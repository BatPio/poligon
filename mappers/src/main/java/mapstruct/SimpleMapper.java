package mapstruct;

import model.dto.SimpleDTO;
import model.internal.SimpleInternal;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SimpleMapper {
    SimpleDTO toDto(SimpleInternal source);
    SimpleInternal toInternal(SimpleDTO destination);
}