package com.eLearningPro.service;

import java.util.List;

import com.eLearningPro.dto.EnrollmentDTO;
import com.eLearningPro.enums.EnrollmentStatus;

public interface EnrollmentService {
    List<EnrollmentDTO> getAllEnrollments();
    EnrollmentDTO getEnrollmentById(Long id);
    EnrollmentDTO createEnrollment(EnrollmentDTO enrollmentDTO);
    EnrollmentDTO updateEnrollment(Long id, EnrollmentDTO enrollmentDTO);
    void deleteEnrollment(Long id);
    List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId);
    List<EnrollmentDTO> getEnrollmentsByCourse(Long courseId);
    EnrollmentDTO getEnrollmentByStudentAndCourse(Long studentId, Long courseId);
    EnrollmentDTO updateProgress(Long enrollmentId, Double progress);
    EnrollmentDTO markAsCompleted(Long enrollmentId);
    Long getTotalEnrollmentsCount();
    List<EnrollmentDTO> getEnrollmentsByStatus(EnrollmentStatus status);
    Long getCompletedEnrollmentsCount();
    Long getInProgressEnrollmentsCount();
    Long getCompletedEnrollmentsCountByCourse(Long courseId);
}