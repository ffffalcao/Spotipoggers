package passatempo;

import java.awt.*;
import javax.swing.*;

public class Icones {

    public static Icon play(int tamanho, Color cor) {
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cor);
                int m = tamanho / 5;
                int[] px = {x + m, x + m, x + tamanho - m};
                int[] py = {y + m, y + tamanho - m, y + tamanho / 2};
                g2.fillPolygon(px, py, 3);
                g2.dispose();
            }
        };
    }

    public static Icon pause(int tamanho, Color cor) {
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cor);
                int m = tamanho / 4;
                int bw = tamanho / 5;
                g2.fillRoundRect(x + m - bw/2, y + m, bw, tamanho - 2*m, 2, 2);
                g2.fillRoundRect(x + tamanho - m - bw/2, y + m, bw, tamanho - 2*m, 2, 2);
                g2.dispose();
            }
        };
    }

    public static Icon anterior(int tamanho, Color cor) {
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cor);
                int m = tamanho / 4;
                // Barra vertical esquerda
                g2.fillRect(x + m, y + m, 2, tamanho - 2*m);
                // Triângulo
                int[] px = {x + tamanho - m, x + tamanho - m, x + m + 3};
                int[] py = {y + m, y + tamanho - m, y + tamanho / 2};
                g2.fillPolygon(px, py, 3);
                g2.dispose();
            }
        };
    }

    public static Icon proximo(int tamanho, Color cor) {
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cor);
                int m = tamanho / 4;
                // Triângulo
                int[] px = {x + m, x + m, x + tamanho - m - 3};
                int[] py = {y + m, y + tamanho - m, y + tamanho / 2};
                g2.fillPolygon(px, py, 3);
                // Barra vertical direita
                g2.fillRect(x + tamanho - m - 2, y + m, 2, tamanho - 2*m);
                g2.dispose();
            }
        };
    }

    public static Icon shuffle(int tamanho, Color cor) {
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cor);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int m = tamanho / 4;
                int t = y + m + (tamanho - 2*m) / 3;
                int b = y + tamanho - m - (tamanho - 2*m) / 3;
                // X de cruzamento
                g2.drawLine(x + m, t, x + tamanho - m - 3, b);
                g2.drawLine(x + m, b, x + tamanho - m - 3, t);
                // Setas
                int ax = x + tamanho - m;
                g2.drawLine(ax - 4, t - 3, ax, t);
                g2.drawLine(ax - 4, t + 3, ax, t);
                g2.drawLine(ax - 4, b - 3, ax, b);
                g2.drawLine(ax - 4, b + 3, ax, b);
                g2.dispose();
            }
        };
    }

    public static Icon repetir(int tamanho, Color cor) {
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cor);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int m = tamanho / 4;
                // Retângulo arredondado (loop)
                g2.drawRoundRect(x + m, y + m + 2, tamanho - 2*m, tamanho - 2*m - 4, 4, 4);
                // Seta direita-cima
                int ax = x + tamanho - m;
                int ay = y + m + 2;
                g2.drawLine(ax - 3, ay - 2, ax, ay);
                g2.drawLine(ax - 3, ay + 3, ax, ay);
                g2.dispose();
            }
        };
    }

    public static Icon repetirUm(int tamanho, Color cor) {
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cor);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int m = tamanho / 4;
                g2.drawRoundRect(x + m, y + m + 2, tamanho - 2*m, tamanho - 2*m - 4, 4, 4);
                int ax = x + tamanho - m;
                int ay = y + m + 2;
                g2.drawLine(ax - 3, ay - 2, ax, ay);
                g2.drawLine(ax - 3, ay + 3, ax, ay);
                // "1" no centro
                g2.setFont(new Font("SansSerif", Font.BOLD, tamanho / 3));
                g2.drawString("1", x + tamanho/2 - 2, y + tamanho/2 + 3);
                g2.dispose();
            }
        };
    }

    public static Icon volume(int tamanho, Color cor) {
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cor);
                int cx = x + tamanho / 3;
                int cy = y + tamanho / 2;
                // Speaker body
                g2.fillRect(cx - 4, cy - 3, 4, 6);
                int[] px = {cx, cx, cx - 6};
                int[] py = {cy - 3, cy + 3, cy + 6};
                int[] px2 = {cx, cx, cx - 6};
                int[] py2 = {cy - 3, cy + 3, cy - 6};
                g2.fillPolygon(new int[]{cx - 4, cx - 4, cx - 8}, new int[]{cy - 3, cy + 3, cy + 5}, 3);
                g2.fillPolygon(new int[]{cx - 4, cx - 4, cx - 8}, new int[]{cy - 3, cy + 3, cy - 5}, 3);
                // Sound waves
                g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(cx - 1, cy - 4, 6, 8, -45, 90);
                g2.drawArc(cx + 1, cy - 6, 8, 12, -45, 90);
                g2.dispose();
            }
        };
    }

    public static Icon musicNote(int tamanho, Color cor) {
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cor);
                int cx = x + tamanho / 2;
                int cy = y + tamanho / 2;
                int r = tamanho / 3;
                // Círculo de disco
                g2.fillOval(cx - r, cy - r, 2*r, 2*r);
                // Furo no meio
                g2.setColor(new Color(40, 40, 40));
                int hr = r / 3;
                g2.fillOval(cx - hr, cy - hr, 2*hr, 2*hr);
                // Haste
                g2.setColor(cor);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(cx + r - 2, cy, cx + r - 2, cy - r - r/2);
                // Bandeirinha
                g2.fillRect(cx + r - 2, cy - r - r/2, r, r/2);
                g2.dispose();
            }
        };
    }

    public static Icon busca(int tamanho, Color cor) {
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cor);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int r = tamanho / 3;
                int cx = x + tamanho / 2 - 2;
                int cy = y + tamanho / 2 - 2;
                g2.drawOval(cx - r, cy - r, 2*r, 2*r);
                int dx = (int)(r * 0.7);
                g2.drawLine(cx + dx, cy + dx, x + tamanho - 3, y + tamanho - 3);
                g2.dispose();
            }
        };
    }

    public static Icon pasta(int tamanho, Color cor) {
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cor);
                int m = tamanho / 5;
                // Aba da pasta
                g2.fillRoundRect(x + m, y + m, tamanho / 3, (tamanho - 2*m) / 5, 2, 2);
                // Corpo da pasta
                g2.fillRoundRect(x + m, y + m + 3, tamanho - 2*m, tamanho - 2*m - 3, 3, 3);
                g2.dispose();
            }
        };
    }
}
