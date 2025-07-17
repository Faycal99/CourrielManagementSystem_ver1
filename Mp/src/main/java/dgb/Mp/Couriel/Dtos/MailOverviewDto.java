package dgb.Mp.Couriel.Dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MailOverviewDto {

    private String name; // Month name
    private Long total;
}
