/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport;

import java.sql.Timestamp;

/**
 *
 * @author TrungBH
 */
public class Users {
    private String aliasName;
    private String birthPlace;
    private String cellphone;
    private Timestamp createDate;
    private Timestamp dateOfBirth;
    private String description;
    private String email;
    private String fax;
    private String fullName;
    private Long gender;
    private String identityCard;
    private Timestamp issueDateIdent;
    private Timestamp issueDatePassport;
    private String issuePlaceIdent;
    private String issuePlacePassport;
    private Timestamp lastBlockDate;
    private Timestamp lastChangePassword;
    private Long locationId;
    private Long loginFailureCount;
    private Timestamp lastLoginFailure;
    private Long managerId;
    private String passportNumber;
    private String password;
    private String staffCode;
    private Long status;
    private String telephone;
    private Long userTypeId;
    private String userName;
    private Long userId;
    private Long userRight;
    private String position;
    private String isCheck;
    private Long isAdmin;
    private Long unitTreeNodeId;
    private String managerName;
    private Long profileId;
    private Timestamp lastResetPassword;
    private String ip;
    private String ipLAN;
    private String rawPassword;
    private Long deptId;
    private String deptLevel;
    private Long posId;
    private String deptName;
    private Long checkIp;
    private Long checkIpLAN;
    private Timestamp lastLogin;
    private String deptDisplayName;
    private Long checkValidTime;
    private Timestamp validFrom;
    private Timestamp validTo;
    private Timestamp startTimeToChangePassword;
    private Timestamp lastLock;
    private Timestamp lastUnlock;
    private Long passwordChanged;
    private Timestamp currentTimestamp;

    public Timestamp getCurrentTimestamp() {
      return this.currentTimestamp;
    }

    public void setCurrentTimestamp(Timestamp currentTimestamp) {
      this.currentTimestamp = currentTimestamp;
    }

    public Timestamp getLastLock() {
      return this.lastLock;
    }

    public void setLastLock(Timestamp lastLock) {
      this.lastLock = lastLock;
    }

    public String getAliasName() {
      return this.aliasName;
    }

    public void setAliasName(String aliasName) {
      this.aliasName = aliasName;
    }

    public String getBirthPlace() {
      return this.birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
      this.birthPlace = birthPlace;
    }

    public String getCellphone() {
      return this.cellphone;
    }

    public void setCellphone(String cellphone) {
      this.cellphone = cellphone;
    }

    public Long getCheckIp() {
      return this.checkIp;
    }

    public void setCheckIp(Long checkIp) {
      this.checkIp = checkIp;
    }

    public Long getCheckIpLAN() {
      return this.checkIpLAN;
    }

    public void setCheckIpLAN(Long checkIpLAN) {
      this.checkIpLAN = checkIpLAN;
    }

    public Long getCheckValidTime() {
      return this.checkValidTime;
    }

    public void setCheckValidTime(Long checkValidTime) {
      this.checkValidTime = checkValidTime;
    }

    public Timestamp getCreateDate() {
      return this.createDate;
    }

    public void setCreateDate(Timestamp createDate) {
      this.createDate = createDate;
    }

    public Timestamp getDateOfBirth() {
      return this.dateOfBirth;
    }

    public void setDateOfBirth(Timestamp dateOfBirth) {
      this.dateOfBirth = dateOfBirth;
    }

    public String getDeptDisplayName() {
      return this.deptDisplayName;
    }

    public void setDeptDisplayName(String deptDisplayName) {
      this.deptDisplayName = deptDisplayName;
    }

    public Long getDeptId() {
      return this.deptId;
    }

    public void setDeptId(Long deptId) {
      this.deptId = deptId;
    }

    public String getDeptLevel() {
      return this.deptLevel;
    }

    public void setDeptLevel(String deptLevel) {
      this.deptLevel = deptLevel;
    }

    public String getDeptName() {
      return this.deptName;
    }

    public void setDeptName(String deptName) {
      this.deptName = deptName;
    }

