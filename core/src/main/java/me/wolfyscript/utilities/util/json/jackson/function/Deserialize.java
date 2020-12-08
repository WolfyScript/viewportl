package me.wolfyscript.utilities.util.json.jackson.function;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;

public interface Deserialize<T> {

    T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException;
}
