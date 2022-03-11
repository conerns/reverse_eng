package org.openmrs.api.db.hibernate;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.openmrs.*;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.PatientProgramDAO;
import org.openmrs.customdatatype.CustomDatatypeUtil;

import java.util.*;

public class HibernatePatientProgramDAO implements PatientProgramDAO {

	private SessionFactory sessionFactory;

	/**
	 * Hibernate Session Factory
	 *
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#savePatientProgram(org.openmrs.PatientProgram)
	 */
	@Override
	public PatientProgram savePatientProgram(PatientProgram patientProgram) throws DAOException {
		CustomDatatypeUtil.saveAttributesIfNecessary(patientProgram);

		if (patientProgram.getPatientProgramId() == null) {
			sessionFactory.getCurrentSession().save(patientProgram);
		} else {
			sessionFactory.getCurrentSession().merge(patientProgram);
		}

		return patientProgram;
	}

	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getPatientProgram(java.lang.Integer)
	 */
	@Override
	public PatientProgram getPatientProgram(Integer patientProgramId) throws DAOException {
		return (PatientProgram) sessionFactory.getCurrentSession().get(PatientProgram.class, patientProgramId);
	}

	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getPatientPrograms(Patient, Program, Date, Date,
	 *      Date, Date, boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PatientProgram> getPatientPrograms(Patient patient, Program program, Date minEnrollmentDate,
												   Date maxEnrollmentDate, Date minCompletionDate, Date maxCompletionDate, boolean includeVoided)
		throws DAOException {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(PatientProgram.class);
		if (patient != null) {
			crit.add(Restrictions.eq("patient", patient));
		}
		if (program != null) {
			crit.add(Restrictions.eq("program", program));
		}
		if (minEnrollmentDate != null) {
			crit.add(Restrictions.ge("dateEnrolled", minEnrollmentDate));
		}
		if (maxEnrollmentDate != null) {
			crit.add(Restrictions.le("dateEnrolled", maxEnrollmentDate));
		}
		if (minCompletionDate != null) {
			crit.add(Restrictions.or(Restrictions.isNull("dateCompleted"), Restrictions.ge("dateCompleted",
				minCompletionDate)));
		}
		if (maxCompletionDate != null) {
			crit.add(Restrictions.le("dateCompleted", maxCompletionDate));
		}
		if (!includeVoided) {
			crit.add(Restrictions.eq("voided", false));
		}
		crit.addOrder(Order.asc("dateEnrolled"));
		return crit.list();
	}

	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getPatientPrograms(org.openmrs.Cohort,
	 *      java.util.Collection)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PatientProgram> getPatientPrograms(Cohort cohort, Collection<Program> programs) {
		String hql = "from PatientProgram ";
		if (cohort != null || programs != null) {
			hql += "where ";
		}
		if (cohort != null) {
			hql += "patient.patientId in (:patientIds) ";
		}
		if (programs != null) {
			if (cohort != null) {
				hql += "and ";
			}
			hql += " program in (:programs)";
		}
		hql += " order by patient.patientId, dateEnrolled";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		if (cohort != null) {
			query.setParameterList("patientIds", cohort.getMemberIds());
		}
		if (programs != null) {
			query.setParameterList("programs", programs);
		}
		return query.list();
	}

	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#deletePatientProgram(org.openmrs.PatientProgram)
	 */
	@Override
	public void deletePatientProgram(PatientProgram patientProgram) throws DAOException {
		sessionFactory.getCurrentSession().delete(patientProgram);
	}

	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getPatientProgramByUuid(java.lang.String)
	 */
	@Override
	public PatientProgram getPatientProgramByUuid(String uuid) {
		return (PatientProgram) sessionFactory.getCurrentSession().createQuery(
			"from PatientProgram pp where pp.uuid = :uuid").setString("uuid", uuid).uniqueResult();
	}

	@Override
	public PatientState getPatientStateByUuid(String uuid) {
		return (PatientState) sessionFactory.getCurrentSession().createQuery("from PatientState pws where pws.uuid = :uuid")
			.setString("uuid", uuid).uniqueResult();
	}

	@Override
	public List<PatientProgram> getPatientProgramByAttributeNameAndValue(String attributeName, String attributeValue) {
		FlushMode flushMode = sessionFactory.getCurrentSession().getHibernateFlushMode();
		sessionFactory.getCurrentSession().setHibernateFlushMode(FlushMode.MANUAL);
		Query query;
		try {
			query = sessionFactory.getCurrentSession().createQuery(
					"SELECT pp FROM patient_program pp " +
						"INNER JOIN pp.attributes attr " +
						"INNER JOIN attr.attributeType attr_type " +
						"WHERE attr.valueReference = :attributeValue " +
						"AND attr_type.name = :attributeName " +
						"AND pp.voided = 0")
				.setParameter("attributeName", attributeName)
				.setParameter("attributeValue", attributeValue);
			return query.list();
		} finally {
			sessionFactory.getCurrentSession().setHibernateFlushMode(flushMode);
		}
	}

	@Override
	public PatientProgramAttribute getPatientProgramAttributeByUuid(String uuid) {
		return (PatientProgramAttribute) sessionFactory.getCurrentSession().createCriteria(PatientProgramAttribute.class).add(Restrictions.eq("uuid", uuid)).uniqueResult();
	}

	@Override
	public Map<Object, Object> getPatientProgramAttributeByAttributeName(List<Integer> patientIds, String attributeName) {
		Map<Object, Object> patientProgramAttributes = new HashMap<>();
		if (patientIds.isEmpty() || attributeName == null) {
			return patientProgramAttributes;
		}
		String commaSeperatedPatientIds = StringUtils.join(patientIds, ",");
		List<Object> list = sessionFactory.getCurrentSession().createSQLQuery(
				"SELECT p.patient_id as person_id, " +
					" concat('{',group_concat(DISTINCT (coalesce(concat('\"',ppt.name,'\":\"', COALESCE (cn.name, ppa.value_reference),'\"'))) SEPARATOR ','),'}') AS patientProgramAttributeValue  " +
					" from patient p " +
					" join patient_program pp on p.patient_id = pp.patient_id and p.patient_id in (" + commaSeperatedPatientIds + ")" +
					" join patient_program_attribute ppa on pp.patient_program_id = ppa.patient_program_id and ppa.voided=0" +
					" join program_attribute_type ppt on ppa.attribute_type_id = ppt.program_attribute_type_id and ppt.name ='" + attributeName + "' "+
					" LEFT OUTER JOIN concept_name cn on ppa.value_reference = cn.concept_id and cn.concept_name_type= 'FULLY_SPECIFIED' and cn.voided=0 and ppt.datatype like '%ConceptDataType%'" +
					" group by p.patient_id")
			.addScalar("person_id", StandardBasicTypes.INTEGER)
			.addScalar("patientProgramAttributeValue", StandardBasicTypes.STRING)
			.list();

		for (Object o : list) {
			Object[] arr = (Object[]) o;
			patientProgramAttributes.put(arr[0], arr[1]);
		}

		return patientProgramAttributes;

	}
}
