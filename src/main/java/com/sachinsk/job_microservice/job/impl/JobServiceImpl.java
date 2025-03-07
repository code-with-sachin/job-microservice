package com.sachinsk.job_microservice.job.impl;

import com.sachinsk.job_microservice.job.Job;
import com.sachinsk.job_microservice.job.JobRepository;
import com.sachinsk.job_microservice.job.JobService;
import com.sachinsk.job_microservice.job.dto.JobWithCompanyDTO;
import com.sachinsk.job_microservice.job.external.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    // private List<Job> jobs = new ArrayList<>();
    JobRepository jobRepository;

    @Autowired
    RestTemplate restTemplate;

    //constructor - Since job repository is a bean managed by spring.. Because of this constructor it will be Autowired at runtime (No need to manage object & create instance and initialize it)
    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<JobWithCompanyDTO> findAll() {
        List<Job> jobs = jobRepository.findAll();
        List<JobWithCompanyDTO> jobWithCompanyDTOs = new ArrayList<>();

        //using convertDTO method here using streams
        return jobs.stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private JobWithCompanyDTO convertToDTO(Job job) {
        JobWithCompanyDTO jobWithCompanyDTO = new JobWithCompanyDTO();
        jobWithCompanyDTO.setJob(job);

        //RestTemplate restTemplate = new RestTemplate();
        Company company = restTemplate.getForObject(
                "http://COMPANY-MICROSERVICE:8081/companies/" + job.getCompanyId(),
                Company.class);
        jobWithCompanyDTO.setCompany(company);

        return jobWithCompanyDTO;
    }

    @Override
    public void createJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public JobWithCompanyDTO getJobById(Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        //Now we will transform the job into JobWithCompanyDTO object using method
        return convertToDTO(job);
    }

    @Override
    public boolean deleteJobById(Long id) {
        if (jobRepository.existsById(id)) {
            jobRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean updateJobById(Long id, Job updatedJob) {
        Optional<Job> optionalJob = jobRepository.findById(id);
        if (optionalJob.isPresent()) {
            Job job = optionalJob.get();
            job.setTitle(updatedJob.getTitle());
            job.setDescription(updatedJob.getDescription());
            job.setMinSalary(updatedJob.getMinSalary());
            job.setMaxSalary(updatedJob.getMaxSalary());
            job.setLocation(updatedJob.getLocation());
            jobRepository.save(job);
            return true;
        }
        return false;
    }
}
