package main.cgvsu.com.model;

import javafx.geometry.Point2D;
import java.util.*;

public class CubicSplineStrategy implements InterpolationStrategyImpl<Point2D> {

    @Override
    public List<Point2D> calculate(List<Point2D> controlPoints, int segments) {
        if (controlPoints == null || controlPoints.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 control points");
        }

        // Сортируем точки по X для сплайна
        List<Point2D> sortedPoints = new ArrayList<>(controlPoints);
        sortedPoints.sort(Comparator.comparingDouble(Point2D::getX));

        // Проверяем на уникальность X
        for (int i = 1; i < sortedPoints.size(); i++) {
            if (sortedPoints.get(i).getX() == sortedPoints.get(i - 1).getX()) {
                throw new IllegalArgumentException("X coordinates must be unique for cubic spline");
            }
        }

        // Вычисляем вторые производные
        double[] secondDerivatives = calculateSecondDerivatives(sortedPoints);

        List<Point2D> curvePoints = new ArrayList<>();

        // Интерполируем между каждой парой точек
        for (int i = 0; i < sortedPoints.size() - 1; i++) {
            Point2D p1 = sortedPoints.get(i);
            Point2D p2 = sortedPoints.get(i + 1);

            double segmentLength = p2.getX() - p1.getX();
            int segmentSegments = Math.max(segments / (sortedPoints.size() - 1), 10);

            for (int j = 0; j <= segmentSegments; j++) {
                double t = (double) j / segmentSegments;
                double x = p1.getX() + t * segmentLength;
                double y = interpolateSpline(p1, p2, secondDerivatives[i], secondDerivatives[i + 1], x);
                curvePoints.add(new Point2D(x, y));
            }
        }

        return curvePoints;
    }

    @Override
    public Point2D calculatePoint(List<Point2D> points, double t) {
        // Для сплайна t - это x координата
        List<Point2D> sortedPoints = new ArrayList<>(points);
        sortedPoints.sort(Comparator.comparingDouble(Point2D::getX));

        double[] secondDerivatives = calculateSecondDerivatives(sortedPoints);

        // Находим сегмент, в котором находится t
        for (int i = 0; i < sortedPoints.size() - 1; i++) {
            Point2D p1 = sortedPoints.get(i);
            Point2D p2 = sortedPoints.get(i + 1);

            if (t >= p1.getX() && t <= p2.getX()) {
                double y = interpolateSpline(p1, p2, secondDerivatives[i], secondDerivatives[i + 1], t);
                return new Point2D(t, y);
            }
        }

        // Если t вне диапазона, возвращаем ближайшую точку
        return t < sortedPoints.get(0).getX() ? sortedPoints.get(0) : sortedPoints.get(sortedPoints.size() - 1);
    }

    protected double[] calculateSecondDerivatives(List<Point2D> points) {
        int n = points.size();
        double[] x = new double[n];
        double[] y = new double[n];

        for (int i = 0; i < n; i++) {
            x[i] = points.get(i).getX();
            y[i] = points.get(i).getY();
        }

        double[] u = new double[n];
        double[] secondDerivatives = new double[n];

        // Тридиагональная система уравнений
        for (int i = 1; i < n - 1; i++) {
            double sig = (x[i] - x[i - 1]) / (x[i + 1] - x[i - 1]);
            double p = sig * secondDerivatives[i - 1] + 2.0;
            secondDerivatives[i] = (sig - 1.0) / p;

            double dy1 = (y[i] - y[i - 1]) / (x[i] - x[i - 1]);
            double dy2 = (y[i + 1] - y[i]) / (x[i + 1] - x[i]);
            u[i] = (6.0 * (dy2 - dy1) / (x[i + 1] - x[i - 1]) - sig * u[i - 1]) / p;
        }

        // Обратная подстановка
        secondDerivatives[n - 1] = 0;
        for (int i = n - 2; i >= 0; i--) {
            secondDerivatives[i] = secondDerivatives[i] * secondDerivatives[i + 1] + u[i];
        }

        return secondDerivatives;
    }

    protected double interpolateSpline(Point2D p1, Point2D p2, double ypp1, double ypp2, double x) {
        double h = p2.getX() - p1.getX();
        double a = (p2.getX() - x) / h;
        double b = (x - p1.getX()) / h;

        return a * p1.getY() + b * p2.getY() +
                ((a * a * a - a) * ypp1 + (b * b * b - b) * ypp2) * (h * h) / 6.0;
    }
}