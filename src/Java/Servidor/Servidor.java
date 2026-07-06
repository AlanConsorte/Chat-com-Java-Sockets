package Java.Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {
    private static final int PORTA = 50123;

    public static final Logger LOGGER = Logger.getLogger(Servidor.class.getName());
    public static final List<Participante> participantes = new CopyOnWriteArrayList<>();
    public static final ExecutorService fofoqueiro = Executors.newFixedThreadPool(1);

    public static void main(String[] args) {

        LOGGER.setLevel(Level.INFO);

        LOGGER.info("Iniciando o servidor de Chat...");

        ExecutorService poolParticipantes = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            LOGGER.info("Servidor aguardando conexões na porta " + PORTA + "...");

            while (true) {
                Socket socketCliente = serverSocket.accept();
                LOGGER.fine("Nova conexão recebida de: " + socketCliente.getRemoteSocketAddress());
                Participante participante = new Participante(socketCliente);
                participantes.add(participante);
                poolParticipantes.execute(participante);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro crítico no servidor de sockets", e);
        } finally {
            poolParticipantes.shutdown();
            fofoqueiro.shutdown();
        }
    }
}
