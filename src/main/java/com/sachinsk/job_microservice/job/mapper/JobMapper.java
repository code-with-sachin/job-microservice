package com.sachinsk.job_microservice.job.mapper;

import com.sachinsk.job_microservice.job.Job;
import com.sachinsk.job_microservice.job.dto.JobDTO;
import com.sachinsk.job_microservice.job.external.Company;
import com.sachinsk.job_microservice.job.external.Review;

import java.util.List;

public class JobMapper {

    public static JobDTO mapToJobWithCompanyDto(Job job, Company company, List<Review> reviews){
    JobDTO jobDTO = new JobDTO();
    jobDTO.setId(job.getId());
    jobDTO.setTitle(job.getTitle());
    jobDTO.setDescription(job.getDescription());
    jobDTO.setLocation(job.getLocation());
    jobDTO.setMaxSalary(job.getMaxSalary());
    jobDTO.setMinSalary(job.getMinSalary());
    jobDTO.setCompany(company);
    jobDTO.setReview(reviews);

    return jobDTO;
    }
}
