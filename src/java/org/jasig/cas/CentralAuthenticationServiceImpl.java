/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.inspektr.audit.annotation.Auditable;
import org.inspektr.audit.spi.support.DefaultAuditableActionResolver;
import org.inspektr.audit.spi.support.ObjectCreationAuditableActionResolver;
import org.inspektr.audit.spi.support.ReturnValueAsStringResourceResolver;
import org.inspektr.common.ioc.annotation.NotNull;
import org.inspektr.statistics.annotation.Statistic;
import org.jasig.cas.audit.spi.ServiceResourceResolver;
import org.jasig.cas.audit.spi.TicketAsFirstParameterResourceResolver;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationManager;
import org.jasig.cas.authentication.MutableAuthentication;
import org.jasig.cas.authentication.handle.AuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.PersistentIdGenerator;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.authentication.principal.ShibbolethCompatiblePersistentIdGenerator;
import org.jasig.cas.authentication.principal.SimplePrincipal;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.services.UnauthorizedProxyingException;
import org.jasig.cas.services.UnauthorizedServiceException;
import org.jasig.cas.services.UnauthorizedSsoServiceException;
import org.jasig.cas.ticket.ExpirationPolicy;
import org.jasig.cas.ticket.InvalidTicketException;
import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketCreationException;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.TicketGrantingTicketImpl;
import org.jasig.cas.ticket.TicketValidationException;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.util.UniqueTicketIdGenerator;
import org.jasig.cas.validation.Assertion;
import org.jasig.cas.validation.ImmutableAssertionImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 *
 * @author TrungBH
 */
public final class CentralAuthenticationServiceImpl implements CentralAuthenticationService{
    private final Log log = LogFactory.getLog(getClass());

    @NotNull
    private TicketRegistry ticketRegistry;

    @NotNull
    private TicketRegistry serviceTicketRegistry;

    @NotNull
    private AuthenticationManager authenticationManager;

    @NotNull
    private UniqueTicketIdGenerator ticketGrantingTicketUniqueTicketIdGenerator;

    @NotNull
    private Map<String, UniqueTicketIdGenerator> uniqueTicketIdGeneratorsForService;

    @NotNull
    private ExpirationPolicy ticketGrantingTicketExpirationPolicy;

    @NotNull
    private ExpirationPolicy serviceTicketExpirationPolicy;

    @NotNull
    private ServicesManager servicesManager;

    @NotNull
    private PersistentIdGenerator persistentIdGenerator = (PersistentIdGenerator)new ShibbolethCompatiblePersistentIdGenerator();

    @Auditable(action = "TICKET_GRANTING_TICKET_DESTROYED", actionResolverClass = DefaultAuditableActionResolver.class, resourceResolverClass = TicketAsFirstParameterResourceResolver.class)
    @Statistic(name = "DESTROY_TICKET_GRANTING_TICKET", requiredPrecision = {Statistic.Precision.DAY, Statistic.Precision.MINUTE, Statistic.Precision.HOUR})
    @Transactional(readOnly = false)
    public void destroyTicketGrantingTicket(String ticketGrantingTicketId) {
      Assert.notNull(ticketGrantingTicketId);
      if (this.log.isDebugEnabled())
        this.log.debug("Removing ticket [" + ticketGrantingTicketId + "] from registry."); 
      TicketGrantingTicket ticket = (TicketGrantingTicket)this.ticketRegistry.getTicket(ticketGrantingTicketId, TicketGrantingTicket.class);
      if (ticket == null)
        return; 
      if (this.log.isDebugEnabled())
        this.log.debug("Ticket found.  Expiring and then deleting."); 
      ticket.expire();
      this.ticketRegistry.deleteTicket(ticketGrantingTicketId);
    }

