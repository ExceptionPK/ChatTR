package chat;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Room implements Runnable {
    String name;
    List<Mensaje> mensajesRoom = new ArrayList<>();
    HashMap<String, User> users = new HashMap<>();
    DatagramSocket serverSocket = null;

    public Room(String name, DatagramSocket serverSocket) {
        this.name = name;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (mensajesRoom) {
                while (mensajesRoom.isEmpty()) {
                    try {
                        mensajesRoom.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                for (Mensaje m : mensajesRoom) {
                    if (m.contenido.equals("/exit")) {
                        User u = users.get(m.nombreUsuario);
                        Mensaje mensaje = new Mensaje("5", "", "", "");
                        try {
                            u.enviarDatos(serverSocket, mensaje);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        users.remove(m.nombreUsuario);
                        m.contenido = m.nombreUsuario + " ha salido de la sala";
                    }
                }

                mensajesRoom.clear();

                if (users.isEmpty()) {
                    break;
                }
            }
        }
    }
    
    public void setUsers(User user) {
        this.users.put(user.username, user);
    }

    public String getName() {
        return name;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
    
    public void setMensajes(Mensaje m) {
        synchronized (mensajesRoom) {
            this.mensajesRoom.add(m);
            mensajesRoom.notifyAll();
        }
    }
}



