package dgb.Mp.SousDirection;

import dgb.Mp.SousDirection.Dtos.SousDirectionDto;
import dgb.Mp.SousDirection.Dtos.SousDirectionDtoToAdd;
import dgb.Mp.user.User;

import java.util.List;

public interface SousDirectionService {



    public SousDirection getSousDirectionById(long id);
    public List<SousDirectionDto> getAllSousDirections();
    public SousDirectionDto  updateSousDirection (SousDirectionDto sousDirectionDto,Long id);
    public SousDirectionDto addSousDirection (SousDirectionDtoToAdd sousDirectionDtoToAdd,Long directionId);
    public void deleteSousDirection(Long id);
    public List<SousDirectionDto> getAllSousDirectionsByDirectionAndDivisionId(Long divisionId, Long directionId, User user);
}
