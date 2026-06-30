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
    // Logger configurado conforme a especificação
    public static final Logger LOGGER = Logger.getLogger(Servidor.class.getName());

    // Lista thread-safe para suportar modificações concorrentes (entradas/saídas)
    // enquanto é percorrida
    public static final List<Participante> participantes = new CopyOnWriteArrayList<>();

    // Executor de thread única ("fofoqueiro") responsável por despachar as
    // mensagens
    public static final ExecutorService fofoqueiro = Executors.newFixedThreadPool(1);

    public static void main(String[] args) {
        // Define o nível inicial para INFO conforme regra final (mude para Level.FINE
        // no desenvolvimento)
        LOGGER.setLevel(Level.INFO);

        LOGGER.info("Iniciando o servidor de Chat...");

        // Pool de threads dinâmico para atender múltiplos participantes simultaneamente
        ExecutorService poolParticipantes = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            LOGGER.info("Servidor aguardando conexões na porta " + PORTA + "...");

            while (true) {
                Socket socketCliente = serverSocket.accept();
                LOGGER.fine("Nova conexão recebida de: " + socketCliente.getRemoteSocketAddress());

                // Cria a tarefa do participante e envia para o pool de execução
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
