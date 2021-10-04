package ru.nsu.fit.huawei.rgb.model;

import ru.nsu.fit.huawei.rgb.model.Point.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {

    public static final int WIDTH = 15;
    public static final int HEIGHT = 10;

    private final List<List<Point>> field;

    public Game() {
        field = new ArrayList<>(WIDTH);
        for (int i = 0; i < WIDTH; i++) {
            List<Point> row = new ArrayList<>(HEIGHT);
            for (int j = 0; j < HEIGHT; j++) {
                row.add(new Point());
            }
            field.add(row);
        }
    }

    /**
     * Gets array of columns of points to initialize field.
     * Size of {@code points} must be {@value HEIGHT},
     * and each element (row) must have size of {@value WIDTH}.
     *
     * @param points array of columns of points.
     */
    public Game(List<List<Point>> points) {
        if (points.size() != HEIGHT) {
            throw new IllegalArgumentException("Field height differs from " + HEIGHT);
        }
        for (List<Point> row : points) {
            if (row.size() != WIDTH) {
                throw new IllegalArgumentException("Row width differs from " + WIDTH);
            }
        }

        field = new ArrayList<>(WIDTH);
        for (int i = 0; i < WIDTH; i++) {
            field.add(new ArrayList<>());
        }

        for (List<Point> row : points) {
            for (int j = 0; j < row.size(); j++) {
                field.get(j).add(row.get(j));
            }
        }

        for (List<Point> pointList : field) {
            Collections.reverse(pointList);
        }
    }

    /**
     * Returns a color of point by its coordinates on field.
     *
     * @param i horizontal index in bounds [0, {@value WIDTH})
     * @param j vertical index in bounds [0, {@value HEIGHT})
     * @return color of point [i, j]
     */
    public Color getColor(int i, int j) {
        assertIndexes(i, j);

        return field.get(i).get(HEIGHT - 1 - j).getColor();
    }

    /**
     * Sets a color of point by its coordinates on field.
     *
     * @param i     horizontal index in bounds [0, {@value WIDTH})
     * @param j     vertical index in bounds [0, {@value HEIGHT})
     * @param color color to be set to point
     */
    private void setColor(int i, int j, Color color) {
        assertIndexes(i, j);

        field.get(i).get(HEIGHT - 1 - j).setColor(color);
    }

    public List<Coords> getCluster(int i, int j) {
        assertIndexes(i, j);

        Coords start = new Coords(i, j);

        List<Coords> result = new ArrayList<>();
        Color color = getColor(i, j);
        tryToExtendCluster(start, color, result);

        return result;
    }

    private void tryToExtendCluster(Coords coords, Color color, List<Coords> cluster) {
        int x = coords.getX();
        int y = coords.getY();
        if (color != Color.Empty &&
            areIndexesValid(x, y) &&
            !cluster.contains(coords) &&
            getColor(x, y) == color) {
            cluster.add(coords);
            tryToExtendCluster(new Coords(x, y + 1), color, cluster);
            tryToExtendCluster(new Coords(x, y - 1), color, cluster);
            tryToExtendCluster(new Coords(x + 1, y), color, cluster);
            tryToExtendCluster(new Coords(x - 1, y), color, cluster);
        }
    }

    /**
     * Removes the cluster to which the selected point belongs, if point isn't empty.
     *
     * @param i horizontal index in bounds [0, {@value WIDTH})
     * @param j vertical index in bounds [0, {@value HEIGHT})
     * @return size of removed cluster
     */
    public int pop(int i, int j) {
        assertIndexes(i, j);
        if (getColor(i, j) == Color.Empty) {
            return 0;
        }

        List<Coords> cluster = getCluster(i, j);
        if (cluster.size() < 2) {
            return 0;
        }

        for (Coords coords : cluster) {
            setColor(coords.getX(), coords.getY(), Color.Empty);
        }
        fit();

        return cluster.size();
    }

    private void fit() {
        siftDown();
        condenseLeft();
    }

    private void siftDown() {
        for (List<Point> column : field) {
            for (int i = column.size() - 1; i >= 0; i--) {
                if (column.get(i).getColor() == Color.Empty) {
                    column.remove(i);
                    column.add(new Point(Color.Empty));
                }
            }
        }
    }

    private void condenseLeft() {
        for (int i = 0; i < field.size(); i++) {
            List<Point> column = field.get(i);
            boolean isEmptyColumn = column.stream()
                    .map(Point::getColor)
                    .allMatch(c -> c == Color.Empty);
            if (isEmptyColumn) {
                field.remove(column);
                field.add(column);
            }
        }
    }

    public long getPointsCount() {
        return field.stream()
                .flatMap(List::stream)
                .map(Point::getColor)
                .filter(c -> c != Color.Empty)
                .count();
    }

    private void assertIndexes(int i, int j) {
        if (i < 0 || i >= WIDTH) {
            throw new IndexOutOfBoundsException(i);
        }
        if (j < 0 || j >= HEIGHT) {
            throw new IndexOutOfBoundsException(j);
        }
    }

    private boolean areIndexesValid(int i, int j) {
        return i >= 0 && i < WIDTH &&
               j >= 0 && j < HEIGHT;
    }

    public void print() {
        for (List<Point> column : field) {
            for (Point point : column) {
                System.out.print(point.getColor().getMark());
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }
}
