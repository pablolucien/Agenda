package org.pclg.xtras;
// http://forum.java.sun.com/thread.jsp?forum=31&thread=294564
// author smg123
//

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;

class CFtp {
    private static final boolean debug = false;

    private static final int FTP_PORT = 21;

    static int FTP_UNKNOWN = -1;
    private static final int FTP_SUCCESS = 1;
    private static final int FTP_TRY_AGAIN = 2;
    private static final int FTP_ERROR = 3;
    static int FTP_NOCONNECTION = 100;
    static int FTP_BADUSER = 101;
    static int FTP_BADPASS = 102;

    private static final int FILE_GET = 1;
    public static int FILE_PUT = 2;

    /**
     * socket for data transfer
     */
    private Socket dataSocket;
    private boolean replyPending;
    private boolean binaryMode;
    private boolean passiveMode;
    private boolean m_bGettingFile;

    /**
     * user name for login
     */
	private String user;

    /**
     * password for login
     */
	private String password;

    /**
     * last command issued
     */
	private String command;

    /**
     * The last reply code from the ftp daemon.
     */
	private int lastReplyCode;

    /**
     * Welcome message from the server, if any.
     */
    public String welcomeMsg;

    /**
     * Array of strings (usually 1 entry) for the last reply
     * from the server.
     */
	private Vector serverResponse = new Vector(1);

    /**
     * Socket for communicating with server.
     */
	private Socket serverSocket;

    /**
     * Stream for printing to the server.
     */
	private PrintWriter serverOutput;

    /**
     * Buffered stream for reading replies from server.
     */
	private InputStream serverInput;

    /* String to hold the file we are up/downloading */
	private String strFileNameAndPath;
    private String m_strSource;
    private String m_strDestination;

    public void setFilename(final String strFile) {
        strFileNameAndPath = strFile;
    }

    String getFileName() {
        return strFileNameAndPath;
    }

    public void setSourceFile(final String strSourceFile) {
        m_strSource = strSourceFile;
    }

    public void setDestinationFile(final String strDestinationFile) {
        m_strDestination = strDestinationFile;
    }

    public String getSourceFile() {
        return m_strSource;
    }

    public String getDestinationFile() {
        return m_strDestination;
    }

    String getCurrentDirectory() {
        return CSystem.getCurrentDir();
    }

    /**
     * Return server connection status
     */
	boolean serverIsOpen() {
        return serverSocket != null;
    }

    /**
     * Set Passive mode Trasfers
     */
    public void setPassive(final boolean mode) {
        passiveMode = mode;
    }

