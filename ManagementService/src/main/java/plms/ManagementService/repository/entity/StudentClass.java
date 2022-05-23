package plms.ManagementService.repository.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import plms.ManagementService.repository.compositePrimaryKey.StudentClassId;

import javax.persistence.*;


@Entity
@Table(name = "STUDENT_CLASS")
@IdClass(StudentClassId.class)
@Getter
@Setter
@NoArgsConstructor
public class StudentClass {
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STUDENT_id")
    private Student student;
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CLASS_id")
    private Class class_;
}
