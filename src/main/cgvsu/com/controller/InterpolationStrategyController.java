package main.cgvsu.com.controller;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import main.cgvsu.com.model.*;
import main.cgvsu.com.view.InterpolationViewerImpl;
import main.cgvsu.com.view.Point2DViewer;

import java.util.List;

public class InterpolationStrategyController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    @FXML
    private RadioButton bezierRadio;

    @FXML
    private RadioButton lagrangeRadio;

    @FXML
    private RadioButton splineRadio;

    @FXML
    private Slider segmentsSlider;

    @FXML
    private Label segmentsLabel;

    private ToggleGroup InterpolationTypeGroup;

    private final PointManagerImpl<Point2D> pointManager = new Point2DManager();
    private InterpolationStrategyImpl<Point2D> currentStrategy;
    private final InterpolationViewerImpl<Point2D> interpolationViewer = new Point2DViewer();

    private int segments = 100;
    private boolean isDragging = false;
    private int draggedPointIndex = -1;
    private static final double DRAG_RADIUS = 10.0;

    @FXML
    private void initialize() {
        setupInterpolationTypeGroup();
        setupCanvasResizeListeners();
        setupMouseHandlers();
        setupSegmentsSlider();
        updateStrategy();
    }

    private void setupInterpolationTypeGroup() {
        InterpolationTypeGroup = new ToggleGroup();
        bezierRadio.setToggleGroup(InterpolationTypeGroup);
        lagrangeRadio.setToggleGroup(InterpolationTypeGroup);
        splineRadio.setToggleGroup(InterpolationTypeGroup);

        InterpolationTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateStrategy();
                redrawCanvas();
            }
        });
    }

    private void setupSegmentsSlider() {
        segmentsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            segments = newValue.intValue();
            segmentsLabel.setText("Сегментов: " + segments);
            redrawCanvas();
        });

        segmentsLabel.setText("Сегментов: " + segments);
    }

    private void updateStrategy() {
        RadioButton selectedRadio = (RadioButton) InterpolationTypeGroup.getSelectedToggle();
        String selectedType = selectedRadio.getUserData().toString();

        currentStrategy = InterpolationStrategyFactory.createStrategy(
                InterpolationStrategyFactory.StrategyType.valueOf(selectedType)
        );
    }

    private void setupCanvasResizeListeners() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> {
            canvas.setWidth(newValue.doubleValue());
            redrawCanvas();
        });

        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> {
            canvas.setHeight(newValue.doubleValue());
            redrawCanvas();
        });
    }

    private void setupMouseHandlers() {
        canvas.setOnMouseClicked(this::handleMouseClick);
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseReleased);
        canvas.setOnMouseMoved(this::handleMouseMoved);
    }

    private void handleMouseMoved(MouseEvent event) {
        Point2D mousePoint = new Point2D(event.getX(), event.getY());
        int nearestIndex = pointManager.findNearestPointIndex(mousePoint, DRAG_RADIUS);

        if (nearestIndex != -1 && !isDragging) {
            canvas.setCursor(Cursor.HAND);
        } else {
            canvas.setCursor(Cursor.DEFAULT);
        }
    }

    private void handleMouseClick(MouseEvent event) {
        Point2D clickPoint = new Point2D(event.getX(), event.getY());

        if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
            handlePrimaryClick(clickPoint);
        } else if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
            handleSecondaryClick(clickPoint);
        }
    }

    private void handlePrimaryClick(Point2D clickPoint) {
        if (pointManager.findNearestPoint(clickPoint, DRAG_RADIUS) == null) {
            pointManager.addPoint(clickPoint);
            redrawCanvas();
        }
    }

    private void handleSecondaryClick(Point2D clickPoint) {
        int nearestIndex = pointManager.findNearestPointIndex(clickPoint, DRAG_RADIUS);

        if (nearestIndex != -1) {
            pointManager.removePoint(nearestIndex);
        } else {
            pointManager.clearPoints();
        }

        resetDragging();
        redrawCanvas();
    }

    private void handleMousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            Point2D clickPoint = new Point2D(event.getX(), event.getY());
            draggedPointIndex = pointManager.findNearestPointIndex(clickPoint, DRAG_RADIUS);

            if (draggedPointIndex != -1) {
                isDragging = true;
                canvas.setCursor(Cursor.CLOSED_HAND);
            }
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (isDragging && draggedPointIndex != -1) {
            Point2D newPosition = new Point2D(event.getX(), event.getY());
            pointManager.updatePoint(draggedPointIndex, newPosition);
            redrawCanvas();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        resetDragging();
    }

    private void resetDragging() {
        isDragging = false;
        draggedPointIndex = -1;
        canvas.setCursor(Cursor.DEFAULT);
        redrawCanvas();
    }

    private void redrawCanvas() {
        interpolationViewer.clearCanvas(canvas);

        List<Point2D> points = pointManager.getPoints();

        interpolationViewer.drawControlPoints(canvas, points, draggedPointIndex);

        if (pointManager.hasEnoughPointsForCurve() && currentStrategy != null) {
            try {
                List<Point2D> curvePoints = currentStrategy.calculate(points, segments);
                interpolationViewer.drawCurve(canvas, curvePoints);

                if (currentStrategy instanceof BezierStrategy) {
                    interpolationViewer.drawControlPolygon(canvas, points);
                }
            } catch (IllegalArgumentException e) {
                interpolationViewer.drawErrorText(canvas, "Ошибка: " + e.getMessage());
            }
        }

        interpolationViewer.drawDebugInfo(canvas, getDebugInfo());
    }

    private String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Точек: ").append(pointManager.getPointCount()).append("\n");

        RadioButton selected = (RadioButton) InterpolationTypeGroup.getSelectedToggle();
        if (selected != null) {
            info.append("Выбрано: ").append(selected.getText()).append("\n");
        }

        if (isDragging) {
            info.append("Перетаскивание точки: ").append(draggedPointIndex + 1).append("\n");
        }

        return info.toString();
    }
}