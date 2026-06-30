package Java.Servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;

public class Participante implements Runnable {
    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String apelido = "Anônimo";

    public Participante(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // A primeira mensagem enviada pelo cliente deve ser o protocolo de
            // identificação: "LOGIN:apelido"
            String primeiraLinha = in.readLine();
            if (primeiraLinha != null && primeiraLinha.startsWith("LOGIN:")) {
                this.apelido = primeiraLinha.substring(6);
            }

            Servidor.LOGGER.info("Participante '" + apelido + "' conectou com sucesso.");

            String mensagem;
            // Lê continuamente as mensagens enviadas pelo cliente
            while ((mensagem = in.readLine()) != null) {
                if (mensagem.equalsIgnoreCase("##sair##")) {
                    Servidor.LOGGER.fine("Participante '" + apelido + "' solicitou saída (##sair##).");
                    break; // Encerra o laço e finaliza a tarefa
                }

                // Cria o serviço de entrega e repassa para a thread fofoqueira
                ServicoMensagem servico = new ServicoMensagem(apelido, mensagem);
                Servidor.fofoqueiro.execute(servico);
            }

        } catch (IOException e) {
            Servidor.LOGGER.log(Level.WARNING, "Erro na comunicação com o participante '" + apelido + "'", e);
        } finally {
            fecharConexao();
        }
    }

    // Método utilizado pela thread fofoqueira para encaminhar as mensagens a este
    // cliente
    public void enviarMensagem(String mensagemFormatada) {
        if (out != null) {
            out.println(mensagemFormatada);
        }
    }

    private void fecharConexao() {
        try {
            Servidor.participantes.remove(this);
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null && !socket.isClosed())
                socket.close();
            Servidor.LOGGER.info("Participante '" + apelido + "' desconectado do servidor.");
        } catch (IOException e) {
            Servidor.LOGGER.log(Level.SEVERE, "Erro ao fechar socket do participante", e);
        }
    }
}