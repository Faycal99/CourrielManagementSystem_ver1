package dgb.Mp.SousDirection;


import dgb.Mp.Utils.Mapper;
import dgb.Mp.SousDirection.Dtos.SousDirectionDto;
import dgb.Mp.SousDirection.Dtos.SousDirectionDtoToAdd;
import dgb.Mp.user.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;


@Log4j2
@RestController
@RequestMapping("/sousDirections")
@RequiredArgsConstructor
@Tag(name="sousDirection api " , description = "Endpoints for managing sousDirections")

public class SousDirectionController {
            private final SousDirectionService sousDirectionService;
            private final Mapper mapper;


            // Récupérer une sous-direction par ID
            @GetMapping("/{id}")
            public SousDirectionDto getSousDirectionById(@PathVariable Long id) {
                return mapper.toSousDirectionDto(sousDirectionService.getSousDirectionById( id));

            }

            // Récupérer toutes les sous-directions
            @GetMapping("/All")
            public List<SousDirectionDto> getAllSousDirections(){ return sousDirectionService.getAllSousDirections();}
            @PostMapping
            @Operation(summary = "Create new sousDirection",description = "Add new sousDirection")
            public ResponseEntity<SousDirectionDto> create(@RequestBody SousDirectionDtoToAdd sousDirectionDtoToAdd,Long directionId) {
                return ResponseEntity.status(HttpStatus.CREATED).body(sousDirectionService.addSousDirection(sousDirectionDtoToAdd,directionId));
            }



            //  Mettre à jour une sous-direction
            @PutMapping("/{id}")
            public SousDirectionDto updateSousDirection(@PathVariable Long id, @RequestBody SousDirectionDto sousDirectionDto) {
                return sousDirectionService.updateSousDirection(sousDirectionDto, id);
            }


            // Supprimer une sous-direction
            @DeleteMapping("/{id}")
            public ResponseEntity<?> deleteSousDirection(@PathVariable Long id) {
                sousDirectionService.deleteSousDirection(id);
                return ResponseEntity.ok("Sous Direction deleted with id: " + id);
            }
    @GetMapping("/getByDirectionIdAndDivisionId")
    public List<SousDirectionDto> getAllSousDirectionsByDirectionIdAndDivisionId(@RequestParam Long divisionId,
                                                                                 @RequestParam Long directionId,@AuthenticationPrincipal SecurityUser currentUser){ return sousDirectionService.getAllSousDirectionsByDirectionAndDivisionId(divisionId,directionId,currentUser.getUser());}



}
