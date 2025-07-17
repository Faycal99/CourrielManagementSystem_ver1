package dgb.Mp.Couriel;


import dgb.Mp.Couriel.Dtos.*;
import dgb.Mp.Couriel.enums.Couriel_Type;
import dgb.Mp.History.Dto.HistoryDto;
import dgb.Mp.Utils.Mapper;
import dgb.Mp.Utils.PaginatedResponse;
import dgb.Mp.user.SecurityUser;
import dgb.Mp.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name="Couriers api " , description = "Endpoints for managing Couriers")
public class CourielController {
    @Autowired
    private final CourielService courielService;
    private final Mapper mapper;

    @GetMapping("/mail/{courielNumber}")
    public CourielDto getCourierByCourielNumber(String courielNumber) {
        return mapper.toCourielDto(courielService.getByCourielNumber(courielNumber));
    }


    @PostMapping("/mails/type")
    public PaginatedResponse<CourielDtoResponse> getCourielsByType(
            @RequestParam Couriel_Type type, // now enum, not String
            @AuthenticationPrincipal SecurityUser currentUser, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        CourielFilterDto filter = new CourielFilterDto();
        filter.setType(String.valueOf(type));
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<CourielDtoResponse> result = courielService.filterCouriels(currentUser.getUser(), filter,page , size);
        return new PaginatedResponse<>(result);
    }
//    @GetMapping("/mails")
//    public PaginatedResponse<CourielDto> getAllCouriers(@AuthenticationPrincipal SecurityUser currentUser, int page,
//                                                         int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
//        Page<CourielDto> pageResult = courielService.getAllCouriels(currentUser.getUser(), pageable);
//        return new PaginatedResponse<>(pageResult);
//    }

    @GetMapping("/mails/recent")
    public PaginatedResponse<CourielDtoResponse> getRecentCouriers(@AuthenticationPrincipal SecurityUser currentUser) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());
        Page<CourielDtoResponse> pageResult = courielService.getAllCouriels(currentUser.getUser(), pageable);
        return new PaginatedResponse<>(pageResult);
    }
