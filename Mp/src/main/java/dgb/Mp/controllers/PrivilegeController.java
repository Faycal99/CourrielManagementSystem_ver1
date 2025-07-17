package dgb.Mp.controllers;


import dgb.Mp.Utils.Mapper;
import dgb.Mp.privileges.Dtos.PrivilegeDto;
import dgb.Mp.privileges.Dtos.PrivilegeDtoToAdd;
import dgb.Mp.privileges.PrivilegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cm/v1/privileges")
public class PrivilegeController {
    private final PrivilegeService privilegeService;
    private final Mapper mapper;

    @PostMapping
    public PrivilegeDto addPrivilege(@RequestBody PrivilegeDtoToAdd privilegeDtoToAdd) {
       return mapper.toPrivilegeDto( privilegeService.addPrivilege(privilegeDtoToAdd));


    }
}
