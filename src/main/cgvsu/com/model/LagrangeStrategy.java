package main.cgvsu.com.model;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;

public class LagrangeStrategy implements InterpolationStrategyImpl<Point2D> {

    @Override
    public List<Point2D> calculate(List<Point2D> controlPoints, int segments) {
        if (controlPoints == null || controlPoints.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 control points");
        }

        List<Point2D> curvePoints = new ArrayList<>();

        // Для полинома Лагранжа используем равномерные значения параметра
        double minX = controlPoints.stream().mapToDouble(Point2D::getX).min().orElse(0);
        double maxX = controlPoints.stream().mapToDouble(Point2D::getX).max().orElse(1);

        for (int i = 0; i <= segments; i++) {
            double t = (double) i / segments;
            double x = minX + t * (maxX - minX);
            Point2D point = calculatePointForX(controlPoints, x);
            curvePoints.add(point);
        }

        return curvePoints;
    }

    @Override
    public Point2D calculatePoint(List<Point2D> points, double t) {
        // Для полинома Лагранжа t - это x координата
        return calculatePointForX(points, t);
    }

    protected Point2D calculatePointForX(List<Point2D> points, double x) {
        double y = 0;
        int n = points.size();

        for (int i = 0; i < n; i++) {
            double xi = points.get(i).getX();
            double yi = points.get(i).getY();
            double basis = lagrangeBasis(points, i, x);
            y += yi * basis;
        }

        return new Point2D(x, y);
    }

    protected double lagrangeBasis(List<Point2D> points, int i, double x) {
        double result = 1.0;
        double xi = points.get(i).getX();
        int n = points.size();

        for (int j = 0; j < n; j++) {
            if (j != i) {
                double xj = points.get(j).getX();
                result *= (x - xj) / (xi - xj);
            }
        }

        return result;
    }
}