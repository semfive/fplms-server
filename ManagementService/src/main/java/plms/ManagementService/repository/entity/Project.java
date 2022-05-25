package plms.ManagementService.repository.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "PROJECT")
@Getter
@Setter
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Access(AccessType.PROPERTY)
    private Integer id;
    @Column
    private String theme;
    @Column
    private String name;
    @Column
    private String problem;
    @Column
    private String context;
    @Column
    private String actors;
    @Column
    private String requirements;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    private Set<Group> groupSet;

    public Project(Integer id) {
        this.id = id;
    }
}
