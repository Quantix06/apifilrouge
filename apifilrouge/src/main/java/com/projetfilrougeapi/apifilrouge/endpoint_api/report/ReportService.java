package com.projetfilrougeapi.apifilrouge.endpoint_api.report;

import com.projetfilrougeapi.apifilrouge.DTO.ReportRequest;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserController;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportManagerService reportManagerService;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository, ReportManagerService reportManagerService) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.reportManagerService = reportManagerService;
    }


    public EntityModel<Report> createReport(ReportRequest report) {
        User sender = userRepository.findById(report.getSenderUserId())
                .orElseThrow(() -> new RuntimeException("Sender user not found with id: " + report.getSenderUserId()));
        User reportedUser = userRepository.findById(report.getReportedUserId())
                .orElseThrow(() -> new RuntimeException("Reported user not found with id: " + report.getReportedUserId()));

        Report newReport = Report.builder()
                .reportType(ReportType.valueOf(report.getReportType()))
                .description(report.getDescription())
                .senderUser(sender)
                .reportedUser(reportedUser)
                .build();

        reportRepository.save(newReport);

        return EntityModel.of(newReport,
                linkTo(methodOn(ReportController.class).getReportById(newReport.getId())).withSelfRel(),
                linkTo(methodOn(ReportController.class).getAllReports()).withRel("reports"),
                linkTo(methodOn(UserController.class).getUserById(newReport.getSenderUser().getId())).withRel("senderUser"),
                linkTo(methodOn(UserController.class).getUserById(newReport.getReportedUser().getId())).withRel("reportedUser"));

    }

public EntityModel<Report> getReportById(Long id) {
                    Report report = reportRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
                    return EntityModel.of(report,
                            linkTo(methodOn(ReportController.class).getReportById(report.getId())).withSelfRel(),
                            linkTo(methodOn(ReportController.class).getAllReports()).withRel("reports"),
                            linkTo(methodOn(UserController.class).getUserById(report.getSenderUser().getId())).withRel("senderUser"),
                            linkTo(methodOn(UserController.class).getUserById(report.getReportedUser().getId())).withRel("reportedUser"));
                }

    public CollectionModel<EntityModel<Report>> getAllReports() {
        List<EntityModel<Report>> reports = reportRepository.findAll().stream()
                .map(report -> EntityModel.of(report,
                        linkTo(methodOn(ReportController.class).getReportById(report.getId())).withSelfRel(),
                        linkTo(methodOn(ReportController.class).getAllReports()).withRel("reports"),
                        linkTo(methodOn(UserController.class).getUserById(report.getSenderUser().getId())).withRel("senderUser"),
                        linkTo(methodOn(UserController.class).getUserById(report.getReportedUser().getId())).withRel("reportedUser")))
                .collect(Collectors.toList());

        return CollectionModel.of(reports,
                linkTo(methodOn(ReportController.class).getAllReports()).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
    }

    public EntityModel<User> getSenderUserByReportId(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));

        User senderUser = report.getSenderUser();
        return EntityModel.of(senderUser,
                linkTo(methodOn(UserController.class).getUserById(senderUser.getId())).withSelfRel(),
                linkTo(methodOn(ReportController.class).getReportById(report.getId())).withRel("report"),
                linkTo(methodOn(ReportController.class).getAllReports()).withRel("reports"));
    }


    public EntityModel<User> getReportedUserByReportId(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));

        User reportedUser = report.getReportedUser();
        return EntityModel.of(reportedUser,
                linkTo(methodOn(UserController.class).getUserById(reportedUser.getId())).withSelfRel(),
                linkTo(methodOn(ReportController.class).getReportById(report.getId())).withRel("report"),
                linkTo(methodOn(ReportController.class).getAllReports()).withRel("reports"));
    }

    public void deleteReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
        reportRepository.delete(report);
    }
}
