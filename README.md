
Especificação

Servidor
Classe Servidor

  Implementa o método main() da aplicação Servidor.

  O servidor da aplicação deve aguardar conexões dos clientes na porta 50123.

  Múltiplas requisições devem ser atendidas simultaneamente pelo servidor. Deve-se criar uma tarefa Participante e enviar para o pool de threads executar.

  Armazene cada um dos clientes conectados (participantes) em uma lista que deve ser percorrida para enviar a mensagem para todos os participantes. Atente-se para a possibilidade da lista ser modificada        enquanto está sendo percorrida.

  Crie um worker thread (FixedThreadPool) que será responsável por enviar cada mensagem à lista de participantes. Vamos chamar este worker thread de "fofoqueiro".

Classe Participante - Runnable

  Implementa as tarefas responsáveis por manter a comunicação com as aplicações cliente.

  Ao receber uma mensagem do respectivo cliente, o participante deve criar uma tarefa (ServicoMensagem) e solicitar para o worker thread "fofoqueiro" executá-la.

  A tarefa do participante encerra quando receber a mensagem "##sair##" da aplicação cliente.

Classe ServicoMensagem - Runnable

  Atributos:

    apelido (emissor)

    texto

  Representa a tarefa que deve ser executada pelo worker thread "fofoqueiro".

  A tarefa basicamente é percorrer a lista de clientes conectados no servidor e enviar a mensagem para cada um deles (inclusive para o emissor da mensagem). Lembre-se que o acesso a esta lista é concorrente,   participantes podem vir a ser adicionados ou removidos do chat enquanto uma mensagem estiver sendo enviada aos participantes.

  A mensagem enviada deve ser apresentada no console do servidor como um log, no seguinte formato:

    27/04/2021 22:40 FINE (apelido do remetente) - Mensagem

Cliente
Classe Cliente

  Implementa o método main() da aplicação Cliente.

  A aplicação cliente deve receber o endereço IP do servidor e o apelido do participante via linha de comando ao ser executada.

  O cliente deve ler a mensagem digitada pelo usuário e enviar para o servidor. O servidor se encarrega de replicar a mensagem para todos os clientes, inclusive para o emissor da mensagem.

  Execute a aplicação cliente preferencialmente em uma janela conforme a Figura 1.

  Ao fechar a janela, a aplicação cliente deve enviar a mensagem "##sair##" para o servidor.
