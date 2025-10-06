
  package com.eLearningPro.repository;
  
  import org.springframework.data.jpa.repository.JpaRepository;
  import org.springframework.data.jpa.repository.Query;
  import org.springframework.data.repository.query.Param;
  import org.springframework.stereotype.Repository;

import com.eLearningPro.entity.Student;
import com.eLearningPro.entity.User;
import com.eLearningPro.enums.UserStatus;

import java.util.List;
  import java.util.Optional;
  
  @Repository
  public interface StudentRepository extends JpaRepository<Student, Long> {
    
	  
  Optional<Student> findByStudentId(String studentId);  
  
  List<Student> findByStatus(UserStatus status);
  
  List<Student> findByEnabledTrue();
  
  List<Student> findByLockedFalse();
  
  @Query("SELECT s FROM Student s WHERE s.completedCourses > :minCompletedCourses")
  List<Student> findTopPerformers(@Param("minCompletedCourses") int minCompletedCourses);

  @Query("SELECT s FROM Student s WHERE s.overallProgress >= :minProgress")
  List<Student> findByProgressGreaterThanEqual(@Param("minProgress") Double minProgress);

  Optional<Student> findByEmail(String email);
  
  long countByStatus(UserStatus status);
  
  @Query("SELECT AVG(s.overallProgress) FROM Student s WHERE s.overallProgress > 0")
  Double findAverageProgress(); 

  
  }
 