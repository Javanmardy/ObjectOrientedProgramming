import java.util.*;

// ───────────── custom exceptions ──────────────
class WallHitException extends Exception {
}

class InvalidMapException extends RuntimeException {
    InvalidMapException(String m) {
        super(m);
    }
}

// ───────────── Maze ───────────────────────────
class Maze {
    final int H, W;
    char[][] g;

    Maze(int H, int W) {
        this.H = H;
        this.W = W;
        g = new char[H][W];
    }

    boolean in(int x, int y) {
        return 0 <= y && y < H && 0 <= x && x < W;
    }

    boolean wall(int x, int y) {
        return g[y][x] == '#';
    }

    List<int[]> neigh(int x, int y) {
        int[][] d = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        List<int[]> l = new ArrayList<>();
        for (int[] v : d) {
            int nx = x + v[0], ny = y + v[1];
            if (in(nx, ny) && !wall(nx, ny))
                l.add(new int[] { nx, ny });
        }
        return l;
    }

    /* -------- biased DFS: long corridors -------- */
    void generate(int rx, int ry, int px, int py) {
        Random rng = new Random();
        while (true) {
            for (int y = 0; y < H; y++)
                Arrays.fill(g[y], '#');
            Deque<int[]> cellStack = new ArrayDeque<>();
            Deque<int[]> dirStack = new ArrayDeque<>(); // previous direction
            cellStack.push(new int[] { rx, ry });
            dirStack.push(new int[] { 0, 0 }); // none yet
            g[ry][rx] = '.';

            int[][] dir = { { 2, 0 }, { -2, 0 }, { 0, 2 }, { 0, -2 } };
            while (!cellStack.isEmpty()) {
                int[] cur = cellStack.peek();
                int[] prev = dirStack.peek();
                List<int[]> candidates = new ArrayList<>();
                for (int[] d : dir) {
                    int nx = cur[0] + d[0], ny = cur[1] + d[1];
                    if (in(nx, ny) && g[ny][nx] == '#')
                        candidates.add(d);
                }
                if (!candidates.isEmpty()) {
                    int[] chosen;
                    /* 60 % keep going straight if possible */
                    if (!(prev[0] == 0 && prev[1] == 0) && rng.nextInt(10) < 6) {
                        Optional<int[]> straight = candidates.stream()
                                .filter(d -> d[0] == prev[0] && d[1] == prev[1]).findAny();
                        chosen = straight.orElse(candidates.get(rng.nextInt(candidates.size())));
                    } else {
                        chosen = candidates.get(rng.nextInt(candidates.size()));
                    }
                    int nx = cur[0] + chosen[0], ny = cur[1] + chosen[1];
                    int mx = cur[0] + chosen[0] / 2, my = cur[1] + chosen[1] / 2;
                    g[my][mx] = '.';
                    g[ny][nx] = '.';
                    cellStack.push(new int[] { nx, ny });
                    dirStack.push(chosen);
                } else {
                    cellStack.pop();
                    dirStack.pop();
                }
            }
            g[ry][rx] = '.';
            g[py][px] = '.';
            if (reachable(rx, ry, px, py))
                break;
        }
    }

    private boolean reachable(int sx, int sy, int tx, int ty) {
        Set<String> vis = new HashSet<>();
        Deque<int[]> dq = new ArrayDeque<>();
        dq.push(new int[] { sx, sy });
        vis.add(sx + "," + sy);
        while (!dq.isEmpty()) {
            int[] c = dq.pop();
            if (c[0] == tx && c[1] == ty)
                return true;
            for (int[] n : neigh(c[0], c[1])) {
                String k = n[0] + "," + n[1];
                if (vis.add(k))
                    dq.push(n);
            }
        }
        return false;
    }

    void draw(int[] cop, int[] rob) {
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                char ch = g[y][x];
                if (x == cop[0] && y == cop[1] && x == rob[0] && y == rob[1])
                    ch = 'B';
                else if (x == cop[0] && y == cop[1])
                    ch = 'P';
                else if (x == rob[0] && y == rob[1])
                    ch = 'R';
                System.out.print(ch);
            }
            System.out.println();
        }
        System.out.println();
    }
}

