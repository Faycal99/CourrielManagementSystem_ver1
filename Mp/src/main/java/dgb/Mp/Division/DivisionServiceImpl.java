package dgb.Mp.Division;


import dgb.Mp.Division.Dto.DivisionDto;
import dgb.Mp.Division.Dto.DivisionDtoToAdd;
import dgb.Mp.Utils.Mapper;

import dgb.Mp.generalAdvice.customException.DivisionNotFoundException;
import dgb.Mp.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import dgb.Mp.user.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DivisionServiceImpl implements DivisionService {


    private final DivisionRepository divisionRepository;
    private final Mapper mapper;
    private final @Lazy UserService userService;




    @Override
    public Division getDivisionById(Long id) {
        return divisionRepository.findById(id).orElseThrow(() -> new DivisionNotFoundException(id));
    //  return directionGeneraleRepository.findById(id).orElseThrow(()->new DirectionGeneraleNotFoundException(id));
    }


    @Override
    public List<DivisionDto> getAllDivision() {
        return divisionRepository.findAll().stream().map(mapper::toDivisionDto).toList();
    }


    @Override
    public DivisionDto addDivision(DivisionDtoToAdd divisionDtoToAdd) {

       // User director =userService.getUser(divisionDtoToAdd.getDirectorUserId());
        Division division=new Division();
       // division.setDirector(director);
        division.setDivisionName(divisionDtoToAdd.getDivisionName());
     //   DirectionGenerale directionGenerale= directionGeneraleService.getDirectionGeneraleById(DirectionGeneralId);
      //  division.setDirectionGenerale(directionGenerale);

        return mapper.toDivisionDto(divisionRepository.save(division));
    }



    @Override
    public DivisionDto updateDivision(DivisionDto divisionDto, Long id) {


        Division existingDivision = divisionRepository.findById(id).orElseThrow(() -> new DivisionNotFoundException(id));

//        if (divisionDto.getDirectorUserId() != null) {
//            User director = userService.getUser(divisionDto.getDirectorUserId());
//            existingDivision.setDirector(director);
//        }
        if (divisionDto.getDivisionName() != null) {
            existingDivision.setDivisionName(divisionDto.getDivisionName());
        }

//        if (divisionDto.getEmployeesIds() != null) {
//            Set<User> employees = new HashSet<>();
//            divisionDto.getEmployeesIds().forEach(employeeId -> {employees.add(userService.getUser(employeeId));});
//         //   existingDivision.setEmployees(employees);
//        }

        Division updatedDivision = divisionRepository.save(existingDivision);

        return mapper.toDivisionDto(updatedDivision);

    }



    @Override
    public void deleteDivision(Long id) {

        divisionRepository.deleteById(id);
        Division division = divisionRepository.findById(id).orElseThrow(()->new DivisionNotFoundException(id));
        divisionRepository.delete(division);

    }



}
