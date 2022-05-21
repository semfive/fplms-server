package plms.ManagementService.repository.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "GROUP")
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
    @Column(name = "is_empty")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isEmpty;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
    private Set<StudentGroup> studentGroupSet;
}