    public String getDescription() {
      return this.description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getEmail() {
      return this.email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getFax() {
      return this.fax;
    }

    public void setFax(String fax) {
      this.fax = fax;
    }

    public String getFullName() {
      return this.fullName;
    }

    public void setFullName(String fullName) {
      this.fullName = fullName;
    }

    public Long getGender() {
      return this.gender;
    }

    public void setGender(Long gender) {
      this.gender = gender;
    }

    public String getIdentityCard() {
      return this.identityCard;
    }

    public void setIdentityCard(String identityCard) {
      this.identityCard = identityCard;
    }

    public String getIp() {
      return this.ip;
    }

    public void setIp(String ip) {
      this.ip = ip;
    }

    public String getIpLAN() {
      return this.ipLAN;
    }

    public void setIpLAN(String ipLAN) {
      this.ipLAN = ipLAN;
    }

    public Long getIsAdmin() {
      return this.isAdmin;
    }

    public void setIsAdmin(Long isAdmin) {
      this.isAdmin = isAdmin;
    }

    public String getIsCheck() {
      return this.isCheck;
    }

    public void setIsCheck(String isCheck) {
      this.isCheck = isCheck;
    }

    public Timestamp getIssueDateIdent() {
      return this.issueDateIdent;
    }

    public void setIssueDateIdent(Timestamp issueDateIdent) {
      this.issueDateIdent = issueDateIdent;
    }

    public Timestamp getIssueDatePassport() {
      return this.issueDatePassport;
    }

    public void setIssueDatePassport(Timestamp issueDatePassport) {
      this.issueDatePassport = issueDatePassport;
    }

    public String getIssuePlaceIdent() {
      return this.issuePlaceIdent;
    }

    public void setIssuePlaceIdent(String issuePlaceIdent) {
      this.issuePlaceIdent = issuePlaceIdent;
    }

    public String getIssuePlacePassport() {
      return this.issuePlacePassport;
    }

    public void setIssuePlacePassport(String issuePlacePassport) {
      this.issuePlacePassport = issuePlacePassport;
    }

    public Timestamp getLastBlockDate() {
      return this.lastBlockDate;
    }

    public void setLastBlockDate(Timestamp lastBlockDate) {
      this.lastBlockDate = lastBlockDate;
    }

    public Timestamp getLastChangePassword() {
      return this.lastChangePassword;
    }

    public void setLastChangePassword(Timestamp lastChangePassword) {
      this.lastChangePassword = lastChangePassword;
    }

    public Timestamp getLastLogin() {
      return this.lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
      this.lastLogin = lastLogin;
    }

    public Timestamp getLastLoginFailure() {
      return this.lastLoginFailure;
    }

    public void setLastLoginFailure(Timestamp lastLoginFailure) {
      this.lastLoginFailure = lastLoginFailure;
    }

    public Timestamp getLastResetPassword() {
      return this.lastResetPassword;
    }

    public void setLastResetPassword(Timestamp lastResetPassword) {
      this.lastResetPassword = lastResetPassword;
    }

    public Timestamp getLastUnlock() {
      return this.lastUnlock;
    }

    public void setLastUnlock(Timestamp lastUnlock) {
      this.lastUnlock = lastUnlock;
    }

    public Long getLocationId() {
      return this.locationId;
    }

    public void setLocationId(Long locationId) {
      this.locationId = locationId;
    }

    public Long getLoginFailureCount() {
      return this.loginFailureCount;
    }

    public void setLoginFailureCount(Long loginFailureCount) {
      this.loginFailureCount = loginFailureCount;
    }

    public Long getManagerId() {
      return this.managerId;
    }

    public void setManagerId(Long managerId) {
      this.managerId = managerId;
    }

    public String getManagerName() {
      return this.managerName;
    }

    public void setManagerName(String managerName) {
      this.managerName = managerName;
    }

    public String getPassportNumber() {
      return this.passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
      this.passportNumber = passportNumber;
    }

    public String getPassword() {
      return this.password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public Long getPasswordChanged() {
      return this.passwordChanged;
    }

    public void setPasswordChanged(Long passwordChanged) {
      this.passwordChanged = passwordChanged;
    }

    public Long getPosId() {
      return this.posId;
    }

    public void setPosId(Long posId) {
      this.posId = posId;
    }

    public String getPosition() {
      return this.position;
    }

    public void setPosition(String position) {
      this.position = position;
    }

    public Long getProfileId() {
      return this.profileId;
    }

    public void setProfileId(Long profileId) {
      this.profileId = profileId;
    }

    public String getRawPassword() {
      return this.rawPassword;
    }

    public void setRawPassword(String rawPassword) {
      this.rawPassword = rawPassword;
    }

    public String getStaffCode() {
      return this.staffCode;
    }

    public void setStaffCode(String staffCode) {
      this.staffCode = staffCode;
    }

    public Timestamp getStartTimeToChangePassword() {
      return this.startTimeToChangePassword;
    }

    public void setStartTimeToChangePassword(Timestamp startTimeToChangePassword) {
      this.startTimeToChangePassword = startTimeToChangePassword;
    }

    public Long getStatus() {
      return this.status;
    }

    public void setStatus(Long status) {
      this.status = status;
    }

    public String getTelephone() {
      return this.telephone;
    }

    public void setTelephone(String telephone) {
      this.telephone = telephone;
    }

    public Long getUnitTreeNodeId() {
      return this.unitTreeNodeId;
    }

    public void setUnitTreeNodeId(Long unitTreeNodeId) {
      this.unitTreeNodeId = unitTreeNodeId;
    }

    public Long getUserId() {
      return this.userId;
    }

    public void setUserId(Long userId) {
      this.userId = userId;
    }

    public String getUserName() {
      return this.userName;
    }

    public void setUserName(String userName) {
      this.userName = userName;
    }

    public Long getUserRight() {
      return this.userRight;
    }

    public void setUserRight(Long userRight) {
      this.userRight = userRight;
    }

    public Long getUserTypeId() {
      return this.userTypeId;
    }

    public void setUserTypeId(Long userTypeId) {
      this.userTypeId = userTypeId;
    }

    public Timestamp getValidFrom() {
      return this.validFrom;
    }

    public void setValidFrom(Timestamp validFrom) {
      this.validFrom = validFrom;
    }

    public Timestamp getValidTo() {
      return this.validTo;
    }

    public void setValidTo(Timestamp validTo) {
      this.validTo = validTo;
    }
}
