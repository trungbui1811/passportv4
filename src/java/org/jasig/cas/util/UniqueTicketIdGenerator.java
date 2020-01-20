/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.util;

/**
 *
 * @author TrungBH
 */
public interface UniqueTicketIdGenerator {
    String getNewTicketId(String paramString);
}
