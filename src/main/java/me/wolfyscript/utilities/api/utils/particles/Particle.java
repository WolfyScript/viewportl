package me.wolfyscript.utilities.api.utils.particles;

import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import javax.annotation.Nullable;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
Contains the location, offset, ParticleEffects, etc.
 */
public class Particle {

    private String name;
    private Particle superParticle;
    private org.bukkit.Particle particle;
    private Class<?> dataClass;
    private Object data;
    private double relativeX, relativeY, relativeZ, offsetX, offsetY, offsetZ;
    private int count;
    private double extra;
    private List<String> scripts = new ArrayList<>();
    private String referencePath;

    private Context context = Context.enter();
    private Scriptable scope = context.initSafeStandardObjects();

    public Particle(Particle preset) {
        this.particle = preset.getParticle();
        this.dataClass = particle.getDataType();
        if (this.dataClass.isInstance(preset.getData())) {
            this.data = preset.getData();
        } else {
            this.data = null;
        }
        this.relativeX = preset.getRelativeX();
        this.relativeY = preset.getRelativeY();
        this.relativeZ = preset.getRelativeZ();
        this.offsetX = preset.getOffsetX();
        this.offsetY = preset.getOffsetY();
        this.offsetZ = preset.getOffsetZ();
        this.count = preset.getCount();
        this.extra = preset.getExtra();
    }

    public Particle() {
        this(org.bukkit.Particle.BARRIER);
    }

    public Particle(org.bukkit.Particle particle) {
        this(particle, null);
    }

    public Particle(org.bukkit.Particle particle, Object data) {
        this(particle, 1, 1.0, data);
    }

    public Particle(org.bukkit.Particle particle, int count) {
        this(particle, count, 1.0, null);
    }

    public Particle(org.bukkit.Particle particle, int count, Object data) {
        this(particle, count, 1.0, data);
    }

    public Particle(org.bukkit.Particle particle, int count, double extra) {
        this(particle, count, extra, null);
    }

    public Particle(org.bukkit.Particle particle, int count, double extra, Object data) {
        this(particle, count, 0, 0, 0, extra, data);
    }

    public Particle(org.bukkit.Particle particle, double relativeX, double relativeY, double relativeZ, int count) {
        this(particle, relativeX, relativeY, relativeZ, count, 1.0, null);
    }

    public Particle(org.bukkit.Particle particle, double relativeX, double relativeY, double relativeZ, int count, double extra) {
        this(particle, relativeX, relativeY, relativeZ, count, 1.0, extra);
    }

    public Particle(org.bukkit.Particle particle, double relativeX, double relativeY, double relativeZ, int count, Object data) {
        this(particle, relativeX, relativeY, relativeZ, count, 1.0, data);
    }

    public Particle(org.bukkit.Particle particle, double relativeX, double relativeY, double relativeZ, int count, double extra, Object data) {
        this(particle, relativeX, relativeY, relativeZ, count, 0, 0, 0, extra, data);
    }

    public Particle(org.bukkit.Particle particle, double relativeX, double relativeY, double relativeZ, int count, double offsetX, double offsetY, double offsetZ) {
        this(particle, relativeX, relativeY, relativeZ, count, offsetX, offsetY, offsetZ, 1.0, null);
    }

    public Particle(org.bukkit.Particle particle, double relativeX, double relativeY, double relativeZ, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        this(particle, relativeX, relativeY, relativeZ, count, offsetX, offsetY, offsetZ, extra, null);
    }

    public Particle(org.bukkit.Particle particle, double relativeX, double relativeY, double relativeZ, int count, double offsetX, double offsetY, double offsetZ, Object data) {
        this(particle, relativeX, relativeY, relativeZ, count, offsetX, offsetY, offsetZ, 1.0, data);
    }

