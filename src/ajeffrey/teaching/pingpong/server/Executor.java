package ajeffrey.teaching.pingpong.server;

import ajeffrey.teaching.debug.Debug;
import ajeffrey.teaching.pingpong.client.mainClient;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An Executor class for executing tasks.
 * @author Alan Jeffrey
 * @version 1.0.1
 */

public interface Executor {

    /**
     * Try to execute a given task.
     * The task will be run, if system resources permit.
     * If the system is too busy, then the task will be cancelled.
     *
     * @param task the task to execute
     */
    public void execute(Task task);

    /**
     * An executor.
     */
    public Executor singleton = new ExecutorImpl();
    //public ExecutorService service = Executors.newFixedThreadPool(50);

    public ExecutorService service = new ThreadPoolExecutor(0,50,10,
                                                                TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>());
}



class ExecutorImpl implements Executor {
    public static BlockingDeque<Task> allTask = ServerImpl.allTask;
    public static BlockingDeque<Thread> deque = ServerImpl.queueOfIdleThreads;

    public ExecutorImpl(Task task) {
    }

    public ExecutorImpl() {

    }

    synchronized public void execute (Task task) {
	    Debug.out.println ("Executor.execute: Starting");
	    /* Uncomment below to execute the constraint of having less than 50 workers
	    * ------------------------------------------------------------------------
	    * service.execute(task);
	    */
	    service.execute(task);
	    Debug.out.println ("Executor.execute: Returning");
        Debug.out.println("Blocked Thread pool size: " + String.valueOf(deque.size()));

        /* Was trying something with the new connection. Couldn't get it to work
	    if(deque.size() == 8){
	        service.shutdown();
	        Debug.out.println("Thread pool service has stop and is restarting");
	        ExecutorService restartService = new ThreadPoolExecutor(0,50,10,
                                                                        TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>());
            Debug.out.println("Thread pool service has restarted");
	        Task original = null;
	        boolean restartingAllTask = true;
            AtomicInteger taskNum = new AtomicInteger(0);
	        while(restartingAllTask){
	            Task head = allTask.removeFirst();
	            if(original == head){
	                allTask.addLast(head);
                }
                restartService.execute(head);
                Debug.out.println("Task: " + String.valueOf(taskNum.getAndIncrement()) + " has restarted");
                if(original == null){
                    original = head;
                }
                allTask.addLast(head);
            }
        }*/

    }

}
