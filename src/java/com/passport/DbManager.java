/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import md5.PasswordService;
import org.w3c.dom.Document;

/**
 *
 * @author TrungBH
 */
public class DbManager extends BaseDAO {
    public static final int USER_NOT_EXIST = 0;
    public static final int PASSWORD_NOT_CORRECT = 1;
    public static final int USER_PASSWORD_OK = 2;
    public static final int USER_NOT_ACTIVATED = 3;
    public static final int DOMAIN_NOT_AUTHEN = 4;
    public static final int NEED_CHANGE_PASSWORD = 5;
    public static final int TEMPORARY_LOCK_USER = 6;
    public static final int PASSWORD_EXPIRE = 8;
    public static final int USER_EXPIRE = 9;
    public static final int DAY_FOR_CHANGE_PASS_EXPIRE = 10;
    public static final int DATABASE_ERROR = -1;
    public static final int BEFORE_VALID_TIME = 21;
    public static final int AFTER_VALID_TIME = 22;
    public static final int RESULT_TRUE = 1;
    public static final int RESULT_FALSE = 0;
    public static final int USER_INFOR_LACK = 20;
    public static final int DEFAULT_VALID_VALUE = 100;
    public static final int PASSWORD_NOT_CHANGE_AFTER_RESET = 0;
    public static final int PASSWORD_NOT_CHANGE_AFTER_UNLOCK_EXPIRED = -1;
    private Connection conn = null;
    public static final long A_DAY_TIME = 86400000L;
    public static final long A_MINUTE = 60000L;
    public static final String ACTOR = "Passport";
    public static final long ACTION_TIMEOUT = 60000L;
    private String wan;
    private String ip;
    private String mac;

    public String getWan() {
        return this.wan;
    }

