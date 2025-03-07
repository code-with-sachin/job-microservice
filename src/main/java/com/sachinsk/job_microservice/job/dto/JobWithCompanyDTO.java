package com.sachinsk.job_microservice.job.dto;

import com.sachinsk.job_microservice.job.Job;
import com.sachinsk.job_microservice.job.external.Company;

public class JobWithCompanyDTO {
    private Job job;
    private Company company;

    //Getters and Setters
    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
