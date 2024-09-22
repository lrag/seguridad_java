package com.curso.modelo.persistencia;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.curso.modelo.entidad.Cliente;
import com.curso.modelo.persistencia.util.HibernateUtil;


public class ClienteDaoHibernate implements ClienteDao {

	public void insertar(Cliente cliente){
		Session s = HibernateUtil.getSF().getCurrentSession();
		s.save(cliente);
		s.flush();
		s.evict(cliente);		
	}
	
	public void modificar(Cliente cliente){
		Session s = HibernateUtil.getSF().getCurrentSession();
		s.update(cliente);
		s.flush();
		s.evict(cliente);
	}
	
	public void borrar(Cliente cliente){
		Session s = HibernateUtil.getSF().getCurrentSession();
		s.delete(cliente);
		s.flush();
		s.evict(cliente);		
	}
	
	public Cliente buscar(int id){
		Session s = HibernateUtil.getSF().getCurrentSession();
		Cliente cliente = (Cliente) s.get(Cliente.class, id);
		s.clear();
		return cliente;
	}
	
	public List<Cliente> listar(int primero, int cantidad){
		Session s = HibernateUtil.getSF().getCurrentSession();
		Query q = s.createQuery("select c from Cliente c");
		q.setFirstResult(primero);
		q.setMaxResults(cantidad);
		List<Cliente> clientes = q.list();
		s.clear();
		return clientes;
	}
	
	
}
