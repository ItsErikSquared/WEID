package dev.itserik.weid;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class OnlyOne {

    private static final int PORT = 9999;
    private static ServerSocket socket;

    static void checkIfRunning() {
        try {
            //Bind to localhost adapter with a zero connection queue
            socket = new ServerSocket(PORT,0, InetAddress.getByAddress(new byte[] {127,0,0,1}));
        }
        catch (BindException e) {
            System.err.println("Already running.");
            System.exit(1);
        }
        catch (IOException e) {
            System.err.println("Unexpected error.");
            e.printStackTrace();
            System.exit(2);
        }
    }
}
