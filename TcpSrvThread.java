package org.shared.board.app;



import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * The type Tcp srv thread.
 */
public class TcpSrvThread implements Runnable {
    /**
     * The Socket.
     */
    private Socket sock;

    /**
     * The SharedBoardServerController.
     */
    private ServerController theController;

    /**
     * The SharedBoardServerController.
     */


    /**
     * The constant VERSION.
     */
    public static final int VERSION = 1;

    /**
     * Instantiates a new Tcp srv thread.
     *
     * @param cliSer the cli s
     */
    public TcpSrvThread(final Socket cliSer) {
        sock = cliSer;
        theController = new ServerController(
                new ServerService());

    }

    /**
     * Thread Runnable.
     */
    public void run() {
        Message message;
        InetAddress clientIP;

        clientIP = sock.getInetAddress();

        System.out.println("New client connection from "
                + clientIP.getHostAddress()
                + ", port number " + sock.getPort());


        try {
            MessageFormat mf = new MessageFormat(sock);

            do {
                message = mf.readMessage();


                if (message.code() == MessageCodes.COMMTEST) {
                    mf.sendMessage(VERSION, MessageCodes.ACK, "");
                }
                if (message.code() == MessageCodes.UPLOAD_FILE) {
                    try {
                        int result = theController.upload(message);

                        mf.sendMessage(VERSION, result, "");
                    } catch (IllegalArgumentException e) {
                        mf.sendMessage(VERSION, MessageCodes.ERR,
                                e.getMessage());
                    }
                }
                if (message.code() == MessageCodes.DOWNLOAD_FILE) {
                    try {
                        int result = theController.download(message);

                        mf.sendMessage(VERSION, result, "");
                    } catch (IllegalArgumentException e) {
                        mf.sendMessage(VERSION, MessageCodes.ERR,
                                e.getMessage());
                    }
                }
                if (message.code() == MessageCodes.DELETE_FILE) {
                    try {
                        int result = theController.delete(message);

                        mf.sendMessage(VERSION, result, "");
                    } catch (IllegalArgumentException e) {
                        mf.sendMessage(VERSION, MessageCodes.ERR,
                                e.getMessage());
                    }
                }

                if (message.code() == MessageCodes.AUTH) {
                    try {
                        int result = theController.authenticate(message);

                        mf.sendMessage(VERSION, result, "");
                    } catch (IllegalArgumentException e) {
                        mf.sendMessage(VERSION, MessageCodes.ERR,
                                e.getMessage());
                    }
                }

            } while (message.code() != MessageCodes.DISCONN);

            mf.sendMessage(VERSION, MessageCodes.ACK, "");
            System.out.println("Client " + clientIP.getHostAddress()
                    + ", port number: " + sock.getPort()
                    + " disconnected");

            sock.close();
        } catch (IOException ex) {
            System.out.println("IOException");
        }
    }
}
