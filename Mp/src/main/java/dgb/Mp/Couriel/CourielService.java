package dgb.Mp.Couriel;

import dgb.Mp.Couriel.Dtos.*;
import dgb.Mp.History.Dto.HistoryDto;
import dgb.Mp.user.User;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface CourielService {
    Page<CourielDtoResponse> filterCouriels(User user,CourielFilterDto courielDtoFilter,  int page, int size);


    public CourielDtoToUpdate updateCouriel(UpdateCourielRequest updateCourielRequest, User user) throws IOException;

    public void deleteCouriel(String courielNumber,User user);


    public Page<CourielDtoResponse> getAllCouriels(User user, Pageable pageable);
    public List<HistoryDto> getHistoryForCouriel(Long courielId);
    ByteArrayInputStream courielToCsv(List<CourielDto> couriels) throws IOException;

    Couriel getByCourielNumber(String Couriel_Number);
    public ByteArrayInputStream exportToExcel(List<CourielDtoResponse> couriels) throws IOException;
    CourielDto addCouriel_2(CreateCourrielRequest request, User user);
    void cleanDatabaseFromMissingDiskData();
    public ApiResponse<CreateCourrielResponse> addFilesToCourriel(
            String courrielNumber,
            List<MultipartFile> filesToSave
    ) throws IOException;
    public ResponseEntity<Resource> downloadFile(String courrielNumber, String fileName);
    public DashboardStatsDto getDashboardStats(User user);
    public List<MailOverviewDto> getMonthlyMailOverview(User user);
    public ResponseEntity<Resource> downloadAllFiles( String courrielNumber);
//

}
