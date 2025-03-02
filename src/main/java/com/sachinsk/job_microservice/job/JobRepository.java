package com.sachinsk.job_microservice.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//in below Long is the data type of Primary Key - id & Job is the Entity type
@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
}
