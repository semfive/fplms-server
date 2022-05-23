package plms.ManagementService.repository.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "STUDENT_GROUP")
@Getter
@Setter
@NoArgsConstructor
public class StudentGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Access(AccessType.PROPERTY)
    private Integer id;
    @Column
    private Integer vote;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STUDENT_id")
    private Student student;
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    @JoinColumn(name = "GROUP_id")
    private Group group;
}
