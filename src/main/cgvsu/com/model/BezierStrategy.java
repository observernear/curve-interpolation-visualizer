package main.cgvsu.com.model;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class BezierStrategy implements InterpolationStrategyImpl<Point2D> {

    @Override
    public List<Point2D> calculate(List<Point2D> controlPoints, int segments) {
        if (controlPoints == null || controlPoints.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 control points");
        }

        List<Point2D> curvePoints = new ArrayList<>();

        for (int i = 0; i <= segments; i++) {
            double t = (double) i / segments;
            Point2D point = calculatePoint(controlPoints, t);
            curvePoints.add(point);
        }

        return curvePoints;
    }

    @Override
    public Point2D calculatePoint(List<Point2D> points, double t) {
        // return calculatePointBernstein(points, t);
        return calculatePointCasteljau(points, t);
    }

    protected Point2D calculatePointCasteljau(List<Point2D> points, double t) {
        int n = points.size() - 1;
        if (n == 0) {
            return points.getFirst();
        }

        Point2D[] temp = new Point2D[n + 1];
        for (int i = 0; i <= n; i++) {
            temp[i] = points.get(i);
        }

        for (int k = 1; k <= n; k++) {
            for (int i = 0; i <= n - k; i++) {
                double x = (1 - t) * temp[i].getX() + t * temp[i + 1].getX();
                double y = (1 - t) * temp[i].getY() + t * temp[i + 1].getY();
                temp[i] = new Point2D(x, y);
            }
        }

        return temp[0];
    }

    protected static Point2D calculatePointBernstein(List<Point2D> points, double t) {
        int n = points.size() - 1;
        double x = 0;
        double y = 0;

        for (int i = 0; i <= n; i++) {
            double binomial = binomialCoefficient(n, i);
            double bernstein = binomial * Math.pow(t, i) * Math.pow(1 - t, n - i);

            x += bernstein * points.get(i).getX();
            y += bernstein * points.get(i).getY();
        }

        return new Point2D(x, y);
    }

    protected static double binomialCoefficient(int n, int k) {
        if (k < 0 || k > n) return 0;
        if (k == 0 || k == n) return 1;

        double result = 1;
        for (int i = 1; i <= k; i++) {
            result = result * (n - k + i) / i;
        }
        return result;
    }
}