    public void setWan(String wan) {
        this.wan = wan;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public DbManager() {
        try {
            this.conn = DBConnect.getInstance().getConnection();
        } catch (Exception e) {
            this.logger.error(e, e);
        } 
    }

    public void close() {
        this.logger.info("Connection is closed ");
        closeResource(this.conn);
    }

    public void logLogin(String userName) {
        PreparedStatement stmt = null;
        try {
            if (this.conn == null) {
                this.logger.error("Connection is null");
                return;
            } 
            String strSQL = "insert into Event_Log_Login(event_id, user_name, event_date, ip, wan, mac, action) values (log_seq.nextVal,?, to_char(sysdate, 'DD-Mon-YYYY:hh:mi:ssam'), ?,?,?,'LOGIN_SUCCESS' )";
            stmt = this.conn.prepareStatement(strSQL);
            stmt.setString(1, userName);
            stmt.setString(2, this.ip);
            stmt.setString(3, this.wan);
            stmt.setString(4, this.mac);
            stmt.executeUpdate();
        } catch (Exception ex) {
            this.logger.error(ex, ex);
        } finally {
            closeResource(stmt);
        } 
    }

    public void logLoginIncorrectPassword(String userName) {
        PreparedStatement stmt = null;
        try {
            if (this.conn == null) {
                this.logger.error("Connection is null");
                return;
            } 
            StringBuilder sb = new StringBuilder();
            sb.append("insert into ").append("Event_Log_Login(event_id, user_name, event_date, ip, wan, mac, action) ").append("values (log_seq.nextVal,?, systimestamp, ?,?,?, 'LOGIN_INCORRECT_PASSWORD')");
            stmt = this.conn.prepareStatement(sb.toString());
            stmt.setString(1, userName);
            stmt.setString(2, this.ip);
            stmt.setString(3, this.wan);
            stmt.setString(4, this.mac);
            stmt.executeUpdate();
        } catch (Exception ex) {
            this.logger.error(ex, ex);
        } finally {
            closeResource(stmt);
        } 
    }

    public AuthenticationErrorCode checkLackingInfo(Users ubo) {
        if (ubo.getEmail() == null || ubo.getEmail().trim().length() == 0 || ubo.getCellphone() == null || ubo.getCellphone().trim().length() == 0 || ubo.getIdentityCard() == null || ubo.getIdentityCard().trim().length() == 0 || ubo.getPosId() == null || ubo.getPosId().longValue() == 0L || ubo.getDeptId() == null || ubo.getDeptId().longValue() == 0L || (Configuration.getInstance().isIpFilter() && (ubo.getIp() == null || ubo.getIp().trim().length() == 0)))
            return AuthenticationErrorCode.USER_INFOR_LACK; 
        return AuthenticationErrorCode.SUCCESS;
    }

    public AuthenticationErrorCode checkValidTime(Users ubo) {
        Long checkValidTime = ubo.getCheckValidTime();
        if (checkValidTime != null && checkValidTime.longValue() == 1L) {
            Timestamp validFrom = ubo.getValidFrom();
            Timestamp validTo = ubo.getValidTo();
            Timestamp current = new Timestamp((new Date()).getTime());
            if (validFrom == null)
                return AuthenticationErrorCode.VALID_FROM_NULL; 
            if (validFrom != null && current.before(validFrom))
                return AuthenticationErrorCode.BEFORE_VALID_TIME; 
            if (validTo != null && current.after(new Timestamp(validTo.getTime() + 86400000L)))
                return AuthenticationErrorCode.AFTER_VALID_TIME; 
        } 
        return AuthenticationErrorCode.SUCCESS;
    }

    public AuthenticationErrorCode checkAccount(String userName, String password) {
        return checkAccount(userName, password, false);
    }

    public AuthenticationErrorCode checkAccount(String userName, String password, boolean isChangePass) {
        this.logger.info("Check user [" + userName + "]");
        PreparedStatement stmt = null;
        PreparedStatement stmtUpdate = null;
        PreparedStatement stmtLog = null;
        AuthenticationErrorCode errorCode = AuthenticationErrorCode.USER_NOT_EXIST;
        String ecryptPass = null;
        PasswordService pwdSrv = PasswordService.getInstance();
        try {
            ecryptPass = pwdSrv.encrypt1(userName, password);
        } catch (Exception ex) {
            this.logger.error(ex, ex);
        } 
        Users ubo = getUser(userName);
        if (ubo == null)
            return AuthenticationErrorCode.USER_NOT_EXIST; 
        if (ubo.getStatus().longValue() != 1L)
            return AuthenticationErrorCode.USER_NOT_ACTIVATED; 
        if (!isChangePass) {
            errorCode = checkLackingInfo(ubo);
            if (errorCode != AuthenticationErrorCode.SUCCESS)
                return errorCode; 
        } 
        errorCode = checkValidTime(ubo);
        if (errorCode != AuthenticationErrorCode.SUCCESS)
            return errorCode; 
        try {
            if (!isChangePass) {
              return AuthenticationErrorCode.NEED_CHANGE_PASSWORD;
            }  
            errorCode = AuthenticationErrorCode.PASSWORD_NOT_CORRECT;
            if (ubo.getPassword().replaceAll("\r\n", "").equalsIgnoreCase(ecryptPass.replaceAll("\r\n", ""))) {
                StringBuilder sb = new StringBuilder();
                sb.append("update users set ").append("LOGIN_FAILURE_COUNT = 0, ").append("LAST_LOGIN = systimestamp ").append("where user_id = ?");
                stmtUpdate = this.conn.prepareStatement(sb.toString());
                stmtUpdate.setLong(1, ubo.getUserId().longValue());
                stmtUpdate.executeUpdate();
                return AuthenticationErrorCode.USER_PASSWORD_OK;
            } 
        } catch (SQLException e) {
            this.logger.error(e, e);
            return AuthenticationErrorCode.DATABASE_ERROR;
        } catch (Exception e) {
            this.logger.error(e, e);
            return AuthenticationErrorCode.DATABASE_ERROR;
        } finally {
            closeResource(stmt);
            closeResource(stmtUpdate);
            closeResource(stmtLog);
        } 
        return errorCode;
    }

    public void logErrorSendSms(long userId, String userName, String message, String msisdn) {
        PreparedStatement stmtLog = null;
        String description = "Error send message to msisdn " + msisdn + " : \"" + message + "\"";
        try {
            String strSQL = "insert into Event_Log(event_id, user_name, action, event_date, description, actor, user_id) values (log_seq.nextVal,?, 'ERROR_SEND_SMS', to_char(sysdate, 'DD-Mon-YYYY:hh:mi:ssam'), ? ,?, ?)";
            stmtLog = this.conn.prepareStatement(strSQL);
            stmtLog.setString(1, userName);
            stmtLog.setString(2, description);
            stmtLog.setString(3, "Passport");
            stmtLog.setLong(4, userId);
            stmtLog.executeUpdate();
        } catch (Exception e) {
            this.logger.error(e, e);
        } finally {
            closeResource(stmtLog);
        } 
    }

    public void logSuccessSendSms(long userId, String userName, String message, String msisdn) {
        PreparedStatement stmtLog = null;
        String description = "Success send message to msisdn " + msisdn + " : \"" + message + "\"";
        try {
            String strSQL = "insert into Event_Log(event_id, user_name, action, event_date, description, actor, user_id) values (log_seq.nextVal,?, 'SUCCESS_SEND_SMS', to_char(sysdate, 'DD-Mon-YYYY:hh:mi:ssam'), ? ,?, ?)";
            stmtLog = this.conn.prepareStatement(strSQL);
            stmtLog.setString(1, userName);
            stmtLog.setString(2, description);
            stmtLog.setString(3, "Passport");
            stmtLog.setLong(4, userId);
            stmtLog.executeUpdate();
        } catch (Exception e) {
            this.logger.error(e, e);
        } finally {
            closeResource(stmtLog);
        } 
    }

    public int authenDomain(String domainCode, String strUserName) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            if (this.conn == null) {
                this.logger.error("Connection is null");
                return -1;
            } 
            Long appID = getAppID(domainCode);
            String strSQL = "SELECT count(*) from users u, role_user ru, role_object ro, objects o, roles r where u.user_id = ru.user_id and ru.role_id = r.role_id and r.role_id = ro.role_id and ro.object_id = o.object_id and r.status = 1 and ru.is_active = 1 and ru.is_admin = 0 and ro.is_active = 1 and o.status = 1 and u.user_name = ? and o.app_id = ?";
            stmt = this.conn.prepareStatement(strSQL);
            stmt.setString(1, strUserName.toLowerCase());
            stmt.setLong(2, appID.longValue());
            rs = stmt.executeQuery();
            if (rs.next() && rs.getLong(1) > 0L)
                return 1; 
        } catch (Exception e) {
            this.logger.error(e, e);
            return -1;
        } finally {
            closeResource(rs);
            closeResource(stmt);
        } 
        return 0;
    }

    public Document getRolesData(String strUserName, Document doc) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            if (this.conn == null) {
                this.logger.error("Connection is null");
                return doc;
            } 
            String strSQL = "select r.* from roles r, role_user ru, users u where is_active = 1 and is_admin = 0 and r.role_id = ru.role_id and ru.user_id = u.user_id and r.status = 1 and u.user_name = ?";
            stmt = this.conn.prepareStatement(strSQL);
            stmt.setString(1, strUserName.toLowerCase());
            long start = System.currentTimeMillis();
            rs = stmt.executeQuery();
            long end = System.currentTimeMillis();
            if (this.logger.isDebugEnabled())
                this.logger.debug("Thoi gian truy van lay danh sach vai tro nguoi dung: " + strUserName + " --> " + (end - start)); 
            start = end;
            JDBCUtil.add2Document(rs, doc, "Roles");
            end = System.currentTimeMillis();
            if (this.logger.isDebugEnabled())
                this.logger.debug("Thoi gian fetch danh sach vai tro nguoi dung: " + strUserName + " --> " + (end - start)); 
        } catch (Exception ex) {
            this.logger.error(ex, ex);
            return doc;
        } finally {
            closeResource(rs);
            closeResource(stmt);
        } 
        return doc;
    }

    public Document getUserData(String strUserName, Document doc) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            if (this.conn == null) {
                this.logger.error("Connection is null");
                return doc;
            } 
            String strSQL = "SELECT u.*, p.*, trunc(u.last_change_password)+ p.password_valid_time -trunc(systimestamp) as TIME_TO_PASSWORD_EXPIRE  FROM users u, profile p WHERE user_name = ? AND status = 1 AND u.profile_id = p.id";
            stmt = this.conn.prepareStatement(strSQL);
            stmt.setString(1, strUserName.toLowerCase());
            long start = System.currentTimeMillis();
            rs = stmt.executeQuery();
            long end = System.currentTimeMillis();
            if (this.logger.isDebugEnabled())
                this.logger.debug("Thoi gian truy van lay thong tin nguoi dung: " + strUserName + " --> " + (end - start)); 
            start = end;
            JDBCUtil.add2Document(rs, doc, "UserData");
            end = System.currentTimeMillis();
            if (this.logger.isDebugEnabled())
                this.logger.debug("Thoi gian fetch thong tin nguoi dung: " + strUserName + " --> " + (end - start)); 
        } catch (Exception e) {
            this.logger.error(e, e);
            return doc;
        } finally {
            closeResource(rs);
            closeResource(stmt);
        } 
        return doc;
    }

    public Document getDept(String strUserName, Document doc) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            if (this.conn == null) {
                this.logger.error("Connection is null");
                return doc;
            } 
            String strSQL = "select d.*, p.pos_code, p.pos_name, lo.location_code, lo.location_name, lo.location_type from department d, dept_user_pos dup, users u, position p, location lo where d.dept_id = dup.dept_id and u.user_id = dup.user_id and dup.pos_id = p.pos_id and d.location_id = lo.location_id and u.user_name = ?";
            stmt = this.conn.prepareStatement(strSQL);
            stmt.setString(1, strUserName.toLowerCase());
            long start = System.currentTimeMillis();
            rs = stmt.executeQuery();
            long end = System.currentTimeMillis();
            if (this.logger.isDebugEnabled())
                this.logger.debug("Thoi gian truy van lay danh sach phong ban nguoi dung: " + strUserName + " --> " + (end - start)); 
            start = end;
            JDBCUtil.add2Document(rs, doc, "Depts");
            end = System.currentTimeMillis();
            if (this.logger.isDebugEnabled())
                this.logger.debug("Thoi gian fetch danh sach phong ban nguoi dung: " + strUserName + " --> " + (end - start)); 
        } catch (Exception e) {
            this.logger.error(e, e);
            return doc;
        } finally {
            closeResource(rs);
            closeResource(stmt);
        } 
        return doc;
    }

    public Document getAppMenu(String appCode, Document doc) throws Exception {
        PreparedStatement stmt1 = null;
        ResultSet rs1 = null;
        try {
            if (this.conn == null) {
                this.logger.error("Connection is null");
                return doc;
            } 
            Long appID = getAppID(appCode);
            String strSQL = "SELECT DISTINCT o.* from objects o where o.app_id = ?";
            stmt1 = this.conn.prepareStatement(strSQL);
            stmt1.setLong(1, appID.longValue());
            long start = System.currentTimeMillis();
            rs1 = stmt1.executeQuery();
            long end = System.currentTimeMillis();
            if (this.logger.isDebugEnabled())
                this.logger.debug("Thoi gian truy van lay danh sach chuc nang ung dung: " + (end - start)); 
            start = end;
            JDBCUtil.add2Document(rs1, doc, "ObjectAll");
            end = System.currentTimeMillis();
            if (this.logger.isDebugEnabled())
                this.logger.debug("Thoi gian fetch danh sach chuc nang ung dung: " + (end - start)); 
        } catch (Exception e) {
            this.logger.error(e, e);
            return doc;
        } finally {
            closeResource(rs1);
            closeResource(stmt1);
        } 
        return doc;
    }

    public Document getListObjects(String strUserName, String appCode, Document doc) throws Exception {
        PreparedStatement stmt1 = null;
        PreparedStatement stmtCheck = null;
        ResultSet rsCheck = null;
        ResultSet rs1 = null;
        try {
            if (this.conn == null) {
                this.logger.error("Connection is null");
                return doc;
            } 
            Long appID = getAppID(appCode);
            String strSQL = "SELECT DISTINCT o.* from users u, role_user ru, role_object ro, objects o, roles r where u.user_id = ru.user_id and ru.role_id = r.role_id and ru.role_id = ro.role_id and ro.object_id = o.object_id and r.status = 1 and ru.is_active = 1 and ru.is_admin = 0 and ro.is_active = 1 and o.status = 1 and u.user_name = ? and o.app_id = ?";
            stmt1 = this.conn.prepareStatement(strSQL);
            stmt1.setString(1, strUserName.toLowerCase());
            stmt1.setLong(2, appID.longValue());
            long start = System.currentTimeMillis();
            rs1 = stmt1.executeQuery();
            long end = System.currentTimeMillis();
            if (this.logger.isDebugEnabled())
                this.logger.debug("Thoi gian truy van lay danh sach quyen nguoi dung: " + strUserName + " --> " + (end - start)); 
            start = end;
            JDBCUtil.add2Document(rs1, doc, "ObjectAll");
            end = System.currentTimeMillis();
            if (this.logger.isDebugEnabled())
                this.logger.debug("Thoi gian fetch danh sach quyen nguoi dung: " + strUserName + " --> " + (end - start)); 
        } catch (Exception e) {
            this.logger.error(e, e);
            return doc;
        } finally {
            closeResource(rs1);
            closeResource(rsCheck);
            closeResource(stmtCheck);
            closeResource(stmt1);
        } 
        return doc;
    }

    public int changePassword(String userName, String oldPassword, String newPassword) {
        PreparedStatement stmt = null;
        PreparedStatement stmtPass = null;
        String oldEcryptPass = null;
        String newEcryptPass = null;
        PasswordService pwdSrv = PasswordService.getInstance();
        try {
          oldEcryptPass = pwdSrv.encrypt1(userName, oldPassword);
          newEcryptPass = pwdSrv.encrypt1(userName, newPassword);
        } catch (Exception ex) {
          this.logger.error(ex, ex);
        } 
        String strSQL = "UPDATE users set LAST_CHANGE_PASSWORD = to_char(sysdate, 'DD-Mon-YYYY:hh:mi:ssam'), password = ?, passwordchanged = 1 WHERE user_name = ? AND lower(password) = ?";
        try {
            if (this.conn == null) {
                this.logger.error("Connection is null");
                return -1;
            } 
            Long userId = getUserId(userName);
            if (userId == null || userId.longValue() == 0L)
                return 0; 
            stmt = this.conn.prepareStatement(strSQL);
            stmt.setString(1, newEcryptPass);
            stmt.setString(2, userName.toLowerCase());
            stmt.setString(3, (oldEcryptPass != null) ? oldEcryptPass.toLowerCase() : "");
            int i = stmt.executeUpdate();
            if (i != 0) {
                strSQL = "insert into Event_Log(event_id, user_name, action, event_date, ip, wan, mac, user_id) values (log_seq.nextVal,?, 'PASSWORD_CHANGE', to_char(sysdate, 'DD-Mon-YYYY:hh:mi:ssam'), ?,?, ?, ?)";
                stmtPass = this.conn.prepareStatement(strSQL);
                stmtPass.setString(1, userName);
                stmtPass.setString(2, this.ip);
                stmtPass.setString(3, this.wan);
                stmtPass.setString(4, this.mac);
                stmtPass.setLong(5, userId.longValue());
                stmtPass.executeUpdate();
                return 1;
            } 
        } catch (Exception e) {
            this.logger.error(e, e);
            return -1;
        } finally {
            closeResource(stmt);
            closeResource(stmtPass);
        } 
        return 0;
    }

    public Long getUserId(String user) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Long iReturn = Long.valueOf(0L);
        try {
            if (this.conn == null) {
                this.logger.error("Connection is null");
                return iReturn;
            } 
            String strSQL = "select user_id from users where user_name = ? And status = 1";
            stmt = this.conn.prepareStatement(strSQL);
            stmt.setString(1, user.toLowerCase());
            rs = stmt.executeQuery();
            if (rs.next()) {
                iReturn = Long.valueOf(rs.getLong("user_id"));
            } else {
                iReturn = Long.valueOf(0L);
            } 
        } catch (Exception e) {
            this.logger.error(e);
            return Long.valueOf(0L);
        } finally {
            closeResource(rs);
            closeResource(stmt);
        } 
        return iReturn;
    }

    public String getAppLockMessage(String appCode) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String iReturn = "";
        try {
            if (this.conn == null) {
                this.logger.error("Connection is null");
                return iReturn;
            } 
            String strSQL = "select lock_description from applications where lower(app_code)= ?";
            stmt = this.conn.prepareStatement(strSQL);
            stmt.setString(1, appCode.toLowerCase());
            rs = stmt.executeQuery();
            if (rs.next()) {
                iReturn = rs.getString("lock_description");
            } else {
                iReturn = "";
            } 
        } catch (Exception e) {
            this.logger.error(e);
            iReturn = "";
        } finally {
            closeResource(rs);
            closeResource(stmt);
        } 
        return iReturn;
    }

    public Long getAppID(String appCode) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Long iReturn = Long.valueOf(0L);
        try {
            if (this.conn == null) {
                this.logger.error("Connection is null");
                return iReturn;
            } 
            String strSQL = "select app_id, status from applications where lower(app_code)= ?";
            stmt = this.conn.prepareStatement(strSQL);
            stmt.setString(1, appCode.toLowerCase());
            rs = stmt.executeQuery();
            if (rs.next()) {
                if (rs.getLong("status") == 1L) {
                    iReturn = Long.valueOf(rs.getLong("app_id"));
                } else {
                    iReturn = Long.valueOf(-1L);
                } 
            } else {
                iReturn = Long.valueOf(0L);
            } 
        } catch (Exception e) {
            this.logger.error(e);
            return Long.valueOf(0L);
        } finally {
            closeResource(rs);
            closeResource(stmt);
        } 
        return iReturn;
    }

    public Document getListApp(String strUserName, Document doc) throws Exception {
        strUserName = strUserName.trim().toLowerCase();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            if (this.conn == null) {
                this.logger.error("Connection is null");
                return doc;
            } 
            String strSQL = "select DISTINCT(a.app_code), a.app_name, a.description from applications a, users u, role_user ru, roles r, role_object ro, objects o where u.user_id = ru.user_id and   ru.role_id = r.role_id and   r.role_id = ro.role_id and   ro.object_id = o.object_id and   o.app_id = a.app_id and   a.status = 1 and   o.status = 1 and   r.status = 1 and   ro.is_active = 1 and   ru.is_active = 1 and   ru.is_admin = 0 and   u.user_name = ?";
            stmt = this.conn.prepareStatement(strSQL);
            stmt.setString(1, strUserName);
            rs = stmt.executeQuery();
            JDBCUtil.add2Document(rs, doc, "AppAll");
        } catch (Exception e) {
            this.logger.error(e, e);
            return doc;
        } finally {
            closeResource(rs);
            closeResource(stmt);
        } 
        return doc;
    }

    public String getCellphone(String user) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String iReturn = "";
        try {
            if (this.conn == null) {
                this.logger.error("Connection is null");
                return "";
            } 
            String strSQL = "select cellphone from users where user_name = ? And status = 1";
            stmt = this.conn.prepareStatement(strSQL);
            stmt.setString(1, user.toLowerCase());
            rs = stmt.executeQuery();
            if (rs.next()) {
                iReturn = rs.getString("cellphone");
            } else {
                iReturn = "";
            } 
        } catch (Exception e) {
            this.logger.error(e);
            return "";
        } finally {
            closeResource(rs);
            closeResource(stmt);
        } 
        return iReturn;
    }

    public Long getCheckIp(String user) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Long iReturn = Long.valueOf(-1L);
        try {
            if (this.conn == null) {
                this.logger.error("Connection is null");
                return Long.valueOf(-1L);
            } 
            String strSQL = "select CHECK_IP from users where user_name = ?";
            stmt = this.conn.prepareStatement(strSQL);
            stmt.setString(1, user.toLowerCase());
            rs = stmt.executeQuery();
            if (rs.next()) {
                iReturn = Long.valueOf(rs.getLong("CHECK_IP"));
            } else {
                iReturn = Long.valueOf(-1L);
            } 
        } catch (Exception e) {
            this.logger.error(e, e);
            return Long.valueOf(-1L);
        } finally {
            closeResource(rs);
            closeResource(stmt);
        } 
        return iReturn;
    }

    public Users getUser(String userName) {
        if (this.conn == null) {
          this.logger.error("Connection is null");
          return null;
        } 
        Users ubo = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String strSQL = "SELECT user_name, password,status, passwordchanged,  LAST_LOGIN_FAILURE,LAST_CHANGE_PASSWORD,LAST_LOGIN,LAST_RESET_PASSWORD,  start_time_to_change_password, last_unlock, last_lock,  dept_id, pos_id, email, cellphone, identity_card, ip, user_id,  check_valid_time, valid_from, valid_to, LAST_BLOCK_DATE, systimestamp  FROM users  where user_name = ? and status =  1";
            stmt = this.conn.prepareStatement(strSQL);
            stmt.setString(1, userName.toLowerCase());
            rs = stmt.executeQuery();
            if (rs.next()) {
                ubo = new Users();
                ubo.setEmail(rs.getString("email"));
                ubo.setCellphone(rs.getString("cellphone"));
                ubo.setIdentityCard(rs.getString("identity_card"));
                ubo.setPosId(Long.valueOf(rs.getLong("pos_id")));
                ubo.setDeptId(Long.valueOf(rs.getLong("dept_id")));
                ubo.setIp(rs.getString("ip"));
                ubo.setUserId(Long.valueOf(rs.getLong("user_id")));
                ubo.setPasswordChanged(Long.valueOf(rs.getLong("passwordchanged")));
                ubo.setStatus(Long.valueOf(rs.getLong("status")));
                ubo.setLastLoginFailure(rs.getTimestamp("LAST_LOGIN_FAILURE"));
                ubo.setLastChangePassword(rs.getTimestamp("LAST_CHANGE_PASSWORD"));
                ubo.setLastLogin(rs.getTimestamp("LAST_LOGIN"));
                ubo.setLastResetPassword(rs.getTimestamp("LAST_RESET_PASSWORD"));
                ubo.setCheckValidTime(Long.valueOf(rs.getLong("check_valid_time")));
                ubo.setValidFrom(rs.getTimestamp("valid_from"));
                ubo.setValidTo(rs.getTimestamp("valid_to"));
                ubo.setUserName(rs.getString("user_name"));
                ubo.setPassword(rs.getString("password"));
                ubo.setStartTimeToChangePassword(rs.getTimestamp("start_time_to_change_password"));
                ubo.setLastUnlock(rs.getTimestamp("last_unlock"));
                ubo.setLastLock(rs.getTimestamp("last_lock"));
                ubo.setLastBlockDate(rs.getTimestamp("LAST_BLOCK_DATE"));
                ubo.setCurrentTimestamp(rs.getTimestamp("systimestamp"));
            } 
        } catch (Exception e) {
            this.logger.error("Get info for user [" + userName + "] error.", e);
            ubo = null;
        } finally {
            closeResource(rs);
            closeResource(stmt);
        } 
        return ubo;
    }

    public int lockAccount(long userId) throws Exception {
        PreparedStatement stmt = null;
        try {
            String strSQL = "UPDATE users set status = 0, last_lock = systimestamp WHERE user_id = ?";
            stmt = this.conn.prepareStatement(strSQL);
            stmt.setLong(1, userId);
            int result = stmt.executeUpdate();
            return result;
        } catch (Exception e) {
            this.logger.error("Lock account error.", e);
            throw e;
        } finally {
            closeResource(stmt);
        } 
    }

    public int lockAccount(String userName) throws Exception {
        PreparedStatement stmt = null;
        try {
            String strSQL = "UPDATE users set status = 0, last_lock = systimestamp WHERE user_name = ?";
            stmt = this.conn.prepareStatement(strSQL);
            stmt.setString(1, userName.toLowerCase());
            int result = stmt.executeUpdate();
            closeResource(stmt);
            return result;
        } catch (Exception e) {
            this.logger.error("Lock account error.", e);
            throw e;
        } finally {
            if (stmt != null)
                stmt.close(); 
        } 
    }

    public List<String> getWhiteListIp() throws SQLException {
        List<String> ips = new ArrayList<>();
        if (this.conn == null) {
            this.logger.error("Connection is null");
            return null;
        } 
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "select ip from white_list";
            stmt = this.conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                ips.add(rs.getString("ip"));
                this.logger.info("Ip redirect : " + (String)ips.get(0));
            } 
        } catch (SQLException ex) {
            this.logger.error("Error when getting white list ip ", ex);
            closeResource(stmt);
            closeResource(rs);
            throw ex;
        } finally {
            closeResource(stmt);
            closeResource(rs);
        } 
        return ips;
    }
}
