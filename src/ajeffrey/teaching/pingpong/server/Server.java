package ajeffrey.teaching.pingpong.server;

import java.net.Socket;

import ajeffrey.teaching.pingpong.client.mainClient;
import com.macfaq.io.SafeBufferedReader;
import com.macfaq.io.SafePrintWriter;

import java.io.IOException;
import java.util.concurrent.BlockingDeque;


import ajeffrey.teaching.io.SocketIO;

import ajeffrey.teaching.debug.Debug;

/**
 * An Ping-Pong server class.
 * @author Alan Jeffrey
 * @version 1.0.1
 */
public interface Server {

    public void start ();
    public final static ServerFactory factory = new ServerFactoryImpl ();

}

class ServerFactoryImpl implements ServerFactory {

    public Server build (final int portNumber) {
	return new ServerImpl (portNumber);
    }

}

class TaskImpl implements Task {

    final Socket socket;
    final SafeBufferedReader in;
    final SafePrintWriter out;
	public static BlockingDeque deque = mainClient.queueOfIdleThreads;

	TaskImpl (final Socket socket) throws IOException {
	this.socket = socket;
	this.in = SocketIO.singleton.buildSafeBufferedReader (socket);
	this.out = SocketIO.singleton.buildSafePrintWriter (socket,"\n");
    }

    synchronized public void run () {
	try {
	    Debug.out.println ("Task.run: Starting");
	    out.println ("PINGPONG server ready.");
	    String line = in.readLine ();
	    while ((line !=null) && (!line.startsWith ("QUIT"))) {
		Debug.out.println ("Task.run: got " + line);
		if (line.startsWith ("PING")){
		    Debug.out.println ("Task.run: Printing PONG message");
		    out.println ("PONG");
		} else if (line.startsWith ("PONG")) {
		    Debug.out.println ("Task.run: Printing PING message");
		    out.println ("PING");
		} else {
		    Debug.out.println ("Task.run: Printing error message");
		    out.println ("ERROR");
		}
		line = in.readLine ();
	    }
	    Debug.out.println ("Task.run: Returning");
	} catch (final IOException ex) {
	    Debug.out.println ("Task.run: Caught " + ex);
	} finally {
	    try {
		socket.close ();
	    } catch (final IOException ex) {
		Debug.out.println ("Task.run: Caught " + ex + " on closing");
	    }
	}
    }

    synchronized public void cancel () {
	try {
	    Debug.out.println ("Task.cancel: Starting");
	    out.println ("PINGPONG server to busy.");
	} catch (final IOException ex) {
	    Debug.out.println ("Task.cancel: Caught " + ex);
	} finally {
	    try {
		socket.close ();
	    } catch (final IOException ex) {
		Debug.out.println ("Task.cancel: Caught " + ex + " on closing");
	    }
	}
    }

}
