package dgb.Mp.Utils;

import dgb.Mp.Couriel.Couriel;
import dgb.Mp.Couriel.Dtos.CourielDto;
import dgb.Mp.Couriel.Dtos.CourielDtoResponse;
import dgb.Mp.Couriel.Dtos.CourielDtoToUpdate;
import dgb.Mp.Division.Division;
import dgb.Mp.Division.Dto.DivisionDto;
import dgb.Mp.History.Dto.HistoryDto;
import dgb.Mp.History.History;
import dgb.Mp.Notification.Dto.NotificationDTO;
import dgb.Mp.Notification.Notification;
import dgb.Mp.Picrures.Dtos.PictureDto;
import dgb.Mp.Picrures.Picture;
import dgb.Mp.Role.Dtos.RoleDto;
import dgb.Mp.Role.Role;
import dgb.Mp.Role.enums.RoleName;
import dgb.Mp.SousDirection.Dtos.SousDirectionDto;
import dgb.Mp.SousDirection.SousDirection;
import dgb.Mp.Direction.Dtos.DirectionDto;
import dgb.Mp.Direction.Direction;

import dgb.Mp.privileges.Dtos.PrivilegeDto;

import dgb.Mp.privileges.Privilege;
import dgb.Mp.user.Dtos.UserDto;
import dgb.Mp.user.Dtos.UserDtoAllUsers;
import dgb.Mp.user.Dtos.UserDtoToAdd;
import dgb.Mp.user.User;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



@org.mapstruct.Mapper(componentModel = "spring")
public interface Mapper {

    PictureDto toPictureDto(Picture picture);

    @Mapping(source = "privileges", target = "privilegesIdsList")
    RoleDto toRoleDto(Role role);
    List<HistoryDto> toHistoryDtoList(List<History> historyList);
    @Mapping(source = "courrier.id", target = "courrierId")
    @Mapping(source = "createdBy.email", target = "createdById")
    @Mapping(source = "updatedBy.email", target = "updatedById")
    HistoryDto toHistoryDto(History history);

    @Mappings({
            @Mapping(source = "division.id", target = "divisionId"),
            @Mapping(source = "direction.id", target = "directionId"),
            @Mapping(source = "souDirection.id", target = "souDirectionId")
            //@Mapping(source = "role.name", target = "role")
    })
    UserDtoToAdd toUserDtoToAdd(User user);

    @Mappings({
            @Mapping(source = "division.divisionName", target = "divisionId"),
            @Mapping(source = "direction.directionName", target = "directionId"),
            @Mapping(source = "souDirection.sousDirectionName", target = "souDirectionId"),
            @Mapping(source = "role.name", target = "role")
    })
    UserDtoAllUsers toUserDtoAllUsers(User user);


    UserDto toUserDto(User user);

    default RoleName map(String value) {
        return RoleName.valueOf(value); // for mapping from string to enum
    }
    static Set<String> toSetOfString(Set<Privilege> privileges){

        if (privileges == null) {

            return null;
        }
        return privileges.stream()
                .map(Privilege->(Privilege.getName().toString()))
                .collect(Collectors.toSet());


    }
//

    PrivilegeDto toPrivilegeDto(Privilege privilege);
    DivisionDto toDivisionDto(Division division);

    @Mappings({
            @Mapping(source = "fromDivision.id", target = "fromDivisionId"),
            @Mapping(source = "fromDirection.id", target = "fromDirectionId"),
            @Mapping(source = "fromSouDirection.id", target = "fromSousDirectionId"),
            @Mapping(source = "toDivision.id",target = "toDivisionId"),
            @Mapping(source = "toDirection.id",target = "toDirectionId"),
            @Mapping(source = "toSouDirection.id", target = "toSousDirectionId"),

    })
CourielDto toCourielDto(Couriel couriel);

//    @Mappings({
//            @Mapping(source = "fromDivision.divisionName", target = "fromDivisionId"),
//            @Mapping(source = "fromDirection.directionName", target = "fromDirectionId"),
//            @Mapping(source = "fromSouDirection.sousDirectionName", target = "fromSousDirectionId"),
//            @Mapping(source = "toDivision.divisionName",target = "toDivisionId"),
//            @Mapping(source = "toDirection.directionName",target = "toDirectionId"),
//            @Mapping(source = "toSouDirection.sousDirectionName", target = "toSousDirectionId"),
//
//    })
@Mappings({
        @Mapping(target = "fromDivisionId", expression = "java(FormatUtils.formatOrgUnitName(couriel.getFromDivision() != null ? couriel.getFromDivision().getDivisionName() : null))"),
        @Mapping(target = "fromDirectionId", expression = "java(FormatUtils.formatOrgUnitName(couriel.getFromDirection() != null ? couriel.getFromDirection().getDirectionName() : null))"),
        @Mapping(target = "fromSousDirectionId", expression = "java(FormatUtils.formatOrgUnitName(couriel.getFromSouDirection() != null ? couriel.getFromSouDirection().getSousDirectionName() : null))"),

        @Mapping(target = "toDivisionId", expression = "java(FormatUtils.formatOrgUnitName(couriel.getToDivision() != null ? couriel.getToDivision().getDivisionName() : null))"),
        @Mapping(target = "toDirectionId", expression = "java(FormatUtils.formatOrgUnitName(couriel.getToDirection() != null ? couriel.getToDirection().getDirectionName() : null))"),
        @Mapping(target = "toSousDirectionId", expression = "java(FormatUtils.formatOrgUnitName(couriel.getToSouDirection() != null ? couriel.getToSouDirection().getSousDirectionName() : null))")
})
    CourielDtoResponse toCourielDtoResponse(Couriel couriel);

    CourielDtoToUpdate toCourielDtoToUpdate(Couriel couriel);




    @Mapping(source = "direction.id", target = "directionId")
    SousDirectionDto toSousDirectionDto(SousDirection sousDirection);
    @Mapping(source = "division.id", target = "divisionId")
    DirectionDto toDirectionDto (Direction direction);




}
