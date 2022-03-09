package org.openmrs.api.db.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Daemon;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.UserRolesDAO;

import java.util.List;

public class HibernateUserRolesDAO implements UserRolesDAO {

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	/**
	 * Set session factory
	 *
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


	@Override
	@SuppressWarnings("unchecked")
	public List<Privilege> getAllPrivileges() throws DAOException {
		return sessionFactory.getCurrentSession().createQuery("from Privilege p order by p.privilege").list();
	}

	@Override
	public Privilege getPrivilege(String p) throws DAOException {
		return (Privilege) sessionFactory.getCurrentSession().get(Privilege.class, p);
	}

	@Override
	public void deletePrivilege(Privilege privilege) throws DAOException {
		sessionFactory.getCurrentSession().delete(privilege);
	}

	@Override
	public Privilege savePrivilege(Privilege privilege) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(privilege);
		return privilege;
	}

	@Override
	public void deleteRole(Role role) throws DAOException {
		sessionFactory.getCurrentSession().delete(role);
	}

	@Override
	public Role saveRole(Role role) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(role);
		return role;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Role> getAllRoles() throws DAOException {
		return sessionFactory.getCurrentSession().createQuery("from Role r order by r.role").list();
	}

	@Override
	public Role getRole(String r) throws DAOException {
		return (Role) sessionFactory.getCurrentSession().get(Role.class, r);
	}

	@Override
	public Privilege getPrivilegeByUuid(String uuid) {
		return (Privilege) sessionFactory.getCurrentSession().createQuery("from Privilege p where p.uuid = :uuid")
			.setString("uuid", uuid).uniqueResult();
	}

	@Override
	public Role getRoleByUuid(String uuid) {
		return (Role) sessionFactory.getCurrentSession().createQuery("from Role r where r.uuid = :uuid").setString("uuid",
			uuid).uniqueResult();
	}

	/**
	 * @see org.openmrs.api.UserService#getUsersByRole(org.openmrs.Role)
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsersByRole(Role role) throws DAOException {

		return (List<User>) sessionFactory.getCurrentSession().createCriteria(User.class, "u").createCriteria("roles", "r")
			.add(Restrictions.like("r.role", role.getRole())).add(Restrictions.ne("u.uuid", Daemon.getDaemonUserUuid())).addOrder(Order.asc("u.username")).list();

	}

}
