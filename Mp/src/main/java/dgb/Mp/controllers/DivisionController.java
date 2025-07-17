package dgb.Mp.controllers;

import dgb.Mp.Division.Division;
import dgb.Mp.Division.DivisionService;
import dgb.Mp.Division.Dto.DivisionDto;
import dgb.Mp.Division.Dto.DivisionDtoToAdd;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/v1/Divisions")
@Tag(name = "Division API" , description = "Endpoints for managing Divisions")

public class DivisionController {

    private final DivisionService divisionService;

    public DivisionController(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Only One Division By ID", description = "Get Only One Division By ID")
    public ResponseEntity<Division> getDivisionById(@PathVariable Long id) {

        return ResponseEntity.ok(divisionService.getDivisionById(id));
    }

    @GetMapping()
    @Operation(summary = "Get All Divisions", description = "Get All Divisions")
    public ResponseEntity<List<DivisionDto>> getAllDivision() {

        return ResponseEntity.ok(divisionService.getAllDivision());

    }

    @PostMapping

    @Operation(summary = "Create a new Division", description = "Adds a new division and returns the created object.")
    public ResponseEntity<DivisionDto> createDivision(@RequestBody DivisionDtoToAdd divisionDtoToAdd) {

        return ResponseEntity.status(HttpStatus.CREATED).body(divisionService.addDivision(divisionDtoToAdd));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Division", description = "Update a Division")
    public DivisionDto updateDivision(@RequestBody DivisionDto divisionDto ,@PathVariable Long id) {

        return divisionService.updateDivision(divisionDto,id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Division", description = "Delete a Division")

    public ResponseEntity<String> deleteDivision(@PathVariable Long id) {

        divisionService.deleteDivision(id);
        return ResponseEntity.ok("division deleted successfully");

    }

}