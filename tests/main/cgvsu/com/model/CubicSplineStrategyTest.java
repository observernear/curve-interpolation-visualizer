package main.cgvsu.com.model;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CubicSplineStrategyTest {

    @Test
    void testCalculate_TooFewPoints() {
        CubicSplineStrategy strategy = new CubicSplineStrategy();
        List<Point2D> points = List.of(new Point2D(0, 0));
        assertThrows(IllegalArgumentException.class, () -> strategy.calculate(points, 10));
    }

    @Test
    void testCalculate_DuplicateX() {
        CubicSplineStrategy strategy = new CubicSplineStrategy();
        List<Point2D> points = List.of(new Point2D(0, 0), new Point2D(0, 1));
        assertThrows(IllegalArgumentException.class, () -> strategy.calculate(points, 10));
    }

    @Test
    void testCalculate_NormalCase() {
        CubicSplineStrategy strategy = new CubicSplineStrategy();
        List<Point2D> points = List.of(
                new Point2D(0, 0),
                new Point2D(1, 2),
                new Point2D(2, 0)
        );

        List<Point2D> curve = strategy.calculate(points, 10);
        assertNotNull(curve);
        assertTrue(curve.size() > 0);

        assertEquals(0, curve.get(0).getY(), 1e-6);
        assertEquals(0, curve.get(curve.size() - 1).getY(), 1e-6);
    }

    @Test
    void testCalculatePoint_InsideRange() {
        CubicSplineStrategy strategy = new CubicSplineStrategy();
        List<Point2D> points = List.of(
                new Point2D(0, 0),
                new Point2D(1, 1)
        );

        Point2D p = strategy.calculatePoint(points, 0.5);
        assertEquals(0.5, p.getX(), 1e-6);
        assertEquals(0.5, p.getY(), 1e-6);
    }

    @Test
    void testCalculatePoint_OutsideRange() {
        CubicSplineStrategy strategy = new CubicSplineStrategy();
        List<Point2D> points = List.of(
                new Point2D(0, 0),
                new Point2D(1, 1)
        );

        Point2D p1 = strategy.calculatePoint(points, -1);
        assertEquals(0, p1.getX(), 1e-6);
        assertEquals(0, p1.getY(), 1e-6);

        Point2D p2 = strategy.calculatePoint(points, 2);
        assertEquals(1, p2.getX(), 1e-6);
        assertEquals(1, p2.getY(), 1e-6);
    }
}
