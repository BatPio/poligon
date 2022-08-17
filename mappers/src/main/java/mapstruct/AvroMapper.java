package mapstruct;

import common.OffsetDateTimeMapper;
import model.avro.ComplexAvro;
import model.internal.ComplexInternal;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {OffsetDateTimeMapper.class},
        builder = @Builder( disableBuilder = true ))
public interface AvroMapper {

    ComplexAvro toAvro(ComplexInternal source);
    ComplexInternal toInternal(ComplexAvro destination);

}
