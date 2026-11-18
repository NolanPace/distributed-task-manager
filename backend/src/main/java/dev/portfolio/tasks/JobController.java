package dev.portfolio.tasks;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins="http://localhost:5173")
public class JobController {
 private final JobService service;
 public JobController(JobService service){this.service=service;}
 @GetMapping public List<JobService.JobView> all(){return service.list();}
 @PostMapping public ResponseEntity<JobService.JobView> create(@RequestBody JobService.JobRequest request){
  return ResponseEntity.status(201).body(service.submit(request));
 }
 @PostMapping("/{id}/retry") public ResponseEntity<JobService.JobView> retry(@PathVariable UUID id){
  return service.retry(id).map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
 }
 @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<Map<String,String>> invalid(IllegalArgumentException e){
  return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));
 }
}