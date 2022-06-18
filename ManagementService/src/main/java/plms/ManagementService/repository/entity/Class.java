package plms.ManagementService.repository.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "`CLASS`")
@Getter
@Setter
@NoArgsConstructor
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Access(AccessType.PROPERTY)
    private Integer id;
    @Column
    private String name;
    @Column(name = "enroll_key")
    private String enrollKey;
    @Column(name = "cycle_duration")
    private Integer cycleDuration;
    @Column(name = "is_disable", insertable = false)
    private Boolean isDisable;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SEMESTER_code")
    private Semester semester;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SUBJECT_id")
    private Subject subject;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "classSet")
    @JsonManagedReference
    Set<Student> studentSet;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "classEntity")
    @JsonManagedReference
    private Set<Group> groupSet;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LECTURER_id")
    private Lecturer lecturer;

    public Class(Integer id) {
        this.id = id;
    }

    public Class(String name, String enrollKey, Subject subject, Lecturer lecturer) {
        this.name = name;
        this.enrollKey = enrollKey;
        this.subject = subject;
        this.lecturer = lecturer;
    }
}
