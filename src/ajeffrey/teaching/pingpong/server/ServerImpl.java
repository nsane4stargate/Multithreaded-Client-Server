package ajeffrey.teaching.pingpong.server;

import ajeffrey.teaching.debug.Debug;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ServerImpl implements Server {

    final int portNumber;
	public static BlockingDeque<Task> allTask = new LinkedBlockingDeque<Task>();
   	public static BlockingDeque<Thread> queueOfIdleThreads = new LinkedBlockingDeque<>();

	public ServerImpl (final int portNumber) {
	this.portNumber = portNumber;
	Debug.out.println ("ServerImpl: built");
    }

    synchronized public void start () {
	Debug.out.println ("ServerImpl.start: Starting");
	try {
	    Debug.out.println ("ServerImpl.start: Opening server socket");
	    final ServerSocket serverSocket = new ServerSocket (2000);
	    while (true) {
		Debug.out.println ("ServerImpl.start: Waiting for connection");
		final Socket socket = serverSocket.accept ();
		Debug.out.println ("ServerImpl.start: Got connection");
		final Task task = new TaskImpl (socket);
		allTask.add(task);
		Debug.out.println ("ServerImpl.start: Executing task");
		Executor.singleton.execute (task);
	    }
	} catch (final IOException ex) {
	    Debug.out.println ("ServerImpl.start: Caught " + ex);
	}
    }

}
