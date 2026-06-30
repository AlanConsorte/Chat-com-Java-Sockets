package Java.Servidor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServicoMensagem implements Runnable {
    private final String apelido;
    private final String texto;
    private final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public ServicoMensagem(String apelido, String texto) {
        this.apelido = apelido;
        this.texto = texto;
    }

    @Override
    public void run() {
        String timestamp = df.format(new Date());

        // Exibe o Log formatado estritamente conforme especificado no console do
        // Servidor usando Logger
        Servidor.LOGGER.fine(timestamp + " FINE (" + apelido + ")\n" + texto);

        // Formata a mensagem para envio aos clientes (exibido na interface gráfica)
        String mensagemParaClientes = timestamp + " (" + apelido + ") – " + texto;

        // Transmite a mensagem para todos os conectados (incluindo o emissor) de forma
        // thread-safe
        for (Participante p : Servidor.participantes) {
            p.enviarMensagem(mensagemParaClientes);
        }
    }
}
