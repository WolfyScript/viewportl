package me.wolfyscript.utilities.api.utils.particles;

import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
Contains the location, offset, ParticleEffects, etc.
 */
public class Particle {

    private NamespacedKey namespacedKey;
    private Particle superParticle;
    private org.bukkit.Particle particle;

    private static Scriptable scope;

    static {
        scope = Context.enter().initSafeStandardObjects();
        Context.exit();
    }

    private Material icon;

    private Class<?> dataClass;
    private Object data;
    private Double relativeX, relativeY, relativeZ, offsetX, offsetY, offsetZ;
    private Integer count;
    private Double extra;
    private List<String> scripts;
    private String name;
    private List<String> description;

    public Particle(Particle superParticle) {
        setSuperParticle(superParticle);
    }

    public Particle() {
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
        this(particle, 0, 0, 0, count, extra, data);
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
        this.scripts = new ArrayList<>();
        this.data = data;
        this.icon = Material.FIREWORK_STAR;
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.relativeZ = relativeZ;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.count = count;
        this.extra = extra;
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public void setNamespacedKey(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
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
        return hasParticle() || !hasSuperParticle() ? particle : getSuperParticle().getParticle();
    }

    public void setParticle(org.bukkit.Particle particle) {
        this.particle = particle;
        this.dataClass = particle.getDataType();
    }

    public Object getData() {
        return hasData() || !hasSuperParticle() ? data : getSuperParticle().getData();
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Material getIcon() {
        return hasIcon() || !hasSuperParticle() ? this.icon : getSuperParticle().getIcon();
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public String getName() {
        return hasName() || !hasSuperParticle() ? this.name : getSuperParticle().getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDescription() {
        return hasDescription() || !hasSuperParticle() ? this.description : getSuperParticle().getDescription();
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public Class<?> getDataClass() {
        return hasDataClass() || !hasSuperParticle() ? this.dataClass : getSuperParticle().getDataClass();
    }

    public Double getRelativeX() {
        return hasRelativeX() || !hasSuperParticle() ? this.relativeX : getSuperParticle().getRelativeX();
    }

    public void setRelativeX(double relativeX) {
        this.relativeX = relativeX;
    }

    public Double getRelativeY() {
        return hasRelativeY() || !hasSuperParticle() ? this.relativeY : getSuperParticle().getRelativeY();
    }

    public void setRelativeY(double relativeY) {
        this.relativeY = relativeY;
    }

    public Double getRelativeZ() {
        return hasRelativeZ() || !hasSuperParticle() ? this.relativeZ : getSuperParticle().getRelativeZ();
    }

    public void setRelativeZ(double relativeZ) {
        this.relativeZ = relativeZ;
    }

    public Double getOffsetX() {
        return hasOffsetX() || !hasSuperParticle() ? this.offsetX : getSuperParticle().getOffsetX();
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public Double getOffsetY() {
        return hasOffsetY() || !hasSuperParticle() ? this.offsetY : getSuperParticle().getOffsetY();
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public Double getOffsetZ() {
        return hasOffsetZ() || !hasSuperParticle() ? this.offsetZ : getSuperParticle().getOffsetZ();
    }

    public void setOffsetZ(double offsetZ) {
        this.offsetZ = offsetZ;
    }

    public Integer getCount() {
        return hasCount() || !hasSuperParticle() ? this.count : getSuperParticle().getCount();
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Double getExtra() {
        return hasExtra() || !hasSuperParticle() ? this.extra : getSuperParticle().getExtra();
    }

    public void setExtra(double extra) {
        this.extra = extra;
    }

    public List<String> getScripts() {
        return hasScripts() || !hasSuperParticle() ? this.scripts : getSuperParticle().getScripts();
    }

    public boolean hasIcon() {
        return this.icon != null;
    }

    public boolean hasName() {
        return this.name != null;
    }

    public boolean hasDescription() {
        return this.description != null;
    }

    public boolean hasRelativeX() {
        return this.relativeX != null;
    }

    public boolean hasRelativeY() {
        return this.relativeY != null;
    }

    public boolean hasRelativeZ() {
        return this.relativeZ != null;
    }

    public boolean hasOffsetX() {
        return this.offsetX != null;
    }

    public boolean hasOffsetY() {
        return this.offsetY != null;
    }

    public boolean hasOffsetZ() {
        return this.offsetZ != null;
    }

    public boolean hasCount() {
        return this.count != null;
    }

    public boolean hasExtra() {
        return this.extra != null;
    }

    public boolean hasParticle() {
        return this.particle != null;
    }

    public boolean hasData() {
        return this.data != null;
    }

    public boolean hasDataClass() {
        return this.dataClass != null;
    }

    public boolean hasScripts() {
        return this.scripts != null;
    }

    public void addScript(String script) {
        if (scripts == null) {
            scripts = new ArrayList<>();
        }
        scripts.add(script);
    }

    @Override
    public String toString() {
        return "Particle[" + namespacedKey + ", extends:{" + getSuperParticle() + "}, particle: " + getParticle() + ", scripts:" + getScripts() + "]";
    }

    void prepare(String referencePath) {
        Context context = Context.enter();
        if (getScripts() != null) {
            for (String script : getScripts()) {
                if (script.startsWith("file=")) {
                    try {
                        FileInputStream inputStream = new FileInputStream(referencePath + File.separator + script.substring("file=".length()));
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        context.evaluateReader(scope, bufferedReader, "<cmd>", 1, null);
                        Context.exit();
                    } catch (IOException e) {
                        Main.getMainUtil().sendDebugMessage(e.getMessage());
                    }
                } else {
                    context.evaluateString(scope, script, "<cmd>", 1, null);
                    Context.exit();
                }
            }
        }
    }

    public void spawnOnLocation(Location location, int tick) {
        Data particleData = new Data(this);
        if (getScripts() != null && !getScripts().isEmpty()) {
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
        if (getScripts() != null && !getScripts().isEmpty()) {
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
        if (getScripts() != null && !getScripts().isEmpty()) {
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
                if (data == null || getDataClass() == null || !getDataClass().isInstance(data)) {
                    location.getWorld().spawnParticle(getParticle(), location.add(x, y, z), count, offsetX, offsetY, offsetZ, extra);
                } else {
                    location.getWorld().spawnParticle(getParticle(), location.add(x, y, z), count, offsetX, offsetY, offsetZ, extra, data);
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
            this.data = particle.getData();
            this.relativeX = particle.getRelativeX() == null ? 0 : particle.getRelativeX();
            this.relativeY = particle.getRelativeY() == null ? 0 : particle.getRelativeY();
            this.relativeZ = particle.getRelativeZ() == null ? 0 : particle.getRelativeZ();
            this.offsetX = particle.getOffsetX() == null ? 0 : particle.getOffsetX();
            this.offsetY = particle.getOffsetY() == null ? 0 : particle.getOffsetY();
            this.offsetZ = particle.getOffsetZ() == null ? 0 : particle.getOffsetZ();
            this.count = particle.getCount() == null ? 1 : particle.getCount();
            this.extra = particle.getExtra() == null ? 1 : particle.getExtra();
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