    int readServerResponse() throws IOException {
        final StringBuffer replyBuf = new StringBuffer(32);
        int c;
        int continuingCode = -1;
        int code = -1;
        String response;

        try {
            while (true) {
                while ((c = serverInput.read()) != -1) {
                    if (c == '\r') {
                        if ((c = serverInput.read()) != '\n') {
                            replyBuf.append('\r');
                        }
                    }
                    replyBuf.append((char) c);
                    if (c == '\n') {
                        break;
                    }
                }
                response = replyBuf.toString();
                replyBuf.setLength(0);

                try {
                    code = Integer.parseInt(response.substring(0, 3));
                }
                catch (final NumberFormatException e) {
                    code = -1;
                }
                catch (final StringIndexOutOfBoundsException e) {
                    /* this line doesn't contain a response code, so
               we just completely ignore it */
                    continue;
                }
                serverResponse.addElement(response);

                if (continuingCode != -1) {
                    /* we've seen a XXX- sequence */
                    if (code != continuingCode || (response.length() >= 4
                            && response.charAt(3) == '-')) {
                        continue;
                    } else {
                        /* seen the end of code sequence */
                        continuingCode = -1;
                        break;
                    }

                } else
                if (response.length() >= 4 && response.charAt(3) == '-') {
                    continuingCode = code;
                    continue;
                } else {
                    break;
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        if (debug) {
            CSystem.PrintDebugMessage("readServerResponse done");
        }
        return lastReplyCode = code;
    }

    /**
     * Sends command <i>cmd</i> to the server.
     */
	void sendServer(final String cmd) {
        if (debug) {
            CSystem.PrintDebugMessage("sendServer start");
        }
        serverOutput.println(cmd);

        if (debug) {
            CSystem.PrintDebugMessage("sendServer done");
        }
    }

    /**
     * Returns all server response strings.
     */
	String getResponseString() {
        String s = new String();
        for (int i = 0; i < serverResponse.size(); i++) {
            s += serverResponse.elementAt(i);
        }
        serverResponse = new Vector(1);
        return s;
    }

    String getResponseStringNoReset() {
        String s = new String();
        for (int i = 0; i < serverResponse.size(); i++) {
            s += serverResponse.elementAt(i);
        }
        return s;
    }


    /**
     * issue the QUIT command to the FTP server and close the connection.
     */
	void closeServer() throws IOException {
        if (serverIsOpen()) {
            issueCommand("QUIT");
            if (! serverIsOpen()) {
                return;
            }

            serverSocket.close();
            serverSocket = null;
            serverInput = null;
            serverOutput = null;
        }
    }

    int issueCommand(final String cmd) throws IOException {
        command = cmd;
        int reply;
        if (debug) {
            CSystem.PrintDebugMessage(cmd);
        }

        if (replyPending) {
            if (debug) {
                CSystem.PrintDebugMessage("replyPending");
            }

            if (readReply() == FTP_ERROR) {
                System.out.print("Error reading pending reply\n");
            }
        }
        replyPending = false;

        do {
            sendServer(cmd);
            reply = readReply();
        } while (reply == FTP_TRY_AGAIN);
        return reply;
    }

    void issueCommandCheck(final String cmd) throws IOException {
        if (issueCommand(cmd) != FTP_SUCCESS) {
            throw new FtpProtocolException(cmd);
        }
    }

    int readReply() throws IOException {
        lastReplyCode = readServerResponse();

        switch (lastReplyCode / 100) {
            case 1:
                replyPending = true;
                /* falls into ... */

            case 2://This case is for future purposes. If not properly used, it might cause an infinite loop.
                //Don't add any code here , unless you know what you are doing.

            case 3:
                return FTP_SUCCESS;

            case 5:
                if (lastReplyCode == 530) {
                    if (user == null) {
                        throw new FtpLoginException("Not logged in");
                    }
                    return FTP_ERROR;
                }

                if (lastReplyCode == 550) {
                    if (!command.startsWith("PASS")) {
                        throw new FileNotFoundException(command);
                    } else {
                        throw new FtpLoginException("Error: Wrong Password!");
                    }
                }
        }
        return FTP_ERROR;
    }

    Socket openDataConnection(final String cmd) throws IOException {
        ServerSocket portSocket = null;
        String portCmd;
        final InetAddress myAddress = InetAddress.getLocalHost();
        final byte[] addr = myAddress.getAddress();
        int shift;
        final String ipaddress;
        int port = 0;
        IOException e;

        if (this.passiveMode) {
            CSystem.PrintDebugMessage("Passive Mode Transfers");
            /* First let's attempt to initiate Passive transfers */

            try {    // PASV code
                getResponseString();
                if (issueCommand("PASV") == FTP_ERROR) {
                    e = new FtpProtocolException("PASV");
                    throw e;
                }

                String reply = getResponseStringNoReset();
                reply = reply
                        .substring(reply.indexOf("(") + 1, reply.indexOf(")"));
                final StringTokenizer st = new StringTokenizer(reply, ",");
                final String[] nums = new String[6];
                int i = 0;

                while (st.hasMoreElements()) {
                    try {
                        nums[i] = st.nextToken();
                        i++;
                    }
                    catch (final Exception a) {
                        a.printStackTrace();
                    }
                }

                ipaddress = nums[0] + "." + nums[1] + "." + nums[2] + "."
                        + nums[3];

                try {
                    final int firstbits = Integer.parseInt(nums[4]) << 8;
                    final int lastbits = Integer.parseInt(nums[5]);
                    port = firstbits + lastbits;
                }
                catch (final Exception b) {
                    b.printStackTrace();
                }

                if ((ipaddress != null) && (port != 0)) {
                    dataSocket = new Socket(ipaddress, port);
                } else {
                    e = new FtpProtocolException("PASV");
                    throw e;
                }

                if (issueCommand(cmd) == FTP_ERROR) {
                    e = new FtpProtocolException(cmd);
                    throw e;
                }

            }
            catch (final FtpProtocolException fpe) {  // PASV was not supported...resort to PORT
                portCmd = "PORT ";

                /* append host addr */
                for (int i = 0; i < addr.length; i++) {
                    portCmd = portCmd + (addr[i] & 0xFF) + ",";
                }

                /* append port number */
                portCmd = portCmd + ((portSocket.getLocalPort() >>> 8) & 0xff)
                        + ","
                        + (portSocket.getLocalPort() & 0xff);
                if (issueCommand(portCmd) == FTP_ERROR) {
                    e = new FtpProtocolException("PORT");
                    portSocket.close();
                    throw e;
                }

                if (issueCommand(cmd) == FTP_ERROR) {
                    e = new FtpProtocolException(cmd);
                    portSocket.close();
                    throw e;
                }
                dataSocket = portSocket.accept();
                portSocket.close();

            }
        }//end if passive
        else {  //do a port transfer
            CSystem.PrintDebugMessage("Port Mode Transfers");
            try {
                portSocket = new ServerSocket(0, 1, myAddress);
            }
            catch (final Exception b) {
                b.printStackTrace();
            }

            portCmd = "PORT ";

            /* append host addr */
            for (int i = 0; i < addr.length; i++) {
                portCmd = portCmd + (addr[i] & 0xFF) + ",";
            }

            /* append port number */
            portCmd = portCmd + ((portSocket.getLocalPort() >>> 8) & 0xff) + ","
                    + (portSocket.getLocalPort() & 0xff);
            if (issueCommand(portCmd) == FTP_ERROR) {
                e = new FtpProtocolException("PORT");
                portSocket.close();
                throw e;
            }

            if (issueCommand(cmd) == FTP_ERROR) {
                e = new FtpProtocolException(cmd);
                portSocket.close();
                throw e;
            }
            dataSocket = portSocket.accept();
            portSocket.close();
        }//end of port transfer

        return dataSocket;     // return the dataSocket
    }


    /**
     * open a FTP connection to host <i>host</i>.
     */
    public void openServer(final String host)
            throws IOException, UnknownHostException {
        final int port = FTP_PORT;
        if (serverSocket != null) {
            closeServer();
        }
        serverSocket = new Socket(host, FTP_PORT);
        serverOutput = new PrintWriter(
                new BufferedOutputStream(serverSocket.getOutputStream()), true);
        serverInput = new BufferedInputStream(serverSocket.getInputStream());
    }

    /**
     * open a FTP connection to host <i>host</i> on port <i>port</i>.
     */
	void openServer(final String host, final int port)
            throws IOException, UnknownHostException {
        if (serverSocket != null) {
            closeServer();
        }
        serverSocket = new Socket(host, port);
        //serverSocket.setSoLinger(true,30000);
        serverOutput = new PrintWriter(
                new BufferedOutputStream(serverSocket.getOutputStream()),
                true);
        serverInput = new BufferedInputStream(serverSocket.getInputStream());

        if (readReply() == FTP_ERROR) {
            throw new FtpConnectException("Welcome message");
        }
    }


    /**
     * login user to a host with username <i>user</i> and password
     * <i>password</i>
     */
    public void login(final String user, final String password) throws IOException {

        if (!serverIsOpen()) {
            throw new FtpLoginException("Error: not connected to host.\n");
        }
        this.user = user;
        this.password = password;
        if (issueCommand("USER " + user) == FTP_ERROR) {
            throw new FtpLoginException("Error: User not found.\n");
        }
        if (password != null && issueCommand("PASS " + password) == FTP_ERROR) {
            throw new FtpLoginException("Error: Wrong Password.\n");
        }
    }

    /**
     * login user to a host with username <i>user</i> and no password
     * such as HP server which uses the form "<username>/<password>,user.<group>
     */
    public void login(final String user) throws IOException {

        if (!serverIsOpen()) {
            throw new FtpLoginException("not connected to host");
        }

        this.user = user;

        if (issueCommand("USER " + user) == FTP_ERROR) {
            throw new FtpLoginException("Error: Invalid Username.\n");
        }
    }

    /**
     * GET a file from the FTP server in Ascii mode
     */
    public BufferedReader getAscii(final String filename) throws IOException {
        m_bGettingFile = true;
        Socket s = null;
        try {
            s = openDataConnection("RETR " + filename);
        }
        catch (final FileNotFoundException fileException) {
            throw new FileNotFoundException();
        }
        return new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    /**
     * GET a file from the FTP server in Binary mode
     */
    public BufferedInputStream getBinary(final String filename) throws IOException {
        m_bGettingFile = true;
        Socket s = null;
        try {
            s = openDataConnection("RETR " + filename);
        }
        catch (final FileNotFoundException fileException) {
            throw new FileNotFoundException();
        }
        return new BufferedInputStream(s.getInputStream());
    }

    /**
     * PUT a file to the FTP server in Ascii mode
     */
    public BufferedWriter putAscii(final String filename) throws IOException {
        m_bGettingFile = false;
        final Socket s = openDataConnection("STOR " + filename);
        return new BufferedWriter(new OutputStreamWriter(s.getOutputStream()),
                4096);
    }

    /**
     * PUT a file to the FTP server in Binary mode
     */
    public BufferedOutputStream putBinary(final String filename) throws IOException {
        m_bGettingFile = false;
        final Socket s = openDataConnection("STOR " + filename);
        return new BufferedOutputStream(s.getOutputStream());
    }

    /**
     * APPEND (with create) to a file to the FTP server in Ascii mode
     */
    public BufferedWriter appendAscii(final String filename) throws IOException {
        m_bGettingFile = false;
        final Socket s = openDataConnection("APPE " + filename);
        return new BufferedWriter(new OutputStreamWriter(s.getOutputStream()),
                4096);
    }

    /**
     * APPEND (with create) to a file to the FTP server in Binary mode
     */
    public BufferedOutputStream appendBinary(final String filename)
            throws IOException {
        m_bGettingFile = false;
        final Socket s = openDataConnection("APPE " + filename);
        return new BufferedOutputStream(s.getOutputStream());
    }

    /**
     * NLIST files on a remote FTP server
     */
    public BufferedReader nlist() throws IOException {
        final Socket s = openDataConnection("NLST");
        return new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    /**
     * LIST files on a remote FTP server
     */
    public BufferedReader list() throws IOException {
        final Socket s = openDataConnection("LIST");
        return new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    public BufferedReader ls() throws IOException {
        final Socket s = openDataConnection("LS");
        return new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    public BufferedReader dir() throws IOException {
        final Socket s = openDataConnection("DIR");
        return new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    /**
     * CD to a specific directory on a remote FTP server
     */
    public void cd(final String remoteDirectory) throws IOException {
        issueCommandCheck("CWD " + remoteDirectory);
    }

    public void cwd(final String remoteDirectory) throws IOException {
        issueCommandCheck("CWD " + remoteDirectory);
    }

    /**
     * Rename a file on the remote server
     */
    public void rename(final String oldFile, final String newFile) throws IOException {
        issueCommandCheck("RNFR " + oldFile);
        issueCommandCheck("RNTO " + newFile);
    }

    /**
     * Site Command
     */
    public void site(final String params) throws IOException {
        issueCommandCheck("SITE " + params);
    }

    /**
     * Set transfer type to 'I'
     */
    public void binary() throws IOException {
        issueCommandCheck("TYPE I");
        binaryMode = true;
    }

    /**
     * Set transfer type to 'A'
     */
    public void ascii() throws IOException {
        issueCommandCheck("TYPE A");
        binaryMode = false;
    }

    /**
     * Send Abort command
     */
    public void abort() throws IOException {
        issueCommandCheck("ABOR");
    }

    /**
     * Go up one directory on remots system
     */
    public void cdup() throws IOException {
        issueCommandCheck("CDUP");
    }

    /**
     * Create a directory on the remote system
     */
    public void mkdir(final String s) throws IOException {
        issueCommandCheck("MKD " + s);
    }

    /**
     * Delete the specified directory from the ftp file system
     */
    public void rmdir(final String s) throws IOException {
        issueCommandCheck("RMD " + s);
    }

    /**
     * Delete the file s from the ftp file system
     */
    public void delete(final String s) throws IOException {
        issueCommandCheck("DELE " + s);
    }

    /**
     * Get the name of the present working directory on the ftp file system
     */
    public void pwd() throws IOException {
        issueCommandCheck("PWD");
    }

    /**
     * Retrieve the system type from the remote server
     */
    public void syst() throws IOException {
        issueCommandCheck("SYST");
    }


    /**
     * New FTP client connected to host <i>host</i>.
     */
    public CFtp(final String host) throws IOException {
        openServer(host, FTP_PORT);
    }

    /**
     * New FTP client connected to host <i>host</i>, port <i>port</i>.
     */
    public CFtp(final String host, final int port) throws IOException {
        openServer(host, port);
    }

    public void SetFileMode(final int nMode) {
        if (nMode == FILE_GET) {
            m_bGettingFile = true;
        } else {
            m_bGettingFile = false;
        }
    }
}

// Exception handlers

class FtpLoginException extends FtpProtocolException {
    private static final long serialVersionUID = 6406994067029277959L;

    FtpLoginException(final String s) {
        super(s);
    }
}

class FtpConnectException extends FtpProtocolException {
    private static final long serialVersionUID = 6427214008403918367L;

    FtpConnectException(final String s) {
        super(s);
    }
}

class FtpProtocolException extends IOException {
    private static final long serialVersionUID = -2077934423747078680L;

    FtpProtocolException(final String s) {
        super(s);
  }
}
