package plms.ManagementService.repository.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "CLASS")
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
    @Column
    private String semester;
    @Column(name = "is_disable")
    private Boolean isDisable;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBJECT_id")
    private Subject subject;
}
