package frc.robot.lib;

import org.ghrobotics.lib.mathematics.units.Length;
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity;
import org.ghrobotics.lib.mathematics.units.derivedunits.VelocityKt;
import org.ghrobotics.lib.mathematics.units.derivedunits.Volt;
import org.ghrobotics.lib.mathematics.units.derivedunits.VoltKt;
import org.ghrobotics.lib.wrappers.FalconMotor;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;

public class FalconSparkMax extends CANSparkMax implements FalconMotor<Length> {

	// NativeUnitLengthModel model;
	CANEncoder neoEncoder;
	Length wheelDiameter;
	double reduction;
	CANPIDController controller;
	int m_currentPIDSlot = 0;

	public static final int kDefaultStallCurrent = 30;
	public static final int kFreeCurrent = 60;

	public FalconSparkMax(int port, MotorType type, Length wheelDiameter, double reduction) {
		super(port, type);
		neoEncoder = new CANEncoder(this);
		controller = super.getPIDController();
		this.reduction = reduction;

		setSmartCurrentLimit(kDefaultStallCurrent, kFreeCurrent);
	}

	public CANPIDController getController() {
		return controller;
	}

	public void setClosedLoopGains(int slot, PIDSettings settings) {
		controller.setP(settings.kp, slot);
		controller.setI(settings.ki, slot);
		controller.setD(settings.kd, slot);
		controller.setFF(settings.kf, slot);
		m_currentPIDSlot = slot;
	}

	@Override
	public double getPercentOutput() {
		return super.getAppliedOutput();
	}

	@Override
	public Velocity<Length> getVelocity() {
		var rawRPM = neoEncoder.getPosition() / reduction;
		var sufaceSpeed = wheelDiameter.times(Math.PI).times(rawRPM);
		var toReturn = VelocityKt.getVelocity(sufaceSpeed);
		return toReturn;
	}

	public Length getDistance() {
		var rawRevs = neoEncoder.getPosition();
		var distance = wheelDiameter.times(Math.PI).times(rawRevs);
		return distance;
	}

	@Override
	public Volt getVoltageOutput() {
		var toReturn = super.getAppliedOutput() / super.getBusVoltage();
		return VoltKt.getVolt(toReturn);
	}

	@Override
	public void setPercentOutput(double arg0) {
		super.set(arg0);
		;
	}

	@Override
	public void setVelocity(Velocity arg0) {
		controller.setReference(arg0.getValue(), ControlType.kVelocity);
	}

	@Override
	public void setVelocityAndArbitraryFeedForward(Velocity<Length> arg0, double arg1) {
		double setpoint = arg0.getValue();
		controller.setReference(setpoint, ControlType.kVelocity, m_currentPIDSlot, arg1);
	}

}
