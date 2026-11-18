import React, { useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import './style.css';
type Job={id:string,name:string,status:'QUEUED'|'RUNNING'|'COMPLETED'|'FAILED',attempts:number,message:string,createdAt:string};
const API='http://localhost:8080/api/jobs';
function App(){
 const [jobs,setJobs]=useState<Job[]>([]),[name,setName]=useState('Generate report'),[duration,setDuration]=useState(600),[fail,setFail]=useState(false),[error,setError]=useState('');
 async function refresh(){try{const r=await fetch(API);if(!r.ok)throw Error('Unable to fetch jobs');setJobs(await r.json());setError('')}catch(e){setError(String(e))}}
 useEffect(()=>{void refresh();const interval=setInterval(()=>void refresh(),1500);return()=>clearInterval(interval)},[]);
 async function submit(e:React.FormEvent){e.preventDefault();const r=await fetch(API,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({name,durationMs:duration,shouldFail:fail})});if(!r.ok){setError(await r.text())}else{await refresh()}}
 async function retry(id:string){await fetch(API+'/'+id+'/retry',{method:'POST'});await refresh()}
 return <main><header><span>JAVA + REACT / TASK PROCESSING</span><h1>Worker Control Center</h1><p>Queue jobs, monitor execution, and retry failures.</p></header>
 <section><h2>Submit job</h2><form onSubmit={submit}><label>Job name<input required maxLength={80} value={name} onChange={e=>setName(e.target.value)}/></label><label>Duration (ms)<input type="number" min="0" max="10000" value={duration} onChange={e=>setDuration(Number(e.target.value))}/></label><label className="inline"><input type="checkbox" checked={fail} onChange={e=>setFail(e.target.checked)}/> Simulate failure</label><button type="submit">Queue job</button></form></section>
 {error&&<p role="alert" className="error">{error}. Is Spring Boot running?</p>}
 <section><div className="heading"><h2>Jobs ({jobs.length})</h2><button onClick={()=>void refresh()}>Refresh</button></div><div className="grid">{jobs.map(job=><article key={job.id}><div className={'status '+job.status}>{job.status}</div><h3>{job.name}</h3><p>{job.message}</p><small>Attempts: {job.attempts} · {new Date(job.createdAt).toLocaleTimeString()}</small>{job.status==='FAILED'&&<button onClick={()=>void retry(job.id)}>Retry</button>}</article>)}</div></section></main>
}
createRoot(document.getElementById('root')!).render(<React.StrictMode><App/></React.StrictMode>);