package com.curso.modelo.servicio;

public class ServicioUsuariosProxy implements com.curso.modelo.servicio.ServicioUsuarios {
  private String _endpoint = null;
  private com.curso.modelo.servicio.ServicioUsuarios servicioUsuarios = null;
  
  public ServicioUsuariosProxy() {
    _initServicioUsuariosProxy();
  }
  
  public ServicioUsuariosProxy(String endpoint) {
    _endpoint = endpoint;
    _initServicioUsuariosProxy();
  }
  
  private void _initServicioUsuariosProxy() {
    try {
      servicioUsuarios = (new com.curso.modelo.servicio.ServicioUsuariosServiceLocator()).getServicioUsuariosPort();
      if (servicioUsuarios != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)servicioUsuarios)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)servicioUsuarios)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (servicioUsuarios != null)
      ((javax.xml.rpc.Stub)servicioUsuarios)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.curso.modelo.servicio.ServicioUsuarios getServicioUsuarios() {
    if (servicioUsuarios == null)
      _initServicioUsuariosProxy();
    return servicioUsuarios;
  }
  
  public void modificarUsuario(com.curso.modelo.servicio.UsuarioDTO arg0) throws java.rmi.RemoteException{
    if (servicioUsuarios == null)
      _initServicioUsuariosProxy();
    servicioUsuarios.modificarUsuario(arg0);
  }
  
  
}