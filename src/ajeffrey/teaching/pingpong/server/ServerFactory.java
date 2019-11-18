package ajeffrey.teaching.pingpong.server;

/**
 * A Ping-Pong server factory class.
 * @author Alan Jeffrey
 * @version 1.0.1
 */
public interface ServerFactory {

    public Server build (int portNumber);

}
