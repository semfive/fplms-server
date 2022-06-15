package plms.ManagementService.model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateCycleReportRequest extends CreateCycleReportRequest{
    private Integer id;
}
