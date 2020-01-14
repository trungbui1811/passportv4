/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport;

/**
 *
 * @author TrungBH
 */
public enum AuthenticationErrorCode {
    SUCCESS, PASSWORD_NOT_CORRECT, USER_NOT_ACTIVATED, PASSWORD_EXPIRE, USER_EXPIRE, TEMPORARY_LOCK_USER, USER_NOT_EXIST, NEED_CHANGE_PASSWORD, DAY_FOR_CHANGE_PASS_EXPIRE, DATABASE_ERROR, USER_INFOR_LACK, BEFORE_VALID_TIME, AFTER_VALID_TIME, USER_PASSWORD_OK, VALID_FROM_NULL;
}
