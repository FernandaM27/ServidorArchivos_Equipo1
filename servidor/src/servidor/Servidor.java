/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Dany/Fer/Jc/Alfonsin
 */
public class Servidor {
    //private static byte[] message = new byte[1024]; 

    public static void main(String[] args) throws SocketException, IOException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(2);
       // DatagramSocket socket = new DatagramSocket(9999);
      //DatagramPacket packet = new DatagramPacket(message, message.length);
              
//                socket.receive(packet);
//                service.execute(new SendFile(packet,socket));

        for (int i = 0; i < 2; i++) {
            service.execute(new SendFile());
            //Una vez que recibe, se tiene que esperar un ratito 
            //para volver a enviar
            Thread.sleep(30000);
        }
        
        
    }
}
