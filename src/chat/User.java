package chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class User {
    private String username;
    private InetAddress ipUsuario;
    private int puerto;

    public User(String username, InetAddress ipUsuario, int puerto) {
        this.username = username;
        this.ipUsuario = ipUsuario;
        this.puerto = puerto;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public InetAddress getIpUsuario() {
        return ipUsuario;
    }
    public void setIpUsuario(InetAddress ipUsuario) {
        this.ipUsuario = ipUsuario;
    }
    public String getUsername() {
        return username;
    }
    public int getPuerto() {
        return puerto;
    }
    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public void enviarDatos(DatagramSocket serverSocket, Mensaje mensaje) throws IOException {
        byte[] mensajeBytes = mensaje.toString().getBytes();
        DatagramPacket dp = new DatagramPacket(mensajeBytes, mensajeBytes.length, ipUsuario, puerto);
        serverSocket.send(dp);
        System.out.println("Mensaje enviado: " + mensaje);
    }
}




