package plms.ManagementService.repository.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "SUBJECT")
@Getter
@Setter
@NoArgsConstructor
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Access(AccessType.PROPERTY)
    private Integer id;
    @Column
    private String name;
    @Column(name = "is_disable")
    private Boolean isDisable;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "subject")
    private List<Class> classList;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "subject")
    private Set<Project> projectSet;
    public Subject(Integer id) {
        this.id = id;
    }
}
