package passatempo;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ImageIcon;

public class TelaSpotipoggers extends JFrame{
    private JLabel capaLabel;
    private JButton playPauseButton;
    private JList<Musica> listaMusicas;
    private JProgressBar barraProgresso;
    private List<Musica> musicas;
    private PlayerMp3 playerMP3 = new PlayerMp3();
    private boolean tocando = false;
    private PlayerWav playerWav = new PlayerWav();
    private Timer timerProgresso;

    //Cria a interface gráfica.
    public TelaSpotipoggers(){
        setTitle("Spotipoggers");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

    //Capa do álbum.
    capaLabel = new JLabel();
    capaLabel.setPreferredSize(new Dimension(400, 200));
    capaLabel.setHorizontalAlignment(JLabel.CENTER);
    capaLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    add(capaLabel, BorderLayout.NORTH);

    //Lista de músicas.
    //Aqui é basicamente um vetor de strings que vai ser exibido na lista.
    musicas = new ArrayList<>();
    musicas.add(new Musica("Música 1", "Artista 1", "caminho/para/musica1.mp3", "caminho/para/capa1.jpg"));
    musicas.add(new Musica("Música 2", "Artista 2", "caminho/para/musica2.mp3", "caminho/para/capa2.jpg"));
    musicas.add(new Musica("Música 3", "Artista 3", "caminho/para/musica3.mp3", "caminho/para/capa3.jpg"));

    DefaultListModel<Musica> model = new DefaultListModel<>();
    for(Musica m : musicas){
        model.addElement(m);
    }

    listaMusicas = new JList<>(model);
    add(new JScrollPane(listaMusicas), BorderLayout.CENTER);

    //Painel inferior com botão e barra de progresso.
    JPanel painelInferior = new JPanel(new BorderLayout());

    playPauseButton = new JButton("Toca essa porra");
    playPauseButton.addActionListener(e -> {
        Musica selecionada = listaMusicas.getSelectedValue();
        if (selecionada != null){
            if (tocando){
                playerMP3.tocar(selecionada.getCaminhoArquivo());
                playPauseButton.setText("Pausar");
                tocando = true;
            } else {
                playerMP3.parar();
                playPauseButton.setText("Reproduzir");
                tocando = false;
            }
        }
    });
    painelInferior.add(playPauseButton, BorderLayout.WEST);
    barraProgresso = new JProgressBar();
    painelInferior.add(barraProgresso, BorderLayout.CENTER);

    add(painelInferior, BorderLayout.SOUTH);

    //Atualiza a capa ao selecionar uma música
    listaMusicas.addListSelectionListener(new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            Musica selecionada = listaMusicas.getSelectedValue();
            if (selecionada != null) {
                capaLabel.setIcon(new ImageIcon(selecionada.getCaminhoCapa()));
            }
        }
    });
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaSpotipoggers tela = new TelaSpotipoggers();
            tela.setVisible(true);
        });
    }
}
