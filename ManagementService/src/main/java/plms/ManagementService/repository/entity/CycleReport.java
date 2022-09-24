package plms.ManagementService.repository.entity;

import java.sql.Timestamp;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "CYCLE_REPORT")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class CycleReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Access(AccessType.PROPERTY)
    private Integer id;
    @Column
    private String title;
    @Column
    private String content;
    @Column(name = "cycle_number")
    private Integer cycleNumber;
    @Column
    private String feedback;
    @Column
    private Float mark;
    @Column(name = "resource_link")
    private String resourceLink;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_id")
    private Group group;

    public CycleReport(Integer id) {
        this.id = id;
    }

    public CycleReport(String content, Integer cycleNumber, String resourceLink, Group group) {
        this.content = content;
        this.cycleNumber = cycleNumber;
        this.resourceLink = resourceLink;
        this.group = group;
    }


}
