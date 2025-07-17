package dgb.Mp.Couriel;


import dgb.Mp.Couriel.enums.Couriel_Type;
import dgb.Mp.Couriel.enums.Nature;
import dgb.Mp.Couriel.enums.Priority;
import dgb.Mp.Couriel.enums.Status;
import dgb.Mp.Direction.Direction;
import dgb.Mp.Division.Division;
import dgb.Mp.File.File;
import dgb.Mp.History.History;
import dgb.Mp.SousDirection.SousDirection;
import dgb.Mp.Utils.AlgerianMinistries;
import dgb.Mp.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Couriel {


        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        @SequenceGenerator(name = "couriel_seq_gen", sequenceName = "couriel_seq", allocationSize = 1)
        private Long id;

        @NotNull
        @Column(nullable = false, unique = true)
        private String courielNumber;

        @Enumerated(EnumType.STRING)
        private Couriel_Type type;

        @Enumerated(EnumType.STRING)
        private Nature nature;


        private String subject;


        @Enumerated(EnumType.STRING)
        private Priority priority;

        @Enumerated(EnumType.STRING)
        private Status status;



        private LocalDate arrivedDate;
        private LocalDate sentDate;
        private LocalDate returnDate;





        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "from_division_id", nullable = true)
        private Division fromDivision;


        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="from_direction_id",nullable = true)
        private Direction fromDirection;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="from_soudirection_id",nullable = true)
        private SousDirection fromSouDirection;

        private AlgerianMinistries fromExternal;




        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "to_division_id", nullable = true)
        private Division toDivision;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "to_direction_id", nullable = true)
        private Direction toDirection;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="to_soudirection_id",nullable = true)
        private SousDirection toSouDirection;

        private AlgerianMinistries toExternal;

        private String Description;

        @OneToMany(mappedBy = "courrier", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
        private List<History> historyList = new ArrayList<>();

        @ManyToOne
        private User createdBy;

        private String courrielPath;

        @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinColumn(name = "couriel_id")
        private Set<File> courielFiles = new HashSet<>();




}
