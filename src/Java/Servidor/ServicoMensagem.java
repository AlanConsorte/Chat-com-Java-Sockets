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
        Servidor.LOGGER.fine(timestamp + " FINE (" + apelido + ")\n" + texto);
        String mensagemParaClientes = timestamp + " (" + apelido + ") – " + texto;

        for (Participante p : Servidor.participantes) {
            p.enviarMensagem(mensagemParaClientes);
        }
    }
}