//    @PostMapping("/mails")
//    @Operation(summary = "Create new Couriel",description = "add new couriel")
//
//    public ResponseEntity<CourielDto> createCouriel(@Valid @RequestBody CourielDtoToAdd courielDtoToAdd,@AuthenticationPrincipal SecurityUser currentUser) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(courielService.addCouriel(courielDtoToAdd,currentUser.getUser()));
//    }
    @PostMapping(path = "/mails",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourielDto> addCouriel(
            @ModelAttribute CreateCourrielRequest request,
            @AuthenticationPrincipal SecurityUser currentUser) {


        System.out.println("The couriel nature :"+request.getNature());
        System.out.println("The couriel type :"+request.getType());


        System.out.println("*******************************************************");
        System.out.println("This is the request :"+request);

        System.out.println("*******************************************************");

        System.out.println("*******************************************************");    System.out.println("*******************************************************");
        System.out.println("*******************************************************");
        System.out.println("*******************************************************");
        System.out.println("*******************************************************");    System.out.println("*******************************************************");    System.out.println("*******************************************************");    System.out.println("*******************************************************");
        System.out.println("*******************************************************");    System.out.println("*******************************************************");    System.out.println("*******************************************************");   System.out.println("*******************************************************");
        System.out.println("*******************************************************");










        // Assuming UserDetailsImpl stores username or user id

        CourielDto createdCouriel = courielService.addCouriel_2(request, currentUser.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdCouriel);
    }

    @PutMapping(value = "/mails",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CourielDtoToUpdate updateCouriel(
//            @RequestPart String courielNumber,
            @ModelAttribute UpdateCourielRequest request,
            @AuthenticationPrincipal SecurityUser currentUser
//            @RequestPart("couriel") @Valid CourielDtoToUpdate courielDtoToUpdate,
//            @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles,
//            @RequestPart(value = "removeFileIds", required = false) List<Long> removeFileIds
    ) throws IOException {
        if (request.getNewFiles() == null) {
            request.setNewFiles(List.of()); // set empty list
        }

        if (request.getRemovedFiles() == null) {
            request.setRemovedFiles(List.of()); // set empty list
        }


        return courielService.updateCouriel(request, currentUser.getUser());
    }


    @DeleteMapping("/mail")
    public ResponseEntity<?> deleteCouriel( @RequestParam String courielNumber,@AuthenticationPrincipal SecurityUser currentUser) {
        courielService.deleteCouriel(courielNumber,currentUser.getUser());
        return ResponseEntity.ok("Deleted Couriel with number :"+courielNumber);
    }

    @PostMapping("/filter")
    public PaginatedResponse<CourielDtoResponse> filterCouriels(@AuthenticationPrincipal SecurityUser currentUser,@RequestBody CourielFilterDto courielFilterDto, int page,
                                            int size) {

        System.out.println(" \n =======================================================================================");
        System.out.println(" \n Received -----> Page : "+page+" Size : "+size);


        System.out.println(" \n from the Arrived : "+courielFilterDto.getFromarrivedDate());
        System.out.println(" \n to the Arrived : "+courielFilterDto.getToarrivedDate());


        System.out.println(" \n from the Sent : "+courielFilterDto.getFromsentDate());
        System.out.println(" \n to the Sent : "+courielFilterDto.getTosentDate());

        if (courielFilterDto.getCourielNumber() != null) {
            courielFilterDto.setCourielNumber(courielFilterDto.getCourielNumber().replaceAll("[/\\\\]", "_"));
        }
        Page<CourielDtoResponse> filteredCouriels = courielService.filterCouriels(
                currentUser.getUser(), courielFilterDto, page, size
        );




     //   Page<CourielDto> filteredCouriels = courielService.filterCouriels(currentUser.getUser(),courielFilterDto,pageable);

        PaginatedResponse response=new PaginatedResponse<>(filteredCouriels);

        System.out.println(" \n Response  -----> TotalPage = "+response.getTotalPages()+" Page = "+response.getPage()  +" Size : "+size);
        return response;
    }

    @GetMapping("/history/{courielId}")
    public ResponseEntity<List<HistoryDto>> getHistoryForCouriel(@PathVariable Long courielId) {
        List<HistoryDto> historyDtoList = courielService.getHistoryForCouriel(courielId);
        return ResponseEntity.ok(historyDtoList);
    }
    /*
    @GetMapping("/history/{courielNumber}")
      public ResponseEntity<List<FileDto>> getHistoryForCouriel(@PathVariable String courielNumber) {
        List<FileDto> FileDtoList = courielService.getFilesForCouriel(courielNumber);
        return ResponseEntity.ok(FileDtoList);
    * */


    @PostMapping("/export")
    public void exportExcel(@AuthenticationPrincipal SecurityUser currentUser, HttpServletResponse response,@RequestBody CourielFilterDto courielFilterDto,@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<CourielDtoResponse> couriels = courielService.filterCouriels(currentUser.getUser(),courielFilterDto,page,size);
            ByteArrayInputStream stream = courielService.exportToExcel(couriels.getContent());//needs to refactored

            String timestamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String fileName = "gestion_des_courriers_" +timestamp  + ".xlsx";
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename="+fileName);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",  "attachment; filename=" + fileName);

            stream.transferTo(response.getOutputStream());
            response.flushBuffer();


        } catch (Exception e) {
            e.printStackTrace(); // <- LOG IT
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam String courielNumber,
            @RequestParam String fileName) {
        return courielService.downloadFile(courielNumber, fileName);
    }


    @GetMapping("/downloadAll")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam String courielNumber
            ) {
        return courielService.downloadAllFiles(courielNumber);
    }

    @GetMapping(path = "/dashboard/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public DashboardStatsDto getDashboardStats(@AuthenticationPrincipal SecurityUser currentUser) {
        DashboardStatsDto stats = courielService.getDashboardStats(currentUser.getUser());
        return stats;
    }

    @GetMapping("/mail-overview")
    public ResponseEntity<List<MailOverviewDto>> getMailOverview(@AuthenticationPrincipal SecurityUser currentUser) {
        return ResponseEntity.ok(courielService.getMonthlyMailOverview(currentUser.getUser()));
    }
}
