/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.api.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectWriter;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class SelfJsonConfig {

    @JsonIgnore
    protected File file;

    /**
     * This allows you to configure your own function to load the Object from the file.
     * The function is only called when the file exists.
     * <br>
     *
     * @param file         The file to load the config from. Must not be null!
     */
    public SelfJsonConfig(@NotNull File file) {
        this.file = file;
    }

    /**
     * @param file         The file it should be saved in.
     * @param objectWriter The Jackson {@link ObjectWriter}
     * @throws IOException If the file doesn't exist and couldn't be created.
     */
    public void save(File file, ObjectWriter objectWriter) throws IOException {
        if (file != null && (file.exists() || file.createNewFile())) {
            objectWriter.writeValue(file, this);
        } else {
            throw new IOException("Couldn't create config file on save!");
        }
    }

    /**
     * Saves the value to the current file, but a custom {@link ObjectWriter} can be used to write it to the file.
     *
     * @param objectWriter A custom Jackson {@link ObjectWriter}
     * @throws IOException If this config has no file, or the file didn't exist and couldn't be created.
     */
    public void save(ObjectWriter objectWriter) throws IOException {
        save(this.file, objectWriter);
    }

    /**
     * Saves the value to a file without pretty printing.
     *
     * @param file The file to save to.
     * @throws IOException If this config has no file, or the file didn't exist and couldn't be created.
     */
    public void save(File file) throws IOException {
        save(file, false);
    }

    /**
     * Saves the value to a file and pretty printing can be specified.
     *
     * @param file        The file to save to.
     * @param prettyPrint If the config should be pretty printed.
     * @throws IOException If this config has no file, or the file didn't exist and couldn't be created.
     */
    public void save(File file, boolean prettyPrint) throws IOException {
        save(file, JacksonUtil.getObjectWriter(prettyPrint));
    }

    /**
     * Saves the value to the current file without pretty printing.
     *
     * @throws IOException If this config has no file, or the file didn't exist and couldn't be created.
     */
    public void save() throws IOException {
        save(false);
    }

    /**
     * Saves the value to the current file. You can specify if it should be pretty printed.
     *
     * @param prettyPrint If the config should be pretty printed.
     * @throws IOException If this config has no file, or the file didn't exist and couldn't be created.
     */
    public void save(boolean prettyPrint) throws IOException {
        save(JacksonUtil.getObjectWriter(prettyPrint));
    }

}
