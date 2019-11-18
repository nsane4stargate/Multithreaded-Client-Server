package ajeffrey.teaching.io;

import ajeffrey.teaching.debug.Debug;

import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.MalformedURLException;

import com.macfaq.io.SafePrintWriter;
import com.macfaq.io.SafeBufferedReader;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FilterOutputStream;
import java.io.IOException;

/**
 * An interface for building readers and writers from sockets.
 * This interface mainly just saves rewriting lots of boilerplate
 * code for manipulating sockets, but it also makes it easier to
 * close streams built from sockets, by overriding the <code>close</code>
 * method of the stream so that it closes the input stream, the output
 * stream and the socket.
 * @author Alan Jeffrey
 * @version 1.0.2
 */
public interface SocketIO {

    /**
     * Build a new input stream from a socket.
     * When the input stream is closed, the socket is closed too.
     * @param s a socket
     * @return a input stream which reads from the socket
     */
    public InputStream buildInputStream (Socket s) 
	throws IOException;

    /**
     * Build a new output stream from a socket.
     * When the output stream is closed, the socket is closed too.
     * @param s a socket
     * @return a output stream which writes to the socket
     */
    public OutputStream buildOutputStream (Socket s) 
	throws IOException;

    /**
     * Build a new reader from a socket.
     * When the reader is closed, the socket is closed too.
     * @param s a socket
     * @return a reader which reads from the socket
     */
    public Reader buildReader (Socket s) 
	throws IOException;

    /**
     * Build a new writer from a socket.
     * When the writer is closed, the socket is closed too.
     * @param s a socket
     * @return a writer which writes to the socket
     */
    public Writer buildWriter (Socket s) 
	throws IOException;

    /**
     * Build a new buffered reader from a socket.
     * When the reader is closed, the socket is closed too.
     * @param s a socket
     * @return a buffered reader which reads from the socket
     */
    public BufferedReader buildBufferedReader (Socket s) 
	throws IOException;

    /**
     * Build a new safe buffered reader from a socket.
     * When the reader is closed, the socket is closed too.
     * @param s a socket
     * @return a buffered reader which reads from the socket
     */
    public SafeBufferedReader buildSafeBufferedReader (Socket s) 
	throws IOException;

    /**
     * Build a new print writer from a socket.
     * When the print writer is closed, the socket is closed too.
     * @param s a socket
     * @return a print writer which writes to the socket
     */
    public PrintWriter buildPrintWriter (Socket s) 
	throws IOException;

    /**
     * Build a new safe print writer from a socket.
     * When the print writer is closed, the socket is closed too.
     * @param s a socket
     * @param lineSep the line separator
     * @return a safe print writer which writes to the socket
     */
    public SafePrintWriter buildSafePrintWriter (Socket s, String lineSep) 
	throws IOException;

    /**
     * Build a new buffered stream from a socket.
     * When the stream is closed, the socket is closed too.
     * @param s a socket
     * @return a buffered stream which reads from the socket
     */
    public BufferedInputStream buildBufferedInputStream (Socket s) 
	throws IOException;

    /**
     * Build a new print stream from a socket.
     * When the print stream is closed, the socket is closed too.
     * @param s a socket
     * @return a print stream which writes to the socket
     */
    public PrintStream buildPrintStream (Socket s) 
	throws IOException;

    /**
     * Build a socket from a URL.
     * @param url a URL
     * @param defaultPort the default port if the URL doesn't specify one
     * @return a socket for the host and port specified by the URL
     */
    public Socket buildSocket (URL url, int defaultPort) 
	throws IOException, UnknownHostException;

    /**
     * Build a socket from a URL name.
     * @param url a URL name
     * @param defaultPort the default port if the URL doesn't specify one
     * @return a socket for the host and port specified by the URL
     */
    public Socket buildSocket (String url, int defaultPort) 
	throws IOException, UnknownHostException, MalformedURLException;

    /**
     * A singleton implementation of this interface.
     */
    public final SocketIO singleton = new SocketIOImpl ();

}

class SocketIOImpl implements SocketIO {

    public InputStream buildInputStream (final Socket s) 
	throws IOException 
    {
	return new SocketInputStream (s);
    }

    public OutputStream buildOutputStream (final Socket s) 
	throws IOException 
    {
	return new SocketOutputStream (s);
    }

    public Reader buildReader (final Socket s) 
	throws IOException 
    {
	return new InputStreamReader (buildInputStream (s));
    }

    public Writer buildWriter (final Socket s) 
	throws IOException 
    {
	return new OutputStreamWriter (buildOutputStream (s));
    }

    public BufferedReader buildBufferedReader (final Socket s) 
	throws IOException 
    {
	return new BufferedReader (buildReader (s));
    }

    public SafeBufferedReader buildSafeBufferedReader (final Socket s) 
	throws IOException 
    {
	return new SafeBufferedReader (buildReader (s));
    }

    public PrintWriter buildPrintWriter (final Socket s) 
	throws IOException
    {
	return new PrintWriter (buildWriter (s), true);
    }

    public SafePrintWriter buildSafePrintWriter (final Socket s, final String lineSep) 
	throws IOException
    {
	return new SafePrintWriter (buildWriter (s), true, lineSep);
    }

    public BufferedInputStream buildBufferedInputStream (final Socket s) 
	throws IOException 
    {
	return new BufferedInputStream (buildInputStream (s));
    }

    public PrintStream buildPrintStream (final Socket s) 
	throws IOException
    {
	return new PrintStream (buildOutputStream (s), true);
    }

    public Socket buildSocket (final URL url, final int defaultPort) 
	throws IOException, UnknownHostException 
    {
	if (url.getPort () == -1) {
	    return new Socket (url.getHost (), defaultPort);
	} else {
	    return new Socket (url.getHost (), url.getPort ());
	}
    }

    public Socket buildSocket (final String url, final int defaultPort) 
	throws IOException, UnknownHostException, MalformedURLException 
    {
	return buildSocket (new URL (url), defaultPort);
    }

}

class SocketOutputStream extends FilterOutputStream {

    protected final Socket socket;
    
    protected SocketOutputStream (final Socket socket) 
	throws IOException 
    {
	super (socket.getOutputStream ());
	this.socket = socket;
    }

    public void close () throws IOException {
	Debug.out.println ("SocketOutputStream.close: Starting");
	Debug.out.println ("SocketOutputStream.close: Closing out");
	super.close ();
	Debug.out.println ("SocketOutputStream.close: Closing in");
	socket.getInputStream ().close ();	
	Debug.out.println ("SocketOutputStream.close: Closing socket");
	socket.close ();
	Debug.out.println ("SocketOutputStream.close: Returning");
    }

}

class SocketInputStream extends FilterInputStream {

    protected final Socket socket;
    
    protected SocketInputStream (final Socket socket) 
	throws IOException 
    {
	super (socket.getInputStream ());
	this.socket = socket;
    }

    public void close () throws IOException {
	Debug.out.println ("SocketInputStream.close: Starting");
	Debug.out.println ("SocketInputStream.close: Closing out");
	socket.getOutputStream ().close ();
	Debug.out.println ("SocketInputStream.close: Closing in");
	super.close ();
	Debug.out.println ("SocketInputStream.close: Closing socket");
	socket.close ();
	Debug.out.println ("SocketInputStream.close: Returning");
    }

}
