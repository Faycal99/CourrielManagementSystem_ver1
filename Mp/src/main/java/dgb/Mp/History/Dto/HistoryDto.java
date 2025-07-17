package dgb.Mp.History.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDto {
    private Long id;
    private Long courrierId;
    private String createdById;
    private String updatedById;
    private String actionType;
    private LocalDate timestamp;
}
