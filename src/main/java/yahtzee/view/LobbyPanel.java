package yahtzee.view;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * Lobby screen with polished look: centered card, rounded inputs, dynamic logo scaling and soft gradient.
 * Behaviour/API unchanged.
 */
public class LobbyPanel extends JPanel {

    private static final String SERVER_IP = "56.228.19.115";

    private final JTextField nickField;
    private final JButton findGameButton;
    private final JLabel statusLabel;
    private final Timer animationTimer;

    public LobbyPanel() {
        /* ---------- background ---------- */
        setOpaque(false);
        setLayout(new GridBagLayout()); // easy centering

        /* ---------- card ------------ */
        Card card = new Card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 50, 40, 50));

        /* ---------- Logo ---------- */
        ImageIcon raw = new ImageIcon("C:/Users/zahid/IdeaProjects/YahtzeeGame/src/main/java/yahtzee/images/yahtzee_logo.png");
        Image scaled = raw.getImage().getScaledInstance(260, -1, Image.SCALE_SMOOTH); // keep aspect ratio
        JLabel logo = new JLabel(new ImageIcon(scaled));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(logo);
        card.add(Box.createVerticalStrut(30));

        /* ---------- Nick label ---------- */
        JLabel nickLabel = new JLabel("Takma Ad:");
        nickLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nickLabel.setFont(nickLabel.getFont().deriveFont(Font.BOLD, 15f));
        card.add(nickLabel);
        card.add(Box.createVerticalStrut(6));

        /* ---------- Nick field ---------- */
        nickField = new JTextField("Player", 18);
        nickField.setFont(nickField.getFont().deriveFont(15f));
        nickField.setMaximumSize(new Dimension(280, 40));
        nickField.setHorizontalAlignment(JTextField.CENTER);
        nickField.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xB0B0B0), 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        card.add(nickField);
        card.add(Box.createVerticalStrut(18));

        /* ---------- Button ---------- */
        findGameButton = new JButton("Oyun Bul");
        findGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        findGameButton.setFont(findGameButton.getFont().deriveFont(Font.BOLD, 15f));
        stylizeButton(findGameButton);
        card.add(findGameButton);
        card.add(Box.createVerticalStrut(25));

        /* ---------- Status ---------- */
        statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 14f));
        card.add(statusLabel);

        /* ---------- add card to center ---------- */
        add(card);

        /* ---------- Waiting‑dots animation ---------- */
        animationTimer = new Timer(500, e -> {
            String current = statusLabel.getText();
            if (current.startsWith("Başka bir oyuncu bekleniyor")) {
                int dots = (current.length() - "Başka bir oyuncu bekleniyor".length()) % 4;
                statusLabel.setText("Başka bir oyuncu bekleniyor" + ".".repeat(dots + 1));
            }
        });
    }

    /* -------------------------------------------------- */
    private void stylizeButton(JButton b) {
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.setBackground(new Color(0x007BFF));
        b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(10, 30, 10, 30));
    }

    /* -------------------------------------------------- */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, new Color(242, 242, 242), 0, h, new Color(219, 219, 219));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), h);
        g2.dispose();
    }

    /* --------------------  Public API  -------------------- */
    public String getIp() {
        return SERVER_IP;
    }

    public String getNick() {
        return nickField.getText().trim();
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
        if (status.startsWith("Başka bir oyuncu bekleniyor")) animationTimer.start();
        else animationTimer.stop();
    }

    public void reset() {
        nickField.setEnabled(true);
        findGameButton.setEnabled(true);
        setStatus(" ");
    }

    public void addFindGameListener(ActionListener l) {
        findGameButton.addActionListener(l);
    }

    /* ========= inner rounded card ========= */
    private static class Card extends JPanel {
        private static final int ARC = 18;

        Card() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 240));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
            g2.setColor(new Color(0xD0D0D0));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
