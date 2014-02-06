package de.fhtrier.gdig.demos.jumpnrun.common.gamelogic;

import org.newdawn.slick.Color;

import de.fhtrier.gdig.demos.jumpnrun.identifiers.Constants;

public class StateColor {
	public static final int BLACK = 0;
	public static final int RED = 1;
	public static final int GREEN = RED << 1;
	public static final int YELLOW = RED | GREEN;
	public static final int BLUE = RED << 2;
	public static final int MAGENTA = RED | BLUE;
	public static final int CYAN = GREEN | BLUE;
	public static final int WHITE = RED | GREEN | BLUE;

	/**
	 * Creates a new Color Object matching the Color for the specified
	 * Color-Index.
	 * 
	 * @param colorConst
	 *            The ColorIndex (e.g. StateColor.BLACK)
	 * @return A new Color Object
	 * @throws IllegalArgumentException
	 *             If an invalid Index is specified
	 */
	public static Color constIntoColor(int colorConst)
			throws IllegalArgumentException {
		switch (colorConst) {
		case BLACK:
			return new Color(Color.black);
		case RED:
			return new Color(Color.red);
		case GREEN:
			return new Color(0.2f, 0.8f, 0.1f);
		case YELLOW:
			return new Color(Color.yellow);
		case BLUE:
			return new Color(0.2f, 0.2f, 1.0f);
		case MAGENTA:
			return new Color(Color.magenta);
		case CYAN:
			return new Color(Color.cyan);
		case WHITE:
			return new Color(Color.white);
		default:
			throw new IllegalArgumentException("Undefined parameter");
		}
	}

	public static float constIntoBrightness(int colorConst)
			throws IllegalArgumentException {
		switch (colorConst) {
		default:
			return Constants.GamePlayConstants.playerBrightness;
		}
	}
}