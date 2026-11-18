package dev.portfolio.tasks;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.TimeUnit;
class JobServiceTest {
 @Test void executesJob() throws Exception {
  JobService s=new JobService();
  try {
   var view=s.submit(new JobService.JobRequest("integration",0,false));
   JobService.JobView current=view;
   for(int i=0;i<100 && current.status()!=JobService.Status.COMPLETED;i++){
    TimeUnit.MILLISECONDS.sleep(10);current=s.list().get(0);
   }
   assertEquals(JobService.Status.COMPLETED,current.status());
   assertEquals(1,current.attempts());
  }finally{s.shutdown();}
 }
 @Test void rejectsBadRequest(){
  JobService s=new JobService();
  try{assertThrows(IllegalArgumentException.class,()->s.submit(new JobService.JobRequest("",0,false)));}
  finally{s.shutdown();}
 }
}