// ───────────── common entity ───────────────────
abstract class Entity {
    int x, y;

    Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    void move(int nx, int ny, Maze m) throws WallHitException {
        if (!m.in(nx, ny) || m.wall(nx, ny))
            throw new WallHitException();
        x = nx;
        y = ny;
    }

    static int dist(int ax, int ay, int bx, int by) {
        return Math.abs(ax - bx) + Math.abs(ay - by);
    }

    abstract void update(Maze m, Police cop, Robber rob) throws Exception;
}

/* ───────────── Police ────────────────────────── */
class Police extends Entity {
    Random rng = new Random();
    Set<String> visited = new HashSet<>();
    int still = 0;

    Police(int x, int y) {
        super(x, y);
    }

    private boolean inSight(Maze m, Robber r) {
        if (y == r.y) {
            for (int xx = Math.min(x, r.x) + 1; xx < Math.max(x, r.x); xx++)
                if (m.wall(xx, y))
                    return false;
            return true;
        }
        if (x == r.x) {
            for (int yy = Math.min(y, r.y) + 1; yy < Math.max(y, r.y); yy++)
                if (m.wall(x, yy))
                    return false;
            return true;
        }
        return false;
    }

    @Override
    void update(Maze m, Police p, Robber r) {
        int oldX = x, oldY = y;
        visited.add(x + "," + y);

        int[] step = inSight(m, r) ? bfsStep(m, r.x, r.y)
                : nearestUnvisitedStep(m);
        if (step == null) {
            List<int[]> opts = m.neigh(x, y);
            if (!opts.isEmpty())
                step = opts.get(rng.nextInt(opts.size()));
        }
        if (step != null)
            try {
                move(step[0], step[1], m);
            } catch (WallHitException ignored) {
            }

        // dead-lock nudge
        if (x == oldX && y == oldY)
            still++;
        else
            still = 0;
        if (still >= 2) {
            List<int[]> opts = m.neigh(x, y);
            Collections.shuffle(opts);
            for (int[] mv : opts) {
                try {
                    move(mv[0], mv[1], m);
                    break;
                } catch (WallHitException ignored) {
                }
            }
            still = 0;
        }
    }

    /* ---- helpers (keys="x,y") ---- */
    private int[] nearestUnvisitedStep(Maze m) {
        Queue<int[]> q = new ArrayDeque<>();
        Map<String, int[]> par = new HashMap<>();
        q.add(new int[] { x, y });
        par.put(x + "," + y, null);
        while (!q.isEmpty()) {
            int[] c = q.poll();
            if (!(c[0] == x && c[1] == y) && !visited.contains(c[0] + "," + c[1]))
                return backtrackFirst(par, c);
            for (int[] n : m.neigh(c[0], c[1])) {
                String k = n[0] + "," + n[1];
                if (!par.containsKey(k)) {
                    par.put(k, c);
                    q.add(n);
                }
            }
        }
        return null;
    }

    private int[] bfsStep(Maze m, int tx, int ty) {
        Queue<int[]> q = new ArrayDeque<>();
        Map<String, int[]> par = new HashMap<>();
        q.add(new int[] { x, y });
        par.put(x + "," + y, null);
        while (!q.isEmpty()) {
            int[] c = q.poll();
            if (c[0] == tx && c[1] == ty)
                return backtrackFirst(par, c);
            for (int[] n : m.neigh(c[0], c[1])) {
                String k = n[0] + "," + n[1];
                if (!par.containsKey(k)) {
                    par.put(k, c);
                    q.add(n);
                }
            }
        }
        return null;
    }

    private int[] backtrackFirst(Map<String, int[]> par, int[] tgt) {
        int[] cur = tgt;
        while (true) {
            int[] parent = par.get(cur[0] + "," + cur[1]);
            if (parent == null || (parent[0] == x && parent[1] == y))
                return cur;
            cur = parent;
        }
    }
}

