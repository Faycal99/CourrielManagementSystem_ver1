package dgb.Mp.Couriel;

import dgb.Mp.Couriel.Dtos.*;
import dgb.Mp.Couriel.enums.Couriel_Type;
import dgb.Mp.Couriel.enums.Nature;
import dgb.Mp.Direction.DirectionService;
import dgb.Mp.Division.DivisionService;
import dgb.Mp.File.File;
import dgb.Mp.History.Dto.HistoryDto;
import dgb.Mp.History.History;
import dgb.Mp.History.HistoryRepository;
import dgb.Mp.Notification.NotificationService;
import dgb.Mp.Notification.enums.Operations;
import dgb.Mp.Role.enums.RoleName;
import dgb.Mp.SousDirection.SousDirectionService;
import dgb.Mp.Utils.FormatUtils;
import dgb.Mp.Utils.Mapper;

import dgb.Mp.generalAdvice.customException.HaveNotPermissionForThat;
import dgb.Mp.user.User;
import dgb.Mp.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.compress.utils.ArchiveUtils.sanitize;


@Slf4j
@Service
@RequiredArgsConstructor
public class


CourielServiceImpl implements CourielService{
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    @Value("${file.storagePath}")
    private String basePath;

    @Value("${file.maxSize}")
    private long maxFileSize;

    @Value("${file.totalMaxSize}")
    private long totalMaxSize;
    @Autowired
    private Mapper mapper;
    @Autowired
    private EntityManager entityManager;

@Autowired
    private HistoryRepository historyRepository;
    private final DivisionService divisionService;

    @Autowired
    private CourielRepository courielRepository;
    @Autowired
    private DirectionService directionService;
    @Autowired
    private SousDirectionService sousDirectionService;


    private final UserRepository userRepository;

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;


private final NotificationService notificationService;

    @Override
    public Page<CourielDtoResponse> filterCouriels(User user,CourielFilterDto filter,  int page, int size) {

        RoleName roleName = user.getRole().getName();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Main query (without pagination)
        CriteriaQuery<Couriel> cq = cb.createQuery(Couriel.class);
        Root<Couriel> root = cq.from(Couriel.class);
        List<Predicate> predicates = buildPredicates(filter, cb, root, user);
        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(root.get("id")));

        List<Couriel> couriels = entityManager.createQuery(cq).getResultList();

        // Apply filtering based on History (creator ownership rules)
        List<Couriel> filtered = couriels.stream()
                .filter(couriel -> couriel.getHistoryList().stream().anyMatch(h -> {
                    boolean isCreateAction = "CREATE".equalsIgnoreCase(h.getActionType());
                    if (!isCreateAction || h.getCreatedBy() == null) return false;

                    User creator = h.getCreatedBy();

                    if (roleName == RoleName.ADMIN) {
                        if (user.getDivision() != null && user.getDirection() == null) {
                            return creator.getDivision() != null &&
                                    creator.getDivision().getId().equals(user.getDivision().getId());
                        } else if (user.getDirection() != null && user.getSouDirection() == null) {
                            return creator.getDirection() != null &&
                                    creator.getDirection().getId().equals(user.getDirection().getId());
                        }
                    } else if (roleName == RoleName.USER) {
                        return creator.getSouDirection() != null &&
                                user.getSouDirection() != null &&
                                creator.getSouDirection().getId().equals(user.getSouDirection().getId());
                    }

                    return false;
                }))
                .collect(Collectors.toList());

        // Pagination (manually apply pagination after filtering)
        int total = filtered.size();
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        int fromIndex = (int) pageable.getOffset();
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), total);

        List<CourielDtoResponse> dtos = filtered.subList(fromIndex, toIndex).stream()
                .map(mapper::toCourielDtoResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, total);


    }
