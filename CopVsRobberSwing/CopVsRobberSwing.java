import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// ----------- Custom Exceptions -------------
class WallHitException extends Exception {
}

class CaughtException extends Exception {
}

public class CopVsRobberSwing extends JPanel {
    // Config
    static final int ROWS = 17, COLS = 29, CELL = 28;
    static final int FPS = 14;

    int[][] maze;
    Entity robber, cop;
    int frame = 0;
    javax.swing.Timer timer;

    public CopVsRobberSwing() {
        setPreferredSize(new Dimension(COLS * CELL + 1, ROWS * CELL + 1));
        setBackground(Color.BLACK);

        generateMaze();
        resetEntities();

        timer = new javax.swing.Timer(1000 / FPS, e -> gameTick());
        timer.start();
    }

    void generateMaze() {
        // DFS Maze Generator, like in JS
        maze = new int[ROWS][COLS];
        for (int[] row : maze)
            Arrays.fill(row, 1); // 1 = wall, 0 = path

        dfs(1, 1, new Random());
    }

    void dfs(int x, int y, Random rng) {
        maze[y][x] = 0;
        int[][] dirs = { { 2, 0 }, { -2, 0 }, { 0, 2 }, { 0, -2 } };
        java.util.List<int[]> dlist = new java.util.ArrayList<>(Arrays.asList(dirs));
        Collections.shuffle(dlist, rng);
        for (int[] d : dlist) {
            int nx = x + d[0], ny = y + d[1];
            if (in(nx, ny) && maze[ny][nx] == 1) {
                maze[y + d[1] / 2][x + d[0] / 2] = 0;
                dfs(nx, ny, rng);
            }
        }
    }

    boolean in(int x, int y) {
        return x >= 0 && x < COLS && y >= 0 && y < ROWS;
    }

    void resetEntities() {
        robber = new Entity(1, 1, new Color(255, 82, 82));
        cop = new Entity(COLS - 2, ROWS - 2, new Color(64, 224, 255));
    }

    void gameTick() {
        frame++;
        try {
            robberMove();
        } catch (WallHitException ignored) {
        }
        try {
            copMove();
        } catch (WallHitException ignored) {
        }

        // Catch check
        if (robber.x == cop.x && robber.y == cop.y) {
            timer.stop();
            repaint();
            JOptionPane.showMessageDialog(this, "Cop caught the robber!\nFrames: " + frame);
            restart();
            return;
        }
        repaint();
    }

    // Robber logic
    void robberMove() throws WallHitException {
        // If cop in sight, run
        if (robber.see(cop, maze)) {
            java.util.List<int[]> moves = robber.validMoves(maze);
            int maxDist = -1;
            int[] best = moves.get(0);
            for (int[] m : moves) {
                int d = Math.abs(m[0] - cop.x) + Math.abs(m[1] - cop.y);
                if (d > maxDist) {
                    maxDist = d;
                    best = m;
                }
            }
            robber.tryMove(best[0], best[1], maze);
        } else {
            // Random valid move, avoid back immediately
            java.util.List<int[]> moves = robber.validMoves(maze);
            java.util.List<int[]> filtered = new java.util.ArrayList<>();
            for (int[] m : moves)
                if (!(m[0] == robber.lastX && m[1] == robber.lastY))
                    filtered.add(m);
            java.util.List<int[]> pick = filtered.isEmpty() ? moves : filtered;
            int[] choice = pick.get(new Random().nextInt(pick.size()));
            robber.tryMove(choice[0], choice[1], maze);
        }
    }

    // Cop logic (BFS path)
    void copMove() throws WallHitException {
        java.util.List<int[]> path = cop.bfsTo(robber.x, robber.y, maze);
        if (path.size() >= 2) {
            int[] next = path.get(1);
            cop.tryMove(next[0], next[1], maze);
        }
    }

    void restart() {
        generateMaze();
        resetEntities();
        frame = 0;
        timer.restart();
        repaint();
    }

