package Java.Cliente;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Cliente extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(Cliente.class.getName());
    private static final int PORTA_SERVIDOR = 50123;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private JTextArea areaChat;
    private JTextField campoMensagem;
    private JButton botaoEnviar;

    public Cliente(String ip, String apelido) {
        super("Chat - " + apelido);
        configurarInterface();
        conectarAoServidor(ip, apelido);
    }

    private void configurarInterface() {
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        areaChat = new JTextArea();
        areaChat.setEditable(false);
        areaChat.setLineWrap(true);
        areaChat.setWrapStyleWord(true);
        add(new JScrollPane(areaChat), BorderLayout.CENTER);

        JPanel painelInferior = new JPanel(new BorderLayout());
        campoMensagem = new JTextField();
        botaoEnviar = new JButton("Send");

        painelInferior.add(campoMensagem, BorderLayout.CENTER);
        painelInferior.add(botaoEnviar, BorderLayout.EAST);
        add(painelInferior, BorderLayout.SOUTH);

        botaoEnviar.addActionListener(e -> enviarMensagemDigitada());
        campoMensagem.addActionListener(e -> enviarMensagemDigitada());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (out != null) {
                    out.println("##sair##");
                }
                fecharTudo();
            }
        });
    }

    private void conectarAoServidor(String ip, String apelido) {
        try {
            socket = new Socket(ip, PORTA_SERVIDOR);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("LOGIN:" + apelido);

            new Thread(this::escutarServidor).start();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Não foi possível conectar ao servidor " + ip, e);
            System.exit(1);
        }
    }

    private void escutarServidor() {
        try {
            String mensagemRecebida;
            while ((mensagemRecebida = in.readLine()) != null) {
                String finalMsg = mensagemRecebida;
                SwingUtilities.invokeLater(() -> areaChat.append(finalMsg + "\n"));
            }
        } catch (IOException e) {
            LOGGER.fine("Conexão com o servidor encerrada.");
        }
    }

    private void enviarMensagemDigitada() {
        String texto = campoMensagem.getText().trim();
        if (!texto.isEmpty() && out != null) {
            out.println(texto);
            campoMensagem.setText("");
        }
    }

    private void fecharTudo() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao fechar recursos do cliente", e);
        }
    }

    public static void main(String[] args) {

        if (args.length < 2) {
            LOGGER.log(Level.SEVERE,
                    "Uso incorreto! Execute passando parâmetros: java chat.Cliente <IP_SERVIDOR> <APELIDO>");
            System.exit(1);
        }

        String ipServidor = args[0];
        String apelidoUsuario = args[1];

        SwingUtilities.invokeLater(() -> {
            Cliente cliente = new Cliente(ipServidor, apelidoUsuario);
            cliente.setVisible(true);
        });
    }
}