/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author TrungBH
 */
public class BaseDAO {
    protected final Log logger = LogFactory.getLog(getClass());
  
    public void closeResource(ResultSet rs) {
        try {
            if (rs != null)
                rs.close(); 
        } catch (Exception e) {
            this.logger.error("Close result set error.", e);
        } 
    }

    public void closeResource(PreparedStatement stmt) {
        try {
            if (stmt != null)
                stmt.close(); 
        } catch (Exception e) {
            this.logger.error("Close prepare statement error.", e);
        } 
    }

    public void closeResource(Connection conn) {
        try {
            if (conn != null)
                conn.close(); 
        } catch (Exception e) {
            this.logger.error("Close connection error.", e);
        } 
    }
}
