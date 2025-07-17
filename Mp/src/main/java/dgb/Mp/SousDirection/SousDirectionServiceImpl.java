package dgb.Mp.SousDirection;
import dgb.Mp.Couriel.Couriel;
import dgb.Mp.Couriel.CourielRepository;
import dgb.Mp.Direction.Direction;
import dgb.Mp.Direction.DirectionService;
import dgb.Mp.generalAdvice.customException.SousDirectionNotFoundException;
import dgb.Mp.Utils.Mapper;
import dgb.Mp.SousDirection.Dtos.SousDirectionDtoToAdd;
import dgb.Mp.SousDirection.Dtos.SousDirectionDto;
import dgb.Mp.user.User;
import dgb.Mp.user.UserService;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
@Getter
@Setter


@Slf4j
@Service
@RequiredArgsConstructor
public class SousDirectionServiceImpl implements SousDirectionService{
    private final SousDirectionRepository sousDirectionRepository;
    private final Mapper mapper ;
    private final UserService userService;
    private final DirectionService directionService;
    private final CourielRepository courielRepository;


    @Override
    public SousDirection getSousDirectionById(long id) {
            return sousDirectionRepository.findById(id).orElseThrow(()->new dgb.Mp.generalAdvice.customException.SousDirectionNotFoundException(id));
        }



    @Override
    public List<SousDirectionDto> getAllSousDirections() {
        return sousDirectionRepository.findAll().stream().map(mapper::toSousDirectionDto).collect(Collectors.toList());
    }



    @Override
    public SousDirectionDto addSousDirection(SousDirectionDtoToAdd sousDirectionDtoToAdd, Long directionId) {
      //  User director=userService.getUser(sousDirectionDtoToAdd.getDirectorUserId());
        SousDirection sousDirection= new SousDirection();
       // sousDirection.setDirector(director);
        sousDirection.setSousDirectionName(sousDirectionDtoToAdd.getSousDirectionName());
        Set<User> employees = new HashSet<>();
        Direction direction =directionService.getDirectionById(directionId);
        if(direction != null){
            if (!direction.getDivision().getId().equals(sousDirectionDtoToAdd.getDivisionId())) {
                throw new RuntimeException("The direction does not belong to the specified division.");
            }
        }

        // Validate that this direction belongs to the divisionId passed




        sousDirection.setDirection(direction);

//        sousDirectionDtoToAdd.getEmployeesIds().forEach(employeesId -> {
//            employees.add(userService.getUser(employeesId));
//        });
     //   sousDirection.setEmployees(employees);

        return mapper.toSousDirectionDto(sousDirectionRepository.save(sousDirection));
    }



    @Override
    public SousDirectionDto updateSousDirection(SousDirectionDto  sousDirectionDto, Long id) {
        SousDirection existingSousDirection = sousDirectionRepository.findById(id).orElseThrow(() -> new SousDirectionNotFoundException(id));

//        if (sousDirectionDto.getDirectorUserId() != null) {
//            User director = userService.getUser(sousDirectionDto.getDirectorUserId());
//            existingSousDirection.setDirector(director);
//        }
        if (sousDirectionDto.getSousDirectionName() != null) {
            existingSousDirection.setSousDirectionName(sousDirectionDto.getSousDirectionName());
        }
        if(sousDirectionDto.getDirectionId() != null) {
            Direction direction =directionService.getDirectionById(sousDirectionDto.getDirectionId());
            existingSousDirection.setDirection(direction);
        }
//        if (sousDirectionDto.getEmployeesIds() != null) {
//            Set<User> employees = new HashSet<>();
//            sousDirectionDto.getEmployeesIds().forEach(employeeId -> {employees.add(userService.getUser(employeeId));});
//          //  existingSousDirection.setEmployees(employees);
//        }

        SousDirection updatedSousDirection= sousDirectionRepository.save(existingSousDirection);

        return mapper.toSousDirectionDto(updatedSousDirection);
    }

//    @Override
//    public void deleteSousDirection(Long id) {
//       SousDirection sousDirection= sousDirectionRepository.findById(id).orElseThrow(()->new SousDirectionNotFoundException(id));
//        sousDirectionRepository.delete(sousDirection);
//
//    }

    @Override
    public List<SousDirectionDto> getAllSousDirectionsByDirectionAndDivisionId(Long divisionId, Long directionId,User currentUser) {
        List<SousDirection> sousDirections = sousDirectionRepository
                .findByDirection_IdAndDirection_Division_Id(directionId, divisionId);



        if (sousDirections.isEmpty()) {
            throw new RuntimeException("No sous-directions found for directionId=" + directionId + " and divisionId=" + divisionId);
        }
        boolean isAdmin = "ADMIN".equals(String.valueOf(currentUser.getRole().getName()));

        // If not admin, exclude the user's own sous-direction
        if (!isAdmin && currentUser.getSouDirection() != null) {
            sousDirections = sousDirections.stream()
                    .filter(sd -> !Objects.equals(sd.getId(), currentUser.getSouDirection().getId()))
                    .collect(Collectors.toList());
        }

        return sousDirections.stream()
                .map(mapper::toSousDirectionDto)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public void deleteSousDirection(Long id) {
        SousDirection sousDirection = sousDirectionRepository.findById(id)
                .orElseThrow(() -> new SousDirectionNotFoundException(id));

        List<Couriel> fromCouriels = courielRepository.findByFromSouDirection_Id(id);
        List<Couriel> toCouriels = courielRepository.findByToSouDirection_Id(id);

        courielRepository.deleteAll(fromCouriels);
        courielRepository.deleteAll(toCouriels);

        sousDirectionRepository.delete(sousDirection);
    }

/*    @Override
    public List<DirectionDto> getDirectionsByDivisionId(Long divisionId) {
        List<Direction> directions = directionRepository.findByDivision_Id(divisionId);

        if (directions.isEmpty()) {
            throw new DirectionNotFoundException(divisionId);
        }
        return directions.stream().map(mapper::toDirectionDto).collect(Collectors.toList());
    }*/


}
