package orca.nodeagent2.agent.server.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface IScheduleRepository extends CrudRepository<ScheduleEntry, Long> {

	List<ScheduleEntry> findByName(String name);
	
}
