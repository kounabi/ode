/*
 * File:      $RCSfile$
 * Copyright: (C) 1999-2005 FiveSight Technologies Inc.
 *
 */
package org.apache.ode.ra;

import org.apache.ode.ra.transports.OdeTransport;
import org.apache.ode.ra.transports.rmi.RMITransport;

import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.security.auth.Subject;

/**
 * JCA {@link ManagedConnectionFactory} implementation.
 */
public class OdeManagedConnectionFactory implements ManagedConnectionFactory {
  private static final long serialVersionUID = 1L;
  private PrintWriter _logWriter;

  /** Default connection request information. */
  private OdeConnectionRequestInfo _defaultCRI = new OdeConnectionRequestInfo(null,"");

  public OdeManagedConnectionFactory() {
    try {
      setTransport(RMITransport.class.getName());
    } catch (ResourceException re) {
      //ignore (perhaps we should log)
    }
  }

  public void setTransport(String transportClassName) throws ResourceException {
    try {
      Class tclass = Class.forName(transportClassName);
      _defaultCRI.transport = (OdeTransport) tclass.newInstance();
    } catch (IllegalAccessException e) {
      ResourceException re = new ResourceException("Class-access error for transport class \"" + transportClassName + "\". ", e);
      throw re;
    } catch (InstantiationException e) {
      ResourceException re = new ResourceException("Error instantiating transport class \"" + transportClassName + "\". ", e );
      throw re;
    } catch (ClassNotFoundException e) {
      ResourceException re = new ResourceException("Transport class \"" + transportClassName + "\" not found in class path. ", e);
      throw re;

    }
  }

  public void setURL(String url) throws ResourceException {
    _defaultCRI.url = url;
  }

  public void setProperty(String key, String val) throws ResourceException {
    if (key.equals("URL"))
      setURL(val);
    else if (key.equals("Transport"))
      setTransport(val);
    else
      _defaultCRI.properties.setProperty(key,val);
  }

  public Object createConnectionFactory() throws ResourceException {
    return new OdeConnectionFactoryImpl(this, new OdeConnectionManager());
  }

  public Object createConnectionFactory(ConnectionManager connectionManager) throws ResourceException {
    return new OdeConnectionFactoryImpl(this, connectionManager);
  }

  public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo connectionRequestInfo) throws ResourceException {
    OdeConnectionRequestInfo cri = (OdeConnectionRequestInfo) (connectionRequestInfo != null ? connectionRequestInfo : _defaultCRI);

    if (cri.transport == null)
      throw new ResourceException("No transport.");

    try {
      return new OdeManagedConnectionImpl(cri.transport.createPipe(cri.url, cri.properties), subject, connectionRequestInfo);
    } catch (RemoteException ex) {
      ResourceException re = new ResourceException("Unable to create connection: " + ex.getMessage(), ex);
      throw re;
    }
  }

  public ManagedConnection matchManagedConnections(Set candidates, Subject subject, ConnectionRequestInfo connectionRequestInfo)
          throws ResourceException
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public PrintWriter getLogWriter() throws ResourceException {
    return _logWriter;
  }

  public void setLogWriter(PrintWriter printWriter) throws ResourceException {
    _logWriter = printWriter;
  }


}
