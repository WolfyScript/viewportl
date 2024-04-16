package com.wolfyscript.utilities.bukkit.chat

import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.chat.Chat
import com.wolfyscript.utilities.chat.ClickActionCallback
import com.wolfyscript.utilities.language.Translations
import com.wolfyscript.utilities.platform.adapters.Player
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.regex.Pattern

/**
 * This class implements the non-deprecated features of the Chat API and
 * combines the other deprecated methods by extending the which contains the deprecated implementations.
 */
class BukkitChat(override val wolfyUtils: WolfyUtils) : Chat {

    override var chatPrefix: Component = Component.text("[" + wolfyUtils.name + "]")
    override val miniMessage: MiniMessage =
        MiniMessage.builder().tags(TagResolver.standard()) /*.debug(System.out::println)*/.build()

    private val translations: Translations = wolfyUtils.translations

    override fun sendMessage(player: Player, component: Component) {
        wolfyUtils.core.platform.audiences.player(player.uuid()).sendMessage(component)
    }

    override fun sendMessage(player: Player, prefix: Boolean, component: Component) {
        sendMessage(
            player,
            if (prefix) {
                chatPrefix.append(component)
            } else {
                component
            }
        )
    }

    override fun sendMessages(player: Player, vararg components: Component) {
        for (component in components) {
            sendMessage(player, component)
        }
    }

    override fun sendMessages(player: Player, prefix: Boolean, vararg components: Component) {
        for (component in components) {
            sendMessage(player, prefix, component)
        }
    }

    override fun translated(key: String): Component {
        return translations.getComponent(key)
    }

    override fun translated(key: String, vararg resolvers: TagResolver): Component {
        return translations.getComponent(key, *resolvers)
    }

    override fun translated(key: String, resolver: TagResolver): Component {
        return translations.getComponent(key, resolver)
    }

    override fun executable(player: Player, discard: Boolean, action: ClickActionCallback): ClickEvent {
        var id: UUID
        do {
            id = UUID.randomUUID()
        } while (CLICK_DATA_MAP.containsKey(id))
        CLICK_DATA_MAP[id] = PlayerAction(wolfyUtils, player, action, discard)
        return ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/wua $id")
    }

    /**
     * Listener for chat specific features.
     */
    class ChatListener : Listener {

        @EventHandler
        fun actionRemoval(event: PlayerQuitEvent) {
            CLICK_DATA_MAP.keys.removeIf { CLICK_DATA_MAP[it]!!.uuid == event.player.uniqueId }
        }

    }

    companion object {
        private val ADVENTURE_PLACEHOLDER_PATTERN: Pattern = Pattern.compile("([!?#]?)([a-z0-9_-]*)")
        private val LEGACY_PLACEHOLDER_PATTERN: Pattern = Pattern.compile("%([^%]+)%")
        private val CLICK_DATA_MAP: MutableMap<UUID, PlayerAction> = HashMap()

        @JvmStatic
        fun removeClickData(uuid: UUID) {
            CLICK_DATA_MAP.remove(uuid)
        }

        @JvmStatic
        fun getClickData(uuid: UUID): PlayerAction? {
            return CLICK_DATA_MAP[uuid]
        }
    }
}
