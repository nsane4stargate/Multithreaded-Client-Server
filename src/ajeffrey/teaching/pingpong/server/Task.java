package ajeffrey.teaching.pingpong.server;

import ajeffrey.teaching.debug.Debug;

/**
 * A Task class for tasks.
 * @author Alan Jeffrey
 * @version 1.0.1
 */

public interface Task extends Runnable {

    /**
     * Cancel this task.
     * After a task has been built, it should either be run,
     * using the <code>run()</code> method, or cancelled,
     * using the <code>cancel()</code> method.  This sends
     * appropriate error messages, and cleans up any resources
     * used by the task.
     */
    public void cancel ();

}

