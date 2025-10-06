package com.elearning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.elearning.dto.StudentDTO;
import com.elearning.dto.UserDTO;
import com.elearning.enums.UserRole;
import com.elearning.enums.UserStatus;
import com.elearning.service.UserService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // UNIVERSAL REGISTRATION ENDPOINT - Uses UserDTO which should have all common fields
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UniversalRegistrationRequest registrationRequest) {
        try {
            System.out.println("Registration request received for: " + registrationRequest.getEmail() + " with role: " + registrationRequest.getRole());
            
            // Validate role - only allow STUDENT and INSTRUCTOR for public registration
            if (registrationRequest.getRole() == null) {
                registrationRequest.setRole(UserRole.ROLE_STUDENT); // Default to STUDENT
            }
            
            // Don't allow ADMIN registration through public endpoint for security
            if (registrationRequest.getRole() == UserRole.ROLE_ADMIN) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Invalid role",
                    "message", "Admin registration is not allowed through public endpoint"
                ));
            }
            
            UserDTO userDTO = new UserDTO();
            userDTO.setFirstName(registrationRequest.getFirstName());
            userDTO.setLastName(registrationRequest.getLastName());
            userDTO.setEmail(registrationRequest.getEmail());
            userDTO.setPassword(registrationRequest.getPassword());
            userDTO.setRole(registrationRequest.getRole());
            userDTO.setStatus(UserStatus.ACTIVE);
            
            UserDTO createdUser = userService.createUser(userDTO);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User registered successfully as " + registrationRequest.getRole(),
                "userId", createdUser.getId(),
                "email", createdUser.getEmail(),
                "role", createdUser.getRole()
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Registration failed",
                "message", e.getMessage()
            ));
        }
    }

    // STUDENT REGISTRATION ENDPOINT - SPECIFIC (Using UserDTO since StudentDTO might not have password field)
    @PostMapping("/students/register")
    public ResponseEntity<?> registerStudent(@Valid @RequestBody StudentRegistrationRequest registrationRequest) {
        try {
            // Create as UserDTO with STUDENT role
            UserDTO userDTO = new UserDTO();
            userDTO.setFirstName(registrationRequest.getFirstName());
            userDTO.setLastName(registrationRequest.getLastName());
            userDTO.setEmail(registrationRequest.getEmail());
            userDTO.setPassword(registrationRequest.getPassword());
            userDTO.setRole(UserRole.ROLE_STUDENT);
            userDTO.setStatus(UserStatus.ACTIVE);
            
            UserDTO createdUser = userService.createUser(userDTO);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Student registered successfully",
                "studentId", createdUser.getId(),
                "email", createdUser.getEmail(),
                "role", UserRole.ROLE_STUDENT
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Student registration failed",
                "message", e.getMessage()
            ));
        }
    }

    // INSTRUCTOR REGISTRATION ENDPOINT - Using UserDTO
    @PostMapping("/instructors/register")
    public ResponseEntity<?> registerInstructor(@Valid @RequestBody InstructorRegistrationRequest registrationRequest) {
        try {
            // Create as UserDTO with INSTRUCTOR role
            UserDTO userDTO = new UserDTO();
            userDTO.setFirstName(registrationRequest.getFirstName());
            userDTO.setLastName(registrationRequest.getLastName());
            userDTO.setEmail(registrationRequest.getEmail());
            userDTO.setPassword(registrationRequest.getPassword());
            userDTO.setRole(UserRole.ROLE_INSTRUCTOR);
            userDTO.setStatus(UserStatus.ACTIVE);
            
            UserDTO createdUser = userService.createUser(userDTO);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Instructor registered successfully",
                "instructorId", createdUser.getId(),
                "email", createdUser.getEmail(),
                "role", UserRole.ROLE_INSTRUCTOR
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Instructor registration failed",
                "message", e.getMessage()
            ));
        }
    }

    // ADMIN REGISTRATION ENDPOINT - Using UserDTO
    @PostMapping("/admins/register")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody AdminRegistrationRequest registrationRequest) {
        try {
            // Add security check here - only allow if current user is admin
            System.out.println("Admin registration attempt for: " + registrationRequest.getEmail());
            
            // Create as UserDTO with ADMIN role
            UserDTO userDTO = new UserDTO();
            userDTO.setFirstName(registrationRequest.getFirstName());
            userDTO.setLastName(registrationRequest.getLastName());
            userDTO.setEmail(registrationRequest.getEmail());
            userDTO.setPassword(registrationRequest.getPassword());
            userDTO.setRole(UserRole.ROLE_ADMIN);
            userDTO.setStatus(UserStatus.ACTIVE);
            
            UserDTO createdUser = userService.createUser(userDTO);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Admin registered successfully",
                "adminId", createdUser.getId(),
                "email", createdUser.getEmail(),
                "role", UserRole.ROLE_ADMIN
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Admin registration failed",
                "message", e.getMessage()
            ));
        }
    }

    // GET endpoints for different user types - Using existing service methods
    @GetMapping("/instructors")
    public ResponseEntity<List<UserDTO>> getAllInstructors() {
        // Use your existing service method with role filter
        List<UserDTO> instructors = userService.getUsersByRole(UserRole.ROLE_INSTRUCTOR);
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/instructors/{id}")
    public ResponseEntity<UserDTO> getInstructorById(@PathVariable Long id) {
        UserDTO instructor = userService.getUserById(id);
        // Verify the user is actually an instructor
        if (instructor != null && instructor.getRole() == UserRole.ROLE_INSTRUCTOR) {
            return ResponseEntity.ok(instructor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/admins")
    public ResponseEntity<List<UserDTO>> getAllAdmins() {
        List<UserDTO> admins = userService.getUsersByRole(UserRole.ROLE_ADMIN);
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/admins/{id}")
    public ResponseEntity<UserDTO> getAdminById(@PathVariable Long id) {
        UserDTO admin = userService.getUserById(id);
        // Verify the user is actually an admin
        if (admin != null && admin.getRole() == UserRole.ROLE_ADMIN) {
            return ResponseEntity.ok(admin);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // KEEP ALL YOUR EXISTING ENDPOINTS EXACTLY AS THEY WERE BEFORE...
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        userDTO.setId(id);
        UserDTO updatedUser = userService.updateUser(userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDTO> updateUserStatus(@PathVariable Long id, @RequestParam UserStatus status) {
        UserDTO updatedUser = userService.updateUserStatus(id, status);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserDTO> changeUserRole(@PathVariable Long id, @RequestParam UserRole role) {
        UserDTO updatedUser = userService.changeUserRole(id, role);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/unlock")
    public ResponseEntity<UserDTO> unlockAccount(@PathVariable Long id) {
        UserDTO updatedUser = userService.unlockAccount(id);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String keyword) {
        List<UserDTO> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable UserRole role) {
        List<UserDTO> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<UserDTO>> getUsersByStatus(@PathVariable UserStatus status) {
        List<UserDTO> users = userService.getUsersByStatus(status);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUserCount() {
        Long count = userService.getUserCount();
        return ResponseEntity.ok(Map.of("totalUsers", count));
    }

    @GetMapping("/count/role/{role}")
    public ResponseEntity<Map<String, Long>> getUserCountByRole(@PathVariable UserRole role) {
        Long count = userService.getUserCountByRole(role);
        return ResponseEntity.ok(Map.of("userCount", count));
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getUserStatistics() {
        Map<String, Long> statistics = userService.getUserStatistics();
        return ResponseEntity.ok(statistics);
    }

    // Student-specific endpoints (using your existing service methods)
    @GetMapping("/students")
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> students = userService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        StudentDTO student = userService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @PostMapping("/students")
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO createdStudent = userService.createStudent(studentDTO);
        return ResponseEntity.ok(createdStudent);
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO updatedStudent = userService.updateStudent(id, studentDTO);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        userService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/students/status/{status}")
    public ResponseEntity<List<StudentDTO>> getStudentsByStatus(@PathVariable UserStatus status) {
        List<StudentDTO> students = userService.getStudentsByStatus(status);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/progress/{minProgress}")
    public ResponseEntity<List<StudentDTO>> getStudentsByProgress(@PathVariable Double minProgress) {
        List<StudentDTO> students = userService.getStudentsByProgress(minProgress);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/average-progress")
    public ResponseEntity<Map<String, Double>> getAverageProgress() {
        Double averageProgress = userService.getAverageProgress();
        return ResponseEntity.ok(Map.of("averageProgress", averageProgress));
    }

    @GetMapping("/students/course/{courseId}/average-progress")
    public ResponseEntity<Map<String, Double>> getAverageProgressByCourse(@PathVariable Long courseId) {
        Double averageProgress = userService.getAverageProgressByCourse(courseId);
        return ResponseEntity.ok(Map.of("averageProgress", averageProgress));
    }

    @GetMapping("/students/{studentId}/course-progress")
    public ResponseEntity<Map<String, Object>> getStudentCourseProgress(@PathVariable Long studentId) {
        Map<String, Object> progress = userService.getStudentCourseProgress(studentId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/students/course/{courseId}")
    public ResponseEntity<List<StudentDTO>> getStudentsByCourse(@PathVariable Long courseId) {
        List<StudentDTO> students = userService.getStudentsByCourse(courseId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/count")
    public ResponseEntity<Map<String, Long>> getTotalStudentCount() {
        Long count = userService.getTotalStudentCount();
        return ResponseEntity.ok(Map.of("totalStudents", count));
    }
}

// REGISTRATION REQUEST DTOs (Keep these as inner classes or move to separate files)

class UniversalRegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole role;

    public UniversalRegistrationRequest() {}

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}

class StudentRegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String dateOfBirth;

    public StudentRegistrationRequest() {}

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}

class InstructorRegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String department;
    private String expertise;
    private String qualifications;

    public InstructorRegistrationRequest() {}

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getExpertise() { return expertise; }
    public void setExpertise(String expertise) { this.expertise = expertise; }
    
    public String getQualifications() { return qualifications; }
    public void setQualifications(String qualifications) { this.qualifications = qualifications; }
}

class AdminRegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String adminLevel;

    public AdminRegistrationRequest() {}

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }
}