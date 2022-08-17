package mapstruct;

import common.OffsetDateTimeMapper;
import model.dto.ComplexDTO;
import model.internal.ComplexInternal;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {OffsetDateTimeMapper.class})
public interface ComplexMapper {

    ComplexDTO toDto(ComplexInternal source);
    ComplexInternal toInternal(ComplexDTO destination);
}
