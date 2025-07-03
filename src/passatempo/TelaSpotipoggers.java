package passatempo;

import javax.swing.*;
import java.awt.*;

public class TelaSpotipoggers extends JFrame{
    private JLabel capaLabel;
    private JButton playPauseButton;
    private JList<String> listaMusicas;
    private JProgressBar barraProgresso;

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
    String[] musicas = {"Música 1", "Música 2", "Música 3"};
    listaMusicas = new JList<>(musicas);
    add(new JScrollPane(listaMusicas), BorderLayout.CENTER);

    //Painel inferior com botão e barra de progresso.
    JPanel painelInferior = new JPanel(new BorderLayout());

    playPauseButton = new JButton("Toca essa porra");
    painelInferior.add(playPauseButton, BorderLayout.WEST);

    barraProgresso = new JProgressBar();
    painelInferior.add(barraProgresso, BorderLayout.CENTER);

    add(painelInferior, BorderLayout.SOUTH);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaSpotipoggers tela = new TelaSpotipoggers();
            tela.setVisible(true);
        });
    }
}
