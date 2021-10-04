package ru.nsu.fit.huawei.rgb.model;

import java.util.Objects;

public class Point {

    public enum Color {
        Red("R"), Green("G"), Blue("B"), Empty("*");

        private final String mark;

        Color(String mark) {
            this.mark = mark;
        }

        public String getMark() {
            return mark;
        }
    }

    private Color color;

    public Point(Color color) {
        this.color = color;
    }

    public Point() {
        color = Color.Empty;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Point point = (Point) o;
        return color == point.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }

    @Override
    public String toString() {
        return color.name();
    }
}
