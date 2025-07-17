package dgb.Mp.Division;

import dgb.Mp.Division.Dto.DivisionDto;
import dgb.Mp.Division.Dto.DivisionDtoToAdd;

import java.util.List;

public interface DivisionService {

    public Division getDivisionById(Long id);


    public List<DivisionDto> getAllDivision();


    DivisionDto addDivision(DivisionDtoToAdd divisionDtoToAdd);


    DivisionDto updateDivision(DivisionDto divisonDto, Long id);


    void deleteDivision(Long id);



}
