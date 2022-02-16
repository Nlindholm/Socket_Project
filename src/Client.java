import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
//import java.util.Random;
import java.util.Scanner;

public class Client {

    //size of the reply codes from the server
    //(reply code indicates whether the client request was accepted or rejected by server)
    private final static int SERVER_CODE_LENGTH = 1;

    public static void main(String[] args) throws IOException{

        if (args.length != 2) {
            System.err.println("Usage: java Client <server_IP> <server_port>");
            System.exit(0);
        }

        int serverPort = Integer.parseUnsignedInt(args[1]);
        String serverAddr = args[0];

        String command;
        do{
            Scanner keyboard = new Scanner(System.in);
            System.out.println("enter a command (D, G, L, R, or Q):");
            //Commands are NOT case-sensitive.
            command = keyboard.next().toUpperCase();
            //This is t0 read/clear the new-line character:
            keyboard.nextLine();

            switch (command) {
                case "L":
                    //List all files (ignoring directories) in the server directory
                    //(file name : file size)
                    ByteBuffer buffer = ByteBuffer.wrap("L".getBytes());
                    SocketChannel channel = SocketChannel.open();
                    channel.connect(new InetSocketAddress(serverAddr, serverPort));
                    //System.out.println("TCP connection established.");

                    //The random sleep is for testing purpose only!
                    // try {
                    //    Thread.sleep(new Random().nextInt(20000));
                    // }catch(InterruptedException e){;}

                    //read from the buffer into the channel
                    channel.write(buffer);

                    //before writing to buffer, clear buffer
                    //("position" set to zero, "limit" set to "capacity")
                    buffer.clear();

                    int bytesRead;
                    //read will return -1 if the server has closed the TCP connection
                    // (when server has done sending)
                    if (serverCode(channel).equals("F")) {
                        System.out.println("Server rejected the request.");
                    } else {
                        ByteBuffer data = ByteBuffer.allocate(1024);
                        while ((bytesRead = channel.read(data)) != -1) {
                            //before reading from buffer, flip buffer
                            //("limit" set to current position, "position" set to zero)
                            data.flip();
                            byte[] a = new byte[bytesRead];
                            //copy bytes from buffer to array
                            //(all bytes between "position" and "limit" are copied)
                            data.get(a);
                            String serverMessage = new String(a);
                            System.out.println(serverMessage);
                        }
                    }
                    channel.close();
                    break;

                case "D":
                    File f = new File("");

                    if (f.delete())
                        System.out.println("File was deleted");
                    else
                        System.out.println("File was not deleted");
                    //Delete a file
                    //Ask the user for the file name
                    //Notify the user whether the operation is successful
                    break;

                case "G":
                    try (BufferedInputStream inputStream = new BufferedInputStream(new URL("").openStream());

                    FileOutputStream fileOS = new FileOutputStream("/Users/username/Documents/file_name.txt") {
                        byte data[] = new byte[1024];
                        int byteContent;
                        while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                            fileOS.write(data, 0, byteContent);
                        }
                    } catch (IOException e) {

                }









                    //Get a file from the server
                    //Ask the user for the file name
                    //Notify the user whether the operation is successful
                    break;

                case "R":
                    //Rename a file
                    //Ask the user for the original file name
                    //and the new file name.
                    //Notify the user whether the operation is successful.
                    break;

                default:
                    if (!command.equals("Q")){
                        System.out.println("Unknown command!");
                    }
            }
        }while(!command.equals("Q"));
    }

    private static String serverCode(SocketChannel channel) throws IOException{
        ByteBuffer buffer = ByteBuffer.allocate(SERVER_CODE_LENGTH);
        int bytesToRead = SERVER_CODE_LENGTH;

        //make sure we read the entire server reply
        while((bytesToRead -= channel.read(buffer)) > 0);

        //before reading from buffer, flip buffer
        buffer.flip();
        byte[] a = new byte[SERVER_CODE_LENGTH];
        //copy bytes from buffer to array
        buffer.get(a);
        String serverReplyCode = new String(a);

        //System.out.println(serverReplyCode);

        return serverReplyCode;
    }
}
