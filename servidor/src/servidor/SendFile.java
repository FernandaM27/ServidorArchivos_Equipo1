/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 *
 * @author Alfon
 */
public class SendFile implements Runnable {
    private DatagramSocket socket;
    private String serverRoute;
    private DatagramPacket datos;
    
    public SendFile(DatagramPacket datos, DatagramSocket socket) {
        this.datos = datos;
        this.socket=socket;
        
        serverRoute = "C:\\Users\\fermi\\Downloads\\UDP\\random";
    }

    @Override
    public void run() {
         this.createFile();
        
    }

    public void createFile() {
        try {
            
            System.out.println("Receiving file name");
            byte[] data = datos.getData(); // Reading the name in bytes
            String fileName = new String(data, 0, datos.getLength()); // Converting the name to string
            System.out.println("Creating file");
//            File f = new File(serverRoute + "\\" + fileName); // Creating the file
            // Creating the stream through which we write the file content

//            receiveFile(outToFile, socket); // Receiving the file
            byte[] bytes= this.readFileToByteArray(this.buscarArchivo(fileName));
                
            this.sendFile(socket, bytes, datos.getAddress(), datos.getPort());
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private File buscarArchivo(String archivo) throws FileNotFoundException {
        File f = new File(serverRoute + "\\" + archivo);
        System.out.println(f.getName());
        System.out.println(f.exists());
//        FileOutputStream outToFile = new FileOutputStream(f);
        return f;
    }


    
    private void sendFile(DatagramSocket socket, byte[] fileByteArray, InetAddress address, int port) throws IOException {
        System.out.println("Sending file");
        System.out.println(address.toString());
        int sequenceNumber = 0; // For order
        boolean flag; // To see if we got to the end of the file
        int ackSequence = 0; // To see if the datagram was received correctly
        
        byte[] ack = new byte[2]; // Create another packet for datagram ackknowledgement
        
        
        System.out.println("tamaño:"+fileByteArray.length);
        for (int i = 0; i < fileByteArray.length; i = i + 1021) {
            System.out.println(".");
            sequenceNumber += 1;

            byte[] message = new byte[1024]; // First two bytes of the data are for control (datagram integrity and order)
            
            // Create message
            message[0] = (byte) (sequenceNumber >> 8);
            message[1] = (byte) (sequenceNumber);

            if ((i + 1021) >= fileByteArray.length) { // Have we reached the end of file?
                flag = true;
                message[2] = (byte) (1); // We reached the end of the file (last datagram to be send)
            } else {
                flag = false;
                message[2] = (byte) (0); // We haven't reached the end of the file, still sending datagrams
            }

            if (!flag) {
                System.arraycopy(fileByteArray, i, message, 3, 1021);
            } else { // If it is the last datagram
                System.arraycopy(fileByteArray, i, message, 3, fileByteArray.length - i);
            }

            DatagramPacket sendPacket = new DatagramPacket(message, message.length, address, port); // The data to be sent
            
            socket.send(sendPacket); // Sending the data
            System.out.println("Sent: Sequence number = " + sequenceNumber);

            boolean ackRec; // Was the datagram received?

            while (true) {

                DatagramPacket ackpack = new DatagramPacket(ack, ack.length);
                
                try {
                    socket.setSoTimeout(50); // Waiting for the server to send the ack
                    socket.receive(ackpack);
                    ackSequence = ((ack[0] & 0xff) << 8) + (ack[1] & 0xff); // Figuring the sequence number
                    ackRec = true; // We received the ack
                } catch (SocketTimeoutException e) {
                    System.out.println("Socket timed out waiting for ack");
                    ackRec = false; // We did not receive an ack
                }

                // If the package was received correctly next packet can be sent
                if ((ackSequence == sequenceNumber) && (ackRec)) {
                    System.out.println("Ack received: Sequence Number = " + ackSequence);
                    break;
                } // Package was not received, so we resend it
                else {
                    socket.send(sendPacket);
                    System.out.println("Resending: Sequence Number = " + sequenceNumber);
                }
            }
        }
    }

    private byte[] readFileToByteArray(File file) {
        FileInputStream fis = null;
        // Creating a byte array using the length of the file
        // file.length returns long which is cast to int
        byte[] bArray = new byte[(int) file.length()];
        try {
            fis = new FileInputStream(file);
            fis.read(bArray);
            fis.close();

        } catch (IOException ioExp) {
            ioExp.printStackTrace();
        }
        return bArray;
    }
    
    
}
