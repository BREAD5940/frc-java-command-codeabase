package frc.robot.lib.obj;

import java.util.Arrays;
import java.util.List;

import org.ghrobotics.lib.mathematics.units.Length;
import org.ghrobotics.lib.mathematics.units.LengthKt;

import frc.robot.lib.Loggable;

public class VisionTarget implements Loggable {

	private final Length width, height;
	private final Length goalHeight;
	public static final List<Length> kVisionTapeShape = Arrays.asList(LengthKt.getInch(12), LengthKt.getInch(5));

	/**
	 * Make a VisionTarget to store basic information about a vision target such as width, height and goal elevation.
	 * @param width
	 * @param height
	 * @param goalElevation
	 */
	public VisionTarget(Length width, Length height, Length goalElevation) {
		this.width = width;
		this.height = height;
		this.goalHeight = goalElevation;
	}

	public VisionTarget(Length goalElevation) {
		this(kVisionTapeShape.get(0), kVisionTapeShape.get(1), goalElevation);
	}

	@Override
	public String getCSVHeader() {
		return "widthInches,heightInches";
	}

	@Override
	public String toCSV() {
		return String.format("%s,%s", width.getInch(), height.getInch());
	}

	/**
	 * @return the width
	 */
	public Length getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public Length getHeight() {
		return height;
	}

	/**
	 * @return the goalHeight
	 */
	public Length getGoalHeight() {
		return goalHeight;
	}

}
