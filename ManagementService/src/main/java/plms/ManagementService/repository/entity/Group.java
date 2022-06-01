package plms.ManagementService.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "`GROUP`")
@Getter
@Setter
@NoArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Access(AccessType.PROPERTY)
    private Integer id;
    @Column
    private Integer number;
    @Column(name = "member_quantity")
    private Integer memberQuantity;
    @Column(name = "enroll_time")
    private Timestamp enrollTime;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
    @JsonManagedReference
    private Set<StudentGroup> studentGroupSet;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLASS_id")
    @JsonIgnore
    private Class classEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_id")
    private Project project;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
    @JsonManagedReference
    private Set<CycleReport> cycleReportSet;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
    private Set<ProgressReport> progressReportSet;

    public Group(Integer id) {
        this.id = id;
    }
}
