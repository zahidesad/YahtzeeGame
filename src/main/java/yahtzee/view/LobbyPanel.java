package yahtzee.view;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * Lobby screen: handles user nickname input, server connection, and waiting animation.
 */
public class LobbyPanel extends JPanel {

    private static final String SERVER_IP = "localhost"; // Game server address

    private final JTextField nickField;     // Input for player nickname
    private final JButton findGameButton;   // Button to initiate matchmaking
    private final JLabel statusLabel;       // Displays connection/status messages
    private final Timer animationTimer;     // Animates waiting dots

    /**
     * Build lobby UI: centered card with logo, nickname field, and find-game button.
     */
    public LobbyPanel() {
        setOpaque(false);
        setLayout(new GridBagLayout()); // Center the card panel

        // Card container with rounded border
        Card card = new Card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 50, 40, 50));

        // Logo at top, scaled smoothly
        URL logoUrl = getClass().getResource("/yahtzee/images/yahtzee_logo.png");
        if (logoUrl == null) {
            throw new RuntimeException("Logo can't find: yahtzee_logo.png");
        }
        ImageIcon raw = new ImageIcon(logoUrl);
        Image scaled = raw.getImage()
                .getScaledInstance(260, -1, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(scaled));

        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(logo);
        card.add(Box.createVerticalStrut(30));

        // Nickname label
        JLabel nickLabel = new JLabel("Nickname:");
        nickLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nickLabel.setFont(nickLabel.getFont().deriveFont(Font.BOLD, 15f));
        card.add(nickLabel);
        card.add(Box.createVerticalStrut(6));

        // Nickname input field
        nickField = new JTextField("Player", 18);
        nickField.setFont(nickField.getFont().deriveFont(15f));
        nickField.setMaximumSize(new Dimension(280, 40));
        nickField.setHorizontalAlignment(JTextField.CENTER);
        nickField.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xB0B0B0), 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        card.add(nickField);
        card.add(Box.createVerticalStrut(18));

        // Find game button
        findGameButton = new JButton("Find Game");
        findGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        findGameButton.setFont(findGameButton.getFont().deriveFont(Font.BOLD, 15f));
        stylizeButton(findGameButton);
        card.add(findGameButton);
        card.add(Box.createVerticalStrut(25));

        // Status label for messages and waiting animation
        statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 14f));
        card.add(statusLabel);

        add(card); // Add card to center of panel

        // Timer for appending dots to waiting status
        animationTimer = new Timer(500, e -> {
            String text = statusLabel.getText();
            if (text.startsWith("Waiting for opponent")) {
                int dots = (text.length() - "Waiting for opponent".length()) % 4;
                statusLabel.setText("Waiting for opponent" + ".".repeat(dots + 1));
            }
        });
    }

    /**
     * Apply common styling to buttons.
     */
    private void stylizeButton(JButton b) {
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.setBackground(new Color(0x007BFF));
        b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(10, 30, 10, 30));
    }

    /**
     * Paint gradient background behind lobby card.
     */
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

    // --- Public API ---

    /**
     * Get server IP address for connection.
     */
    public String getIp() {
        return SERVER_IP;
    }

    /**
     * Retrieve trimmed nickname from input field.
     */
    public String getNick() {
        return nickField.getText().trim();
    }

    /**
     * Update status text and start/stop waiting animation.
     */
    public void setStatus(String status) {
        statusLabel.setText(status);
        if (status.startsWith("Waiting for opponent")) animationTimer.start();
        else animationTimer.stop();
    }

    /**
     * Reset input state for a new matchmaking attempt.
     */
    public void reset() {
        nickField.setEnabled(true);
        findGameButton.setEnabled(true);
        setStatus(" ");
    }

    /**
     * Register listener for the find-game button.
     */
    public void addFindGameListener(ActionListener l) {
        findGameButton.addActionListener(l);
    }

    /**
     * Inner panel with rounded white card styling.
     */
    private static class Card extends JPanel {
        private static final int ARC = 18; // Corner arc radius

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