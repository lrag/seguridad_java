/**
 * ServicioUsuariosServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.curso.modelo.servicio;

public class ServicioUsuariosServiceLocator extends org.apache.axis.client.Service implements com.curso.modelo.servicio.ServicioUsuariosService {

    public ServicioUsuariosServiceLocator() {
    }


    public ServicioUsuariosServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ServicioUsuariosServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ServicioUsuariosPort
    private java.lang.String ServicioUsuariosPort_address = "http://localhost:8080/Ej02_InyeccionXML_CXF/services/ServicioUsuariosPort";

    public java.lang.String getServicioUsuariosPortAddress() {
        return ServicioUsuariosPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ServicioUsuariosPortWSDDServiceName = "ServicioUsuariosPort";

    public java.lang.String getServicioUsuariosPortWSDDServiceName() {
        return ServicioUsuariosPortWSDDServiceName;
    }

    public void setServicioUsuariosPortWSDDServiceName(java.lang.String name) {
        ServicioUsuariosPortWSDDServiceName = name;
    }

    public com.curso.modelo.servicio.ServicioUsuarios getServicioUsuariosPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ServicioUsuariosPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getServicioUsuariosPort(endpoint);
    }

    public com.curso.modelo.servicio.ServicioUsuarios getServicioUsuariosPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.curso.modelo.servicio.ServicioUsuariosServiceSoapBindingStub _stub = new com.curso.modelo.servicio.ServicioUsuariosServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getServicioUsuariosPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setServicioUsuariosPortEndpointAddress(java.lang.String address) {
        ServicioUsuariosPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.curso.modelo.servicio.ServicioUsuarios.class.isAssignableFrom(serviceEndpointInterface)) {
                com.curso.modelo.servicio.ServicioUsuariosServiceSoapBindingStub _stub = new com.curso.modelo.servicio.ServicioUsuariosServiceSoapBindingStub(new java.net.URL(ServicioUsuariosPort_address), this);
                _stub.setPortName(getServicioUsuariosPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("ServicioUsuariosPort".equals(inputPortName)) {
            return getServicioUsuariosPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://servicio.modelo.curso.com/", "ServicioUsuariosService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://servicio.modelo.curso.com/", "ServicioUsuariosPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ServicioUsuariosPort".equals(portName)) {
            setServicioUsuariosPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