    @Auditable(action = "SERVICE_TICKET", successSuffix = "_CREATED", failureSuffix = "_NOT_CREATED", actionResolverClass = ObjectCreationAuditableActionResolver.class, resourceResolverClass = ServiceResourceResolver.class)
    @Statistic(name = "GRANT_SERVICE_TICKET", requiredPrecision = {Statistic.Precision.DAY, Statistic.Precision.MINUTE, Statistic.Precision.HOUR})
    @Transactional(readOnly = false)
    public String grantServiceTicket(String ticketGrantingTicketId, Service service, Credentials credentials) throws TicketException {
      Assert.notNull(ticketGrantingTicketId, "ticketGrantingticketId cannot be null");
      Assert.notNull(service, "service cannot be null");
      TicketGrantingTicket ticketGrantingTicket = (TicketGrantingTicket)this.ticketRegistry.getTicket(ticketGrantingTicketId, TicketGrantingTicket.class);
      if (ticketGrantingTicket == null)
        throw new InvalidTicketException(); 
      synchronized (ticketGrantingTicket) {
        if (ticketGrantingTicket.isExpired()) {
          this.ticketRegistry.deleteTicket(ticketGrantingTicketId);
          throw new InvalidTicketException();
        } 
      } 
      RegisteredService registeredService = this.servicesManager.findServiceBy(service);
      if (registeredService == null || !registeredService.isEnabled()) {
        this.log.warn("ServiceManagement: Unauthorized Service Access. Service [" + service.getId() + "] not found in Service Registry.");
        throw new UnauthorizedServiceException();
      } 
      if (!registeredService.isSsoEnabled() && credentials == null && ticketGrantingTicket.getCountOfUses() > 0) {
        this.log.warn("ServiceManagement: Service Not Allowed to use SSO.  Service [" + service.getId() + "]");
        throw new UnauthorizedSsoServiceException();
      } 
      if (credentials != null)
        try {
          Authentication authentication = this.authenticationManager.authenticate(credentials);
          Authentication originalAuthentication = ticketGrantingTicket.getAuthentication();
          if (!authentication.getPrincipal().equals(originalAuthentication.getPrincipal()) || !authentication.getAttributes().equals(originalAuthentication.getAttributes()))
            throw new TicketCreationException(); 
        } catch (AuthenticationException e) {
          throw new TicketCreationException(e);
        }  
      UniqueTicketIdGenerator serviceTicketUniqueTicketIdGenerator = this.uniqueTicketIdGeneratorsForService.get(service.getClass().getName());
        ServiceTicket serviceTicket = ticketGrantingTicket.grantServiceTicket(serviceTicketUniqueTicketIdGenerator.getNewTicketId("ST"), service, this.serviceTicketExpirationPolicy, (credentials != null));
      this.serviceTicketRegistry.addTicket((Ticket)serviceTicket);
      if (this.log.isInfoEnabled())
        this.log.info("Granted service ticket [" + serviceTicket.getId() + "] for service [" + service.getId() + "] for user [" + serviceTicket.getGrantingTicket().getAuthentication().getPrincipal().getId() + "]"); 
      return serviceTicket.getId();
    }

    @Auditable(action = "SERVICE_TICKET", successSuffix = "_CREATED", failureSuffix = "_NOT_CREATED", actionResolverClass = ObjectCreationAuditableActionResolver.class, resourceResolverClass = ServiceResourceResolver.class)
    @Statistic(name = "GRANT_SERVICE_TICKET", requiredPrecision = {Statistic.Precision.DAY, Statistic.Precision.MINUTE, Statistic.Precision.HOUR})
    @Transactional(readOnly = false)
    public String grantServiceTicket(String ticketGrantingTicketId, Service service) throws TicketException {
      return grantServiceTicket(ticketGrantingTicketId, service, null);
    }