/* ───────────── Robber ────────────────────────── */
class Robber extends Entity {
    Random rng = new Random();
    Set<String> visited = new HashSet<>();
    int still = 0;

    Robber(int x, int y) {
        super(x, y);
    }

    private boolean inSight(Maze m, Police p) {
        if (y == p.y) {
            for (int xx = Math.min(x, p.x) + 1; xx < Math.max(x, p.x); xx++)
                if (m.wall(xx, y))
                    return false;
            return true;
        }
        if (x == p.x) {
            for (int yy = Math.min(y, p.y) + 1; yy < Math.max(y, p.y); yy++)
                if (m.wall(x, yy))
                    return false;
            return true;
        }
        return false;
    }

    @Override
    void update(Maze m, Police cop, Robber self) {
        int oldX = x, oldY = y;
        visited.add(x + "," + y);

        int[] step;
        if (inSight(m, cop)) {
            List<int[]> opts = m.neigh(x, y);
            if (opts.isEmpty())
                opts.add(new int[] { cop.x, cop.y }); // یک قدم کنار
            step = opts.stream()
                    .max(Comparator.comparingInt(a -> dist(a[0], a[1], cop.x, cop.y))).orElse(null);
        } else {
            step = nearestUnvisitedStep(m);
            if (step == null) {
                List<int[]> opts = m.neigh(x, y);
                step = opts.stream()
                        .max(Comparator.comparingInt(a -> dist(a[0], a[1], cop.x, cop.y))).orElse(null);
            }
        }
        if (step != null)
            try {
                move(step[0], step[1], m);
            } catch (WallHitException ignored) {
            }

        if (x == oldX && y == oldY)
            still++;
        else
            still = 0;
        if (still >= 2) {
            List<int[]> opts = m.neigh(x, y);
            Collections.shuffle(opts);
            for (int[] mv : opts) {
                try {
                    move(mv[0], mv[1], m);
                    break;
                } catch (WallHitException ignored) {
                }
            }
            still = 0;
        }
    }

    private int[] nearestUnvisitedStep(Maze m) {
        Queue<int[]> q = new ArrayDeque<>();
        Map<String, int[]> par = new HashMap<>();
        q.add(new int[] { x, y });
        par.put(x + "," + y, null);
        while (!q.isEmpty()) {
            int[] c = q.poll();
            if (!(c[0] == x && c[1] == y) && !visited.contains(c[0] + "," + c[1]))
                return backtrackFirst(par, c);
            for (int[] n : m.neigh(c[0], c[1])) {
                String k = n[0] + "," + n[1];
                if (!par.containsKey(k)) {
                    par.put(k, c);
                    q.add(n);
                }
            }
        }
        return null;
    }

    private int[] backtrackFirst(Map<String, int[]> par, int[] tgt) {
        int[] cur = tgt;
        while (true) {
            int[] parent = par.get(cur[0] + "," + cur[1]);
            if (parent == null || (parent[0] == x && parent[1] == y))
                return cur;
            cur = parent;
        }
    }
}

/* ───────────── main ─────────────────────────── */
public class SingleRobberExceptionGame {
    static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main(String[] args) throws Exception {
        /* choose a larger maze for longer corridors */
        Maze maze = new Maze(17, 41);
        int rx = 1, ry = 1;
        int px = maze.W - 2, py = maze.H - 2; // police other corner
        maze.generate(rx, ry, px, py);

        Robber rob = new Robber(rx, ry);
        Police cop = new Police(px, py);

        int frame = 0;
        while (true) {
            clear();
            System.out.println("Frame " + (++frame));
            maze.draw(new int[] { cop.x, cop.y }, new int[] { rob.x, rob.y });

            rob.update(maze, cop, rob);
            cop.update(maze, cop, rob);

            if (cop.x == rob.x && cop.y == rob.y) {
                System.out.println("Police caught the robber!");
                break;
            }
            if (frame % 150 == 0) {
                rob.visited.clear();
                cop.visited.clear();
            }

            Thread.sleep(350);
        }
    }
}
