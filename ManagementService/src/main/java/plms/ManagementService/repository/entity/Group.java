package plms.ManagementService.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @Column(name = "member_quantity")
    private Integer memberQuantity;
    @Column(name = "is_empty")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isEmpty;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
    @JsonManagedReference
    private Set<StudentGroup> studentGroupSet;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLASS_id")
    @JsonIgnore
    private Class classEntity;
}