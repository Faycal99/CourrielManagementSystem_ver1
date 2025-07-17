package dgb.Mp.Direction;
import dgb.Mp.Couriel.Couriel;
import dgb.Mp.Couriel.CourielRepository;
import dgb.Mp.Division.Division;
import dgb.Mp.Division.DivisionService;
import dgb.Mp.SousDirection.SousDirection;
import dgb.Mp.generalAdvice.customException.DirectionNotFoundException;
import dgb.Mp.generalAdvice.customException.SousDirectionNotFoundException;
import dgb.Mp.Utils.Mapper;
import dgb.Mp.Direction.Dtos.DirectionDto;
import dgb.Mp.Direction.Dtos.DirectionDtoToAdd;
import dgb.Mp.user.User;
import dgb.Mp.user.UserService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Getter
@Setter


@Slf4j
@Service
@RequiredArgsConstructor

public class DirectionServiceImpl implements DirectionService {
    private final DirectionRepository directionRepository;
    private final DivisionService divisionService;
    private final Mapper mapper;
    private final @Lazy UserService userService;
    private final CourielRepository courielRepository;


    @Override
    public Direction getDirectionById(long id ){
        return directionRepository.findById(id).orElseThrow(()->new dgb.Mp.generalAdvice.customException.DirectionNotFoundException(id));
    }

    @Override
    public List<DirectionDto> getAllDirections() {
        return directionRepository.findAll().stream().map(mapper::toDirectionDto).collect(Collectors.toList());
    }


    @Override
    public DirectionDto updateDirection(DirectionDto directionDto, Long id) {
        Direction existingDirection = directionRepository.findById(id).orElseThrow(() -> new DirectionNotFoundException(id));

//        if (directionDto.getDirectorUserId() != null) {
//            User director = userService.getUser(directionDto.getDirectorUserId());
//            existingDirection.setDirector(director);
//        }
        if (directionDto.getDirectionName() != null) {
            existingDirection.setDirectionName(directionDto.getDirectionName());
        }
        if (directionDto.getDivisionId() != null) {
            Division division = divisionService.getDivisionById(directionDto.getDivisionId());
            existingDirection.setDivision(division);
        }


//        if (directionDto.getEmployeesIds() != null) {
//            Set<User> employees = new HashSet<>();
//            directionDto.getEmployeesIds().forEach(employeeId -> {employees.add(userService.getUser(employeeId));});
//          //  existingDirection.setEmployees(employees);
//        }

        /*
        *
      //  existingDirection.setDirectionGenerale(directionGenerale);*/

        Direction updatedDirection= directionRepository.save(existingDirection);

        return mapper.toDirectionDto(updatedDirection);
    }

    @Override
    public DirectionDto addDirection(DirectionDtoToAdd directionDtoToAdd,Long divisionId) {
      //  User director=userService.getUser(directionDtoToAdd.get());
        Direction direction= new Direction();
      //  direction.setDirector(director);
        direction.setDirectionName(directionDtoToAdd.getDirectionName());
    //    Set<User> employees = new HashSet<>();
        Division division= divisionService.getDivisionById(divisionId);
        direction.setDivision(division);
//        directionDtoToAdd.getEmployeesIds().forEach(employeesId -> {
//            employees.add(userService.getUser(employeesId));
//        });
     //
        //   direction.setEmployees(employees);

        return mapper.toDirectionDto(directionRepository.save(direction));


    }

    @Override
    public void deleteDirection(Long id) {
        Direction direction= directionRepository.findById(id).orElseThrow(()->new DirectionNotFoundException(id));


        List<Couriel> fromCouriels = courielRepository.findByFromDirection_Id(id);
        List<Couriel> toCouriels = courielRepository.findByToDirection_Id(id);

        courielRepository.deleteAll(fromCouriels);
        courielRepository.deleteAll(toCouriels);



       directionRepository.delete(direction);

    }

    @Override
    public List<DirectionDto> getDirectionsByDivisionId(Long divisionId) {
        List<Direction> directions = directionRepository.findByDivision_Id(divisionId);

        if (directions.isEmpty()) {
            throw new DirectionNotFoundException(divisionId);
        }
        return directions.stream().map(mapper::toDirectionDto).collect(Collectors.toList());
    }

}
