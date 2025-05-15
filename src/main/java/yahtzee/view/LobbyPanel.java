package yahtzee.view;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class LobbyPanel extends JPanel {
    private static final String SERVER_IP = "56.228.19.115";

    private JTextField nickField;
    private JButton findGameButton;
    private JLabel statusLabel;
    private Timer animationTimer;

    public LobbyPanel() {
        // Gradient background handled in paintComponent
        setOpaque(false);
        setLayout(new BorderLayout());

        /* ---------- Central content container ---------- */
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(40, 60, 40, 60));

        ImageIcon originalIcon  = new ImageIcon("C:/Users/zahid/IdeaProjects/YahtzeeGame/src/main/java/yahtzee/images/yahtzee_logo.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel();
        logo.setIcon(new ImageIcon(scaledImage));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(logo);
        content.add(Box.createVerticalStrut(25));

        /* ---------- Nickname label ---------- */
        JLabel nickLabel = new JLabel("Takma Ad:");
        nickLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nickLabel.setFont(nickLabel.getFont().deriveFont(Font.BOLD, 16f));
        content.add(nickLabel);
        content.add(Box.createVerticalStrut(8));

        /* ---------- Nickname text field ---------- */
        nickField = new JTextField("Player", 15);
        nickField.setFont(nickField.getFont().deriveFont(15f));
        nickField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        nickField.setHorizontalAlignment(JTextField.CENTER);
        nickField.setBorder(new CompoundBorder(
                new LineBorder(new Color(150, 150, 150), 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        content.add(nickField);
        content.add(Box.createVerticalStrut(20));

        /* ---------- Find‑game button ---------- */
        findGameButton = new JButton("Oyun Bul");
        findGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        findGameButton.setFont(findGameButton.getFont().deriveFont(Font.BOLD, 16f));
        findGameButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        findGameButton.setFocusPainted(false);
        findGameButton.setBorder(new EmptyBorder(12, 30, 12, 30));
        findGameButton.setBackground(new Color(0x007BFF));     // Bootstrap‑style primary blue
        findGameButton.setForeground(Color.WHITE);
        content.add(findGameButton);
        content.add(Box.createVerticalStrut(25));

        /* ---------- Status label ---------- */
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setFont(statusLabel.getFont().deriveFont(14f));
        content.add(statusLabel);

        /* ---------- Add content to panel ---------- */
        add(content, BorderLayout.CENTER);

        /* ---------- Waiting‑dots animation ---------- */
        animationTimer = new Timer(500, e -> {
            String current = statusLabel.getText();
            if (current.startsWith("Başka bir oyuncu bekleniyor")) {
                int dots = (current.length() - "Başka bir oyuncu bekleniyor".length()) % 4;
                statusLabel.setText("Başka bir oyuncu bekleniyor" + ".".repeat(dots + 1));
            }
        });
    }

    /* -----------------------  PAINT  ----------------------- */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Soft vertical gradient for a modern feel
        Graphics2D g2 = (Graphics2D) g.create();
        int h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, new Color(250, 250, 250), 0, h, new Color(224, 224, 224));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), h);
        g2.dispose();
    }

    /* --------------------  Original API  -------------------- */

    public String getIp() {
        return SERVER_IP;
    }

    public String getNick() {
        return nickField.getText().trim();
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
        if (status.startsWith("Başka bir oyuncu bekleniyor")) {
            animationTimer.start();
        } else {
            animationTimer.stop();
        }
    }

    public void reset() {
        nickField.setEnabled(true);
        findGameButton.setEnabled(true);
        setStatus(" ");
    }

    public void addFindGameListener(ActionListener listener) {
        findGameButton.addActionListener(listener);
    }
}
