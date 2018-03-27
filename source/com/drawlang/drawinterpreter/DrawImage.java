package com.drawlang.drawinterpreter;

import javafx.scene.paint.*;
import javafx.scene.image.*;

import java.util.*;

public class DrawImage extends DrawInstance {
	
	public WritableImage image;
	private PixelWriter pixelWriter;
	private PixelReader pixelReader;


	public DrawImage(WritableImage image) {
		super(null);
		this.image = image;
		pixelReader = image.getPixelReader();
		pixelWriter = image.getPixelWriter();
	}

	@Override
	Object get(Token name) {
		switch (name.lexeme) {
			case "getSubimage":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 4;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						int x = (int)(double)arguments.get(0), y = (int)(double)arguments.get(1);
						int w = (int)(double)arguments.get(2), h = (int)(double)arguments.get(3);
						return new DrawImage(new WritableImage(pixelReader, x, y, w, h));
					}
				};
			// returns a pixel at a specified position
			case "getPixel":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 2;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						int x = (int)(double) arguments.get(0), y = (int)(double)arguments.get(1);
						return new DrawColor(pixelReader.getColor(x, y));
					}
				};

				// returns a resized version of the image
				case "resizeImage":
					return new DrawCallable() {
						@Override
						public int arity() {
							return 2;
						}

						@Override
						public Object call(Interpreter interpreter, List<Object> arguments) {
							int w = (int)(double)arguments.get(0), h = (int)(double)arguments.get(1);
							// create image view
							ImageView imageView = new ImageView(image);
							// resize image view
							imageView.setFitWidth(w);
							imageView.setFitHeight(h);
							// convert image view to image
							return new DrawImage(new WritableImage(imageView.snapshot(null, null).getPixelReader(), w, h));
						}
					};

			// returns a new image with a given color removed
			case "setTransparentColor":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 1;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						// gets color to be removed
						Color transparentColor = ((DrawColor)arguments.get(0)).color;
						WritableImage result = new WritableImage(pixelReader, (int)image.getWidth(), (int)image.getHeight());
						PixelReader resultReader = result.getPixelReader();
						PixelWriter resultWriter = result.getPixelWriter();
						for(int y = 0; y < image.getHeight(); y++) {
							for (int x = 0; x < image.getWidth(); x++) {
								// if transparent color is equal to the pixel then make the pixel transparent
								Color pixel = pixelReader.getColor(x, y);
								if (transparentColor.equals(pixel)) {
									resultWriter.setColor(x, y, 
										new Color(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), 0));
								}
							}
						}
						// returns the new version of the image with the transparency
						return new DrawImage(result);
					}
				};

			// sets a pixel to a given colour
			case "setPixel":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 3;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						int x = (int)(double) arguments.get(0), y = (int)(double)arguments.get(1);
						Color color = ((DrawColor) arguments.get(2)).color;
						pixelWriter.setColor(x, y, color);
						return null;
					}
				};
			case "width":
				return image.getWidth();
			case "height":
				return image.getHeight();
			default:
				throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
		}

	}
}

