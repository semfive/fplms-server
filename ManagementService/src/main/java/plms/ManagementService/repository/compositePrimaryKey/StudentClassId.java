package plms.ManagementService.repository.compositePrimaryKey;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentClassId implements Serializable {
	
	private int STUDENT_id;
	private int CLASS_id;
	
	@Override
	public int hashCode() {
		return Objects.hash(STUDENT_id, CLASS_id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		StudentClassId studentClassId = (StudentClassId) obj;
		return STUDENT_id == studentClassId.getSTUDENT_id() && 
				CLASS_id == studentClassId.getCLASS_id();
	}
	
	
}