    @Auditable(action = "PROXY_GRANTING_TICKET", successSuffix = "_CREATED", failureSuffix = "_NOT_CREATED", actionResolverClass = ObjectCreationAuditableActionResolver.class, resourceResolverClass = ReturnValueAsStringResourceResolver.class)
    @Statistic(name = "GRANT_PROXY_TICKET", requiredPrecision = {Statistic.Precision.DAY, Statistic.Precision.MINUTE, Statistic.Precision.HOUR})
    @Transactional(readOnly = false)
    public String delegateTicketGrantingTicket(String serviceTicketId, Credentials credentials) throws TicketException {
      Assert.notNull(serviceTicketId, "serviceTicketId cannot be null");
      Assert.notNull(credentials, "credentials cannot be null");
      try {
        Authentication authentication = this.authenticationManager.authenticate(credentials);
        ServiceTicket serviceTicket = (ServiceTicket)this.serviceTicketRegistry.getTicket(serviceTicketId, ServiceTicket.class);
        if (serviceTicket == null || serviceTicket.isExpired())
          throw new InvalidTicketException(); 
        RegisteredService registeredService = this.servicesManager.findServiceBy(serviceTicket.getService());
        if (registeredService == null || !registeredService.isEnabled() || !registeredService.isAllowedToProxy()) {
          this.log.warn("ServiceManagement: Service Attempted to Proxy, but is not allowed.  Service: [" + serviceTicket.getService().getId() + "]");
          throw new UnauthorizedProxyingException();
        } 
        TicketGrantingTicket ticketGrantingTicket = serviceTicket.grantTicketGrantingTicket(this.ticketGrantingTicketUniqueTicketIdGenerator.getNewTicketId("TGT"), authentication, this.ticketGrantingTicketExpirationPolicy);
        this.ticketRegistry.addTicket((Ticket)ticketGrantingTicket);
        return ticketGrantingTicket.getId();
      } catch (AuthenticationException e) {
        throw new TicketCreationException(e);
      } 
    }

    @Auditable(action = "SERVICE_TICKET_VALIDATE", successSuffix = "D", failureSuffix = "_FAILED", actionResolverClass = ObjectCreationAuditableActionResolver.class, resourceResolverClass = TicketAsFirstParameterResourceResolver.class)
    @Statistic(name = "SERVICE_TICKET_VALIDATE", requiredPrecision = {Statistic.Precision.DAY, Statistic.Precision.MINUTE, Statistic.Precision.HOUR})
    @Transactional(readOnly = false)
    public Assertion validateServiceTicket(String serviceTicketId, Service service) throws TicketException {
      Assert.notNull(serviceTicketId, "serviceTicketId cannot be null");
      Assert.notNull(service, "service cannot be null");
      ServiceTicket serviceTicket = (ServiceTicket)this.serviceTicketRegistry.getTicket(serviceTicketId, ServiceTicket.class);
      RegisteredService registeredService = this.servicesManager.findServiceBy(service);
      if (registeredService == null || !registeredService.isEnabled()) {
        this.log.warn("ServiceManagement: Service does not exist is not enabled, and thus not allowed to validate tickets.   Service: [" + service.getId() + "]");
        throw new UnauthorizedServiceException("Service not allowed to validate tickets.");
      } 
      if (serviceTicket == null) {
        if (this.log.isDebugEnabled())
          this.log.debug("ServiceTicket [" + serviceTicketId + "] does not exist."); 
        throw new InvalidTicketException();
      } 
      try {
        Authentication authToUse = null;
        synchronized (serviceTicket) {
          if (serviceTicket.isExpired()) {
            if (this.log.isDebugEnabled())
              this.log.debug("ServiceTicket [" + serviceTicketId + "] has expired."); 
            throw new InvalidTicketException();
          } 
          if (!serviceTicket.isValidFor(service)) {
            if (this.log.isErrorEnabled())
              this.log.error("ServiceTicket [" + serviceTicketId + "] with service [" + serviceTicket.getService().getId() + " does not match supplied service [" + service + "]"); 
            throw new TicketValidationException(serviceTicket.getService());
          } 
        } 
        int authenticationChainSize = serviceTicket.getGrantingTicket().getChainedAuthentications().size();
        Authentication authentication = serviceTicket.getGrantingTicket().getChainedAuthentications().get(authenticationChainSize - 1);
          Principal principal = authentication.getPrincipal();
        String principalId = registeredService.isAnonymousAccess() ? this.persistentIdGenerator.generate(principal, serviceTicket.getService()) : principal.getId();
        if (!registeredService.isIgnoreAttributes()) {
          Map<String, Object> attributes = new HashMap<>();
          for (String attribute : registeredService.getAllowedAttributes()) {
            Object value = principal.getAttributes().get(attribute);
            if (value != null)
              attributes.put(attribute, value); 
          } 
          SimplePrincipal simplePrincipal = new SimplePrincipal(principalId, attributes);
          MutableAuthentication mutableAuthentication = new MutableAuthentication((Principal)simplePrincipal, authentication.getAuthenticatedDate());
          mutableAuthentication.getAttributes().putAll(authentication.getAttributes());
          mutableAuthentication.getAuthenticatedDate().setTime(authentication.getAuthenticatedDate().getTime());
        } else {
          authToUse = authentication;
        } 
        List<Authentication> authentications = new ArrayList<>();
        for (int i = 0; i < authenticationChainSize - 1; i++)
          authentications.add(serviceTicket.getGrantingTicket().getChainedAuthentications().get(i)); 
        authentications.add(authToUse);
        return (Assertion)new ImmutableAssertionImpl(authentications, serviceTicket.getService(), serviceTicket.isFromNewLogin());
      } finally {
        if (serviceTicket.isExpired())
          this.serviceTicketRegistry.deleteTicket(serviceTicketId); 
      } 
    }

