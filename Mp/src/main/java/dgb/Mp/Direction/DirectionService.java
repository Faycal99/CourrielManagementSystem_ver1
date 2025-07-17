package dgb.Mp.Direction;

import dgb.Mp.Direction.Dtos.DirectionDto;
import dgb.Mp.Direction.Dtos.DirectionDtoToAdd;

import java.util.List;
public interface DirectionService {

    public Direction getDirectionById(long id);
    public List<DirectionDto> getAllDirections();
    public DirectionDto updateDirection (DirectionDto directionDto,Long id);
    public DirectionDto addDirection (DirectionDtoToAdd directionDtoToAdd,Long divisionId);
    public void deleteDirection(Long id);
    public List<DirectionDto> getDirectionsByDivisionId(Long divisionId);
}
