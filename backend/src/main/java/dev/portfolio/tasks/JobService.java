package dev.portfolio.tasks;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.*;
import java.time.Instant;

@Service
public class JobService {
 public enum Status { QUEUED,RUNNING,COMPLETED,FAILED }
 public record JobView(UUID id,String name,Status status,int attempts,String message,Instant createdAt) {}
 public record JobRequest(String name,int durationMs,boolean shouldFail) {}
 private static class Job {
  UUID id=UUID.randomUUID(); String name; Status status=Status.QUEUED; int attempts=0;
  String message="Queued"; Instant createdAt=Instant.now(); int durationMs; boolean shouldFail;
  Job(JobRequest r){ name=r.name();durationMs=r.durationMs();shouldFail=r.shouldFail(); }
  synchronized JobView view(){return new JobView(id,name,status,attempts,message,createdAt);}
 }
 private final ConcurrentMap<UUID,Job> jobs=new ConcurrentHashMap<>();
 private final ExecutorService workers=Executors.newFixedThreadPool(4);
 public List<JobView> list(){return jobs.values().stream().map(Job::view).sorted(Comparator.comparing(JobView::createdAt).reversed()).toList();}
 public JobView submit(JobRequest request){
  if(request.name()==null || request.name().isBlank() || request.durationMs()<0 || request.durationMs()>10000)throw new IllegalArgumentException("Name required, duration 0–10000 ms");
  Job job=new Job(request);jobs.put(job.id,job);dispatch(job);return job.view();
 }
 public Optional<JobView> retry(UUID id){
  Job job=jobs.get(id);if(job==null)return Optional.empty();
  synchronized(job){if(job.status!=Status.FAILED)return Optional.of(job.view());job.status=Status.QUEUED;job.message="Queued for retry";}
  dispatch(job);return Optional.of(job.view());
 }
 private void dispatch(Job job){
  workers.submit(()->{
   synchronized(job){job.status=Status.RUNNING;job.attempts++;job.message="Running";}
   try {
    Thread.sleep(job.durationMs);
    if(job.shouldFail)throw new IllegalStateException("Simulated failure");
    synchronized(job){job.status=Status.COMPLETED;job.message="Completed";}
   }catch(InterruptedException e){
    Thread.currentThread().interrupt();synchronized(job){job.status=Status.FAILED;job.message="Interrupted";}
   }catch(Exception e){synchronized(job){job.status=Status.FAILED;job.message=e.getMessage();}}
  });
 }
 @jakarta.annotation.PreDestroy public void shutdown(){workers.shutdownNow();}
}