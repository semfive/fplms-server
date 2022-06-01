package plms.ManagementService.repository.entity;


import java.sql.Timestamp;

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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PROGRESS_REPORT")
public class ProgressReport {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer id;
	@Column
	private String content;
	@Column(name = "report_time")
	private Timestamp reportTime;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STUDENT_id")
	Student student;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GROUP_id")
	Group group;
	
}
