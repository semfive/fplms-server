package plms.ManagementService.repository.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
	private Timestamp startDate;
	@Column(name = "end_date")
	private Timestamp endDate;
	
	public Semester(String code) {
		super();
		this.code = code;
	}
	
	
}
