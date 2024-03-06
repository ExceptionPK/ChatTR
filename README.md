# Chat en Tiempo Real con Java
Este es un simple proyecto de chat en tiempo real implementado en Java utilizando programación de sockets. Permite a múltiples usuarios conectarse a un servidor de chat, unirse a diferentes salas, enviar y recibir mensajes en tiempo real.

# Funcionalidades
- Conexión Cliente-Servidor: Implementación de sockets para establecer una comunicación en tiempo real entre el cliente y el servidor.
- Gestión de Salas de Chat: Capacidad para crear, listar y unirse a salas de chat, incluida la gestión de usuarios en las salas.
- Envío y Recepción de Mensajes: Los usuarios pueden enviar mensajes dentro de una sala que serán recibidos por todos los miembros de dicha sala en tiempo real.
- Interfaz de Usuario: Se proporciona una interfaz de usuario de línea de comandos para el cliente.
- Comandos del Chat: Implementación de comandos especiales como /list, /create, /join, /exit y la funcionalidad para enviar mensajes.
- Manejo de Múltiples Clientes: El servidor maneja conexiones concurrentes de múltiples clientes de manera eficiente y segura, utilizando hilos separados para cada cliente.

# Uso
1.	Ejecución del Servidor:
- Compile y ejecute la clase ChatServer para iniciar el servidor de chat.
2.	Ejecución del Cliente:
- Compile y ejecute la clase ChatClient para conectar clientes al servidor y comenzar a chatear.
