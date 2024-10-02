package com.demo.tally_db_sync.repo;

import com.demo.tally_db_sync.model.TallyJson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TallyJsonRepository extends JpaRepository<TallyJson, Integer> {

}
