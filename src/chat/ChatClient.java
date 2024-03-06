package chat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {
	private int servidor = 0;
	private InetAddress ipServidor = null;
	private DatagramSocket clientSocket;
	private List<Mensaje> mensajesRecibidos = new ArrayList<>();
    private final static String COD_TEXTO = "UTF-8";
    private String username, room, host = "";
    private boolean Sala = false;

    public static void main(String[] args) throws UnsupportedEncodingException, IOException {
        new ChatClient().start();
    }

    public ChatClient() throws SocketException {
        this.clientSocket = new DatagramSocket();
    }

    public void start() throws UnsupportedEncodingException, IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Bienvenido al Sistema de Chat en Tiempo Real");
        System.out.println("--------------------------------------------");
        System.out.println("Ingresa tu nombre de usuario:");
        username = sc.next();
        System.out.println("Ingresa la IP del servidor al que quieres conectarte:");
        host = sc.next();
        try {
            ipServidor = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("Ingresa el puerto del servidor:");
        servidor = sc.nextInt();
        System.out.println("Intentando conectar");
        enviarMensaje(new Mensaje("0", username, "", ""));
        while (mensajesRecibidos.isEmpty()) {
            waitForMessage();
        }
        procesarMensajeRecibido(mensajesRecibidos.remove(0));

        while (true) {
            if (!Sala) {
                showMenu(sc);
            }
            waitForMessage();
            procesarMensajeRecibido(mensajesRecibidos.remove(0));
        }
    }
    
    public void enviarMensaje(Mensaje mensaje) throws IOException {
        enviarDatos(servidor, ipServidor, mensaje.toString());
    }

    public void enviarDatos(int numPuerto, InetAddress ipServidor, String mensaje) throws IOException {
        DatagramPacket dp = new DatagramPacket(mensaje.getBytes(), mensaje.getBytes().length, ipServidor, numPuerto);
        clientSocket.send(dp);
    }

    private void showMenu(Scanner sc) throws IOException {
        System.out.println("1. Salas disponibles");
        System.out.println("2. Crear nueva sala");
        System.out.println("3. Unirse a sala existente");
        System.out.println("4. Salir del chat");
        int opcion = sc.nextInt();
        switch (opcion) {
            case 1:
                enviarMensaje(new Mensaje("1", username, "", ""));
                break;
            case 2:
                System.out.println("Ingresa el nombre de la nueva sala:");
                room = sc.next();
                enviarMensaje(new Mensaje("2", username, room, ""));
                break;
            case 3:
                System.out.println("Ingresa el nombre de la sala a la que desea unirse");
                room = sc.next();
                enviarMensaje(new Mensaje("3", username, room, ""));
                break;
            case 4:
                System.out.println("Cerrando conexión...");
                System.out.println("Gracias por usar el chat. ¡Hasta la próxima!");
                enviarMensaje(new Mensaje("5", username, room, ""));
                sc.close();
                System.exit(0);
                break;
            default:
                System.out.println("Opción no válida");
                break;
        }
    }

    private void waitForMessage() {
        synchronized (mensajesRecibidos) {
            while (mensajesRecibidos.isEmpty()) {
                try {
                    mensajesRecibidos.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    

    public void salaChat() throws IOException {
        InputStreamReader is = new InputStreamReader(System.in, COD_TEXTO);
        BufferedReader bfr = new BufferedReader(is);
        new Thread(() -> {
            while (true) {
                String lineaLeida;
                try {
                    lineaLeida = bfr.readLine();
                    Mensaje mensaje = new Mensaje("4", username, room, lineaLeida);
                    enviarMensaje(mensaje);
                    if ("/exit".equals(lineaLeida)) {
                        Sala = false;
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void procesarMensajeRecibido(Mensaje recibido) throws IOException {
        switch (recibido.accion) {
            case "0":
                System.out.println("Conexión establecida con el servidor.");
                break;
            case "1":
                if ("404".equals(recibido.getContenido())) {
                    System.out.println("No hay salas disponibles");
                } else {
                    String[] partes = recibido.getContenido().split("\\.");
                    Arrays.stream(partes).forEach(sala -> System.out.println("-" + sala));
                }
                break;
            case "2":
                System.out.println("Sala creada con éxito");
                System.out.println("Has accedido a la sala " + room);
                System.out.println("Para salir escribe '/exit'");
                Sala = true;
                salaChat();
                break;
            case "3":
                if ("200".equals(recibido.getContenido())) {
                    System.out.println("Has accedido a la sala " + room);
                    System.out.println("Para salir escribe '/exit' ");
                    Sala = true;
                    salaChat();
                } else {
                    System.out.println("Esa sala no existe");
                }
                break;
            case "4":
                System.out.println(recibido.getContenido());
                break;
            case "5":
                Sala = false;
                break;
            default:
                System.out.println("Opción no válida");
                break;
        }
    }
}

