package org.openmrs.api.db.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.ProgramAttributeType;
import org.openmrs.api.db.ProgramAttributeTypeDAO;

import java.util.List;

public class HibernateProgramAttributeTypeDAO implements ProgramAttributeTypeDAO {

	private SessionFactory sessionFactory;

	/**
	 * Hibernate Session Factory
	 *
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public List<ProgramAttributeType> getAllProgramAttributeTypes() {
		return sessionFactory.getCurrentSession().createCriteria(ProgramAttributeType.class).list();
	}

	@Override
	public ProgramAttributeType getProgramAttributeType(Integer id) {
		return (ProgramAttributeType) sessionFactory.getCurrentSession().get(ProgramAttributeType.class, id);
	}

	@Override
	public ProgramAttributeType getProgramAttributeTypeByUuid(String uuid) {
		return (ProgramAttributeType) sessionFactory.getCurrentSession().createCriteria(ProgramAttributeType.class).add(
			Restrictions.eq("uuid", uuid)).uniqueResult();
	}

	@Override
	public ProgramAttributeType saveProgramAttributeType(ProgramAttributeType programAttributeType) {
		sessionFactory.getCurrentSession().saveOrUpdate(programAttributeType);
		return programAttributeType;
	}

	@Override
	public void purgeProgramAttributeType(ProgramAttributeType type) {
		sessionFactory.getCurrentSession().delete(type);
	}
}
