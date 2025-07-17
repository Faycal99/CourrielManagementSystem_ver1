package dgb.Mp.Couriel.Dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDto {

    private Long totalMails;
    private Long incomingMails;
    private Long outgoingMails;
    private Long activeUsers;


}
