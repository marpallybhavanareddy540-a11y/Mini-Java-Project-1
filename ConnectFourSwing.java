import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class ConnectFourSwing extends JFrame {

    static final int ROWS = 6;
    static final int COLS = 7;
    static final int CELL_SIZE = 92;

    // ---- Kid-friendly palette ----
    static final Color SKY_TOP = new Color(255, 214, 165);
    static final Color SKY_BOTTOM = new Color(154, 206, 235);
    static final Color FRAME_TOP = new Color(120, 88, 220);
    static final Color FRAME_BOTTOM = new Color(84, 56, 176);
    static final Color HOLE = new Color(233, 240, 245);
    static final Color HOLE_SHADOW = new Color(200, 210, 220);
    static final Color CREAM = new Color(255, 255, 255);
    static final Color RED_LIGHT = new Color(255, 138, 128);
    static final Color RED = new Color(239, 83, 80);
    static final Color RED_DARK = new Color(183, 28, 28);
    static final Color GOLD_LIGHT = new Color(255, 224, 130);
    static final Color GOLD = new Color(255, 179, 0);
    static final Color GOLD_DARK = new Color(191, 122, 0);
    static final Color GREEN_BTN = new Color(102, 187, 106);
    static final Color GREEN_BTN_DARK = new Color(56, 142, 60);
    static final Color BLUE_BTN = new Color(77, 171, 245);
    static final Color BLUE_BTN_DARK = new Color(25, 118, 210);
    static final Color PURPLE_BTN = new Color(179, 136, 255);
    static final Color PURPLE_BTN_DARK = new Color(106, 27, 154);

    static final Font FONT_TITLE = pickFont(Font.BOLD, 40);
    static final Font FONT_SUB = pickFont(Font.PLAIN, 16);
    static final Font FONT_BTN = pickFont(Font.BOLD, 20);
    static final Font FONT_STATUS = pickFont(Font.BOLD, 22);
    static final Font FONT_SCORE = pickFont(Font.PLAIN, 15);

    enum Mode { FRIEND, AI }
    enum Difficulty { EASY, HARD }

    char[][] board = new char[ROWS][COLS];
    char currentPlayer = 'R';
    boolean gameOver = false;
    boolean inputLocked = false;
    int redScore = 0;
    int goldScore = 0;
    int hoverCol = -1;
    Mode mode = Mode.FRIEND;
    Difficulty difficulty = Difficulty.EASY;
    final Random random = new Random();

    CardLayout cardLayout = new CardLayout();
    JPanel cards = new JPanel();
    JLabel statusLabel;
    JLabel scoreLabel;
    JLabel goldNameLabel;
    BoardPanel boardPanel;

    static Font pickFont(int style, int size) {
        String[] preferred = {"Comic Sans MS", "Baloo 2", "SansSerif"};
        for (String name : preferred) {
            Font f = new Font(name, style, size);
            if (f.getFamily().equalsIgnoreCase(name) || name.equals("SansSerif")) {
                return f;
            }
        }
        return new Font("SansSerif", style, size);
    }

    public ConnectFourSwing() {
        setTitle("Connect Four \u2014 for young champions");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cards.setLayout(cardLayout);
        cards.add(buildMenuCard(), "menu");
        cards.add(buildDifficultyCard(), "difficulty");
        cards.add(buildGameCard(), "game");

        setContentPane(cards);
        showCard("menu");

        pack();
        setLocationRelativeTo(null);
    }

    // ---------------- MENU CARD ----------------
    JPanel buildMenuCard() {
        SkyPanel panel = new SkyPanel();
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(CELL_SIZE * COLS + 140, CELL_SIZE * ROWS + 220));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = centered(new JLabel("\uD83D\uDD34 CONNECT FOUR \uD83D\uDFE1", SwingConstants.CENTER));
        title.setFont(FONT_TITLE);
        title.setForeground(new Color(60, 40, 20));

        JLabel subtitle = centered(new JLabel("a friendly game for everyone \uD83D\uDE0E", SwingConstants.CENTER));
        subtitle.setFont(FONT_SUB);
        subtitle.setForeground(new Color(80, 60, 40));
        subtitle.setBorder(BorderFactory.createEmptyBorder(6, 0, 40, 0));

        JButton friendBtn = makeBigButton("\uD83D\uDC6B  Play with a Friend", GREEN_BTN, GREEN_BTN_DARK);
        friendBtn.addActionListener(e -> {
            mode = Mode.FRIEND;
            startNewGame();
            showCard("game");
        });

        JButton aiBtn = makeBigButton("\uD83E\uDD16  Play with AI", BLUE_BTN, BLUE_BTN_DARK);
        aiBtn.addActionListener(e -> showCard("difficulty"));

        friendBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        aiBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(title);
        content.add(subtitle);
        content.add(friendBtn);
        content.add(Box.createVerticalStrut(18));
        content.add(aiBtn);

        panel.add(content);
        return panel;
    }

    // ---------------- DIFFICULTY CARD ----------------
    JPanel buildDifficultyCard() {
        SkyPanel panel = new SkyPanel();
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(CELL_SIZE * COLS + 140, CELL_SIZE * ROWS + 220));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = centered(new JLabel("How tricky should the AI be?", SwingConstants.CENTER));
        title.setFont(FONT_STATUS);
        title.setForeground(new Color(60, 40, 20));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 34, 0));

        JButton easyBtn = makeBigButton("\uD83D\uDE42  Easy \u2014 just for fun", GREEN_BTN, GREEN_BTN_DARK);
        easyBtn.addActionListener(e -> {
            mode = Mode.AI;
            difficulty = Difficulty.EASY;
            startNewGame();
            showCard("game");
        });

        JButton hardBtn = makeBigButton("\uD83D\uDE0E  Hard \u2014 bring it on", PURPLE_BTN, PURPLE_BTN_DARK);
        hardBtn.addActionListener(e -> {
            mode = Mode.AI;
            difficulty = Difficulty.HARD;
            startNewGame();
            showCard("game");
        });

        JButton backBtn = makeSmallButton("\u2190 Back");
        backBtn.addActionListener(e -> showCard("menu"));

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        easyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        hardBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(title);
        content.add(easyBtn);
        content.add(Box.createVerticalStrut(18));
        content.add(hardBtn);
        content.add(Box.createVerticalStrut(28));
        content.add(backBtn);

        panel.add(content);
        return panel;
    }

    // ---------------- GAME CARD ----------------
    JPanel buildGameCard() {
        SkyPanel root = new SkyPanel();
        root.setLayout(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(28, 34, 28, 34));

        JButton homeBtn = makeSmallButton("\u2190 Menu");
        homeBtn.addActionListener(e -> showCard("menu"));
        JPanel homeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        homeRow.setOpaque(false);
        homeRow.add(homeBtn);

        goldNameLabel = new JLabel("", SwingConstants.CENTER); // reserved for future use
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(FONT_STATUS);
        statusLabel.setForeground(new Color(60, 40, 20));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 4, 0));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLabel = new JLabel("", SwingConstants.CENTER);
        scoreLabel.setFont(FONT_SCORE);
        scoreLabel.setForeground(new Color(80, 60, 40));
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 20, 0));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(statusLabel);
        headerPanel.add(scoreLabel);

        boardPanel = new BoardPanel();

        // FIX: use explicit GridBagConstraints so the board is reliably
        // centered and sized at its preferred size, regardless of how
        // much extra space the wrapper/window ends up with.
        JPanel boardWrapper = new JPanel(new GridBagLayout());
        boardWrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        boardWrapper.add(boardPanel, gbc);

        JButton newRoundBtn = makeBigButton("\uD83D\uDD01  New Round", GREEN_BTN, GREEN_BTN_DARK);
        newRoundBtn.addActionListener(e -> newRound());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));
        buttonPanel.add(newRoundBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(homeRow, BorderLayout.WEST);
        top.add(headerPanel, BorderLayout.CENTER);

        root.add(top, BorderLayout.NORTH);
        root.add(boardWrapper, BorderLayout.CENTER);
        root.add(buttonPanel, BorderLayout.SOUTH);
        return root;
    }

    void showCard(String name) {
        cardLayout.show(cards, name);
        cards.revalidate();
        cards.repaint();
        if (boardPanel != null) {
            boardPanel.revalidate();
            boardPanel.repaint();
        }
    }

    JLabel centered(JLabel label) {
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    JButton makeBigButton(String text, Color bg, Color bgDark) {
        JButton btn = roundedButton(text, bg, bgDark, Color.WHITE, FONT_BTN, 16, 30);
        btn.setMaximumSize(new Dimension(360, 60));
        btn.setPreferredSize(new Dimension(360, 60));
        return btn;
    }

    JButton makeSmallButton(String text) {
        return roundedButton(text, new Color(255, 255, 255, 200), new Color(210, 210, 210), new Color(70, 60, 50),
                pickFont(Font.BOLD, 14), 8, 18);
    }

    JButton roundedButton(String text, Color bg, Color bgDark, Color fg, Font font, int vPad, int hPad) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int shadowOffset = getModel().isPressed() ? 1 : 4;
                int h = getHeight();
                g2.setColor(bgDark);
                g2.fill(new RoundRectangle2D.Double(0, shadowOffset, getWidth(), h - shadowOffset, 18, 18));
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, getModel().isPressed() ? shadowOffset : 0, getWidth(), h - shadowOffset, 18, 18));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(font);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(vPad, hPad, vPad + 4, hPad));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ---------------- GAME LOGIC ----------------
    void startNewGame() {
        redScore = 0;
        goldScore = 0;
        newRound();
    }

    void initBoard() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                board[r][c] = ' ';
    }

    void newRound() {
        initBoard();
        currentPlayer = 'R';
        gameOver = false;
        inputLocked = false;
        updateStatusLabel();
        updateScoreLabel();
        if (boardPanel != null) boardPanel.repaint();
    }

    String playerLabel(char p) {
        if (p == 'R') return "Player Red";
        return (mode == Mode.AI) ? "the AI" : "Player Gold";
    }

    void updateStatusLabel() {
        if (gameOver) return;
        if (mode == Mode.AI && currentPlayer == 'G') {
            statusLabel.setText("\uD83E\uDD16 AI is thinking\u2026");
            statusLabel.setForeground(GOLD_DARK);
        } else {
            String name = currentPlayer == 'R' ? "Red" : "Gold";
            statusLabel.setText("\u25CF Player " + name + "'s turn");
            statusLabel.setForeground(currentPlayer == 'R' ? RED_DARK : GOLD_DARK);
        }
    }

    void updateScoreLabel() {
        String goldTag = (mode == Mode.AI) ? "AI" : "GOLD";
        scoreLabel.setText("RED " + redScore + "   \u2014   " + goldScore + " " + goldTag);
    }

    void handleColumnClick(int col) {
        if (gameOver || inputLocked) return;
        if (mode == Mode.AI && currentPlayer != 'R') return; // human is always Red

        playMove(col);
    }

    void playMove(int col) {
        int row = dropPiece(board, col, currentPlayer);
        if (row == -1) return;

        boardPanel.repaint();

        if (checkWin(board, row, col, currentPlayer)) {
            gameOver = true;
            if (currentPlayer == 'R') redScore++; else goldScore++;
            updateScoreLabel();
            String name = playerLabel(currentPlayer);
            statusLabel.setText("\uD83C\uDF89 " + name + " wins! Great game!");
            statusLabel.setForeground(currentPlayer == 'R' ? RED_DARK : GOLD_DARK);
            return;
        }

        if (isBoardFull(board)) {
            gameOver = true;
            statusLabel.setText("\uD83E\uDD1D It's a draw \u2014 nice try both!");
            statusLabel.setForeground(new Color(70, 60, 50));
            return;
        }

        currentPlayer = (currentPlayer == 'R') ? 'G' : 'R';
        updateStatusLabel();

        if (mode == Mode.AI && currentPlayer == 'G' && !gameOver) {
            inputLocked = true;
            javax.swing.Timer timer = new javax.swing.Timer(550, e -> {
                int aiCol = chooseAiColumn();
                playMove(aiCol);
                inputLocked = false;
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    int dropPiece(char[][] b, int col, char player) {
        for (int r = ROWS - 1; r >= 0; r--) {
            if (b[r][col] == ' ') {
                b[r][col] = player;
                return r;
            }
        }
        return -1;
    }

    boolean isBoardFull(char[][] b) {
        for (int c = 0; c < COLS; c++)
            if (b[0][c] == ' ') return false;
        return true;
    }

    boolean checkWin(char[][] b, int row, int col, char player) {
        int[][] dirs = { {0, 1}, {1, 0}, {1, 1}, {1, -1} };
        for (int[] d : dirs) {
            int count = 1;
            count += countDir(b, row, col, player, d[0], d[1]);
            count += countDir(b, row, col, player, -d[0], -d[1]);
            if (count >= 4) return true;
        }
        return false;
    }

    int countDir(char[][] b, int row, int col, char player, int dRow, int dCol) {
        int count = 0;
        int r = row + dRow, c = col + dCol;
        while (r >= 0 && r < ROWS && c >= 0 && c < COLS && b[r][c] == player) {
            count++;
            r += dRow;
            c += dCol;
        }
        return count;
    }

    List<Integer> validColumns(char[][] b) {
        List<Integer> cols = new ArrayList<>();
        for (int c = 0; c < COLS; c++)
            if (b[0][c] == ' ') cols.add(c);
        return cols;
    }

    char[][] copyBoard(char[][] src) {
        char[][] copy = new char[ROWS][COLS];
        for (int r = 0; r < ROWS; r++)
            copy[r] = src[r].clone();
        return copy;
    }

    // ---------------- SIMPLE AI ----------------
    int chooseAiColumn() {
        List<Integer> valid = validColumns(board);
        if (valid.isEmpty()) return -1;

        if (difficulty == Difficulty.EASY) {
            // Mostly random, but still grabs an obvious win sometimes
            if (random.nextInt(100) < 25) {
                Integer win = findWinningColumn(valid, 'G');
                if (win != null) return win;
            }
            return valid.get(random.nextInt(valid.size()));
        }

        // HARD: win if possible, else block, else prefer center columns
        Integer win = findWinningColumn(valid, 'G');
        if (win != null) return win;

        Integer block = findWinningColumn(valid, 'R');
        if (block != null) return block;

        int[] preference = {3, 2, 4, 1, 5, 0, 6};
        for (int col : preference) {
            if (valid.contains(col) && random.nextInt(100) < 85) return col;
        }
        return valid.get(random.nextInt(valid.size()));
    }

    Integer findWinningColumn(List<Integer> valid, char player) {
        for (int col : valid) {
            char[][] sim = copyBoard(board);
            int row = dropPiece(sim, col, player);
            if (row != -1 && checkWin(sim, row, col, player)) {
                return col;
            }
        }
        return null;
    }

    // ---------------- PANELS ----------------
    class SkyPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            GradientPaint sky = new GradientPaint(0, 0, SKY_TOP, 0, getHeight(), SKY_BOTTOM);
            g2.setPaint(sky);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    class BoardPanel extends JPanel {
        BoardPanel() {
            Dimension size = new Dimension(CELL_SIZE * COLS + 36, CELL_SIZE * ROWS + 36);
            setPreferredSize(size);
            // FIX: also pin the minimum size. Without this, GridBagLayout
            // can shrink the component to 0x0 in some space-constrained
            // layouts, which paints nothing and looks like a "missing board".
            setMinimumSize(size);
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int col = (e.getX() - 18) / CELL_SIZE;
                    if (col >= 0 && col < COLS) handleColumnClick(col);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    hoverCol = -1;
                    repaint();
                }
            });
            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int col = (e.getX() - 18) / CELL_SIZE;
                    hoverCol = (col >= 0 && col < COLS) ? col : -1;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            try {
                int boardW = CELL_SIZE * COLS;
                int boardH = CELL_SIZE * ROWS;

                GradientPaint frameGrad = new GradientPaint(0, 0, FRAME_TOP, boardW, boardH, FRAME_BOTTOM);
                g2.setPaint(frameGrad);
                g2.fill(new RoundRectangle2D.Double(0, 0, boardW + 36, boardH + 36, 34, 34));

                boolean humanTurnNow = !gameOver && !(mode == Mode.AI && currentPlayer == 'G');
                if (humanTurnNow && hoverCol >= 0 && board[0][hoverCol] == ' ') {
                    g2.setColor(new Color(255, 255, 255, 45));
                    g2.fillRoundRect(18 + hoverCol * CELL_SIZE, 18, CELL_SIZE, boardH, 16, 16);
                }

                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        int x = 18 + c * CELL_SIZE;
                        int y = 18 + r * CELL_SIZE;

                        RadialGradientPaint holeGrad = new RadialGradientPaint(
                            new Point(x + CELL_SIZE / 2, y + CELL_SIZE / 2), CELL_SIZE / 2f,
                            new float[]{0f, 1f},
                            new Color[]{CREAM, HOLE}
                        );
                        g2.setPaint(holeGrad);
                        g2.fillOval(x + 7, y + 7, CELL_SIZE - 14, CELL_SIZE - 14);
                        g2.setColor(HOLE_SHADOW);
                        g2.setStroke(new BasicStroke(2f));
                        g2.drawOval(x + 7, y + 7, CELL_SIZE - 14, CELL_SIZE - 14);

                        char piece = board[r][c];
                        if (piece == 'R') {
                            drawPiece(g2, x, y, RED_LIGHT, RED, RED_DARK);
                        } else if (piece == 'G') {
                            drawPiece(g2, x, y, GOLD_LIGHT, GOLD, GOLD_DARK);
                        }
                    }
                }
            } catch (Throwable t) {
                // FIX: catch Throwable (not just Exception) so an Error
                // during painting can never fail silently and leave the
                // board area blank with no clue why. We now always show
                // something AND print the real cause to the console.
                t.printStackTrace();
                g2.setColor(Color.RED);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.drawString("Draw error: " + t, 10, 20);
            }
        }

        void drawPiece(Graphics2D g2, int x, int y, Color highlight, Color mid, Color dark) {
            int inset = 11;
            int size = CELL_SIZE - inset * 2;

            g2.setColor(new Color(0, 0, 0, 55));
            g2.fillOval(x + inset + 2, y + inset + 4, size, size);

            RadialGradientPaint grad = new RadialGradientPaint(
                new Point(x + inset + (int) (size * 0.35), y + inset + (int) (size * 0.3)),
                size * 0.8f,
                new float[]{0f, 0.6f, 1f},
                new Color[]{highlight, mid, dark}
            );
            g2.setPaint(grad);
            g2.fillOval(x + inset, y + inset, size, size);

            g2.setColor(new Color(255, 255, 255, 90));
            g2.fillOval(x + inset + (int) (size * 0.16), y + inset + (int) (size * 0.12), (int) (size * 0.35), (int) (size * 0.22));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConnectFourSwing frame = new ConnectFourSwing();
            frame.setVisible(true);
        });
    }
}