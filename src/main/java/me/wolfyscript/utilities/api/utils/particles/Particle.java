package me.wolfyscript.utilities.api.utils.particles;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.json.jackson.serialization.ParticleSerialization;
import me.wolfyscript.utilities.libraries.org.mozilla.javascript.Context;
import me.wolfyscript.utilities.libraries.org.mozilla.javascript.Function;
import me.wolfyscript.utilities.libraries.org.mozilla.javascript.Scriptable;
import me.wolfyscript.utilities.main.WUPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
Contains the location, offset, ParticleEffects, etc.
 */
@JsonSerialize(using = ParticleSerialization.Serializer.class)
@JsonDeserialize(using = ParticleSerialization.Deserializer.class)
public class Particle {

    private NamespacedKey namespacedKey;
    private Particle superParticle;
    private org.bukkit.Particle particle;

    private static final Scriptable scope;

    static {
        scope = Context.enter().initSafeStandardObjects();
        Context.exit();
    }

    private Material icon;
    private Class<?> dataClass;
    private Object data;
    private Vector relative, offset;
    private Integer count;
    private Double extra;
    private List<String> scripts;
    private String name;
    private List<String> description;

    public Particle(Particle particle) {
        this.dataClass = particle.dataClass;
        this.data = particle.data;
        this.relative = particle.getRelative();
        this.offset = particle.getOffset();
        this.count = particle.getCount();
        this.extra = particle.getExtra();
        this.scripts = particle.getScripts();
        this.name = particle.getName();
        this.description = particle.getDescription();
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
        this(particle, new Vector(0,0,0), count, extra, data);
    }

    public Particle(org.bukkit.Particle particle, Vector relative, int count) {
        this(particle, relative, count, 1.0, null);
    }

    public Particle(org.bukkit.Particle particle, Vector relative, int count, double extra) {
        this(particle, relative, count, 1.0, extra);
    }

    public Particle(org.bukkit.Particle particle, Vector relative, int count, Object data) {
        this(particle, relative, count, 1.0, data);
    }

    public Particle(org.bukkit.Particle particle, Vector relative, int count, double extra, Object data) {
        this(particle, relative, count, new Vector(0,0,0), extra, data);
    }

    public Particle(org.bukkit.Particle particle, Vector relative, int count, Vector offset) {
        this(particle, relative, count, offset, 1.0, null);
    }

    public Particle(org.bukkit.Particle particle, Vector relative, int count, Vector offset, double extra) {
        this(particle, relative, count, offset, extra, null);
    }

    public Particle(org.bukkit.Particle particle, Vector relative, int count, Vector offset, Object data) {
        this(particle, relative, count, offset, 1.0, data);
    }

    public Particle(org.bukkit.Particle particle, Vector relative, int count, Vector offset, double extra, Object data) {
        this.particle = particle;
        this.dataClass = particle.getDataType();
        this.scripts = new ArrayList<>();
        this.data = data;
        this.icon = Material.FIREWORK_STAR;
        this.relative = new Vector();
        this.offset = new Vector();
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
        return particle;
    }

    public void setParticle(org.bukkit.Particle particle) {
        this.particle = particle;
        this.dataClass = particle.getDataType();
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Material getIcon() {
        return this.icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getDataClass() {
        return dataClass;
    }

    public void setDataClass(Class<?> dataClass) {
        this.dataClass = dataClass;
    }

    public Vector getRelative() {
        return relative;
    }

    public void setRelative(Vector relative) {
        this.relative = relative;
    }

    public Vector getOffset() {
        return offset;
    }

    public void setOffset(Vector offset) {
        this.offset = offset;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getExtra() {
        return extra;
    }

    public void setExtra(Double extra) {
        this.extra = extra;
    }

    public List<String> getScripts() {
        return new ArrayList<>(scripts);
    }

    public void setScripts(List<String> scripts) {
        this.scripts = scripts;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
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
            scripts.forEach(script -> {
                if (script.startsWith("file=")) {
                    try {
                        FileInputStream inputStream = new FileInputStream(referencePath + File.separator + script.substring("file=".length()));
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        context.evaluateReader(scope, bufferedReader, "<cmd>", 1, null);
                        Context.exit();
                    } catch (IOException e) {
                        WUPlugin.getWolfyUtilities().sendDebugMessage(e.getMessage());
                    }
                } else {
                    context.evaluateString(scope, script, "<cmd>", 1, null);
                    Context.exit();
                }
            });
        }
    }

    public void spawnOnLocation(Location location, int tick) {
        Data particleData = new Data(this);
        if (scripts != null && !scripts.isEmpty()) {
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
        if (scripts != null && !scripts.isEmpty()) {
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
        if (scripts != null && !scripts.isEmpty()) {
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
        spawn(location, particleData.relative, particleData.count, particleData.offset, particleData.extra, particleData.data);
    }

    private void spawn(Location location, Vector relative, int count, Vector offset, double extra, @Nullable Object data) {
        Bukkit.getScheduler().runTask(WUPlugin.getInstance(), () -> {
            if (location.getWorld() != null) {
                if (getDataClass() == null || !getDataClass().isInstance(data)) {
                    location.getWorld().spawnParticle(getParticle(), location.add(relative), count, offset.getX(), offset.getY(), offset.getZ(), extra);
                } else {
                    location.getWorld().spawnParticle(getParticle(), location.add(relative), count, offset.getX(), offset.getY(), offset.getZ(), extra, data);
                }
            }
        });
    }

    public static class Data {

        private final Class<?> dataClass;
        private Object data;
        private Vector relative, offset;
        private int count;
        private double extra;

        public Data(Particle particle) {
            this.dataClass = particle.getDataClass();
            this.relative = particle.getRelative();
            this.offset = particle.getOffset();
            this.data = particle.getData();
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

        public Vector getOffset() {
            return offset;
        }

        public Vector getRelative() {
            return relative;
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
