/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport;

import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import javax.sql.DataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 *
 * @author TrungBH
 */
public class DBConnect {
    private DataSource dataSource;
    private GenericObjectPool connectionPool = null;
    private static com.passport.DBConnect instance;
    private final Log logger = LogFactory.getLog(getClass());
    private String userName;
    private String password;
    private String url;
    private String smsAcc;
    private String smsPass;
    private String smsUrl;
    private String smsTmpUrl;
    private String smsNum;

    public static com.passport.DBConnect getInstance() {
        if (instance == null)
            instance = new com.passport.DBConnect(); 
        return instance;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSmsAcc() {
        return this.smsAcc;
    }

    public void setSmsAcc(String smsAcc) {
        this.smsAcc = smsAcc;
    }

    public String getSmsNum() {
        return this.smsNum;
    }

    public void setSmsNum(String smsNum) {
        this.smsNum = smsNum;
    }

    public String getSmsPass() {
        return this.smsPass;
    }

    public void setSmsPass(String smsPass) {
        this.smsPass = smsPass;
    }

    public String getSmsTmpUrl() {
        return this.smsTmpUrl;
    }

    public void setSmsTmpUrl(String smsTmpUrl) {
        this.smsTmpUrl = smsTmpUrl;
    }

    public String getSmsUrl() {
        return this.smsUrl;
    }

    public void setSmsUrl(String smsUrl) {
        this.smsUrl = smsUrl;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private DBConnect() {
        this.logger.info("Init DBConnect");
        URL url1 = Thread.currentThread().getContextClassLoader().getResource("database.cfg");
        String filePath = URLDecoder.decode(url1.getPath());
        String decryptString = EncryptDecryptUtils.decryptFile(filePath);
        HashMap<String, String> map = new HashMap<>();
        String[] properties = decryptString.split("\r\n");
        for (String property : properties) {
            String[] temp = property.split("=", 2);
            if (temp.length == 2)
                map.put(temp[0], temp[1]); 
        } 
        this.userName = map.get("USERNAME");
        this.password = map.get("PASSWORD");
        this.url = map.get("DBURL");
        this.smsAcc = map.get("SmsAcc");
        this.smsPass = map.get("SmsPass");
        this.smsNum = map.get("SmsNum");
        this.smsUrl = map.get("SmsUrl");
        this.smsTmpUrl = map.get("SmsTmpUrl");
        this.logger.info("Decrypt database info successfull");
        this.logger.info("DBURL=" + this.url);
        this.logger.info("USERNAME=" + this.userName);
        this.logger.info("SmsUrl=" + this.smsUrl);
        try {
            Class.forName(Configuration.getInstance().getJdbcDriver());
            this.connectionPool = new GenericObjectPool(null);
            DriverManagerConnectionFactory driverManagerConnectionFactory = new DriverManagerConnectionFactory(this.url, this.userName, this.password);
            PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory((ConnectionFactory)driverManagerConnectionFactory, (ObjectPool)this.connectionPool, null, null, false, true);
            poolableConnectionFactory.setValidationQuery("SELECT 1 FROM DUAL");
            if (Configuration.getInstance().getMaxConnectionActive() != null) {
                this.connectionPool.setMaxActive(Configuration.getInstance().getMaxConnectionActive().intValue());
            } else {
                this.connectionPool.setMaxActive(100);
            } 
            if (Configuration.getInstance().getMaxConnectionIdle() != null) {
                this.connectionPool.setMaxIdle(Configuration.getInstance().getMaxConnectionIdle().intValue());
            } else {
                this.connectionPool.setMaxIdle(10);
            } 
            if (Configuration.getInstance().getMinConnectionIdle() != null) {
                this.connectionPool.setMinIdle(Configuration.getInstance().getMinConnectionIdle().intValue());
            } else {
                this.connectionPool.setMinIdle(0);
            } 
            this.dataSource = (DataSource)new PoolingDataSource((ObjectPool)this.connectionPool);
        } catch (ClassNotFoundException e) {
            this.logger.error("Cannot find jdbc driver: ", e);
        } catch (Exception ex) {
            this.logger.error("Cannot create connection pool: ", ex);
        } 
    }

    public Connection getConnection() {
        this.logger.info("Getting database connection: MaxActive=" + this.connectionPool.getMaxActive() + "/CurrentActive=" + this.connectionPool.getNumActive() + "/CurrentIdle=" + this.connectionPool.getNumIdle());
        try {
            return this.dataSource.getConnection();
        } catch (SQLException ex) {
            this.logger.error("Cannot get connection", ex);
            return null;
        } 
    }
}
