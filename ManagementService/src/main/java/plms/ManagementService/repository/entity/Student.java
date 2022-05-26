package plms.ManagementService.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "STUDENT")
@Getter
@Setter
@NoArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Access(AccessType.PROPERTY)
    private Integer id;
    @Column
    private String name;
    @Column
    private String email;
    @Column
    private String code;
    @Column(name = "is_disable")
    private Boolean isDisable;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "STUDENT_CLASS",
            joinColumns = @JoinColumn(name = "STUDENT_id"),
            inverseJoinColumns = @JoinColumn(name = "CLASS_id"))
    @JsonBackReference
    Set<Class> classSet;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student")
    @JsonBackReference
    private Set<StudentGroup> studentGroupSet;

    public Student(Integer id) {
        this.id = id;
    }
}
