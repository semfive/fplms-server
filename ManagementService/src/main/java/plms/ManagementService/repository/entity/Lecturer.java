package plms.ManagementService.repository.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "LECTURER")
@Getter
@Setter
@NoArgsConstructor
public class Lecturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Access(AccessType.PROPERTY)
    private Integer id;
    @Column
    private String name;
    @Column
    private String email;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "is_disable")
    private Boolean isDisable;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "lecturer")
    @JsonManagedReference
    private Set<Class> classSet;
}