    @Auditable(action = "TICKET_GRANTING_TICKET", successSuffix = "_CREATED", failureSuffix = "_NOT_CREATED", actionResolverClass = ObjectCreationAuditableActionResolver.class, resourceResolverClass = ReturnValueAsStringResourceResolver.class)
    @Statistic(name = "CREATE_TICKET_GRANTING_TICKET", requiredPrecision = {Statistic.Precision.DAY, Statistic.Precision.MINUTE, Statistic.Precision.HOUR})
    @Transactional(readOnly = false)
    public String createTicketGrantingTicket(Credentials credentials) throws TicketCreationException {
      Assert.notNull(credentials, "credentials cannot be null");
      if (this.log.isDebugEnabled())
        this.log.debug("Attempting to create TicketGrantingTicket for " + credentials); 
      try {
        Authentication authentication = this.authenticationManager.authenticate(credentials);
        TicketGrantingTicketImpl ticketGrantingTicketImpl = new TicketGrantingTicketImpl(this.ticketGrantingTicketUniqueTicketIdGenerator.getNewTicketId("TGT"), authentication, this.ticketGrantingTicketExpirationPolicy);
        this.ticketRegistry.addTicket((Ticket)ticketGrantingTicketImpl);
        return ticketGrantingTicketImpl.getId();
      } catch (AuthenticationException e) {
        throw new TicketCreationException(e);
      } 
    }

    public void setTicketRegistry(TicketRegistry ticketRegistry) {
      this.ticketRegistry = ticketRegistry;
      if (this.serviceTicketRegistry == null)
        this.serviceTicketRegistry = ticketRegistry; 
    }

    public void setServiceTicketRegistry(TicketRegistry serviceTicketRegistry) {
      this.serviceTicketRegistry = serviceTicketRegistry;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
      this.authenticationManager = authenticationManager;
    }

    public void setTicketGrantingTicketExpirationPolicy(ExpirationPolicy ticketGrantingTicketExpirationPolicy) {
      this.ticketGrantingTicketExpirationPolicy = ticketGrantingTicketExpirationPolicy;
    }

    public void setTicketGrantingTicketUniqueTicketIdGenerator(UniqueTicketIdGenerator uniqueTicketIdGenerator) {
      this.ticketGrantingTicketUniqueTicketIdGenerator = uniqueTicketIdGenerator;
    }

    public void setServiceTicketExpirationPolicy(ExpirationPolicy serviceTicketExpirationPolicy) {
      this.serviceTicketExpirationPolicy = serviceTicketExpirationPolicy;
    }

    public void setUniqueTicketIdGeneratorsForService(Map<String, UniqueTicketIdGenerator> uniqueTicketIdGeneratorsForService) {
      this.uniqueTicketIdGeneratorsForService = uniqueTicketIdGeneratorsForService;
    }

    public void setServicesManager(ServicesManager servicesManager) {
      this.servicesManager = servicesManager;
    }

    public void setPersistentIdGenerator(PersistentIdGenerator persistentIdGenerator) {
      this.persistentIdGenerator = persistentIdGenerator;
    }
}
