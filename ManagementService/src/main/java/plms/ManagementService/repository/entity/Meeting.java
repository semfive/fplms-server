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

@Entity
@Table(name = "MEETING")
@Getter
@Setter
@NoArgsConstructor
public class Meeting {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	@Access(AccessType.PROPERTY)
	private Integer id;
	@Column
	private String title;
	@Column
	private String link;
	@Column
	private String feedback;
	@Column(name = "schedule_time")
	private Timestamp scheduleTime;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GROUP_id")
	private Group group;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LECTURER_id")
	private Lecturer lecturer;
	
}
