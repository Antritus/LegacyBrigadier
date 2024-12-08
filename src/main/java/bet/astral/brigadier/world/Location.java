package bet.astral.brigadier.world;

import org.jetbrains.annotations.NotNull;

public interface Location {
    double getX();
    double getY();
    double getZ();
    float getYaw();
    float getPitch();
    @NotNull
    World getWorld();

    class LocationImpl implements Location{
        private double x, y, z;
        private float yaw, pitch;
        private World world;

        public LocationImpl(double x, double y, double z, float yaw, float pitch, World world) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            this.world = world;
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }

        @Override
        public double getZ() {
            return z;
        }

        @Override
        public float getYaw() {
            return yaw;
        }

        @Override
        public float getPitch() {
            return pitch;
        }

        @Override
        public @NotNull World getWorld() {
            return world;
        }
    }
}
