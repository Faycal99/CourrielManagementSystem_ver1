package dgb.Mp.Notification;


import dgb.Mp.Direction.Direction;
import dgb.Mp.Direction.DirectionRepository;
import dgb.Mp.Division.Division;
import dgb.Mp.Division.DivisionRepository;
import dgb.Mp.Notification.Dto.NotificationDTO;
import dgb.Mp.SousDirection.SousDirection;
import dgb.Mp.SousDirection.SousDirectionRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class NotificationMapper {

    @Autowired
    protected DivisionRepository divisionRepository;

    @Autowired
    protected DirectionRepository directionRepository;

    @Autowired
    protected SousDirectionRepository sousDirectionRepository;

    @Mapping(source = "divisionId", target = "DivisionName", qualifiedByName = "mapDivisionIdToName")
    @Mapping(source = "directionId", target = "DirectionName", qualifiedByName = "mapDirectionIdToName")
    @Mapping(source = "sousDirectionId", target = "SousDirectionName", qualifiedByName = "mapSousDirectionIdToName")
    @Mapping(source = "time", target = "time", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(source = "id", target = "id")
    public abstract NotificationDTO toDto(Notification notification);

    @Named("mapDivisionIdToName")
    protected String mapDivisionIdToName(Long id) {
        if (id == null) return null;
        return divisionRepository.findById(id).map(Division::getDivisionName).orElse(null);
    }

    @Named("mapDirectionIdToName")
    protected String mapDirectionIdToName(Long id) {
        if (id == null) return null;
        return directionRepository.findById(id).map(Direction::getDirectionName).orElse(null);
    }

    @Named("mapSousDirectionIdToName")
    protected String mapSousDirectionIdToName(Long id) {
        if (id == null) return null;
        return sousDirectionRepository.findById(id).map(SousDirection::getSousDirectionName).orElse(null);
    }


}
