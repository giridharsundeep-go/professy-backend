package com.greatleyposhley.professy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign Key to user_account table
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    @NotFound(action = NotFoundAction.IGNORE)
    private UserAccount userAccount;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Foreign Key to roles table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Roles role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_id_prefix")
    private EmployeeIdPrefix employeeIdPrefix = EmployeeIdPrefix.EMP;

    @Column(name = "employee_id_number", length = 50)
    private String employeeIdNumber;

    // Self-referencing Foreign Key for Manager
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Users manager;

    @Column(name = "location_country", length = 2)
    private String locationCountry;

    @Column(name = "location_state", length = 100)
    private String locationState;

    @Column(name = "location_city", length = 100)
    private String locationCity;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_work_model")
    private LocationWorkModel locationWorkModel = LocationWorkModel.HQ;

    @Column(name = "location_desk_code", length = 50)
    private String locationDeskCode;

    @Column(name = "profile_picture_url", length = 2048)
    private String profilePictureUrl;

    // --- Enums ---

    public enum EmployeeIdPrefix {
        EMP, CON, EXC
    }

    public enum LocationWorkModel {
        HQ, REMOTE, HYBRID
    }

    // --- Constructors ---

    public Users() {
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public EmployeeIdPrefix getEmployeeIdPrefix() {
        return employeeIdPrefix;
    }

    public void setEmployeeIdPrefix(EmployeeIdPrefix employeeIdPrefix) {
        this.employeeIdPrefix = employeeIdPrefix;
    }

    public String getEmployeeIdNumber() {
        return employeeIdNumber;
    }

    public void setEmployeeIdNumber(String employeeIdNumber) {
        this.employeeIdNumber = employeeIdNumber;
    }

    public Users getManager() {
        return manager;
    }

    public void setManager(Users manager) {
        this.manager = manager;
    }

    public String getLocationCountry() {
        return locationCountry;
    }

    public void setLocationCountry(String locationCountry) {
        this.locationCountry = locationCountry;
    }

    public String getLocationState() {
        return locationState;
    }

    public void setLocationState(String locationState) {
        this.locationState = locationState;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public LocationWorkModel getLocationWorkModel() {
        return locationWorkModel;
    }

    public void setLocationWorkModel(LocationWorkModel locationWorkModel) {
        this.locationWorkModel = locationWorkModel;
    }

    public String getLocationDeskCode() {
        return locationDeskCode;
    }

    public void setLocationDeskCode(String locationDeskCode) {
        this.locationDeskCode = locationDeskCode;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}