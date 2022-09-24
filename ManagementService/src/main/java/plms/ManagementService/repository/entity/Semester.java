package plms.ManagementService.repository.entity;

import java.sql.Date;
import java.util.Set;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SEMESTER")
@Getter
@Setter
@NoArgsConstructor
public class Semester {
	@Id
	@Column
	private String code;
	@Column(name = "start_date")
	private Date startDate;
	@Column(name = "end_date")
	private Date endDate;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "semester")
	private Set<Class> classSet;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "semester")
	private Set<Project> projectSet;
	
	public Semester(String code) {
		super();
		this.code = code;
	}
	
	
}
