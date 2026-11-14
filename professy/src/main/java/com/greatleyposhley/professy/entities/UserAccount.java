package com.greatleyposhley.professy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_account",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_account_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_user_account_phone", columnNames = "phone")
        },
        indexes = {
                @Index(name = "idx_email", columnList = "email"),
                @Index(name = "idx_phone", columnList = "phone")
        }
)
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "password", nullable = true, length = 255)
    @com.fasterxml.jackson.annotation.JsonProperty(access=com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name="credential_version",nullable=false)
    private int credentialVersion;
    @Column(name="password_setup_required",nullable=false)
    private boolean passwordSetupRequired;
    @Column(name="email_verified",nullable=false)
    private boolean emailVerified;
    public boolean getEmailVerified(){return emailVerified;}
    public void setEmailVerified(boolean value){emailVerified=value;}
    public int getCredentialVersion(){return credentialVersion;}
    public void setCredentialVersion(int value){credentialVersion=value;}
    public boolean getPasswordSetupRequired(){return passwordSetupRequired;}
    public void setPasswordSetupRequired(boolean value){passwordSetupRequired=value;}

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "org_id", nullable = false)
    private Long orgId;

    @Column(name = "role_id")
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_id_prefix", length = 3)
    private EmployeeIdPrefix employeeIdPrefix = EmployeeIdPrefix.EMP;

    @Column(name = "employee_id_number", length = 50)
    private String employeeIdNumber;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "location_country", length = 2)
    private String locationCountry;

    @Column(name = "location_state", length = 100)
    private String locationState;

    @Column(name = "location_city", length = 100)
    private String locationCity;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_work_model", length = 10)
    private LocationWorkModel locationWorkModel = LocationWorkModel.HQ;

    @Column(name = "location_desk_code", length = 50)
    private String locationDeskCode;

    @Column(name = "profile_picture_url", length = 2048)
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "org_role", nullable = false, length = 20)
    private OrgRole orgRole = OrgRole.ORG_USER;

    @Column(name="department_id") private Long departmentId;
    @Column(name="function_id") private Long functionId;
    @Column(name="division_id") private Long divisionId;
    @Transient private String departmentName;
    @Transient private String functionName;
    @Transient private String divisionName;
    @Transient private boolean organizationAssignmentProvided;
    @Transient private boolean departmentProvided,functionProvided,divisionProvided;
    public Long getDepartmentId(){return departmentId;}
    public void setDepartmentId(Long value){departmentId=value;organizationAssignmentProvided=true;departmentProvided=true;}
    public Long getFunctionId(){return functionId;}
    public void setFunctionId(Long value){functionId=value;organizationAssignmentProvided=true;functionProvided=true;}
    public Long getDivisionId(){return divisionId;}
    public void setDivisionId(Long value){divisionId=value;organizationAssignmentProvided=true;divisionProvided=true;}
    public String getDepartmentName(){return departmentName;}
    public void setDepartmentName(String value){departmentName=value;}
    public String getFunctionName(){return functionName;}
    public void setFunctionName(String value){functionName=value;}
    public String getDivisionName(){return divisionName;}
    public void setDivisionName(String value){divisionName=value;}
    @JsonIgnore public boolean isDepartmentProvided(){return departmentProvided;}
    @JsonIgnore public boolean isFunctionProvided(){return functionProvided;}
    @JsonIgnore public boolean isDivisionProvided(){return divisionProvided;}
    @JsonIgnore public boolean isOrganizationAssignmentProvided(){return organizationAssignmentProvided;}

    public UserAccount() {
    }

    @PrePersist
    protected void onCreate() {
        if (isActive == null) {
            isActive = true;
        }
        if (employeeIdPrefix == null) {
            employeeIdPrefix = EmployeeIdPrefix.EMP;
        }
        if (locationWorkModel == null) {
            locationWorkModel = LocationWorkModel.HQ;
        }
        if (orgRole == null) {
            orgRole = OrgRole.ORG_USER;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
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

    public OrgRole getOrgRole() {
        return orgRole;
    }

    public void setOrgRole(OrgRole orgRole) {
        this.orgRole = orgRole;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", gender=" + gender +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isActive=" + isActive +
                ", lastLogin=" + lastLogin +
                ", orgId=" + orgId +
                ", roleId=" + roleId +
                ", employeeIdPrefix=" + employeeIdPrefix +
                ", employeeIdNumber='" + employeeIdNumber + '\'' +
                ", managerId=" + managerId +
                ", locationCountry='" + locationCountry + '\'' +
                ", locationState='" + locationState + '\'' +
                ", locationCity='" + locationCity + '\'' +
                ", locationWorkModel=" + locationWorkModel +
                ", locationDeskCode='" + locationDeskCode + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", orgRole=" + orgRole +
                '}';
    }
}