    // Rendering
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Maze
        for (int y = 0; y < ROWS; ++y)
            for (int x = 0; x < COLS; ++x) {
                g.setColor(maze[y][x] == 1 ? new Color(40, 40, 40) : new Color(70, 70, 70));
                g.fillRect(x * CELL, y * CELL, CELL, CELL);
            }
        // Robber & Cop
        robber.draw(g);
        cop.draw(g);

        // Grid lines
        // Grid lines
        g.setColor(new Color(25, 25, 25));
        for (int x = 0; x <= COLS; ++x)
            g.drawLine(x * CELL, 0, x * CELL, ROWS * CELL);
        for (int y = 0; y <= ROWS; ++y)
            g.drawLine(0, y * CELL, COLS * CELL, y * CELL);

        // Frame
        g.setColor(Color.GREEN);
        g.drawRect(1, 1, COLS * CELL - 2, ROWS * CELL - 2);

        // Frame counter
        g.setFont(new Font("Consolas", Font.BOLD, 17));
        g.setColor(Color.WHITE);
        g.drawString("Frame: " + frame, 10, 22);
    }

    // Entity class
    static class Entity {
        int x, y, lastX, lastY;
        Color color;

        Entity(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.lastX = x;
            this.lastY = y;
            this.color = color;
        }

        void draw(Graphics g) {
            g.setColor(color);
            g.fillRoundRect(x * CELL + 3, y * CELL + 3, CELL - 6, CELL - 6, 10, 10);
        }

        void tryMove(int nx, int ny, int[][] maze) throws WallHitException {
            if (maze[ny][nx] == 1)
                throw new WallHitException();
            lastX = x;
            lastY = y;
            x = nx;
            y = ny;
        }

        java.util.List<int[]> validMoves(int[][] maze) {
            java.util.List<int[]> nbs = new java.util.ArrayList<>();
            int[][] dirs = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
            for (int[] d : dirs) {
                int nx = x + d[0], ny = y + d[1];
                if (nx >= 0 && nx < maze[0].length && ny >= 0 && ny < maze.length && maze[ny][nx] == 0)
                    nbs.add(new int[] { nx, ny });
            }
            return nbs;
        }

        boolean see(Entity other, int[][] maze) {
            if (x == other.x) {
                for (int y2 = Math.min(y, other.y) + 1; y2 < Math.max(y, other.y); ++y2)
                    if (maze[y2][x] == 1)
                        return false;
                return true;
            }
            if (y == other.y) {
                for (int x2 = Math.min(x, other.x) + 1; x2 < Math.max(x, other.x); ++x2)
                    if (maze[y][x2] == 1)
                        return false;
                return true;
            }
            return false;
        }

        private String key(int a, int b) {
            return a + "," + b;
        }

        java.util.List<int[]> bfsTo(int tx, int ty, int[][] maze) {
            int ROWS = maze.length, COLS = maze[0].length;
            java.util.Queue<int[]> q = new java.util.ArrayDeque<>();
            java.util.Map<String, int[]> par = new java.util.HashMap<>();
            q.add(new int[] { x, y });
            par.put(key(x, y), null);
            while (!q.isEmpty()) {
                int[] cur = q.poll();
                if (cur[0] == tx && cur[1] == ty)
                    break;
                for (int[] n : new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } }) {
                    int nx = cur[0] + n[0], ny = cur[1] + n[1];
                    String k = key(nx, ny);
                    if (nx >= 0 && nx < COLS && ny >= 0 && ny < ROWS && maze[ny][nx] == 0 && !par.containsKey(k)) {
                        par.put(k, cur);
                        q.add(new int[] { nx, ny });
                    }
                }
            }
            java.util.List<int[]> path = new java.util.ArrayList<>();
            String k = key(tx, ty);
            if (!par.containsKey(k))
                return path;
            int[] cur = new int[] { tx, ty };
            while (cur != null) {
                path.add(cur);
                cur = par.get(key(cur[0], cur[1]));
            }
            Collections.reverse(path);
            return path;
        }
    }

    // Main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Cop vs Robber 2D Maze (Java)");
            CopVsRobberSwing panel = new CopVsRobberSwing();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(panel);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Restart on Space
            frame.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE)
                        panel.restart();
                }
            });
        });
    }
}
