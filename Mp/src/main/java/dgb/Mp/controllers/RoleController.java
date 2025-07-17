package dgb.Mp.controllers;

import dgb.Mp.Role.Dtos.RoleDto;
import dgb.Mp.Role.Dtos.RoleDtoToAdd;
import dgb.Mp.Role.RoleService;
import dgb.Mp.Utils.Mapper;
import dgb.Mp.user.SecurityUser;
import dgb.Mp.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RestController
@RequestMapping("/cm/v1/roles")
public class RoleController {

    private final RoleService roleService;
    private final Mapper mapper;


    @PostMapping
    public RoleDto addRole(@RequestBody RoleDtoToAdd roleDto, @AuthenticationPrincipal SecurityUser currentUser) {
        if (currentUser == null) {
            System.out.println("No authenticated user found!");
            throw new IllegalStateException("No authenticated user provided");
        }
        System.out.println("Authenticated user: " + currentUser.getUsername());
        User entityUser = currentUser.getUser();
       return mapper.toRoleDto( roleService.AddRole(roleDto,entityUser));


    }
}
