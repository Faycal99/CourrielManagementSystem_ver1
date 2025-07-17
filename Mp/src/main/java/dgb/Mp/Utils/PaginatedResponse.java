package dgb.Mp.Utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {
    private List<T> items;
    private long total;
    private int totalPages;
  //  private int currentPage;
    private int page;



    public PaginatedResponse(Page<T> page) {
        this.items = page.getContent();
        this.total = page.getTotalElements();
        this.totalPages = page.getTotalPages();
       // this.currentPage = page.getNumber();
        this.page = page.getNumber();

    }
}
