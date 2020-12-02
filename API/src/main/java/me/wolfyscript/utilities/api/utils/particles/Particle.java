package me.wolfyscript.utilities.api.utils.particles;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.scripting.ScriptUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import javax.script.Invocable;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
Contains the location, offset, ParticleEffects, etc.
 */
public class Particle {

    private WolfyUtilities wolfyUtilities;
    private NamespacedKey namespacedKey;
    private NamespacedKey superParticle;
    private org.bukkit.Particle particle;

    private String name;
    private List<String> description;
    private Material icon;
    private Class<?> dataClass;
    private Object data;
    private Vector relative, offset;
    private Integer count;
    private Double extra;
    private List<String> scripts;

    public Particle(Particle particle) {
        this.particle = particle.getParticle();
        this.superParticle = getSuperParticle();
        this.name = particle.getName();
        this.description = particle.getDescription();
        this.icon = particle.getIcon();

        this.dataClass = particle.dataClass;
        this.data = particle.data;
        this.relative = particle.getRelative();
        this.offset = particle.getOffset();
        this.count = particle.getCount();
        this.extra = particle.getExtra();
        this.scripts = particle.getScripts();
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
        this(particle, new Vector(0, 0, 0), count, extra, data);
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
        this(particle, relative, count, new Vector(0, 0, 0), extra, data);
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
        this.relative = relative;
        this.offset = offset;
        this.count = count;
        this.extra = extra;
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public void setNamespacedKey(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    public NamespacedKey getSuperParticle() {
        return superParticle;
    }

    public void setSuperParticle(NamespacedKey superParticle) {
        this.superParticle = superParticle;
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
        return scripts != null ? new ArrayList<>(scripts) : null;
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

    public void addScript(String script) {
        if (scripts == null) {
            scripts = new ArrayList<>();
        }
        scripts.add(script);
    }

    @Override
    public String toString() {
        return "Particle{" +
                "namespacedKey=" + namespacedKey +
                ", superParticle=" + superParticle +
                ", particle=" + particle +
                ", icon=" + icon +
                ", dataClass=" + dataClass +
                ", data=" + data +
                ", relative=" + relative +
                ", offset=" + offset +
                ", count=" + count +
                ", extra=" + extra +
                ", scripts=" + scripts +
                ", name='" + name + '\'' +
                ", description=" + description +
                '}';
    }

    void prepare(String referencePath) {
        if (WolfyUtilities.hasJavaXScripting() && getScripts() != null) {
            scripts.forEach(script -> {
                try {
                    if (script.startsWith("file=")) {
                        ScriptUtil.getEngine().eval(new FileReader(referencePath + File.separator + script.substring("file=".length())));
                    } else {
                        ScriptUtil.getEngine().eval(script);
                    }
                } catch (IOException | ScriptException e) {
                    wolfyUtilities.getChat().sendDebugMessage(e.getMessage());
                }
            });
        }
    }

    public void spawnOnLocation(Location location, int tick) {
        Data particleData = new Data(this);
        if (WolfyUtilities.hasJavaXScripting() && scripts != null && !scripts.isEmpty()) {
            Invocable invocable = (Invocable) ScriptUtil.getEngine();
            try {
                invocable.invokeFunction("onLocation", location, particleData, tick);
            } catch (ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        spawn(location, particleData);
    }

    public void spawnOnBlock(Block block, int tick) {
        Location location = block.getLocation();
        Data particleData = new Data(this);
        if (WolfyUtilities.hasJavaXScripting() && scripts != null && !scripts.isEmpty()) {
            Invocable invocable = (Invocable) ScriptUtil.getEngine();
            try {
                invocable.invokeFunction("onBlock", block, location, particleData, tick);
            } catch (ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        spawn(location, particleData);
    }

    public void spawnOnPlayer(Player player, EquipmentSlot slot, int tick) {
        Location location = player.getLocation();
        Data particleData = new Data(this);
        if (WolfyUtilities.hasJavaXScripting() && scripts != null && !scripts.isEmpty()) {
            Invocable invocable = (Invocable) ScriptUtil.getEngine();
            try {
                invocable.invokeFunction("onPlayer", player, slot, location, particleData, tick);
            } catch (ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        spawn(location, particleData);
    }

    private void spawn(Location location, Data particleData) {
        spawn(location, particleData.relative, particleData.count, particleData.offset, particleData.extra, particleData.data);
    }

    private void spawn(Location location, Vector relative, int count, Vector offset, double extra, @Nullable Object data) {
        Bukkit.getScheduler().runTask(wolfyUtilities.getPlugin(), () -> {
            if (location.getWorld() != null) {
                if (getDataClass() == null || !getDataClass().isInstance(data)) {
                    location.getWorld().spawnParticle(getParticle(), location.add(relative), count, offset.getX(), offset.getY(), offset.getZ(), extra);
                } else {
                    location.getWorld().spawnParticle(getParticle(), location.add(relative), count, offset.getX(), offset.getY(), offset.getZ(), extra, data);
                }
            }
        });
    }

    public void setWolfyUtilities(WolfyUtilities wolfyUtilities) {
        this.wolfyUtilities = wolfyUtilities;
    }

    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    public static class Data {

        private final Class<?> dataClass;
        private Object data;
        private Vector relative, offset;
        private int count;
        private double extra;

        public Data(Particle particle) {
            this.dataClass = particle.getDataClass();
            this.relative = new Vector(particle.getRelative().getX(), particle.getRelative().getY(), particle.getRelative().getZ());
            this.offset = new Vector(particle.getOffset().getX(), particle.getOffset().getY(), particle.getOffset().getZ());
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

        public void setOffset(Vector offset) {
            this.offset = offset;
        }

        public Vector getRelative() {
            return relative;
        }

        public void setRelative(Vector relative) {
            this.relative = relative;
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
