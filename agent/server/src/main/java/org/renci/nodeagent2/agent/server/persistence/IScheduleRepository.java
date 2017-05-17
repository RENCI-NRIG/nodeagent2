package org.renci.nodeagent2.agent.server.persistence;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface IScheduleRepository extends CrudRepository<ScheduleEntry, Long> {

	/**
	 * Find by plugin name
	 * @param name
	 * @return
	 */
	List<ScheduleEntry> findByName(String name);
	
	/**
	 * Find everything that has a deadline before current time
	 * @return
	 */
	List<ScheduleEntry> findByDeadlineBefore(Date date);
	
	/**
	 * Find by a reservation id
	 * @param r
	 * @return
	 */
	List<ScheduleEntry> findByReservationId(String r);
	
	/**
	 * Find by a plugin name and reservation id
	 * @param name
	 * @param resId
	 * @return
	 */
	List<ScheduleEntry> findByNameAndReservationId(String name, String resId);
	
}
