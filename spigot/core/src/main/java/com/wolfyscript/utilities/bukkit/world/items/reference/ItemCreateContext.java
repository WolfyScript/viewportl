package com.wolfyscript.utilities.bukkit.world.items.reference;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface ItemCreateContext {

    int amount();

    default Optional<StackReference> reference() {
        return Optional.empty();
    }

    default Optional<Player> player() {
        return Optional.empty();
    }

    default Optional<World> world() {
        return Optional.empty();
    }

    /**
     * An empty implementation only containing the required values.
     *
     * @return An empty context only containing the required values
     */
    static ItemCreateContext empty(int amount) {
        return () -> amount;
    }

    static Builder of(int amount) {
        return new Builder.BuilderImpl(amount);
    }

    static Builder of(StackReference reference) {
        return of(reference.amount()).reference(reference);
    }

    /**
     * Builder to construct an ItemCreateContext
     *
     */
    interface Builder {

        Builder reference(StackReference reference);

        Builder player(Player player);

        Builder world(World world);

        ItemCreateContext build();

        class BuilderImpl implements Builder {

            private final int amount;
            private StackReference reference;
            private Player player;
            private World world;

            public BuilderImpl(int amount) {
                this.amount = amount;
            }

            @Override
            public Builder reference(StackReference reference) {
                this.reference = reference;
                return this;
            }

            @Override
            public Builder player(Player player) {
                this.player = player;
                return this;
            }

            @Override
            public Builder world(World world) {
                this.world = world;
                return this;
            }

            @Override
            public ItemCreateContext build() {
                return new ItemCreateContext() {
                    @Override
                    public int amount() {
                        return amount;
                    }

                    @Override
                    public Optional<StackReference> reference() {
                        return Optional.ofNullable(reference);
                    }

                    @Override
                    public Optional<Player> player() {
                        return Optional.ofNullable(player);
                    }

                    @Override
                    public Optional<World> world() {
                        return Optional.ofNullable(world);
                    }
                };
            }
        }

    }

}
