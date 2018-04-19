package com.drawlang.drawinterpreter;

import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.image.*;

import java.util.*;

class DrawCanvas extends DrawInstance {

	private Canvas canvas;
	private GraphicsContext context;
	private PixelWriter pixelWriter;

	DrawCanvas(Canvas canvas) {
		super(null);
		this.canvas = canvas;
		context = canvas.getGraphicsContext2D();
		pixelWriter = context.getPixelWriter();
	}

	@Override
	Object get(Token name) {
		switch (name.lexeme) {
			case "clear":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 0;
					}
	
					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						clear();
						return null;
					}
				};
			case "fillRect":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 4;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						// throws error if arguments are not of type double
						if (
							!(arguments.get(0) instanceof Double &&
							arguments.get(1) instanceof Double &&
							arguments.get(2) instanceof Double &&
							arguments.get(3) instanceof Double)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(number, number, number, number).");
						}

						int x = (int)(double)arguments.get(0), y = (int)(double)arguments.get(1);
						int w = (int)(double)arguments.get(2), h = (int)(double)arguments.get(3);
						context.fillRect(x, y, w, h);
						return null;
					}
				};
			case "fillCircle":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 3;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof Double &&
							arguments.get(1) instanceof Double &&
							arguments.get(2) instanceof Double)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(number, number, number).");
						}

						int x = (int)(double) arguments.get(0), y = (int)(double)arguments.get(1);
						int radius = (int)(double) arguments.get(2);
						context.fillOval(x-radius/2, y-radius/2, radius, radius);
						return null;
					}

				};
			case "fillText":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 3;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof DrawString &&
							arguments.get(1) instanceof Double &&
							arguments.get(2) instanceof Double)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(String, number, number).");
						}
						String text = ((DrawString)arguments.get(0)).toString();
						double x = (double) arguments.get(1), y = (double)arguments.get(2);
						context.fillText(text, x, y);
						return null;
					}

				};
			case "fillPolygon": 
				return new DrawCallable() {
					@Override
					public int arity() {
						return 2;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof DrawArray &&
							arguments.get(1) instanceof DrawArray)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(number[], number[]).");
						}
						// takes in the custom draw array and converts it to a double array
						// for the API to use
						double[] xPoints = toDoubleArray(((DrawArray) arguments.get(0)).elements);
						double[] yPoints = toDoubleArray(((DrawArray) arguments.get(1)).elements);
						// sets the size to the smallest of the two arrays incase the user sent
						// arrays of different lengths
						int size = xPoints.length < yPoints.length ? xPoints.length : yPoints.length;						
						context.fillPolygon(xPoints, yPoints, size);
						return null;
					}
				};
			// fills an arc without using path, last parameter must be either "ROUND", "CHORD", or "OPEN"
			case "fillArc": 
				return new DrawCallable() {
					@Override
					public int arity() {
						return 7;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof Double &&
							arguments.get(1) instanceof Double &&
							arguments.get(2) instanceof Double &&
							arguments.get(3) instanceof Double &&
							arguments.get(4) instanceof Double &&
							arguments.get(5) instanceof Double &&
							arguments.get(6) instanceof String)
							) {
							throw new RuntimeError(
								name, "Expected " + name.lexeme + 
								"(number, number, number, number, number, number, String).");
						}
						int x = (int)(double)arguments.get(0), y = (int)(double)arguments.get(1);
						int w = (int)(double)arguments.get(2), h = (int)(double)arguments.get(3);
						double startAngle = (double)arguments.get(4), arcExtent = (double)arguments.get(5);
						String arcType = (String) arguments.get(6);
						ArcType closure = ArcType.valueOf(arcType);
						context.fillArc(x, y, w, h, startAngle, arcExtent, closure);
						return null;
					}
				};
			case "drawRect":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 4;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof Double &&
							arguments.get(1) instanceof Double &&
							arguments.get(2) instanceof Double &&
							arguments.get(3) instanceof Double)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(numner, numner, number, number).");
						}

						int x = (int)(double)arguments.get(0), y = (int)(double)arguments.get(1);
						int w = (int)(double)arguments.get(2), h = (int)(double)arguments.get(3);
						context.strokeRect(x, y, w, h);
						return null;
					}
				};
			case "drawCircle":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 3;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof Double &&
							arguments.get(1) instanceof Double &&
							arguments.get(2) instanceof Double)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(number, number, number).");
						}

						int x = (int)(double) arguments.get(0), y = (int)(double)arguments.get(1);
						int radius = (int)(double) arguments.get(2);
						context.strokeOval(x-radius/2, y-radius/2, radius, radius);
						return null;
					}

				};
			case "drawPolygon": 
				return new DrawCallable() {
					@Override
					public int arity() {
						return 2;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof DrawArray &&
							arguments.get(1) instanceof DrawArray)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(number[], number[]).");
						}

						double[] xPoints = toDoubleArray(((DrawArray) arguments.get(0)).elements);
						double[] yPoints = toDoubleArray(((DrawArray) arguments.get(1)).elements);
						int size = xPoints.length < yPoints.length ? xPoints.length : yPoints.length;
						// same as fill polygon but uses stroke instead to draw outline				
						context.strokePolygon(xPoints, yPoints, size);
						return null;
					}
				};
			case "drawLine": 
				return new DrawCallable() {
					@Override
					public int arity() {
						return 4;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof Double &&
							arguments.get(1) instanceof Double &&
							arguments.get(2) instanceof Double &&
							arguments.get(3) instanceof Double)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(number, number, number, number).");
						}

						int x = (int)(double)arguments.get(0), y = (int)(double)arguments.get(1);
						int endX = (int)(double)arguments.get(2), endY = (int)(double)arguments.get(3);
						// gets start and end coordinates and uses them to draw a line			
						context.strokeLine(x, y, endX, endY);
						return null;
					}
				};
			case "drawPolyline": 
				return new DrawCallable() {
					@Override
					public int arity() {
						return 2;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof DrawArray &&
							arguments.get(1) instanceof DrawArray)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(number[], number[]).");
						}

						double[] xPoints = toDoubleArray(((DrawArray) arguments.get(0)).elements);
						double[] yPoints = toDoubleArray(((DrawArray) arguments.get(1)).elements);
						int size = xPoints.length < yPoints.length ? xPoints.length : yPoints.length;
						// same as draw polygon but does not explicitly connect shape				
						context.strokePolyline(xPoints, yPoints, size);
						return null;
					}
				};
			// same as fill arc but draws outline instead
			case "drawArc": 
				return new DrawCallable() {
					@Override
					public int arity() {
						return 7;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof Double &&
							arguments.get(1) instanceof Double &&
							arguments.get(2) instanceof Double &&
							arguments.get(3) instanceof Double &&
							arguments.get(4) instanceof Double &&
							arguments.get(5) instanceof Double &&
							arguments.get(6) instanceof String)
							) {
							throw new RuntimeError(
								name, "Expected " + name.lexeme + 
								"(number, number, number, number, number, number, String).");

						}

						int x = (int)(double)arguments.get(0), y = (int)(double)arguments.get(1);
						int w = (int)(double)arguments.get(2), h = (int)(double)arguments.get(3);
						double startAngle = (double)arguments.get(4), arcExtent = (double)arguments.get(5);
						String arcType = (String) arguments.get(6);
						ArcType closure = ArcType.valueOf(arcType);
						context.strokeArc(x, y, w, h, startAngle, arcExtent, closure);
						return null;
					}
				};
			case "drawImage":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 3;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof DrawImage &&
							arguments.get(1) instanceof Double &&
							arguments.get(2) instanceof Double)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(Image, number, number).");
						}
						int x = (int)(double) arguments.get(1), y = (int)(double)arguments.get(2);
						Image image = ((DrawImage) arguments.get(0)).image;
						context.drawImage(image, x, y);
						return null;
					}
				};
			case "toImage":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 0;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						WritableImage result = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
						canvas.snapshot(null, result);
						return new DrawImage(result);
					}
				};
			case "setColor":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 1;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						// throws error if argument is not of type DrawColor
						if (!(arguments.get(0) instanceof DrawColor))
							throw new RuntimeError(name, "Expected " + name.lexeme + "(Color).");

						Color color = ((DrawColor)arguments.get(0)).color;
						context.setFill(color);
						context.setStroke(color);
						return null;
					}
				};
			case "setLineWidth":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 1;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (!(arguments.get(0) instanceof Double)) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(number).");
						}
						context.setLineWidth((double)arguments.get(0));
						return null;
					}
				};
			case "setPixel":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 3;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof Double &&
							arguments.get(1) instanceof Double &&
							arguments.get(2) instanceof DrawColor)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(number, number, Color).");
						}

						int x = (int)(double) arguments.get(0), y = (int)(double)arguments.get(1);
						Color color = ((DrawColor) arguments.get(2)).color;
						pixelWriter.setColor(x, y, color);
						return null;
					}
				};

			// resets current path to empty
			case "beginPath":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 0;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						context.beginPath();
						return null;
					}
				};

			// closes current path
			case "closePath":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 0;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						context.closePath();
						return null;
					}
				};

			// fills current path with current colour
			case "fillPath":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 0;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						context.fill();
						return null;
					}
				};

			// draws outline of current path with current colour
			case "drawPath":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 0;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						context.stroke();
						return null;
					}
				};

			// moves path to given coordinates
			case "moveTo":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 2;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof Double &&
							arguments.get(1) instanceof Double)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(number, number).");
						}
						double x = (double) arguments.get(0), y = (double)arguments.get(1);
						context.moveTo(x, y);
						return null;
					}
				};

			// draws line from path position to given coordinates
			case "lineTo":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 2;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof Double &&
							arguments.get(1) instanceof Double)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(number, number).");
						}

						double x = (double) arguments.get(0), y = (double)arguments.get(1);
						context.lineTo(x, y);
						return null;
					}
				};

			// draws curve from path position to given coordinates
			// this approaches but does not touch a control coordinate
			case "quadraticCurveTo":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 4;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof Double &&
							arguments.get(1) instanceof Double &&
							arguments.get(2) instanceof Double &&
							arguments.get(3) instanceof Double)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(number, number, number, number).");
						}

						double xControl = (double) arguments.get(0), yControl = (double)arguments.get(1);
						double xEnd = (double) arguments.get(2), yEnd = (double)arguments.get(3);
						context.quadraticCurveTo(xControl, yControl, xEnd, yEnd);
						return null;
					}
				};

			// similar to quadratic curve but requires two control points
			// which allows more flexibility
			case "bezierCurveTo":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 6;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof Double &&
							arguments.get(1) instanceof Double &&
							arguments.get(2) instanceof Double &&
							arguments.get(3) instanceof Double &&
							arguments.get(4) instanceof Double &&
							arguments.get(5) instanceof Double)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + 
								"(number, number, number number, number, number).");
						}
						double xControl = (double) arguments.get(0), yControl = (double)arguments.get(1);
						double xControl2 = (double) arguments.get(2), yControl2 = (double)arguments.get(3);
						double xEnd = (double) arguments.get(4), yEnd = (double)arguments.get(5);
						context.bezierCurveTo(xControl, yControl, xControl2, yControl2, xEnd, yEnd);
						return null;
					}
				};
			// adds arc to path
			case "arcTo":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 5;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof Double &&
							arguments.get(1) instanceof Double &&
							arguments.get(2) instanceof Double &&
							arguments.get(3) instanceof Double &&
							arguments.get(4) instanceof Double)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + 
								"(number, number, number, number, number).");
						}

						double x1 = (double) arguments.get(0), y1 = (double)arguments.get(1);
						double x2 = (double) arguments.get(2), y2 = (double)arguments.get(3);
						double radius = (double) arguments.get(4);
						context.arcTo(x1, y1, x2, y2, radius);
						return null;
					}
				};
			// adds arc to path, allows user to specify angles
			case "arc":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 6;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (
							!(arguments.get(0) instanceof Double &&
							arguments.get(1) instanceof Double &&
							arguments.get(2) instanceof Double &&
							arguments.get(3) instanceof Double &&
							arguments.get(4) instanceof Double &&
							arguments.get(5) instanceof Double)
							) {
							throw new RuntimeError(name, "Expected " + name.lexeme + 
								"(number, number, number, number, number, number).");
						}
						double centerX = (double) arguments.get(0), centerY = (double)arguments.get(1);
						double radiusX = (double) arguments.get(2), radiusY = (double)arguments.get(3);
						double startAngle = (double) arguments.get(4), length = (double)arguments.get(5);
						context.arc(centerX, centerY, radiusX, radiusY, startAngle, length);
						return null;
					}
				};

			case "width":
				return canvas.getWidth();
			case "height":
				return canvas.getHeight();
			default:
				throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
		}
	}

	public void clear() {
		context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	// takes in an object array and returns a double array
	// by iterating and casting
	public double[] toDoubleArray(Object[] array) {
		double[] result = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = (double) array[i];
		}

		return result;
	}

}

