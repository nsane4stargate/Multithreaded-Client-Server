package ajeffrey.teaching.pingpong.client;

		import java.net.Socket;

		import ajeffrey.teaching.pingpong.server.Server;
		import ajeffrey.teaching.pingpong.server.ServerImpl;
		import com.macfaq.io.SafeBufferedReader;
		import com.macfaq.io.SafePrintWriter;

		import java.io.IOException;
		import java.util.concurrent.*;
		import java.util.concurrent.atomic.AtomicInteger;

		import ajeffrey.teaching.io.SocketIO;

		import ajeffrey.teaching.debug.Debug;
		import net.jcip.annotations.GuardedBy;
		import  ajeffrey.teaching.pingpong.client.mainClient.*;

/**
 * A Ping-Pong client.
 * @author Alan Jeffrey
 * @version 1.0.1
 */

public class mainClient {
	static AtomicInteger numOfIdleThreads = new AtomicInteger(0);
	static AtomicInteger secs = new AtomicInteger(5);
	public static BlockingDeque<Thread> queueOfIdleThreads = new LinkedBlockingDeque<Thread>();

	public static void main (String[] args) {
		final int numClients;

		Debug.out.addPrintStream(System.err);
		if (args.length == 0) {
			numClients = 10;
		} else {
			numClients = Integer.parseInt(args[0]);
		}
		for (int i = 0; i < numClients; i++) {
			Debug.out.println("Creating client " + i);
			final Client client = new Client();
			client.start();
		}
	}

	synchronized public static void dequeThreadsChecker() throws InterruptedException {
		boolean checking = true;
		Thread firstHead = null;

		while (checking) {
			Thread head = Client.queue.pop();
			if (firstHead == head) {
				Client.queue.addFirst(head);
				break;
			}
			if (head.getState() == Thread.State.BLOCKED || head.getState() == Thread.State.WAITING || head.getState() == Thread.State.TIMED_WAITING) {
				numOfIdleThreads.getAndIncrement();
				queueOfIdleThreads.add(head);
				ServerImpl.queueOfIdleThreads.add(head);

				try {
					head.sleep(1000);
					Debug.out.println(head.getName() + " is sleeping for 1 sec");
					Client.queue.addLast(head);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				Debug.out.println(head.getName() + " is alive and running.");
				Client.queue.addLast(head);
				if (firstHead == null) firstHead = head;
			}
		}
	}
}

class Client implements Runnable {

	final Thread thread = new Thread (this);

	static BlockingDeque<Thread> queue = new LinkedBlockingDeque<Thread>();

	synchronized public void start () {
		thread.start ();
		queue.add(thread);
	}

	synchronized public void run () {
		try {
			while (true) {
				Debug.out.println ("Making connection");
				final Socket socket = new Socket ("127.0.0.1", 2000);
				final SafePrintWriter out =
						SocketIO.singleton.buildSafePrintWriter (socket, "\n");
				final SafeBufferedReader in =
						SocketIO.singleton.buildSafeBufferedReader (socket);
				final String welcome = in.readLine ();
				Debug.out.println ("Got " + welcome);
				while (Math.random () > .1) {
					Debug.out.println("Time till threads are check: " + String.valueOf(mainClient.secs.getAndDecrement()));
					if(mainClient.secs.get() == 0){
						if(!queue.isEmpty()) {
							mainClient.dequeThreadsChecker();
							mainClient.secs.set(5);
						}
					}
					if (Math.random () > .5) {
						Debug.out.println ("Sending PING");
						out.println ("PING");
					} else {
						Debug.out.println ("Sending PONG");
						out.println ("PONG");
					}
					final String line = in.readLine ();
					Debug.out.println ("Got " + line);
					thread.sleep ((long)(Math.random () * 1000));
				}
				Debug.out.println ("Quitting");
				out.println ("QUIT");
				socket.close ();
				thread.sleep ((long)(Math.random () * 2000));
				mainClient.secs.set(5);
			}
		} catch (final IOException ex) {
			Debug.out.println ("Caught " + ex);
		} catch (final InterruptedException ex) {
			Debug.out.println ("Caught " + ex);
		}
	}
}