    public Particle(org.bukkit.Particle particle, double relativeX, double relativeY, double relativeZ, int count, double offsetX, double offsetY, double offsetZ, double extra, Object data) {
        this.particle = particle;
        this.dataClass = particle.getDataType();
        if (this.dataClass.isInstance(data)) {
            this.data = data;
        } else {
            this.data = null;
        }
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.relativeZ = relativeZ;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.count = count;
        this.extra = extra;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSuperParticle(Particle superParticle) {
        this.superParticle = superParticle;
    }

    public Particle getSuperParticle() {
        return superParticle;
    }

    public boolean hasSuperParticle() {
        return superParticle != null;
    }

    public org.bukkit.Particle getParticle() {
        return hasParticle() ? particle : getSuperParticle().getParticle();
    }

    public void setParticle(org.bukkit.Particle particle) {
        this.particle = particle;
    }

    public Object getData() {
        return hasData() ? data : getSuperParticle().getData();
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Class<?> getDataClass() {
        return hasDataClass() ? dataClass : getSuperParticle().getDataClass();
    }

    public double getRelativeX() {
        return hasRelativeX() ? relativeX : getSuperParticle().getRelativeX();
    }

    public void setRelativeX(double relativeX) {
        this.relativeX = relativeX;
    }

    public double getRelativeY() {
        return hasRelativeY() ? relativeY : getSuperParticle().getRelativeY();
    }

    public void setRelativeY(double relativeY) {
        this.relativeY = relativeY;
    }

    public double getRelativeZ() {
        return hasRelativeZ() ? relativeZ : getSuperParticle().getRelativeZ();
    }

    public void setRelativeZ(double relativeZ) {
        this.relativeZ = relativeZ;
    }

    public double getOffsetX() {
        return hasOffsetX() ? offsetX : getSuperParticle().getOffsetX();
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public double getOffsetY() {
        return hasOffsetY() ? offsetY : getSuperParticle().getOffsetY();
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public double getOffsetZ() {
        return hasOffsetZ() ? offsetZ : getSuperParticle().getOffsetZ();
    }

    public void setOffsetZ(double offsetZ) {
        this.offsetZ = offsetZ;
    }

    public int getCount() {
        return hasCount() ? count : getSuperParticle().getCount();
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getExtra() {
        return hasExtra() ? extra : getSuperParticle().getExtra();
    }

    public void setExtra(double extra) {
        this.extra = extra;
    }

    public List<String> getScripts() {
        return hasScripts() ? scripts : getSuperParticle().getScripts();
    }

    public boolean hasRelativeX() {
        return !hasSuperParticle() || this.relativeX != getSuperParticle().relativeX;
    }

    public boolean hasRelativeY() {
        return !hasSuperParticle() || this.relativeY != getSuperParticle().relativeY;
    }

    public boolean hasRelativeZ() {
        return !hasSuperParticle() || this.relativeZ != getSuperParticle().relativeZ;
    }

    public boolean hasOffsetX() {
        return !hasSuperParticle() || this.offsetX != getSuperParticle().offsetX;
    }

    public boolean hasOffsetY() {
        return !hasSuperParticle() || this.offsetY != getSuperParticle().offsetY;
    }

    public boolean hasOffsetZ() {
        return !hasSuperParticle() || this.offsetZ != getSuperParticle().offsetZ;
    }

    public boolean hasCount() {
        return !hasSuperParticle() || this.count != getSuperParticle().count;
    }

    public boolean hasExtra() {
        return !hasSuperParticle() || this.extra != getSuperParticle().extra;
    }

    public boolean hasParticle() {
        return !hasSuperParticle() || this.particle != getSuperParticle().particle;
    }

    public boolean hasData() {
        return !hasSuperParticle() || this.data != getSuperParticle().data;
    }

    public boolean hasDataClass() {
        return !hasSuperParticle() || this.dataClass != getSuperParticle().dataClass;
    }

    public boolean hasScripts() {
        if (!hasSuperParticle()) {
            return true;
        }
        if (this.scripts.isEmpty()) {
            return getSuperParticle().getScripts().isEmpty();
        }
        return !this.scripts.equals(getSuperParticle().scripts);
    }

    public void addScript(String script) {
        scripts.add(script);
    }

    @Override
    public String toString() {
        return "Particle[" + name + ", " + particle.name() + "]";
    }

    void prepare(String referencePath) {
        context = Context.enter();
        for (String script : getScripts()) {
            if (script.startsWith("file=")) {
                try {
                    FileInputStream inputStream = new FileInputStream(referencePath + File.separator + script.substring("file=".length()));
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    context.evaluateReader(scope, bufferedReader, "<cmd>", 1, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                context.evaluateString(scope, script, "<cmd>",1,null);
            }
        }
    }

    public void spawnOnLocation(Location location, int tick) {
        Data particleData = new Data(this);

        if (!getScripts().isEmpty()) {
            Object function = scope.get("onLocation", scope);
            if (function instanceof Function) {
                Function f = (Function) function;
                f.call(Context.enter(), scope, scope, new Object[]{location, particleData, tick});
                Context.exit();
            }
        }
        spawn(location, particleData);
    }

    public void spawnOnBlock(Block block, int tick) {
        Location location = block.getLocation();
        Data particleData = new Data(this);

        if (!getScripts().isEmpty()) {
            Object function = scope.get("onBlock", scope);
            if (function instanceof Function) {
                Function f = (Function) function;
                f.call(Context.enter(), scope, scope, new Object[]{block, location, particleData, tick});
                Context.exit();
            }
        }
        spawn(location, particleData);
    }

    public void spawnOnPlayer(Player player, EquipmentSlot slot, int tick) {
        Location location = player.getLocation();
        Data particleData = new Data(this);

        if (!getScripts().isEmpty()) {
            Object function = scope.get("onPlayer", scope);
            if (function instanceof Function) {
                Function f = (Function) function;
                f.call(Context.enter(), scope, scope, new Object[]{player, slot, location, particleData, tick});
                Context.exit();
            }
        }
        spawn(location, particleData);
    }

    private void spawn(Location location, Data particleData) {
        spawn(location, particleData.relativeX, particleData.relativeY, particleData.relativeZ, particleData.count, particleData.offsetX, particleData.offsetY, particleData.offsetZ, particleData.extra, particleData.data);
    }

    private void spawn(Location location, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable Object data) {
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            if (location.getWorld() != null) {
                if (data == null || !getDataClass().isInstance(data)) {
                    location.getWorld().spawnParticle(particle, location.add(x, y, z), count, offsetX, offsetY, offsetZ, extra);
                } else {
                    location.getWorld().spawnParticle(particle, location.add(x, y, z), count, offsetX, offsetY, offsetZ, extra, data);
                }
            }
        });
    }

    public static class Data {

        private Class<?> dataClass;
        private Object data;
        private double relativeX, relativeY, relativeZ, offsetX, offsetY, offsetZ;
        private int count;
        private double extra;

        public Data(Particle particle) {
            this.dataClass = particle.getDataClass();
            if (particle.dataClass.isInstance(particle.getData())) {
                this.data = particle.getData();
            } else {
                this.data = null;
            }
            this.relativeX = particle.getRelativeX();
            this.relativeY = particle.getRelativeY();
            this.relativeZ = particle.getRelativeZ();
            this.offsetX = particle.getOffsetX();
            this.offsetY = particle.getOffsetY();
            this.offsetZ = particle.getOffsetZ();
            this.count = particle.getCount();
            this.extra = particle.getExtra();
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            if (dataClass.isInstance(data)) {
                this.data = data;
            }
        }

        public double getRelativeX() {
            return relativeX;
        }

        public void setRelativeX(double relativeX) {
            this.relativeX = relativeX;
        }

        public double getRelativeY() {
            return relativeY;
        }

        public void setRelativeY(double relativeY) {
            this.relativeY = relativeY;
        }

        public double getRelativeZ() {
            return relativeZ;
        }

        public void setRelativeZ(double relativeZ) {
            this.relativeZ = relativeZ;
        }

        public double getOffsetX() {
            return offsetX;
        }

        public void setOffsetX(double offsetX) {
            this.offsetX = offsetX;
        }

        public double getOffsetY() {
            return offsetY;
        }

        public void setOffsetY(double offsetY) {
            this.offsetY = offsetY;
        }

        public double getOffsetZ() {
            return offsetZ;
        }

        public void setOffsetZ(double offsetZ) {
            this.offsetZ = offsetZ;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public double getExtra() {
            return extra;
        }

        public void setExtra(double extra) {
            this.extra = extra;
        }
    }
}
