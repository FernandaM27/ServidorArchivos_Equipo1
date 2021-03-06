/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

/**
 *
 * @author Fer/Dany/Alfo/jaisi
 */
public class Cliente {

    public static void main(String[] args) {
        int port = 9999;
        String host = "127.0.0.1"; // local host
        Cliente fs = new Cliente();
        fs.ready(port, host);
    }

    private void receiveFile(FileOutputStream outToFile, DatagramSocket socket) throws IOException {
        System.out.println("Receiving file");
        boolean flag; // Have we reached end of file
        int sequenceNumber = 0; // Order of sequences
        int foundLast = 0; // The las sequence found
        byte[] message = new byte[1024]; // Where the data from the received datagram is stored
        byte[] fileByteArray = new byte[1021];
        DatagramSocket data = new DatagramSocket();
        DatagramPacket receivedPacket = new DatagramPacket(message, message.length);
        while (true) {
            // Where we store the data to be writen to the file

            // Receive packet and retrieve the data
            socket.receive(receivedPacket);

            message = receivedPacket.getData(); // Data to be written to the file

            // Get port and address for sending acknowledgment
            InetAddress address = receivedPacket.getAddress();
            int port = receivedPacket.getPort();

            // Retrieve sequence number
            sequenceNumber = ((message[0] & 0xff) << 8) + (message[1] & 0xff);
            // Check if we reached last datagram (end of file)
            flag = (message[2] & 0xff) == 1;

            // If sequence number is the last seen + 1, then it is correct
            // We get the data from the message and write the ack that it has been received correctly
            if (sequenceNumber == (foundLast + 1)) {
                // set the last sequence number to be the one we just received
                foundLast = sequenceNumber;
                // Retrieve data from message
                System.arraycopy(message, 3, fileByteArray, 0, 1021);
                // Write the retrieved data to the file and print received data sequence number
                outToFile.write(fileByteArray);
                System.out.println("Received: Sequence number:" + foundLast);

                // Send acknowledgement
                sendAck(foundLast, socket, address, port);
            } else {
                System.out.println("Expected sequence number: " + (foundLast + 1) + " but received " + sequenceNumber + ". DISCARDING");
                // Re send the acknowledgement
                sendAck(foundLast + 1, socket, address, port);
            }
            // Check for last datagram
            if (flag) {
                outToFile.close();
                break;
            }
        }
    }

    private static void sendAck(int foundLast, DatagramSocket socket, InetAddress address, int port) throws IOException {
        // send acknowledgement
        byte[] ackPacket = new byte[2];
        ackPacket[0] = (byte) (foundLast >> 8);
        ackPacket[1] = (byte) (foundLast);
        // the datagram packet to be sent
        DatagramPacket acknowledgement = new DatagramPacket(ackPacket, ackPacket.length, address, port);
        socket.send(acknowledgement);
        System.out.println("Sent ack: Sequence Number = " + foundLast);
    }

    private void ready(int port, String host) {

        System.out.println("Choosing file to send");
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(host);
            String fileName;

            File f = new File(obtenerPeticion());
            fileName = f.getName();
            byte[] fileNameBytes = fileName.getBytes(); // File name as bytes to send it
            DatagramPacket fileStatPacket = new DatagramPacket(fileNameBytes, fileNameBytes.length, address, port); // File name packet
            socket.send(fileStatPacket); // Sending the packet with the file name
            this.receiveFile(new FileOutputStream(f), socket);
            //socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private String obtenerPeticion() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("Archivos disponibles");

            System.out.println("--------------------");

            System.out.println("Libro PDF-> F ");

            System.out.println("Archivo TXT-> T");

            System.out.println("Imagen IMG-> I");

            System.out.print("Seleccione una opción: ");

            String peticion = sc.nextLine();

            if (peticion.equalsIgnoreCase("F")) {
                return "prueba.pdf";
                
            } else if (peticion.equalsIgnoreCase("T")) {
                return "prueba.txt";
                
            } else if (peticion.equalsIgnoreCase("I")) {
                return "prueba.png";
                
            } else {
                System.out.println("Ingrese una opción válida");
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
