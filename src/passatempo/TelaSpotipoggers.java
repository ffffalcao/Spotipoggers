package passatempo;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class TelaSpotipoggers extends JFrame {

    // ==================== PALETA DE CORES (SPOTIFY-LIKE) ====================
    private static final Color COR_FUNDO = new Color(18, 18, 18);
    private static final Color COR_FUNDO2 = new Color(24, 24, 24);
    private static final Color COR_FUNDO_CLARO = new Color(40, 40, 40);
    private static final Color COR_FUNDO_HOVER = new Color(50, 50, 50);
    private static final Color COR_TEXTO = new Color(230, 230, 230);
    private static final Color COR_TEXTO_SEC = new Color(170, 170, 170);
    private static final Color COR_VERDE = new Color(29, 185, 84);
    private static final Color COR_BARRA = new Color(80, 80, 80);

    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONTE_SUB = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_PEQUENA = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONTE_BOTAO = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FONTE_BOTAO_GRANDE = new Font("SansSerif", Font.BOLD, 26);

    // Ícones desenhados por Graphics2D (sem depender de Unicode)
    private Icon iconPlay, iconPause, iconAnterior, iconProximo;
    private Icon iconShuffle, iconShuffleAtivo;
    private Icon iconRepetir, iconRepetirAtivo, iconRepetirUm;

    // ==================== COMPONENTES UI ====================
    private JLabel capaLabel, nomeMusicaLabel, artistaLabel;
    private JLabel tempoAtualLabel, tempoTotalLabel;
    private JSlider barraProgresso, volumeSlider;
    private JButton playPauseBtn, anteriorBtn, proximoBtn, shuffleBtn, repeatBtn;
    private JTextField campoBusca;
    private JList<Musica> listaMusicas;
    private DefaultListModel<Musica> modeloLista;

    // ==================== ESTADO ====================
    private List<Musica> todasMusicas = new ArrayList<>();
    private PlayerUniversal player = new PlayerUniversal();
    private ConfigManager config = new ConfigManager();
    private Timer timerProgresso;
    private boolean musicaIniciada = false;
    private boolean ajustandoProgresso = false;
    private boolean shuffle = false;
    private int modoRepetir = 0; // 0=off, 1=playlist, 2=single
    private int indiceMusicaAtual = -1;

    public TelaSpotipoggers() {
        criarIcones();
        configurarJanela();
        add(criarPainelTopo(), BorderLayout.NORTH);
        add(criarPainelCentro(), BorderLayout.CENTER);
        add(criarPainelInferior(), BorderLayout.SOUTH);
        carregarMusicas(config.getPastaMusicas());
        iniciarTimerProgresso();
    }

    private void criarIcones() {
        iconPlay = Icones.play(28, COR_TEXTO);
        iconPause = Icones.pause(28, COR_TEXTO);
        iconAnterior = Icones.anterior(22, COR_TEXTO);
        iconProximo = Icones.proximo(22, COR_TEXTO);
        iconShuffle = Icones.shuffle(18, COR_TEXTO);
        iconShuffleAtivo = Icones.shuffle(18, COR_VERDE);
        iconRepetir = Icones.repetir(18, COR_TEXTO);
        iconRepetirAtivo = Icones.repetir(18, COR_VERDE);
        iconRepetirUm = Icones.repetirUm(18, COR_VERDE);
    }

    private void configurarJanela() {
        setTitle("Spotipoggers");
        setSize(500, 750);
        setMinimumSize(new Dimension(420, 620));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(COR_FUNDO);
        setLocationRelativeTo(null);
    }

    // ==================== PAINEL TOPO ====================
    private JPanel criarPainelTopo() {
        JPanel painel = new JPanel(new BorderLayout(15, 0));
        painel.setBackground(COR_FUNDO2);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        capaLabel = new JLabel("", SwingConstants.CENTER);
        capaLabel.setPreferredSize(new Dimension(120, 120));
        capaLabel.setIcon(Icones.musicNote(60, COR_TEXTO_SEC));
        capaLabel.setBackground(COR_FUNDO_CLARO);
        capaLabel.setOpaque(true);
        painel.add(capaLabel, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(COR_FUNDO2);
        info.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        nomeMusicaLabel = new JLabel("Nenhuma m\u00FAsica");
        nomeMusicaLabel.setFont(FONTE_TITULO);
        nomeMusicaLabel.setForeground(COR_TEXTO);
        nomeMusicaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        artistaLabel = new JLabel("Selecione uma m\u00FAsica");
        artistaLabel.setFont(FONTE_SUB);
        artistaLabel.setForeground(COR_TEXTO_SEC);
        artistaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(nomeMusicaLabel);
        info.add(Box.createVerticalStrut(5));
        info.add(artistaLabel);
        painel.add(info, BorderLayout.CENTER);

        return painel;
    }

    // ==================== PAINEL CENTRO ====================
    private JPanel criarPainelCentro() {
        JPanel painel = new JPanel(new BorderLayout(0, 0));
        painel.setBackground(COR_FUNDO);

        // Barra de busca
        JPanel painelBusca = new JPanel(new BorderLayout(8, 0));
        painelBusca.setBackground(COR_FUNDO);
        painelBusca.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        campoBusca = new JTextField();
        campoBusca.setFont(FONTE_SUB);
        campoBusca.setBackground(COR_FUNDO_CLARO);
        campoBusca.setForeground(COR_TEXTO);
        campoBusca.setCaretColor(COR_TEXTO);
        campoBusca.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        campoBusca.setToolTipText("Buscar m\u00FAsicas...");
        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarMusicas(); }
            public void removeUpdate(DocumentEvent e) { filtrarMusicas(); }
            public void changedUpdate(DocumentEvent e) { filtrarMusicas(); }
        });
        JLabel buscaIcon = new JLabel(Icones.busca(16, COR_TEXTO_SEC));
        buscaIcon.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        painelBusca.add(buscaIcon, BorderLayout.WEST);
        painelBusca.add(campoBusca, BorderLayout.CENTER);
        painel.add(painelBusca, BorderLayout.NORTH);

        // Lista de músicas
        modeloLista = new DefaultListModel<>();
        listaMusicas = new JList<>(modeloLista);
        listaMusicas.setBackground(COR_FUNDO);
        listaMusicas.setForeground(COR_TEXTO);
        listaMusicas.setSelectionBackground(COR_FUNDO_HOVER);
        listaMusicas.setSelectionForeground(COR_TEXTO);
        listaMusicas.setFont(FONTE_SUB);
        listaMusicas.setFixedCellHeight(52);
        listaMusicas.setCellRenderer(new MusicaCellRenderer());
        listaMusicas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int idx = listaMusicas.locationToIndex(e.getPoint());
                    if (idx >= 0) tocarMusica(modeloLista.getElementAt(idx));
                }
            }
        });

        JScrollPane scroll = new JScrollPane(listaMusicas);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COR_FUNDO);
        painel.add(scroll, BorderLayout.CENTER);

        return painel;
    }

    // ==================== PAINEL INFERIOR ====================
    private JPanel criarPainelInferior() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(COR_FUNDO2);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Barra de progresso com tempos
        JPanel pProgresso = new JPanel(new BorderLayout(8, 0));
        pProgresso.setBackground(COR_FUNDO2);
        pProgresso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        tempoAtualLabel = new JLabel("00:00");
        tempoAtualLabel.setFont(FONTE_PEQUENA);
        tempoAtualLabel.setForeground(COR_TEXTO_SEC);
        pProgresso.add(tempoAtualLabel, BorderLayout.WEST);

        barraProgresso = criarSlider();
        barraProgresso.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { ajustandoProgresso = true; }
            public void mouseReleased(MouseEvent e) {
                if (musicaIniciada) player.setPosicao(barraProgresso.getValue());
                ajustandoProgresso = false;
            }
        });
        barraProgresso.addChangeListener(e -> {
            if (ajustandoProgresso) tempoAtualLabel.setText(formatarTempo(barraProgresso.getValue()));
        });
        pProgresso.add(barraProgresso, BorderLayout.CENTER);

        tempoTotalLabel = new JLabel("00:00");
        tempoTotalLabel.setFont(FONTE_PEQUENA);
        tempoTotalLabel.setForeground(COR_TEXTO_SEC);
        pProgresso.add(tempoTotalLabel, BorderLayout.EAST);

        painel.add(pProgresso);
        painel.add(Box.createVerticalStrut(8));

        // Botões de controle
        JPanel pBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        pBotoes.setBackground(COR_FUNDO2);
        pBotoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        shuffleBtn = criarBotaoIcone(iconShuffle, "Aleat\u00F3rio");
        shuffleBtn.addActionListener(e -> toggleShuffle());
        pBotoes.add(shuffleBtn);

        anteriorBtn = criarBotaoIcone(iconAnterior, "Anterior");
        anteriorBtn.addActionListener(e -> musicaAnterior());
        pBotoes.add(anteriorBtn);

        playPauseBtn = criarBotaoIcone(iconPlay, "Tocar");
        playPauseBtn.setPreferredSize(new Dimension(56, 56));
        playPauseBtn.addActionListener(e -> togglePlayPause());
        pBotoes.add(playPauseBtn);

        proximoBtn = criarBotaoIcone(iconProximo, "Pr\u00F3xima");
        proximoBtn.addActionListener(e -> proximaMusica());
        pBotoes.add(proximoBtn);

        repeatBtn = criarBotaoIcone(iconRepetir, "Repetir");
        repeatBtn.addActionListener(e -> toggleRepetir());
        pBotoes.add(repeatBtn);

        painel.add(pBotoes);
        painel.add(Box.createVerticalStrut(8));

        // Volume
        JPanel pVolume = new JPanel(new BorderLayout(8, 0));
        pVolume.setBackground(COR_FUNDO2);
        pVolume.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel volIcon = new JLabel(Icones.volume(16, COR_TEXTO_SEC));
        volIcon.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        pVolume.add(volIcon, BorderLayout.WEST);

        volumeSlider = criarSlider();
        volumeSlider.setMinimum(0);
        volumeSlider.setMaximum(100);
        volumeSlider.setValue((int) (config.getVolume() * 100));
        volumeSlider.addChangeListener(e -> {
            float vol = volumeSlider.getValue() / 100f;
            player.setVolume(vol);
            config.setVolume(vol);
        });
        pVolume.add(volumeSlider, BorderLayout.CENTER);

        painel.add(pVolume);
        painel.add(Box.createVerticalStrut(8));

        // Botão escolher pasta
        JPanel pPasta = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pPasta.setBackground(COR_FUNDO2);
        pPasta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JButton pastaBtn = new JButton("Escolher Pasta de M\u00FAsicas", Icones.pasta(14, COR_TEXTO));
        pastaBtn.setFont(FONTE_PEQUENA);
        pastaBtn.setBackground(COR_FUNDO_CLARO);
        pastaBtn.setForeground(COR_TEXTO);
        pastaBtn.setFocusPainted(false);
        pastaBtn.setBorderPainted(false);
        pastaBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pastaBtn.addActionListener(e -> escolherPasta());
        pPasta.add(pastaBtn);

        painel.add(pPasta);
        return painel;
    }

    // ==================== SLIDER ESTILIZADO ====================
    private JSlider criarSlider() {
        JSlider s = new JSlider(0, 100, 0);
        s.setBackground(COR_FUNDO2);
        s.setFocusable(false);
        s.setUI(new javax.swing.plaf.basic.BasicSliderUI(s) {
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cy = trackRect.y + trackRect.height / 2;
                g2.setColor(COR_BARRA);
                g2.fillRoundRect(trackRect.x, cy - 2, trackRect.width, 4, 4, 4);
                int fill = thumbRect.x + thumbRect.width / 2 - trackRect.x;
                g2.setColor(COR_VERDE);
                g2.fillRoundRect(trackRect.x, cy - 2, fill, 4, 4, 4);
            }
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                int cx = thumbRect.x + thumbRect.width / 2;
                int cy = thumbRect.y + thumbRect.height / 2;
                g2.fillOval(cx - 6, cy - 6, 12, 12);
            }
        });
        return s;
    }

    // ==================== BOTÃO ESTILIZADO ====================
    private JButton criarBotaoIcone(Icon icon, String tooltip) {
        JButton b = new JButton(icon);
        b.setToolTipText(tooltip);
        b.setForeground(COR_TEXTO);
        b.setBackground(COR_FUNDO2);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ==================== CELL RENDERER ====================
    private class MusicaCellRenderer extends JPanel implements ListCellRenderer<Musica> {
        private final JLabel nomeLabel = new JLabel();
        private final JLabel artistaLabel = new JLabel();
        private final JLabel duracaoLabel = new JLabel();
        private final JLabel indicador = new JLabel();

        MusicaCellRenderer() {
            setLayout(new BorderLayout(8, 0));
            setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

            indicador.setFont(FONTE_PEQUENA);
            indicador.setPreferredSize(new Dimension(18, 44));
            indicador.setHorizontalAlignment(SwingConstants.CENTER);
            add(indicador, BorderLayout.WEST);

            JPanel txt = new JPanel(new GridLayout(2, 1, 0, 1));
            txt.setOpaque(false);
            nomeLabel.setFont(FONTE_SUB);
            artistaLabel.setFont(FONTE_PEQUENA);
            txt.add(nomeLabel);
            txt.add(artistaLabel);
            add(txt, BorderLayout.CENTER);

            duracaoLabel.setFont(FONTE_PEQUENA);
            duracaoLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            add(duracaoLabel, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Musica> list,
                Musica musica, int index, boolean sel, boolean focus) {
            boolean tocandoEsta = indiceMusicaAtual >= 0
                && indiceMusicaAtual < todasMusicas.size()
                && musica == todasMusicas.get(indiceMusicaAtual);

            nomeLabel.setText(musica.getNome());
            nomeLabel.setForeground(tocandoEsta ? COR_VERDE : COR_TEXTO);
            artistaLabel.setText(musica.getArtista());
            artistaLabel.setForeground(COR_TEXTO_SEC);
            duracaoLabel.setText(musica.getDuracaoFormatada());
            duracaoLabel.setForeground(COR_TEXTO_SEC);
            indicador.setIcon(tocandoEsta ? Icones.musicNote(12, COR_VERDE) : null);
            indicador.setText("");

            setBackground(sel ? COR_FUNDO_HOVER : (index % 2 == 0 ? COR_FUNDO : COR_FUNDO2));
            return this;
        }
    }

    // ==================== LÓGICA DE MÚSICAS ====================
    private void carregarMusicas(String pasta) {
        todasMusicas = CarregadorMusicas.carregarDaPasta(pasta);
        atualizarLista(todasMusicas);
        if (todasMusicas.isEmpty()) {
            nomeMusicaLabel.setText("Nenhuma m\u00FAsica encontrada");
            artistaLabel.setText("Use o bot\u00E3o abaixo para escolher uma pasta");
        }
    }

    private void atualizarLista(List<Musica> musicas) {
        modeloLista.clear();
        for (Musica m : musicas) modeloLista.addElement(m);
    }

    private void filtrarMusicas() {
        String f = campoBusca.getText().toLowerCase().trim();
        if (f.isEmpty()) {
            atualizarLista(todasMusicas);
        } else {
            List<Musica> filtradas = new ArrayList<>();
            for (Musica m : todasMusicas) {
                if (m.getNome().toLowerCase().contains(f)
                    || m.getArtista().toLowerCase().contains(f)) {
                    filtradas.add(m);
                }
            }
            atualizarLista(filtradas);
        }
    }

    private void tocarMusica(Musica musica) {
        if (musica == null) return;
        indiceMusicaAtual = todasMusicas.indexOf(musica);

        player.parar();
        player.tocar(musica.getCaminhoArquivo());
        player.setVolume(volumeSlider.getValue() / 100f);
        musicaIniciada = true;

        atualizarInfoMusica(musica);
        playPauseBtn.setIcon(iconPause);
        playPauseBtn.setText("");

        // Busca capa online
        buscarCapaOnline(musica);

        long dur = player.getDuracao();
        if (dur <= 0) dur = musica.getDuracaoMs();
        if (dur > 0) {
            barraProgresso.setMaximum((int) dur);
            tempoTotalLabel.setText(formatarTempo(dur));
        } else {
            barraProgresso.setMaximum(100);
            tempoTotalLabel.setText("--:--");
        }
        barraProgresso.setValue(0);
        tempoAtualLabel.setText("00:00");

        // Seleciona na lista visível
        for (int i = 0; i < modeloLista.size(); i++) {
            if (modeloLista.getElementAt(i) == musica) {
                listaMusicas.setSelectedIndex(i);
                listaMusicas.ensureIndexIsVisible(i);
                break;
            }
        }
        listaMusicas.repaint();
    }

    private void togglePlayPause() {
        if (!musicaIniciada) {
            int idx = listaMusicas.getSelectedIndex();
            if (idx < 0 && modeloLista.size() > 0) idx = 0;
            if (idx >= 0) tocarMusica(modeloLista.getElementAt(idx));
            return;
        }
        if (player.isTocando()) {
            player.pausar();
            playPauseBtn.setIcon(iconPlay);
        } else if (player.isPausado()) {
            player.retomar();
            playPauseBtn.setIcon(iconPause);
        } else {
            // Música terminou — replay
            Musica m = getMusicaAtual();
            if (m != null) tocarMusica(m);
        }
    }

    private void proximaMusica() {
        if (todasMusicas.isEmpty()) return;
        int prox;
        if (modoRepetir == 2) {
            prox = indiceMusicaAtual;
        } else if (shuffle) {
            prox = (int) (Math.random() * todasMusicas.size());
        } else {
            prox = indiceMusicaAtual + 1;
            if (prox >= todasMusicas.size()) {
                if (modoRepetir == 1) prox = 0;
                else { pararTudo(); return; }
            }
        }
        tocarMusica(todasMusicas.get(prox));
    }

    private void musicaAnterior() {
        if (todasMusicas.isEmpty()) return;
        // Se passou de 3s, reinicia a música atual
        if (player.getPosicao() > 3000 && indiceMusicaAtual >= 0) {
            player.setPosicao(0);
            barraProgresso.setValue(0);
            tempoAtualLabel.setText("00:00");
            return;
        }
        int ant;
        if (shuffle) {
            ant = (int) (Math.random() * todasMusicas.size());
        } else {
            ant = indiceMusicaAtual - 1;
            if (ant < 0) ant = modoRepetir == 1 ? todasMusicas.size() - 1 : 0;
        }
        tocarMusica(todasMusicas.get(ant));
    }

    private void pararTudo() {
        player.parar();
        musicaIniciada = false;
        playPauseBtn.setIcon(iconPlay);
    }

    private void toggleShuffle() {
        shuffle = !shuffle;
        shuffleBtn.setIcon(shuffle ? iconShuffleAtivo : iconShuffle);
    }

    private void toggleRepetir() {
        modoRepetir = (modoRepetir + 1) % 3;
        switch (modoRepetir) {
            case 0: repeatBtn.setIcon(iconRepetir); break;
            case 1: repeatBtn.setIcon(iconRepetirAtivo); break;
            case 2: repeatBtn.setIcon(iconRepetirUm); break;
        }
    }

    private void escolherPasta() {
        JFileChooser fc = new JFileChooser(config.getPastaMusicas());
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Escolher pasta de m\u00FAsicas");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String pasta = fc.getSelectedFile().getAbsolutePath();
            config.setPastaMusicas(pasta);
            pararTudo();
            indiceMusicaAtual = -1;
            nomeMusicaLabel.setText("Nenhuma m\u00FAsica");
            artistaLabel.setText("Selecione uma m\u00FAsica");
            carregarMusicas(pasta);
        }
    }

    private void atualizarInfoMusica(Musica musica) {
        nomeMusicaLabel.setText(musica.getNome());
        artistaLabel.setText(musica.getArtista());
        // Reseta capa enquanto busca online
        capaLabel.setIcon(Icones.musicNote(60, COR_TEXTO_SEC));
        capaLabel.setText("");
    }

    private void buscarCapaOnline(Musica musica) {
        BuscadorCapas.buscarCapa(musica.getArtista(), musica.getNome(), 120, icon -> {
            javax.swing.SwingUtilities.invokeLater(() -> {
                // Verifica se ainda é a mesma música tocando
                Musica atual = getMusicaAtual();
                if (atual != null && atual == musica) {
                    capaLabel.setIcon(icon);
                    capaLabel.setText("");
                }
            });
        });
    }

    private Musica getMusicaAtual() {
        if (indiceMusicaAtual >= 0 && indiceMusicaAtual < todasMusicas.size())
            return todasMusicas.get(indiceMusicaAtual);
        return null;
    }

    // ==================== TIMER ====================
    private void iniciarTimerProgresso() {
        timerProgresso = new Timer(250, e -> {
            if (!musicaIniciada || ajustandoProgresso) return;

            long pos = player.getPosicao();
            long dur = player.getDuracao();

            barraProgresso.setValue((int) pos);
            tempoAtualLabel.setText(formatarTempo(pos));

            if (dur > 0 && barraProgresso.getMaximum() <= 100) {
                barraProgresso.setMaximum((int) dur);
                tempoTotalLabel.setText(formatarTempo(dur));
            }

            // Detecta fim da música
            if (!player.isTocando() && !player.isPausado() && dur > 0 && pos >= dur - 500) {
                proximaMusica();
            }
        });
        timerProgresso.start();
    }

    // ==================== UTILS ====================
    private static String formatarTempo(long ms) {
        if (ms < 0) ms = 0;
        long seg = ms / 1000;
        return String.format("%02d:%02d", seg / 60, seg % 60);
    }

    // ==================== MAIN ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
            new TelaSpotipoggers().setVisible(true);
        });
    }
}
