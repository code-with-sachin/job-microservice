package com.sachinsk.job_microservice.job.impl;

import com.sachinsk.job_microservice.job.Job;
import com.sachinsk.job_microservice.job.JobRepository;
import com.sachinsk.job_microservice.job.JobService;
import com.sachinsk.job_microservice.job.clients.CompanyClient;
import com.sachinsk.job_microservice.job.clients.ReviewClient;
import com.sachinsk.job_microservice.job.dto.JobDTO;
import com.sachinsk.job_microservice.job.external.Company;
import com.sachinsk.job_microservice.job.external.Review;
import com.sachinsk.job_microservice.job.mapper.JobMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    int attempt=0;

    // private List<Job> jobs = new ArrayList<>();
    JobRepository jobRepository;

    @Autowired
    RestTemplate restTemplate;

    private CompanyClient companyClient;
    private ReviewClient reviewClient;

    //constructor - Since job repository is a bean managed by spring.. Because of this constructor it will be Autowired at runtime (No need to manage object & create instance and initialize it)
    public JobServiceImpl(JobRepository jobRepository,
                          CompanyClient companyClient, ReviewClient reviewClient) {
        this.jobRepository = jobRepository;
        this.companyClient = companyClient;
        this.reviewClient = reviewClient;
    }

    @Override
    //@CircuitBreaker(name = "companyBreaker", fallbackMethod = "companyBreakerFallback")
    @Retry(name = "companyBreaker", fallbackMethod = "companyBreakerFallback")
    public List<JobDTO> findAll() {
        System.out.println("Retry attempt count is:" + ++attempt);
        List<Job> jobs = jobRepository.findAll();
        List<JobDTO> jobDTOS = new ArrayList<>();

        //using convertDTO method here using streams
        return jobs.stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<String> companyBreakerFallback(Exception e){
        List<String> list = new ArrayList<>();
        list.add("Dummy Output");
        return list;
    }

    private JobDTO convertToDTO(Job job) {

//        //RestTemplate restTemplate = new RestTemplate();
//        Company company = restTemplate.getForObject(
//                "http://COMPANY-MICROSERVICE:8081/companies/" + job.getCompanyId(),
//                Company.class);
        Company company = companyClient.getCompany(job.getCompanyId());

//        //Since we will get List in Review, we will use exchange method
//        ResponseEntity<List<Review>> reviewResponse = restTemplate.exchange(
//                "http://REVIEW-MICROSERVICE:8083/reviews?companyId=" + job.getCompanyId(),
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<List<Review>>() {
//                });
//        List<Review> reviews = reviewResponse.getBody();

        List<Review> reviews = reviewClient.getReviews(job.getCompanyId());

        //Using mapper here - to set job
        JobDTO jobDTO = JobMapper.
                mapToJobWithCompanyDto(job, company, reviews);
        //jobDTO.setCompany(company);

        return jobDTO;
    }

    @Override
    public void createJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public JobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        //Now we will transform the job into JobDTO object using method
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
