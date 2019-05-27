package frc.ghrobotics.vision

import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.Timer
import frc.robot.subsystems.DriveTrain
import frc.robot.subsystems.superstructure.SuperStructure
//import org.ghrobotics.frc2019.Constants
//import org.ghrobotics.frc2019.subsystems.drive.DriveSubsystem
import org.ghrobotics.lib.debug.LiveDashboard
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.units.*


object TargetTracker {

    private val targets = mutableSetOf<TrackedTarget>()

    private val table = NetworkTableInstance.getDefault().getTable("limelight")

    fun update() {
        synchronized(targets) {
            val currentTime = Timer.getFPGATimestamp()

            val currentRobotPose = DriveTrain.getInstance().localization()

            // Update and remove old targets
            targets.removeIf {
                it.update(currentTime, currentRobotPose)
                !it.isAlive
            }
            // Publish to dashboard
            LiveDashboard.visionTargets = targets.asSequence()
                .filter { it.isReal }
                .map { it.averagedPose2d }
                .toList()
        }
    }

    fun getDx(): Rotation2d = table.getEntry("tx").getDouble(0.0).degree


    fun addSamples(creationTime: Double, samples: Iterable<Pose2d>) {
        if (creationTime >= Timer.getFPGATimestamp()) return // Cannot predict the future

        synchronized(targets) {
            for (samplePose in samples) {
                val closestTarget = targets.minBy {
                    it.averagedPose2d.translation.distance(samplePose.translation)
                }
                val sample = TrackedTargetSample(creationTime, samplePose)
                if (closestTarget == null
                    || closestTarget.averagedPose2d.translation.distance(samplePose.translation) > kTargetTrackingDistanceErrorTolerance.value
                ) {
                    // Create new target if no targets are within tolerance
                    targets += TrackedTarget(sample)
                } else {
                    // Add sample to target within tolerance
                    closestTarget.addSample(sample)
                }
            }
        }
    }

    fun getBestTarget(isFrontTarget: Boolean) = synchronized(targets) {

//        if(!SuperStructure.getInstance().getPassedThrough())

        targets.asSequence()
            .filter {
                if (!it.isReal) return@filter false
                val x = it.averagedPose2dRelativeToBot.translation.x
                if (isFrontTarget) x.value >= 0.0 else x.value <= 0.0
            }.minBy { it.averagedPose2dRelativeToBot.translation.norm }
    }

    fun getBestTargetUsingReference(referencePose: Pose2d, isFrontTarget: Boolean) = synchronized(targets) {
        targets.asSequence()
            .associateWith { it.averagedPose2d inFrameOfReferenceOf referencePose }
            .filter {
                val x = it.value.translation.x
                it.key.isReal && if (isFrontTarget) x.value > 0.0 else x.value < 0.0
            }
            .minBy { it.value.translation.norm }?.key
    }

    fun getAbsoluteTarget(translation2d: Translation2d) = synchronized(targets) {
        targets.asSequence()
            .filter {
                it.isReal
                    && translation2d.distance(it.averagedPose2d.translation) <= kTargetTrackingDistanceErrorTolerance.value
            }
            .minBy { it.averagedPose2d.translation.distance(translation2d) }
    }

    class TrackedTarget(
        initialTargetSample: TrackedTargetSample
    ) {

        private val samples = mutableSetOf<TrackedTargetSample>()

        /**
         * The averaged pose2d for x time
         */
        var averagedPose2d = initialTargetSample.targetPose
            private set

        var averagedPose2dRelativeToBot = Pose2d()
            private set

        /**
         * Targets will be "alive" when it has at least one data point for x time
         */
        var isAlive = true
            private set

        /**
         * Target will become a "real" target once it has received data points for x time
         */
        var isReal = false
            private set

        var stability = 0.0
            private set

        init {
            addSample(initialTargetSample)
        }

        fun addSample(newSamples: TrackedTargetSample) = synchronized(samples) {
            samples.add(newSamples)
        }

        fun update(currentTime: Double, currentRobotPose: Pose2d) = synchronized(samples) {
            // Remove expired samples
            samples.removeIf { currentTime - it.creationTime >= kTargetTrackingMaxLifetime.value }
            // Update State
            isAlive = samples.isNotEmpty()
            if (samples.size >= 2) isReal = true
            stability = (samples.size / (kVisionCameraFPS * kTargetTrackingMaxLifetime.value))
                .coerceAtMost(1.0)
            // Update Averaged Pose
            var accumulatedX = 0.0
            var accumulatedY = 0.0
            var accumulatedAngle = 0.0
            for (sample in samples) {
                accumulatedX += sample.targetPose.translation.x.value
                accumulatedY += sample.targetPose.translation.y.value
                accumulatedAngle += sample.targetPose.rotation.value
            }
            averagedPose2d = Pose2d(
                Length(accumulatedX / samples.size),
                Length(accumulatedY / samples.size),
                Rotation2d(accumulatedAngle / samples.size)
            )
            averagedPose2dRelativeToBot = averagedPose2d inFrameOfReferenceOf currentRobotPose
        }

    }

    data class TrackedTargetSample(
        val creationTime: Double,
        val targetPose: Pose2d
    )

    // VISION
    const val kVisionCameraFPS = 30.0
    val kVisionCameraPing = 0.75.second
    val kVisionCameraTimeout = 2.second
    val kTargetTrackingDistanceErrorTolerance = 16.inch
    val kTargetTrackingMinLifetime = 0.1.second
    val kTargetTrackingMaxLifetime = 0.5.second
}