//    @Override
//    public CourielDto addCouriel(CourielDtoToAdd courielDtoToAdd, User user) {
//        Couriel couriel = new Couriel();
//        couriel.setCourielNumber(courielDtoToAdd.getCourielNumber());
//        couriel.setType(courielDtoToAdd.getType());
//        couriel.setNature(courielDtoToAdd.getNature());
//        couriel.setSubject(courielDtoToAdd.getSubject());
//        couriel.setPriority(courielDtoToAdd.getPriority());
//        couriel.setStatus(courielDtoToAdd.getStatus());
//        couriel.setArrivedDate(courielDtoToAdd.getArrivedDate());
//        couriel.setSentDate(courielDtoToAdd.getSentDate());
//        couriel.setReturnDate(courielDtoToAdd.getReturnDate());
//        couriel.setDescription(courielDtoToAdd.getDescription());
//
//        // Apply role-based from/to logic
//      if(  !applyOwnershipRules(couriel, courielDtoToAdd, user)){
//          throw new RuntimeException("you are not allowed to add couriel");
//      }
//        if (courielRepository.existsByCourielNumber(courielDtoToAdd.getCourielNumber())) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "A couriel with this number already exists.");
//        }
//
//        Couriel saved = courielRepository.save(couriel);
//
//        History history = new History();
//        history.setCreatedBy(user);
//        history.setCourrier(saved);
//        history.setActionType("CREATE");
//        history.setTimestamp(Instant.now());
//
//        historyRepository.save(history);
//
//        saved.getHistoryList().add(history);
//
//        return mapper.toCourielDto(saved);
//    }
//    private boolean applyOwnershipRules(Couriel couriel, CourielDtoToAdd dto, User user) {
//        boolean applied = false;
//        Couriel_Type type = dto.getType();
//        String role = String.valueOf(user.getRole().getName());
//        boolean isAdmin = "ADMIN".equals(role);
//
//        if (type == Couriel_Type.Arrivé) {
//            if (isAdmin && user.getDirection() != null) {
//                couriel.setToDivision(user.getDivision());
//                couriel.setToDirection(user.getDirection());
//                couriel.setToSouDirection(null);
//                applied = true;
//            } else if (isAdmin && user.getDivision() != null) {
//                couriel.setToDivision(user.getDivision());
//                couriel.setToDirection(null);
//                couriel.setToSouDirection(null);
//                applied = true;
//            } else {
//                couriel.setToDivision(user.getDivision());
//                couriel.setToDirection(user.getDirection());
//                couriel.setToSouDirection(user.getSouDirection());
//                applied = true;
//            }
//
//            couriel.setFromDivision(dto.getFromDivisionId() != null ? divisionService.getDivisionById(dto.getFromDivisionId()) : null);
//            couriel.setFromDirection(dto.getFromDirectionId() != null ? directionService.getDirectionById(dto.getFromDirectionId()) : null);
//            couriel.setFromSouDirection(dto.getFromSousDirectionId() != null ? sousDirectionService.getSousDirectionById(dto.getFromSousDirectionId()) : null);
//            couriel.setFromExternal(dto.getFromExternal());
//
//        } else if (type == Couriel_Type.Départ) {
//            if (isAdmin && user.getDirection() != null) {
//                couriel.setFromDivision(user.getDivision());
//                couriel.setFromDirection(user.getDirection());
//                couriel.setFromSouDirection(null);
//                applied = true;
//            } else if (isAdmin && user.getDivision() != null) {
//                couriel.setFromDivision(user.getDivision());
//                couriel.setFromDirection(null);
//                couriel.setFromSouDirection(null);
//                applied = true;
//            } else {
//                couriel.setFromDivision(user.getDivision());
//                couriel.setFromDirection(user.getDirection());
//                couriel.setFromSouDirection(user.getSouDirection());
//                applied = true;
//            }
//
//            couriel.setToDivision(dto.getToDivisionId() != null ? divisionService.getDivisionById(dto.getToDivisionId()) : null);
//            couriel.setToDirection(dto.getToDirectionId() != null ? directionService.getDirectionById(dto.getToDirectionId()) : null);
//            couriel.setToSouDirection(dto.getToSousDirectionId() != null ? sousDirectionService.getSousDirectionById(dto.getToSousDirectionId()) : null);
//            couriel.setToExternal(dto.getToExternal());
//        }
//        return applied;
//    }

    private List<Predicate> buildPredicates(CourielFilterDto filter, CriteriaBuilder cb, Root<Couriel> root,User user) {
        List<Predicate> predicates = new ArrayList<>();
        RoleName roleName = user.getRole().getName();

        if (roleName == RoleName.ADMIN) {
            if (user.getDivision() != null && user.getDirection() == null) {
                // Admin of a Division (e.g., Division 1)
                Long divisionId = user.getDivision().getId();
                Predicate fromDivision = cb.equal(root.get("fromDivision").get("id"), divisionId);
                Predicate toDivision = cb.equal(root.get("toDivision").get("id"), divisionId);
                predicates.add(cb.or(fromDivision, toDivision));
            } else if (user.getDirection() != null) {
                // Admin of a Direction (e.g., Direction 1 under Division 1)
                Long directionId = user.getDirection().getId();

                // Direction filter
                Predicate fromDirection = cb.equal(root.get("fromDirection").get("id"), directionId);
                Predicate toDirection = cb.equal(root.get("toDirection").get("id"), directionId);
                predicates.add(cb.or(fromDirection, toDirection));
            } else {
                throw new RuntimeException("Admin must be associated with either Direction or Division.");
            }
        } else if (roleName == RoleName.USER) {
            if (user.getSouDirection() != null) {
                // User of a SousDirection (e.g., SousDirection 1 under Direction 1 under Division 1)
                Long sousDirectionId = user.getSouDirection().getId();
                Long directionId = user.getSouDirection().getDirection() != null ? user.getSouDirection().getDirection().getId() : null;
                Long divisionId = directionId != null && user.getSouDirection().getDirection().getDivision() != null
                        ? user.getSouDirection().getDirection().getDivision().getId() : null;

                // Enforce parent Division constraint
                if (divisionId != null) {
                    Predicate fromDivision = cb.equal(root.get("fromDivision").get("id"), divisionId);
                    Predicate toDivision = cb.equal(root.get("toDivision").get("id"), divisionId);
                    predicates.add(cb.or(fromDivision, toDivision));
                }

                // Enforce parent Direction constraint
                if (directionId != null) {
                    Predicate fromDirection = cb.equal(root.get("fromDirection").get("id"), directionId);
                    Predicate toDirection = cb.equal(root.get("toDirection").get("id"), directionId);
                    predicates.add(cb.or(fromDirection, toDirection));
                }

                // SousDirection filter
                if(sousDirectionId != null) {
                Predicate fromSousDirection = cb.equal(root.get("fromSouDirection").get("id"), sousDirectionId);
                Predicate toSousDirection = cb.equal(root.get("toSouDirection").get("id"), sousDirectionId);
                predicates.add(cb.or(fromSousDirection, toSousDirection));}
            } else {
                throw new RuntimeException("User must be associated with a SousDirection.");
            }
        }

        // Other filters
        if (filter.getCourielNumber() != null) {
            predicates.add(cb.equal(root.get("courielNumber"), filter.getCourielNumber()));
        }
        if (filter.getType() != null) {
            Couriel_Type typeEnum = switch (filter.getType().toLowerCase()) {
                case "entrant" -> Couriel_Type.Arrivé;
                case "sortant" -> Couriel_Type.Départ;
                default -> throw new IllegalArgumentException("Invalid type: " + filter.getType());
            };
            predicates.add(cb.equal(root.get("type"), typeEnum));
        }
        if (filter.getNature() != null) {
            Nature natureEnum = switch (filter.getNature().toLowerCase()) {
                case "intern" -> Nature.Interne;
                case "extern" -> Nature.Externe;
                default -> throw new IllegalArgumentException("Invalid nature: " + filter.getNature());
            };
            predicates.add(cb.equal(root.get("nature"), natureEnum));
        }
        if (filter.getSubject() != null && !filter.getSubject().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("subject")), "%" + filter.getSubject().toLowerCase() + "%"));
        }
        if (filter.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), filter.getStatus()));
        }
        if (filter.getPriority() != null) {
            predicates.add(cb.equal(root.get("priority"), filter.getPriority()));
        }
        if (filter.getFromarrivedDate() != null && filter.getToarrivedDate() != null) {
            predicates.add(cb.between(root.get("arrivedDate"), filter.getFromarrivedDate(), filter.getToarrivedDate()));
        }
        if (filter.getFromsentDate() != null && filter.getTosentDate() != null) {
            predicates.add(cb.between(root.get("sentDate"), filter.getFromsentDate(), filter.getTosentDate()));
        }
        if (filter.getFromreturnDate() != null && filter.getToreturnDate() != null) {
            predicates.add(cb.between(root.get("returnDate"), filter.getFromreturnDate(), filter.getToreturnDate()));
        }
        if (filter.getFromExternal() != null) {
            predicates.add(cb.equal(root.get("fromExternal"), filter.getFromExternal()));
        }
        if (filter.getToExternal() != null) {
            predicates.add(cb.equal(root.get("toExternal"), filter.getToExternal()));
        }

        return predicates;
    }



    @Override
    public CourielDtoToUpdate updateCouriel(UpdateCourielRequest updateCourielRequest, User user) throws IOException {


        boolean modified = false;

        String sanitizedNumber = updateCourielRequest.getCourielNumber().replace("/", "_");

        Couriel couriel = courielRepository.findByCourielNumber(sanitizedNumber)
                .orElseThrow(() -> new RuntimeException("Couriel not found with couriel number " + updateCourielRequest.getCourielNumber()));

        if (!canUserModifyCouriel(couriel, user)) {
            throw new HaveNotPermissionForThat("You do not have permission to modify this couriel.");
        }



        if (updateCourielRequest.getStatus() != null && !updateCourielRequest.getStatus().equals(couriel.getStatus())) {
            couriel.setStatus(updateCourielRequest.getStatus());
            modified = true;
        }

        if (updateCourielRequest.getPriority() != null && !updateCourielRequest.getPriority().equals(couriel.getPriority())) {
            couriel.setPriority(updateCourielRequest.getPriority());
            modified = true;
        }
        if(updateCourielRequest.getSubject()!= null && !updateCourielRequest.getSubject().isEmpty()){
            couriel.setSubject(updateCourielRequest.getSubject());
            modified = true;
        }
        if(updateCourielRequest.getDescription()!= null && !updateCourielRequest.getDescription().isEmpty()){
            couriel.setDescription(updateCourielRequest.getDescription());
            modified = true;
        }
        if(updateCourielRequest.getReturnDate()!= null){
            couriel.setReturnDate(updateCourielRequest.getReturnDate());
            modified = true;
        }

//        if (!modified) {
//            return mapper.toCourielDto(couriel); // Nothing changed
//        }
        List<DeleteFileResponse> removedFiles = new ArrayList<>();
        if (updateCourielRequest.getRemovedFiles() != null) {
            List<String> fileNamesToRemove = couriel.getCourielFiles().stream()
                    .filter(f -> updateCourielRequest.getRemovedFiles().contains(f.getFileName()))
                    .map(File::getFileName)
                    .collect(Collectors.toList());

            for (String fileName : fileNamesToRemove) {
                removeFileFromCourriel(couriel.getCourielNumber(), fileName)
                        .ifPresent(removedFiles::add);
            }

            if (!removedFiles.isEmpty()) {
                modified = true;
            }

        }

        List<UploadFileResponse> uploaded = new ArrayList<>();
        List<SkippedFileError> skipped = new ArrayList<>();
        if (updateCourielRequest.getNewFiles() != null) {
            Pair<List<UploadFileResponse>, List<SkippedFileError>> result = handleFileUploadForCouriel(couriel, updateCourielRequest.getNewFiles());
            uploaded = result.getLeft();
            skipped = result.getRight();
            if (!uploaded.isEmpty()) modified = true;
        }

        Couriel updated = courielRepository.save(couriel);

//        if (!modified) {
//            return mapper.toCourielDto(couriel); // Nothing changed
//        }
        History history = new History();
        history.setCourrier(updated);
        history.setUpdatedBy(user);
        history.setActionType("UPDATE");
        history.setTimestamp(LocalDate.now(ZoneId.systemDefault()));

        historyRepository.save(history);

        // Add the history to the list
        updated.getHistoryList().add(history);

        System.out.println("Updating couriel number: " + updateCourielRequest.getCourielNumber());
        System.out.println("New subject: " + updateCourielRequest.getSubject());
        System.out.println("New status: " + updateCourielRequest.getStatus());
        System.out.println("New priority: " + updateCourielRequest.getPriority());


        CourielDtoToUpdate updatedto = new CourielDtoToUpdate();
        updatedto.setPriority(updated.getPriority());
        updatedto.setStatus(updated.getStatus());
        updatedto.setDescription(updated.getDescription());
        updatedto.setSubject(updated.getSubject());
        updatedto.setUploadedFiles(uploaded);
        updatedto.setSkippedFiles(skipped);
        updatedto.setRemovedFiles(removedFiles);

        List<UploadFileResponse> courielFiles = updated.getCourielFiles().stream()
                .map(file -> new UploadFileResponse(
                        file.getFileName(),
                        file.getFilePath(), // or build the path if needed
                        String.valueOf(file.getFileSize()) // or use your format
                ))
                .collect(Collectors.toList());



        String message = "Un courrier a été modifié.";
        notificationService.sendNotification(
                message,
                couriel.getCourielNumber(),
                // ou une Set<String> extraite du request
                Operations.Modifier,     // Enum personnalisé (ex: CREATION, MODIFICATION, SUPPRESSION)
                user.getEmail()
        );


         updatedto.setCourielFiles(courielFiles);

        // Save updated entity and return DTO
        return updatedto;
    }
    private boolean canUserModifyCouriel(Couriel couriel, User user) {
        if (user.getRole().getName() == RoleName.SUPER_ADMIN) {
            return true;
        }

        if (user.getRole().getName() == RoleName.ADMIN) {
            if (user.getDivision() != null) {
                return (couriel.getFromDivision() != null &&
                        couriel.getFromDivision().getId().equals(user.getDivision().getId())) ||
                        (couriel.getToDivision() != null &&
                                couriel.getToDivision().getId().equals(user.getDivision().getId()));
            }

            if (user.getDirection() != null) {
                return (couriel.getFromDirection() != null &&
                        couriel.getFromDirection().getId().equals(user.getDirection().getId())) ||
                        (couriel.getToDirection() != null &&
                                couriel.getToDirection().getId().equals(user.getDirection().getId()));
            }
        }

        // Regular user (SouDirection)
        return user.getSouDirection() != null &&
                ((couriel.getFromSouDirection() != null &&
                        couriel.getFromSouDirection().getId().equals(user.getSouDirection().getId())) ||
                        (couriel.getToSouDirection() != null &&
                                couriel.getToSouDirection().getId().equals(user.getSouDirection().getId())));
    }
    @Override
    public void deleteCouriel(String courielNumber,User user) {
        String sanitizedNumber = courielNumber.replace("/", "_");

        Couriel couriel = courielRepository.findByCourielNumber(sanitizedNumber)
                .orElseThrow(() -> new RuntimeException("Couriel not found with number: " + courielNumber));

        if (user.getRole().getName() == RoleName.USER) {
            throw new HaveNotPermissionForThat("Regular users are not allowed to delete couriels.");
        }
        if (!canUserModifyCouriel(couriel, user)) {
            throw new HaveNotPermissionForThat("You do not have permission to delete this couriel.");
        }

        Path folderPath = null;
        if (couriel.getCourrielPath() != null && !couriel.getCourrielPath().isBlank()) {
            folderPath = Paths.get(couriel.getCourrielPath());

        try {
            if (Files.exists(folderPath)) {
                Files.walk(folderPath)
                        .sorted(Comparator.reverseOrder()) // Delete files first
                        .map(Path::toFile)
                        .forEach(f -> {
                            if (!f.delete()) {
                                System.err.println("Failed to delete: " + f.getAbsolutePath());
                            }
                        });
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to delete files for Courriel " + courielNumber, e);
        }}
        String message = "Un courrier a été supprimé";
        notificationService.sendNotification(
                message,
                couriel.getCourielNumber(),
                // ou une Set<String> extraite du request
                Operations.Supprimer,     // Enum personnalisé (ex: CREATION, MODIFICATION, SUPPRESSION)
                user.getEmail()
        );

        courielRepository.delete(couriel);

    }
    public List<HistoryDto> getHistoryForCouriel(Long courielId) {
     courielRepository.findById(courielId)
                .orElseThrow(() -> new RuntimeException("Couriel not found with id: " + courielId));


        List<History> historyList = historyRepository.findByCourrierIdOrderByIdAsc(courielId);

        return historyList.stream()
                .map(mapper::toHistoryDto)
                .collect(Collectors.toList());
    }
//    @Override
//    public Couriel getCourielById(Long id) {
//        return courielRepository.findById(id).orElseThrow(() -> new RuntimeException("Couriel not found with id: " + id));
//    }

    @Override
    public PageImpl<CourielDtoResponse> getAllCouriels(User user, Pageable pageable) {
        RoleName roleName = user.getRole().getName();

        // Fetch all couriels (you can optimize with limits later if needed)
        List<Couriel> allCouriels = courielRepository.findAll();

        // Apply the same ownership filtering logic from filterCouriels
        List<Couriel> filteredCouriels = allCouriels.stream().filter(couriel ->
                couriel.getHistoryList().stream().anyMatch(h -> {
                    boolean isCreateAction = "CREATE".equalsIgnoreCase(h.getActionType());
                    if (!isCreateAction || h.getCreatedBy() == null) return false;

                    User creator = h.getCreatedBy();

                    if (roleName == RoleName.ADMIN) {
                        if (user.getDivision() != null && user.getDirection() == null) {
                            return (creator.getDivision() != null &&
                                    creator.getDivision().getId().equals(user.getDivision().getId()))
                                    || (creator.getSouDirection() != null &&
                                    creator.getSouDirection().getDirection() != null &&
                                    creator.getSouDirection().getDirection().getDivision() != null &&
                                    creator.getSouDirection().getDirection().getDivision().getId().equals(user.getDivision().getId()));

                        } else if (user.getDirection() != null && user.getSouDirection() == null) {
                            return creator.getDirection() != null &&
                                    creator.getDirection().getId().equals(user.getDirection().getId());
                        }
                    } else if (roleName == RoleName.USER) {
                        return creator.getSouDirection() != null &&
                                user.getSouDirection() != null &&
                                creator.getSouDirection().getId().equals(user.getSouDirection().getId());
                    }

                    return false;
                })
        ).sorted(Comparator.comparing(Couriel::getId).reversed()).collect(Collectors.toList());

        // Manual pagination (if total list is small; otherwise optimize)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredCouriels.size());
        List<CourielDtoResponse> dtos = filteredCouriels.subList(start, end).stream()
                .map(mapper::toCourielDtoResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, filteredCouriels.size());
    }
    public static <T> List<T> mergeDistinct(List<T> list1, List<T> list2) {
        Set<T> combinedSet = new HashSet<>();
        if (list1 != null) combinedSet.addAll(list1);
        if (list2 != null) combinedSet.addAll(list2);
        return new ArrayList<>(combinedSet);
    }
 @Override
public CourielDto addCouriel_2(CreateCourrielRequest request, User user) {
     String sanitized = request.getCourielNumber().replaceAll("[/\\\\]", "_");
     // 1. Check for duplicate courriel number
     if (courielRepository.existsByCourielNumber(sanitized)) {
         throw new ResponseStatusException(HttpStatus.CONFLICT, "A courriel with this number already exists.");
     }

     Couriel_Type typeEnum = switch (request.getType().toLowerCase()) {
         case "entrant" -> Couriel_Type.Arrivé;
         case "sortant" -> Couriel_Type.Départ;
         default -> throw new IllegalArgumentException("Invalid type: " + request.getType());
     };
     Nature natureEnum = switch (request.getNature().toLowerCase()) {
         case "intern" -> Nature.Interne;
         case "extern" -> Nature.Externe;
         default -> throw new IllegalArgumentException("Invalid nature: " + request.getNature());
     };

     String sanitizedNumber = request.getCourielNumber().replace("/", "_");

    // 2. Validate ownership rules
    Couriel couriel = new Couriel();
    couriel.setCourielNumber(sanitizedNumber);
    couriel.setType(typeEnum);
    couriel.setNature(natureEnum);
    couriel.setSubject(request.getSubject());
    couriel.setPriority(request.getPriority());
    couriel.setStatus(request.getStatus());
    couriel.setArrivedDate(request.getArrivedDate());
    couriel.setSentDate(request.getSentDate());
    couriel.setReturnDate(request.getReturnDate());
  //  couriel.setSavedDate(request.getSavedDate());
    couriel.setDescription(request.getDescription());

    if (!applyOwnershipRules_2(couriel, request,typeEnum,natureEnum, user)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to add this couriel.");
    }
     if (typeEnum == Couriel_Type.Arrivé) {
         couriel.setSentDate(null);
     } else if (typeEnum == Couriel_Type.Départ) {
         couriel.setArrivedDate(null);
     }

    // 3. Handle file upload
    List<MultipartFile> uploadedFiles = request.files();
    List<MultipartFile> nonEmptyFiles = uploadedFiles.stream()
            .filter(f -> f != null && !f.getOriginalFilename().isBlank() && f.getSize() > 0)
            .toList();

    if (nonEmptyFiles.isEmpty()) {
        throw new IllegalArgumentException("No valid files uploaded.");
    }

    long totalSize = nonEmptyFiles.stream().mapToLong(MultipartFile::getSize).sum();
    if (totalSize > totalMaxSize) {
        throw new IllegalArgumentException("Total file size exceeds limit of " + (totalMaxSize / (1024 * 1024)) + " MB.");
    }


     Path folderPath = Paths.get(basePath, sanitized);

     try {
        Files.createDirectories(folderPath);
    } catch (IOException e) {
        throw new RuntimeException("Failed to create directory to store files", e);
    }

    Set<File> courielFiles = new HashSet<>();

    for (MultipartFile multipartFile : nonEmptyFiles) {
        try {
            String originalName = Paths.get(multipartFile.getOriginalFilename()).getFileName().toString();

            if (!"application/pdf".equals(multipartFile.getContentType())) {
                continue;
            }
            if (multipartFile.getSize() > maxFileSize) {
                continue;
            }

            String compressedFileName = sanitized + "_" + originalName + ".gz";
            Path compressedPath = folderPath.resolve(compressedFileName);
            Files.createDirectories(compressedPath.getParent());

            try (
                    InputStream input = new BufferedInputStream(multipartFile.getInputStream());
                    OutputStream output = new GZIPOutputStream(Files.newOutputStream(compressedPath))
            ) {
                input.transferTo(output);
            }

            File fileEntity = File.builder()
                    .fileName(compressedFileName)
                    .filePath(compressedPath.toString())
                    .fileType("application/pdf")
                    .fileSize(Files.size(compressedPath))
                    .build();

            courielFiles.add(fileEntity);

        } catch (IOException e) {
            throw new RuntimeException("Failed to compress or save a file", e);
        }
    }
if(request.getFromDivisionId()==null && request.getFromDirectionId()==null && request.getFromSousDirectionId()==null) {

}




    // 4. Set file details in couriel
   // couriel.setCourielType("PDF");
    couriel.setCourrielPath(folderPath.toString());
    couriel.setCourielFiles(courielFiles);

    // 5. Save couriel
    Couriel saved = courielRepository.save(couriel);

    // 6. Add history record
    History history = new History();
    history.setCreatedBy(user);
    history.setCourrier(saved);
    history.setActionType("CREATE");
    history.setTimestamp(LocalDate.now(ZoneId.systemDefault()));
    historyRepository.save(history);
    saved.getHistoryList().add(history);

     String message = "Un nouveau courrier a été archivé.";
     notificationService.sendNotification(
             message,
             couriel.getCourielNumber(),
             // ou une Set<String> extraite du request
             Operations.Archiver,     // Enum personnalisé (ex: CREATION, MODIFICATION, SUPPRESSION)
             user.getEmail()
     );

    // 7. Return response
    return mapper.toCourielDto(saved);
}


    private boolean applyOwnershipRules_2(Couriel couriel, CreateCourrielRequest request,Couriel_Type typeEnum,Nature natureEnum, User user) {
        boolean applied = false;
        Couriel_Type type = typeEnum;
        Nature nature = natureEnum;
        String role = String.valueOf(user.getRole().getName());
        boolean isAdmin = "ADMIN".equals(role);

        if (type == Couriel_Type.Arrivé) {
            couriel.setSentDate(null);

            // Set TO fields based on user
            if (isAdmin && user.getDirection() != null) {
                couriel.setToDivision(user.getDivision());
                couriel.setToDirection(user.getDirection());
                couriel.setToSouDirection(null);

                if (Objects.equals(request.getFromDirectionId(), user.getDirection().getId())
                        && Objects.equals(request.getFromDivisionId(), user.getDivision().getId())
                        && request.getFromSousDirectionId() == null) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to add this couriel and pass it to yourself.");
                }

                applied = true;
            } else if (isAdmin && user.getDivision() != null) {
                couriel.setToDivision(user.getDivision());
                couriel.setToDirection(null);
                couriel.setToSouDirection(null);

                if (Objects.equals(request.getFromDivisionId(), user.getDivision().getId())
                        && request.getFromDirectionId() == null
                        && request.getFromSousDirectionId() == null) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to add this couriel and pass it to yourself.");
                }

                applied = true;
            } else {
                // Simple user
                couriel.setToDivision(user.getDivision());
                couriel.setToDirection(user.getDirection());
                couriel.setToSouDirection(user.getSouDirection());

                if (Objects.equals(request.getFromDirectionId(), user.getDirection().getId())
                        && Objects.equals(request.getFromDivisionId(), user.getDivision().getId())
                        && Objects.equals(request.getFromSousDirectionId(), user.getSouDirection().getId())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to add this couriel and pass it to yourself.");
                }

                applied = true;
            }

            // Set FROM fields based on nature
            if (nature == Nature.Externe) {
                couriel.setFromExternal(request.getFromExternal());
                couriel.setFromDivision(null);
                couriel.setFromDirection(null);
                couriel.setFromSouDirection(null);
            } else {
                couriel.setFromExternal(null);
                couriel.setFromDivision(request.getFromDivisionId() != null ? divisionService.getDivisionById(request.getFromDivisionId()) : null);
                couriel.setFromDirection(request.getFromDirectionId() != null ? directionService.getDirectionById(request.getFromDirectionId()) : null);
                couriel.setFromSouDirection(request.getFromSousDirectionId() != null ? sousDirectionService.getSousDirectionById(request.getFromSousDirectionId()) : null);
            }
        }

        else if (type == Couriel_Type.Départ) {
            couriel.setArrivedDate(null);

            // Set FROM fields based on user
            if (isAdmin && user.getDirection() != null) {
                couriel.setFromDivision(user.getDivision());
                couriel.setFromDirection(user.getDirection());
                couriel.setFromSouDirection(null);

                if (Objects.equals(request.getToDirectionId(), user.getDirection().getId())
                        && Objects.equals(request.getToDivisionId(), user.getDivision().getId())
                        && request.getToSousDirectionId() == null) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to add this couriel and pass it to yourself.");
                }

                applied = true;
            } else if (isAdmin && user.getDivision() != null) {
                couriel.setFromDivision(user.getDivision());
                couriel.setFromDirection(null);
                couriel.setFromSouDirection(null);

                if (Objects.equals(request.getToDivisionId(), user.getDivision().getId())
                        && request.getToDirectionId() == null
                        && request.getToSousDirectionId() == null) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to add this couriel and pass it to yourself.");
                }

                applied = true;
            } else if (!isAdmin && user.getSouDirection() != null) {
                couriel.setFromDivision(user.getDivision());
                couriel.setFromDirection(user.getDirection());
                couriel.setFromSouDirection(user.getSouDirection());

                if (Objects.equals(request.getToDirectionId(), user.getDirection().getId())
                        && Objects.equals(request.getToDivisionId(), user.getDivision().getId())
                        && Objects.equals(request.getToSousDirectionId(), user.getSouDirection().getId())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to add this couriel and pass it to yourself.");
                }

                applied = true;
            }

            // Set TO fields based on nature
            if (nature == Nature.Externe) {
                couriel.setToExternal(request.getToExternal());
                couriel.setToDivision(null);
                couriel.setToDirection(null);
                couriel.setToSouDirection(null);
            } else {
                couriel.setToExternal(null);
                couriel.setToDivision(request.getToDivisionId() != null ? divisionService.getDivisionById(request.getToDivisionId()) : null);
                couriel.setToDirection(request.getToDirectionId() != null ? directionService.getDirectionById(request.getToDirectionId()) : null);
                couriel.setToSouDirection(request.getToSousDirectionId() != null ? sousDirectionService.getSousDirectionById(request.getToSousDirectionId()) : null);
            }
        }

        return applied;
    }


    /// //Export
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public ByteArrayInputStream courielToCsv(List<CourielDto> couriels) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

        // Header
        writer.write("courielNumber,Type,Nature,Subject,ArrivedDate,SentDate,ReturnDate,SavedDate,FromExternal,ToExternal,Description");
        writer.newLine();

        // Data rows
        for (CourielDto c : couriels) {
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                    c.getCourielNumber(),
                    clean(c.getType()),
                    clean(c.getNature()),
                    clean(c.getSubject()),
                    formatDate(c.getArrivedDate()),
                    formatDate(c.getSentDate()),
                    formatDate(c.getReturnDate()),
                 //   formatDate(c.getSavedDate()),
                    safeEnum(c.getFromExternal()),
                    safeEnum(c.getToExternal()),
                    clean(c.getDescription())
            ));
            writer.newLine();
        }

        writer.flush();
        return new ByteArrayInputStream(out.toByteArray());
    }


    @Override
    public Couriel getByCourielNumber(String couriel_Number) {
        return courielRepository.findByCourielNumber(couriel_Number).orElseThrow(() -> new RuntimeException("Couriel not found with id: " + couriel_Number));
    }



    private String safeEnum(Enum<?> e) {
        return (e == null) ? "" : e.name();
    }

    private String formatDate(LocalDate date) {
        return (date == null) ? "" : date.toString();
    }

    private String clean(String value) {
        return (value == null || value.equalsIgnoreCase("null")) ? "" : value.trim();
    }
    @Override
    public ByteArrayInputStream exportToExcel(List<CourielDtoResponse> couriels) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Couriers");

            // Header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "Numéro de courrier","Type", "Nature", "Sujet",
                    "Date d'arrivée", "Date d'envoi", "Date de retour",
                    "Depuis l'éxtérieur", "Vers l'éxtérieur", "Description", "Vers l'intérieur","Depuis l'intérieur"
            };
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Data rows
            int rowIdx = 1;
            for (CourielDtoResponse c : couriels) {

                String ToInternal = "";
                String FromInternal = "";
                String ToExternal = "";
                String FromExternal = "";

                boolean isSortantIntern =
                        "Départ".equalsIgnoreCase(String.valueOf(c.getType())) &&
                                "Interne".equalsIgnoreCase(String.valueOf(c.getNature()));


                boolean isEntrantIntern =
                        "Arrivé".equalsIgnoreCase(String.valueOf(c.getType())) &&
                                "Interne".equalsIgnoreCase(String.valueOf(c.getNature()));

                boolean isSortantExtern = "Départ".equalsIgnoreCase(String.valueOf(c.getType())) &&
                        "Externe".equalsIgnoreCase(String.valueOf(c.getNature()));

                boolean isEntrantExtern = "Arrivé".equalsIgnoreCase(String.valueOf(c.getType())) &&
                        "Externe".equalsIgnoreCase(String.valueOf(c.getNature()));
                if( isSortantExtern ){

                    if(c.getFromSousDirectionId()!=null && c.getFromDirectionId() != null && c.getFromDivisionId()!=null){
                        FromInternal =  c.getFromSousDirectionId();
                    }
                    else if (c.getFromDirectionId() != null && c.getFromDivisionId() != null && c.getFromSousDirectionId()==null) {
                        FromInternal =  c.getFromDirectionId(); // Or use name
                    } else if (c.getFromDivisionId() != null && c.getFromDirectionId() == null && c.getFromSousDirectionId()==null ) {
                        FromInternal = c.getFromDivisionId();
                    }
                    ToExternal= String.valueOf(c.getToExternal());

                }
                if( isEntrantExtern ){
                    if (c.getToSousDirectionId() != null && c.getToDirectionId() != null && c.getToDivisionId()!=null) {
                        ToInternal =  c.getToSousDirectionId();
                    } else if (c.getToDirectionId() != null && c.getToSousDirectionId() == null && c.getToDivisionId()!=null) {
                        ToInternal =  c.getToDirectionId();
                    } else if (c.getToDivisionId() != null && c.getToSousDirectionId() == null && c.getToDirectionId()==null) {
                        ToInternal = c.getToDivisionId();

                    }
                    FromExternal = String.valueOf(c.getFromExternal());

                }

                if (isSortantIntern || isEntrantIntern) {
                    if(c.getFromSousDirectionId()!=null && c.getFromDirectionId() != null && c.getFromDivisionId()!=null){
                        FromInternal =  c.getFromSousDirectionId();
                    }
                    else if (c.getFromDirectionId() != null && c.getFromDivisionId() != null && c.getFromSousDirectionId()==null) {
                        FromInternal =  c.getFromDirectionId(); // Or use name
                    } else if (c.getFromDivisionId() != null && c.getFromDirectionId() == null && c.getFromSousDirectionId()==null ) {
                        FromInternal = c.getFromDivisionId();
                    }

                    if (c.getToSousDirectionId() != null && c.getToDirectionId() != null && c.getToDivisionId()!=null) {
                        ToInternal =  c.getToSousDirectionId();
                    } else if (c.getToDirectionId() != null && c.getToSousDirectionId() == null && c.getToDivisionId()!=null) {
                        ToInternal =  c.getToDirectionId();
                    } else if (c.getToDivisionId() != null && c.getToSousDirectionId() == null && c.getToDirectionId()==null) {
                        ToInternal = c.getToDivisionId();

                    }
                }
                Row row = sheet.createRow(rowIdx++);

               // row.createCell(0).setCellValue(c.getId()); // Correct for numeric
                row.createCell(0).setCellValue(c.getCourielNumber());
                row.createCell(1).setCellValue(safeEnum(c.getType()));
                row.createCell(2).setCellValue(safeEnum(c.getNature()));
                row.createCell(3).setCellValue(clean(c.getSubject()));
                row.createCell(4).setCellValue(formatDate(c.getArrivedDate()));
                row.createCell(5).setCellValue(formatDate(c.getSentDate()));
                row.createCell(6).setCellValue(formatDate(c.getReturnDate()));
                row.createCell(7).setCellValue(FromExternal);
                row.createCell(8).setCellValue(ToExternal);
                row.createCell(9).setCellValue(clean(c.getDescription()));



                row.createCell(10).setCellValue(ToInternal);
                row.createCell(11).setCellValue(FromInternal);


// Correct if numeric
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
    @Transactional
    public void cleanOrphanFilesFromDB() {

        List<Couriel> allCourriels = courielRepository.findAllWithFiles();


        for (Couriel courriel : allCourriels) {
            Iterator<File> iterator = courriel.getCourielFiles().iterator();

            while (iterator.hasNext()) {
                File file = iterator.next();
                Path filePath = Paths.get(file.getFilePath());

                if (!Files.exists(filePath)) {
                    System.out.println("Deleting orphan file record: " + file.getFileName());
                    iterator.remove();
                }
            }

            courielRepository.save(courriel);
        }
    }

    @Transactional
    public void cleanOrphanCourrielsFromDB() {
        List<Couriel> allCourriels = courielRepository.findAll();

        for (Couriel courriel : allCourriels) {
            Path courrielDir = Paths.get(courriel.getCourrielPath());

            if (!Files.exists(courrielDir)) {
                System.out.println("Deleting orphan courriel: " + courriel.getCourielNumber());
                courielRepository.delete(courriel);
            }
        }
    }


    public void cleanDatabaseFromMissingDiskData() {
        cleanOrphanFilesFromDB();
        cleanOrphanCourrielsFromDB();
    }
    public ApiResponse<CreateCourrielResponse> addFilesToCourriel(
            String courrielNumber,
            List<MultipartFile> filesToSave
    ) throws IOException {

        if (filesToSave == null || filesToSave.isEmpty()) {
            throw new RuntimeException("No files provided.");
        }

        Couriel courriel = courielRepository
                .findByCourielNumber(courrielNumber)
                .orElseThrow(() -> new RuntimeException("Courriel not found: " + courrielNumber));

        Path folderPath = Paths.get(basePath, sanitize(courrielNumber));
        Files.createDirectories(folderPath);

        List<UploadFileResponse> uploadedFiles = new ArrayList<>();
        List<SkippedFileError> skippedFiles = new ArrayList<>();

        for (MultipartFile file : filesToSave) {
            if (file.isEmpty()) {
                skippedFiles.add(SkippedFileError.builder()
                        .fileName("unknown")
                        .reason("Empty file")
                        .build());
                continue;
            }

            String originalFileName = Paths.get(file.getOriginalFilename()).getFileName().toString();
            String compressedFileName = courrielNumber + "_" + originalFileName + ".gz";
            Path targetPath = folderPath.resolve(compressedFileName);

            boolean existsOnDisk = Files.exists(targetPath);
            boolean existsInDb = courriel.getCourielFiles().stream()
                    .anyMatch(f -> f.getFileName().equalsIgnoreCase(compressedFileName));

            if (existsOnDisk || existsInDb) {
                String reason;
                if (existsOnDisk) {
                    reason = "File exists on directory (" + folderPath.toString() + ")";
                } else {
                    reason = "File already exists in database.";
                }
                skippedFiles.add(SkippedFileError.builder()
                        .fileName(originalFileName)
                        .reason(reason)
                        .build());
                continue;
            }

            try (InputStream in = file.getInputStream();
                 OutputStream out = new GZIPOutputStream(Files.newOutputStream(targetPath))) {
                in.transferTo(out);
            }

            long compressedSize = Files.size(targetPath) ;

            File fileEntity = File.builder()
                    .fileName(compressedFileName)
                    .fileType("application/pdf")
                    .filePath(targetPath.toString())
                    .fileSize(compressedSize)
                    .build();

            courriel.getCourielFiles().add(fileEntity);

            uploadedFiles.add(UploadFileResponse.builder()
                    .fileName(compressedFileName)
                    .filePath(targetPath.toString())
                    .fileSize(FormatUtils.formatFileSize(compressedSize))
                    .build());
        }

        courielRepository.save(courriel);

        CreateCourrielResponse response = CreateCourrielResponse.builder()
                .courielNumber(courrielNumber)
                .uploadedFiles(uploadedFiles)
                .skippedFiles(skippedFiles)
                .build();

        String message="Courriel : "+courrielNumber+" Updated.";

        if(uploadedFiles.isEmpty()) {
            message="Courriel Not Updated !";
        }

        return ApiResponse.<CreateCourrielResponse>builder()
                .message(message)
                .data(response)
                .build();
    }


    public Pair<List<UploadFileResponse>, List<SkippedFileError>> handleFileUploadForCouriel(
            Couriel couriel,
            List<MultipartFile> filesToSave
    ) throws IOException {
        List<UploadFileResponse> uploadedFiles = new ArrayList<>();
        List<SkippedFileError> skippedFiles = new ArrayList<>();

        Path folderPath = Paths.get(basePath, sanitize(couriel.getCourielNumber()));
        Files.createDirectories(folderPath);

        for (MultipartFile file : filesToSave) {
            if (file.isEmpty()) {
                skippedFiles.add(SkippedFileError.builder()
                        .fileName("unknown")
                        .reason("Empty file")
                        .build());
                continue;
            }

            String originalFileName = Paths.get(file.getOriginalFilename()).getFileName().toString();

            boolean isPdf = file.getContentType() != null && file.getContentType().equalsIgnoreCase("application/pdf")
                    || originalFileName.toLowerCase().endsWith(".pdf");

            if (!isPdf) {
                skippedFiles.add(SkippedFileError.builder()
                        .fileName(originalFileName)
                        .reason("Only PDF files are allowed")
                        .build());
                continue;
            }

            String compressedFileName = couriel.getCourielNumber() + "_" + originalFileName + ".gz";
            Path targetPath = folderPath.resolve(compressedFileName);

            boolean existsOnDisk = Files.exists(targetPath);
            boolean existsInDb = couriel.getCourielFiles().stream()
                    .anyMatch(f -> f.getFileName().equalsIgnoreCase(compressedFileName));

            if (existsOnDisk || existsInDb) {
                skippedFiles.add(SkippedFileError.builder()
                        .fileName(originalFileName)
                        .reason(existsOnDisk ? "File exists on disk" : "File exists in DB")
                        .build());
                continue;
            }

            try (InputStream in = file.getInputStream();
                 OutputStream out = new GZIPOutputStream(Files.newOutputStream(targetPath))) {
                in.transferTo(out);
            }

            long compressedSize = Files.size(targetPath);

            File fileEntity = File.builder()
                    .fileName(compressedFileName)
                    .fileType("application/pdf")
                    .filePath(targetPath.toString())
                    .fileSize(compressedSize)
                    .build();

            couriel.getCourielFiles().add(fileEntity);

            uploadedFiles.add(UploadFileResponse.builder()
                    .fileName(compressedFileName)
                    .filePath(targetPath.toString())
                    .fileSize(FormatUtils.formatFileSize(compressedSize))
                    .build());
        }

        return Pair.of(uploadedFiles, skippedFiles);
    }

    public Optional<DeleteFileResponse> removeFileFromCourriel(String courrielNumber, String filename) {
        Couriel courriel = courielRepository.findByCourielNumber(courrielNumber)
                .orElseThrow(() -> new RuntimeException("Courriel not found: " + courrielNumber));//need adding exception

        File fileToRemove = courriel.getCourielFiles().stream()
                .filter(f -> f.getFileName().equalsIgnoreCase(filename))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("File not found: " + filename + " in DB"));//need adding exception

        if (fileToRemove == null) return Optional.empty(); // not found

        Path filePath = Paths.get(fileToRemove.getFilePath());

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                System.out.println("Warning: file not found on disk: " + filePath);
            }
        } catch (IOException e) {
            System.out.println("Failed to delete file: " + filename + " - " + e.getMessage());
            return Optional.empty(); // failed
        }

        courriel.getCourielFiles().remove(fileToRemove);

        return Optional.of(DeleteFileResponse.builder()
                .courrielNumber(courriel.getCourielNumber())
                .courrielPath(courriel.getCourrielPath())
                .fileName(fileToRemove.getFileName())
                .build());
    }
    public ResponseEntity<Resource> downloadFile(String courrielNumber, String fileName) {
        String sanitizedNumber = courrielNumber.replaceAll("[/\\\\]", "_");

        // Get courriel data from DB using sanitized number
        Couriel courriel = courielRepository.findByCourielNumber(sanitizedNumber)
                .orElseThrow(() -> new RuntimeException("Courriel not found: " + sanitizedNumber));


        // Check if file exist
        File file = courriel.getCourielFiles().stream()
                .filter(f -> f.getFileName().equalsIgnoreCase(fileName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("File not found: " + fileName));//exception to be handeled

        // Check if file exist physically
        Path filePath = Paths.get(file.getFilePath());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("File not found on disk: " + filePath);//exception to be handeled
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            String contentType = Files.probeContentType(filePath); // Can return null
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + file.getFileName() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new RuntimeException("Error while reading file: " + fileName, e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to determine file type: " + fileName, e);
        }
    }

    public ResponseEntity<Resource> downloadAllFiles( String courrielNumber) {
        String sanitizedNumber = courrielNumber.replaceAll("[/\\\\]", "_");

        Couriel courriel = courielRepository.findByCourielNumber(sanitizedNumber)
                .orElseThrow(() -> new RuntimeException("Courriel not found: " + sanitizedNumber));

        Set<File> files = courriel.getCourielFiles();
        if (files.isEmpty()) {
            throw new RuntimeException("No files found for courriel: " + courrielNumber);
        }

        try {
            // Create temporary ZIP file
            Path zipPath = Files.createTempFile("courriel_files_", ".zip");

            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
                for (File file : files) {
                    Path filePath = Paths.get(file.getFilePath());
                    if (!Files.exists(filePath)) continue; // Skip missing files

                    zos.putNextEntry(new ZipEntry(file.getFileName()));
                    Files.copy(filePath, zos);
                    zos.closeEntry();
                }
            }

            Resource resource = new UrlResource(zipPath.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"courriel_" + sanitizedNumber + ".zip\"")
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("Failed to create ZIP archive", e);
        }
    }


    @Override
    public DashboardStatsDto getDashboardStats(User user) {
        RoleName roleName = user.getRole().getName();
        Long outgoingMails = 0L;
        Long incomingMails = 0L;
        Long totalMails = 0L;
        Long activeUsers;

        // 1. Fetch only couriels with CREATE history
        List<Couriel> allCouriels = courielRepository.findAll(); // you can optimize if needed

        List<Couriel> filteredCouriels = allCouriels.stream()
                .filter(couriel -> couriel.getHistoryList().stream().anyMatch(h -> {
                    boolean isCreate = "CREATE".equalsIgnoreCase(h.getActionType());
                    if (!isCreate || h.getCreatedBy() == null) return false;

                    User creator = h.getCreatedBy();

                    if (roleName == RoleName.ADMIN) {
                        if (user.getDivision() != null && user.getDirection() == null) {
                            return creator.getDivision() != null &&
                                    creator.getDivision().getId().equals(user.getDivision().getId());
                        } else if (user.getDirection() != null && user.getSouDirection() == null) {
                            return creator.getDirection() != null &&
                                    creator.getDirection().getId().equals(user.getDirection().getId());
                        }
                    } else if (roleName == RoleName.USER) {
                        return creator.getSouDirection() != null &&
                                user.getSouDirection() != null &&
                                creator.getSouDirection().getId().equals(user.getSouDirection().getId());
                    }

                    return false;
                }))
                .collect(Collectors.toList());

        // 2. Count types
        outgoingMails = filteredCouriels.stream()
                .filter(c -> c.getType() == Couriel_Type.Départ)
                .count();

        incomingMails = filteredCouriels.stream()
                .filter(c -> c.getType() == Couriel_Type.Arrivé)
                .count();

        totalMails = outgoingMails + incomingMails;

        // 3. Active users
        if (roleName == RoleName.USER) {
            activeUsers = 1L; // only self
        } else if (roleName == RoleName.ADMIN) {
            if (user.getDivision() != null && user.getDirection() == null) {
                activeUsers = userRepository.countByDivision_Id(user.getDivision().getId())-1;
            } else if (user.getDirection() != null) {
                activeUsers = userRepository.countByDirection_Id(user.getDirection().getId())-1;
            } else {
                activeUsers = 0L; // fallback
            }
        } else {
            activeUsers = userRepository.count()-1; // SUPER_ADMIN sees all
        }

        return new DashboardStatsDto(totalMails, incomingMails, outgoingMails, activeUsers);
    }

    @Override
    public List<MailOverviewDto> getMonthlyMailOverview(User user) {
        int year = LocalDate.now().getYear();
        List<Object[]> entrantData = new ArrayList<>();
        List<Object[]> sortantData = new ArrayList<>();

        if (user.getDivision() != null && user.getDirection()==null && user.getSouDirection()==null) {
            entrantData = courielRepository.countEntrantByDivisionId(user.getDivision().getId(), year);
            sortantData = courielRepository.countSortantByDivisionId(user.getDivision().getId(), year);
        } else if ( user.getDirection()!=null && user.getSouDirection()==null && user.getDivision()!=null) {
            entrantData = courielRepository.countEntrantByDirectionId(user.getDirection().getId(), year);
            sortantData = courielRepository.countSortantByDirectionId(user.getDirection().getId(), year);
        } else if (user.getSouDirection()!=null && user.getDirection()!=null & user.getDivision()!=null) {
            entrantData = courielRepository.countEntrantBySouDirectionId(user.getSouDirection().getId(), year);
            sortantData = courielRepository.countSortantBySouDirectionId(user.getSouDirection().getId(), year);
        }

        String[] months = { "Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc" };
        Map<Integer, Long> monthTotals = new HashMap<>();

        for (Object[] row : entrantData) {
            int month = (Integer) row[0];
            monthTotals.put(month, monthTotals.getOrDefault(month, 0L) + (Long) row[1]);
        }

        for (Object[] row : sortantData) {
            int month = (Integer) row[0];
            monthTotals.put(month, monthTotals.getOrDefault(month, 0L) + (Long) row[1]);
        }

        List<MailOverviewDto> result = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            result.add(new MailOverviewDto(months[i - 1], monthTotals.getOrDefault(i, 0L)));
        }

        return result;
    }

}
