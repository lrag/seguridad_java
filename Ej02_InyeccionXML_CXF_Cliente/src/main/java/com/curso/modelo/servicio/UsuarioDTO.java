/**
 * Usuario.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.curso.modelo.servicio;

public class UsuarioDTO  implements java.io.Serializable {
    private int id;

    private java.lang.String login;

    private java.lang.String pw;

    private java.lang.String rol;

    public UsuarioDTO() {
    }

    public UsuarioDTO(
           int id,
           java.lang.String login,
           java.lang.String pw,
           java.lang.String rol) {
           this.id = id;
           this.login = login;
           this.pw = pw;
           this.rol = rol;
    }


    /**
     * Gets the id value for this Usuario.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the id value for this Usuario.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Gets the login value for this Usuario.
     * 
     * @return login
     */
    public java.lang.String getLogin() {
        return login;
    }


    /**
     * Sets the login value for this Usuario.
     * 
     * @param login
     */
    public void setLogin(java.lang.String login) {
        this.login = login;
    }


    /**
     * Gets the pw value for this Usuario.
     * 
     * @return pw
     */
    public java.lang.String getPw() {
        return pw;
    }


    /**
     * Sets the pw value for this Usuario.
     * 
     * @param pw
     */
    public void setPw(java.lang.String pw) {
        this.pw = pw;
    }


    /**
     * Gets the rol value for this Usuario.
     * 
     * @return rol
     */
    public java.lang.String getRol() {
        return rol;
    }


    /**
     * Sets the rol value for this Usuario.
     * 
     * @param rol
     */
    public void setRol(java.lang.String rol) {
        this.rol = rol;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UsuarioDTO)) return false;
        UsuarioDTO other = (UsuarioDTO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.id == other.getId() &&
            ((this.login==null && other.getLogin()==null) || 
             (this.login!=null &&
              this.login.equals(other.getLogin()))) &&
            ((this.pw==null && other.getPw()==null) || 
             (this.pw!=null &&
              this.pw.equals(other.getPw()))) &&
            ((this.rol==null && other.getRol()==null) || 
             (this.rol!=null &&
              this.rol.equals(other.getRol())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getId();
        if (getLogin() != null) {
            _hashCode += getLogin().hashCode();
        }
        if (getPw() != null) {
            _hashCode += getPw().hashCode();
        }
        if (getRol() != null) {
            _hashCode += getRol().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UsuarioDTO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://servicio.modelo.curso.com/", "usuario"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("login");
        elemField.setXmlName(new javax.xml.namespace.QName("", "login"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pw");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pw"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rol");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rol"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
