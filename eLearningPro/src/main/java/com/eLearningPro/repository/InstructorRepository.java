package com.eLearningPro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eLearningPro.entity.Instructor;
import com.eLearningPro.enums.UserStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    
    Optional<Instructor> findByInstructorId(String instructorId);
    
    Optional<Instructor> findByEmail(String email);
    
    List<Instructor> findByDepartment(String department);
    
    List<Instructor> findBySpecialization(String specialization);
    
    List<Instructor> findByStatus(UserStatus status);    
    
    
    @Query("SELECT i FROM Instructor i WHERE i.yearsOfExperience >= :minExperience")
    List<Instructor> findByExperienceGreaterThanEqual(@Param("minExperience") Integer minExperience);
    
    @Query("SELECT i.department, COUNT(i) FROM Instructor i GROUP BY i.department")
    List<Object[]> countInstructorsByDepartment();
    
        
    long countByDepartment(String department);
}