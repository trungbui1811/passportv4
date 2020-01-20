/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.services;

import java.util.Collection;
import org.jasig.cas.authentication.principal.Service;

/**
 *
 * @author TrungBH
 */
public interface ServicesManager {
    void save(RegisteredService paramRegisteredService);
  
    RegisteredService delete(long paramLong);

    RegisteredService findServiceBy(Service paramService);

    RegisteredService findServiceBy(long paramLong);

    Collection<RegisteredService> getAllServices();

    boolean matchesExistingService(Service paramService);
}
