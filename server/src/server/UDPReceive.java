/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alfon
 */
public class UDPReceive {

    public static void main(String[] args) {

        final int PUERTO = 5000;
        byte[] buffer = new byte[16525];

        try {
            System.out.println("Iniciado el servidor UDP");
            //Creacion del socket
            DatagramSocket socketUDP = new DatagramSocket(PUERTO);

            //Siempre atendera peticiones
           

                //Preparo la respuesta
                DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);
 //                //Recibo el datagrama
//                socketUDP.receive(peticion);
//                System.out.println("Recibo la informacion del cliente");
                File myFile = new File("C:\\Users\\Alfon\\Documents\\NetbeansProjects\\sistemas distribuidos\\server\\jarjar.jpg");
                buffer = Files.readAllBytes(myFile.toPath());
                System.out.println(myFile.toString());
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), 1001);
                socketUDP.send(packet);
                socketUDP.close();

                //Convierto lo recibido y mostrar el mensaje
//                String mensaje = new String(peticion.getData());
//                System.out.println(mensaje);
// 
//                //Obtengo el puerto y la direccion de origen
//                //Sino se quiere responder, no es necesario
//                int puertoCliente = peticion.getPort();
//                InetAddress direccion = peticion.getAddress();
// 
//                mensaje = "¡Hola mundo desde el servidor!";
//                buffer = mensaje.getBytes();
// 
//                //creo el datagrama
//                DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length, direccion, puertoCliente);
// 
//                //Envio la información
//                System.out.println("Envio la informacion del cliente");
//                socketUDP.send(respuesta);
            

        } catch (SocketException ex) {
            Logger.getLogger(UDPReceive.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UDPReceive.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
