package chat;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.*;

public class ChatServer {

    private final List<Mensaje> mensajesRecibidos = new ArrayList<>();
    private final HashMap<String, Room> rooms = new HashMap<>();
    private final HashMap<String, User> usuarios = new HashMap<>();

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Debe indicarse el puerto");
            System.exit(1);
        }

        int numPuerto = Integer.parseInt(args[0]);
        new ChatServer().startServer(numPuerto);
    }

    public void startServer(int numPuerto) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(numPuerto);
            System.out.println("Servidor en el puerto " + numPuerto);

            System.out.println("Servidor listo para recibir mensajes.");

            procesarMensaje(serverSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void procesarMensaje(DatagramSocket serverSocket) throws IOException {
        while (true) {
            while (mensajesRecibidos.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Mensaje mensajeRecibido = mensajesRecibidos.remove(0);
            int accion = Integer.parseInt(mensajeRecibido.accion);
            User u = usuarios.get(mensajeRecibido.nombreUsuario);

            switch (accion) {
                case 0:
                    u.enviarDatos(serverSocket, new Mensaje("0", "", "", "200"));
                    break;
                case 1:
                    String listaSalas = rooms.isEmpty() ? "404" : String.join(".", rooms.keySet());
                    u.enviarDatos(serverSocket, new Mensaje("1", "", "", listaSalas));
                    break;
                case 2:
                    String nombreSala = mensajeRecibido.nombreSala;
                    System.out.println("Creando sala " + nombreSala);
                    Room r = new Room(nombreSala, serverSocket);
                    rooms.put(nombreSala, r);
                    r.setUsers(u);
                    new Thread(r).start();
                    u.enviarDatos(serverSocket, new Mensaje("2", "", "", "201"));
                    System.out.println("Sala creada con éxito.");
                    break;
                case 3:
                    Room room = rooms.get(mensajeRecibido.nombreSala);
                    if (room != null) {
                        room.setUsers(u);
                        u.enviarDatos(serverSocket, new Mensaje("3", "", "", "200"));
                    } else {
                        u.enviarDatos(serverSocket, new Mensaje("3", "", "", "404"));
                    }
                    break;
                case 4:
                    Room r4 = rooms.get(mensajeRecibido.nombreSala);
                    if (r4 != null) {
                        r4.setMensajes(mensajeRecibido);
                    }
                    break;
                case 5:
                    Room r5 = rooms.get(mensajeRecibido.nombreSala);
                    if (r5 != null) {
                        usuarios.remove(mensajeRecibido.nombreUsuario);
                    }
                    break;
            }
        }
    }
}



