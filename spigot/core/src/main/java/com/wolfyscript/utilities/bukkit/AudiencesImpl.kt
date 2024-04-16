package com.wolfyscript.utilities.bukkit

import com.wolfyscript.utilities.platform.Audiences
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import java.util.*

class AudiencesImpl(private val core: WolfyCoreCommon) : Audiences {

    private var backingAdventure: BukkitAudiences? = null
    private val adventure : BukkitAudiences
        get() {
            checkNotNull(backingAdventure) { "Tried to access Adventure when the plugin was disabled!" }
            return backingAdventure!!
        }

    fun init() {
        this.backingAdventure = BukkitAudiences.create(core.plugin)
    }

    fun unLoad() {
        if (backingAdventure != null) {
            backingAdventure!!.close()
            backingAdventure = null
        }
    }

    override fun player(uuid: UUID): Audience {
        return adventure.player(uuid)
    }

    override fun all(): Audience {
        return adventure.all()
    }

    override fun system(): Audience {
        return adventure.console()
    }
}
