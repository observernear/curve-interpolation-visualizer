package main.cgvsu.com.model;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LagrangeStrategyTest {

    @Test
    void testCalculate_TooFewPoints() {
        LagrangeStrategy strategy = new LagrangeStrategy();
        List<Point2D> points = List.of(new Point2D(0, 0));
        assertThrows(IllegalArgumentException.class, () -> strategy.calculate(points, 10));
    }

    @Test
    void testCalculate_NormalCase() {
        LagrangeStrategy strategy = new LagrangeStrategy();
        List<Point2D> points = List.of(
                new Point2D(0, 0),
                new Point2D(1, 1),
                new Point2D(2, 0)
        );

        List<Point2D> curve = strategy.calculate(points, 10);
        assertNotNull(curve);
        assertEquals(11, curve.size());

        assertEquals(0, curve.get(0).getY(), 1e-6);
        assertEquals(0, curve.get(curve.size() - 1).getY(), 1e-6);
    }

    @Test
    void testCalculatePoint_ExactControlPoint() {
        LagrangeStrategy strategy = new LagrangeStrategy();
        List<Point2D> points = List.of(
                new Point2D(0, 0),
                new Point2D(1, 1),
                new Point2D(2, 0)
        );

        Point2D p = strategy.calculatePoint(points, 1);
        assertEquals(1, p.getX(), 1e-6);
        assertEquals(1, p.getY(), 1e-6);
    }

    @Test
    void testCalculatePoint_BetweenControlPoints() {
        LagrangeStrategy strategy = new LagrangeStrategy();
        List<Point2D> points = List.of(
                new Point2D(0, 0),
                new Point2D(1, 2),
                new Point2D(2, 0)
        );

        Point2D p = strategy.calculatePoint(points, 0.5);
        assertEquals(0.5, p.getX(), 1e-6);
        assertTrue(p.getY() > 0 && p.getY() < 2);
    }
}
