package plms.ManagementService.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import plms.ManagementService.config.interceptor.GatewayInterceptor;
import plms.ManagementService.model.dto.CycleReportDTO;
import plms.ManagementService.model.dto.MeetingDTO;
import plms.ManagementService.model.dto.NotificationDTO;
import plms.ManagementService.model.request.UpdateCycleReportRequest;
import plms.ManagementService.repository.GroupRepository;
import plms.ManagementService.repository.entity.Group;

import java.util.Set;
import java.util.stream.Collectors;


@Service
public class NotificationService {
    private static final Logger logger = LogManager.getLogger(NotificationService.class);
    private static final String SEND_NOTIFICATION_MESSAGE = "Send notification: ";
    private
    @Autowired
    GroupRepository groupRepository;
    @Value("${api.notification.send.url}")
    private String sendNotificationUrl;

    public void sendNotification(NotificationDTO notification) {
        logger.info("{}{}", SEND_NOTIFICATION_MESSAGE, notification);
        ResponseEntity response = GatewayInterceptor.getWebClientBuilder().build().post().uri(sendNotificationUrl).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(notification)).retrieve().toEntity(String.class).block();
        if (!response.getStatusCode().equals(HttpStatus.CREATED)) {
            logger.warn("{}{}{}", SEND_NOTIFICATION_MESSAGE, "Error when call api: ", response.getStatusCode());
            return;
        }
        logger.info("{}{}", SEND_NOTIFICATION_MESSAGE, "Success");
    }

    public void sendMeetingNotification(MeetingDTO meetingDTO) {
        try {
            Group group = groupRepository.findOneById(meetingDTO.getGroupId());
            Set<String> studentEmailInGroupSet = group.getStudentGroupSet().stream().map(studentGroup -> studentGroup.getStudent().getEmail()).collect(Collectors.toSet());
            for (String studentEmail : studentEmailInGroupSet) {
                sendNotification(new NotificationDTO("You have meeting at " + meetingDTO.getScheduleTime() + " in " + group.getClassEntity().getName() + "from lecturer"
                        , meetingDTO.getLink(), studentEmail));
            }
        } catch (Exception e) {
            logger.error("{}{}", "Send meeting: ", e);
        }
    }

    public void sendReportNotification(CycleReportDTO cycleReportDTO) {
        try {
            Group group = groupRepository.findOneById(cycleReportDTO.getGroupId());
            sendNotification(new NotificationDTO("Cycle report of  " + group.getNumber() + " in " + group.getClassEntity().getName()
                    , "class/" + group.getClassEntity().getId() + "/group/" + group.getId() + "view=" + cycleReportDTO.getGroupId(), group.getClassEntity().getLecturer().getEmail()));
        } catch (Exception e) {
            logger.error("{}{}", "Send report: ", e);
        }
    }

    public void sendFeedbackNotification(CycleReportDTO cycleReportDTO) {
        try {
            Group group = groupRepository.findOneById(cycleReportDTO.getGroupId());
            Set<String> studentEmailInGroupSet = group.getStudentGroupSet().stream().map(studentGroup -> studentGroup.getStudent().getEmail()).collect(Collectors.toSet());
            for (String studentEmail : studentEmailInGroupSet) {
                sendNotification(new NotificationDTO("Feedback of  " + cycleReportDTO.getTitle() + " in " + group.getClassEntity().getName()
                        ,"class/" + group.getClassEntity().getId() + "/group/" + group.getId() + "view=" + cycleReportDTO.getGroupId(), studentEmail));
            }
        } catch (Exception e) {
            logger.error("{}{}", "Send report: ", e);
        }
    }
}
