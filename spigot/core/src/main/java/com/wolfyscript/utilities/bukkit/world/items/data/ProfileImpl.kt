package com.wolfyscript.utilities.bukkit.world.items.data

import com.wolfyscript.utilities.bukkit.WolfyCoreCommon
import com.wolfyscript.utilities.world.items.data.Profile
import org.bukkit.Bukkit
import org.bukkit.inventory.meta.SkullMeta
import java.net.URL
import java.util.*

class ProfileImpl(override var id: UUID?, override var name: String?, override var textures: Profile.Textures) :
    Profile {

    companion object {
        internal val ITEM_META_CONVERTER = ItemMetaDataKeyConverter<Profile>({
            if (this is SkullMeta) {
                return@ItemMetaDataKeyConverter if (WolfyCoreCommon.instance.platform.type.isPaper()) {
                    playerProfile?.let {
                        ProfileImpl(it.uniqueId, it.name, TexturesImpl(it.textures.skin, it.textures.cape))
                    }
                } else {
                    ownerProfile?.let {
                        ProfileImpl(it.uniqueId, it.name, TexturesImpl(it.textures.skin, it.textures.cape))
                    }
                }
            }
            null
        }, { profile ->
            if (this is SkullMeta) {
                if (WolfyCoreCommon.instance.platform.type.isPaper()) {
                    if (playerProfile == null) {
                        playerProfile = Bukkit.createProfile(profile.id, profile.name)
                    }
                    playerProfile?.let {
                        it.textures.skin = profile.textures.skin
                        it.textures.cape = profile.textures.cape
                    }
                } else {
                    if (ownerProfile == null) {
                        ownerProfile = Bukkit.createPlayerProfile(profile.id, profile.name)
                    }
                    ownerProfile?.let {
                        it.textures.skin = profile.textures.skin
                        it.textures.cape = profile.textures.cape
                    }
                }
            }
        })
    }

    override fun isComplete(): Boolean {
        return id != null && name != null && !textures.isEmpty()
    }

    class TexturesImpl(override var skin: URL?, override var cape: URL?) : Profile.Textures {
        override fun isEmpty(): Boolean {
            return cape == null && skin == null
        }
    }

}