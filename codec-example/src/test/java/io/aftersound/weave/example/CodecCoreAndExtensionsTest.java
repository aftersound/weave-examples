package io.aftersound.weave.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.codec.*;
import io.aftersound.weave.pojo.Name;
import io.aftersound.weave.utils.MapBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

public class CodecCoreAndExtensionsTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static ActorRegistry<CodecFactory> codecFactoryRegistry;

    @BeforeClass
    public static void init() throws Exception {
        ActorBindings<CodecControl, io.aftersound.weave.codec.CodecFactory, Codec> codecFactoryBindings = ActorBindingsUtil.loadActorBindings(
                Arrays.asList(
                        "io.aftersound.weave.codec.jackson.JacksonCodecFactory",
                        "io.aftersound.weave.codec.jackson.SmileJacksonCodecFactory",
                        "io.aftersound.weave.codec.jackson.BsonJacksonCodecFactory"
                ),
                CodecControl.class,
                Codec.class,
                false
        );
        codecFactoryRegistry = new ActorFactory<>(codecFactoryBindings).createActorRegistryFromBindings(false);
    }

    @AfterClass
    public static void destroy() {
        codecFactoryRegistry = null;
    }

    private String getJson() throws Exception {
        Map<String, String> map = MapBuilder.hashMap()
                .kv("firstName", "9")
                .kv("lastName", "Falcon")
                .build();
        return MAPPER.writeValueAsString(map);
    }

    private JsonNode getJsonNode() throws Exception {
        Map<String, String> map = MapBuilder.hashMap()
                .kv("firstName", "9")
                .kv("lastName", "Falcon")
                .build();
        String json = MAPPER.writeValueAsString(map);
        return MAPPER.readTree(json);
    }

    private Name getName() {
        Name name = new Name();
        name.setFirstName("9");
        name.setLastName("Falcon");
        return name;
    }

    @Test(expected = CodecException.class)
    public void testNonLoadedCodec() {
        Codec.REGISTRY.get().getCodec("ThriftCodec(io.aftersound.weave.thrift.Name,Compact)", codecFactoryRegistry);
    }

    @Test
    public void testClassJsonCodec1() throws Exception {
        final String json = getJson();

        Codec<Object, Object> codec = Codec.REGISTRY.get().getCodec("JacksonCodec(io.aftersound.weave.pojo.Name,Json)", codecFactoryRegistry);

        // assume json is read from persistence layer
        // decode
        Object decoded = codec.decoder().decode(json);
        assertTrue(decoded instanceof Name);
        Name name = (Name) decoded;
        assertEquals("9", name.getFirstName());
        assertEquals("Falcon", name.getLastName());

        // encode
        Object encoded = codec.encoder().encode(decoded);
        // persist
        assertTrue(encoded instanceof String);
        assertEquals(json, encoded);
    }

    @Test
    public void testClassJsonCodec2() throws Exception {
        final String json = getJson();

        Codec<Name, String> codec = Codec.REGISTRY.get().getCodec("JacksonCodec(io.aftersound.weave.pojo.Name,Json)", codecFactoryRegistry);

        // assume json is read from persistence layer
        // decode
        Name decoded = codec.decoder().decode(json);
        assertEquals("9", decoded.getFirstName());
        assertEquals("Falcon", decoded.getLastName());

        // encode
        String encoded = codec.encoder().encode(decoded);
        // persist
        assertEquals(json, encoded);
    }

    @Test
    public void testClassSmileCodec1() throws Exception {
        final Name name = getName();

        Codec<Object, Object> codec = Codec.REGISTRY.get().getCodec("JacksonCodec(io.aftersound.weave.pojo.Name,Smile)", codecFactoryRegistry);

        // encode
        Object encoded = codec.encoder().encode(name);
        // persist
        assertTrue(encoded instanceof byte[]);

        // read from persistence
        // decode
        Object decoded = codec.decoder().decode(encoded);
        assertTrue(decoded instanceof Name);
        Name decodedName = (Name) decoded;
        assertEquals("9", decodedName.getFirstName());
        assertEquals("Falcon", decodedName.getLastName());
    }

    @Test
    public void testClassSmileCodec2() throws Exception {
        final Name name = getName();

        Codec<Name, byte[]> codec = Codec.REGISTRY.get().getCodec("JacksonCodec(io.aftersound.weave.pojo.Name,Smile)", codecFactoryRegistry);

        // encode
        byte[] encoded = codec.encoder().encode(name);

        // read from persistence
        // decode
        Name decoded = codec.decoder().decode(encoded);
        assertEquals("9", decoded.getFirstName());
        assertEquals("Falcon", decoded.getLastName());

        // prove data encoded in Smile cannot be decoded by other codec
        DecodeException decodeException = null;
        try {
            Codec<JsonNode, byte[]> otherCodec = Codec.REGISTRY.get().getCodec("JacksonCodec(JsonNode,Bson)", codecFactoryRegistry);
            otherCodec.decoder().decode(encoded);
        } catch (DecodeException e) {
            decodeException = e;
        }
        assertNotNull(decodeException);
    }

    @Test
    public void testClassBsonCodec1() throws Exception {
        final Name name = getName();

        Codec<Object, Object> codec = Codec.REGISTRY.get().getCodec("JacksonCodec(io.aftersound.weave.pojo.Name,Bson)", codecFactoryRegistry);

        // encode
        Object encoded = codec.encoder().encode(name);
        // persist
        assertTrue(encoded instanceof byte[]);

        // read from persistence
        // decode
        Object decoded = codec.decoder().decode(encoded);
        assertTrue(decoded instanceof Name);
        Name decodedName = (Name) decoded;
        assertEquals("9", decodedName.getFirstName());
        assertEquals("Falcon", decodedName.getLastName());
    }

    @Test
    public void testClassBsonCodec2() throws Exception {
        final Name name = getName();

        Codec<Name, byte[]> codec = Codec.REGISTRY.get().getCodec("JacksonCodec(io.aftersound.weave.pojo.Name,Bson)", codecFactoryRegistry);

        // encode
        byte[] encoded = codec.encoder().encode(name);

        // read from persistence
        // decode
        Name decoded = codec.decoder().decode(encoded);
        assertEquals("9", decoded.getFirstName());
        assertEquals("Falcon", decoded.getLastName());

        // prove data encoded in Bson cannot be decoded by other codec
        DecodeException decodeException = null;
        try {
            Codec<JsonNode, byte[]> otherCodec = Codec.REGISTRY.get().getCodec("JacksonCodec(JsonNode,Smile)", codecFactoryRegistry);
            otherCodec.decoder().decode(encoded);
        } catch (DecodeException e) {
            decodeException = e;
        }
        assertNotNull(decodeException);
    }

    @Test
    public void testJsonNodeJsonCodec1() throws Exception {
        final JsonNode jsonNode = getJsonNode();

        Codec<Object, Object> codec = Codec.REGISTRY.get().getCodec("JacksonCodec(JsonNode,Json)", codecFactoryRegistry);

        // encode as json in string form
        Object encoded = codec.encoder().encode(jsonNode);
        assertTrue(encoded instanceof String);
        // persist

        // read from persistence
        // decode
        Object decoded = codec.decoder().decode(encoded);
        assertTrue(decoded instanceof JsonNode);
        JsonNode decodedJsonNode = (JsonNode) decoded;

        assertEquals("9", decodedJsonNode.get("firstName").asText());
        assertEquals("Falcon", decodedJsonNode.get("lastName").asText());
    }

    @Test
    public void testJsonNodeJsonCodec2() throws Exception {
        final JsonNode jsonNode = getJsonNode();

        Codec<JsonNode, String> codec = Codec.REGISTRY.get().getCodec("JacksonCodec(JsonNode,Json)", codecFactoryRegistry);

        // encode as json in string form
        String encoded = codec.encoder().encode(jsonNode);
        // persist

        // read from persistence
        // decode
        JsonNode decoded = codec.decoder().decode(encoded);

        assertEquals("9", decoded.get("firstName").asText());
        assertEquals("Falcon", decoded.get("lastName").asText());
    }

    @Test
    public void testJsonNodeSmileCodec1() throws Exception {
        final JsonNode jsonNode = getJsonNode();

        Codec<Object, Object> codec = Codec.REGISTRY.get().getCodec("JacksonCodec(JsonNode,Smile)", codecFactoryRegistry);

        // encode as json in string form
        Object encoded = codec.encoder().encode(jsonNode);
        assertTrue(encoded instanceof byte[]);
        // persist

        // read from persistence
        // decode
        Object decoded = codec.decoder().decode(encoded);
        assertTrue(decoded instanceof JsonNode);
        JsonNode decodedJsonNode = (JsonNode) decoded;

        assertEquals("9", decodedJsonNode.get("firstName").asText());
        assertEquals("Falcon", decodedJsonNode.get("lastName").asText());
    }

    @Test
    public void testJsonNodeSmileCodec2() throws Exception {
        final JsonNode jsonNode = getJsonNode();

        Codec<JsonNode, byte[]> codec = Codec.REGISTRY.get().getCodec("JacksonCodec(JsonNode,Smile)", codecFactoryRegistry);

        // encode as json in string form
        byte[] encoded = codec.encoder().encode(jsonNode);
        // persist

        // read from persistence
        // decode
        JsonNode decoded = codec.decoder().decode(encoded);

        assertEquals("9", decoded.get("firstName").asText());
        assertEquals("Falcon", decoded.get("lastName").asText());

        // prove data encoded in Smile cannot be decoded by other codec
        DecodeException decodeException = null;
        try {
            Codec<JsonNode, byte[]> otherCodec = Codec.REGISTRY.get().getCodec("JacksonCodec(JsonNode,Bson)", codecFactoryRegistry);
            otherCodec.decoder().decode(encoded);
        } catch (DecodeException e) {
            decodeException = e;
        }
        assertNotNull(decodeException);
    }

    @Test
    public void testJsonNodeBsonCodec1() throws Exception {
        final JsonNode jsonNode = getJsonNode();

        Codec<Object, Object> codec = Codec.REGISTRY.get().getCodec("JacksonCodec(JsonNode,Bson)", codecFactoryRegistry);

        // encode as json in string form
        Object encoded = codec.encoder().encode(jsonNode);
        assertTrue(encoded instanceof byte[]);
        // persist

        // read from persistence
        // decode
        Object decoded = codec.decoder().decode(encoded);
        assertTrue(decoded instanceof JsonNode);
        JsonNode decodedJsonNode = (JsonNode) decoded;

        assertEquals("9", decodedJsonNode.get("firstName").asText());
        assertEquals("Falcon", decodedJsonNode.get("lastName").asText());
    }

    @Test
    public void testJsonNodeBsonCodec2() throws Exception {
        final JsonNode jsonNode = getJsonNode();

        Codec<JsonNode, byte[]> codec = Codec.REGISTRY.get().getCodec("JacksonCodec(JsonNode,Bson)", codecFactoryRegistry);

        // encode as json in string form
        byte[] encoded = codec.encoder().encode(jsonNode);
        // persist

        // read from persistence
        // decode
        JsonNode decoded = codec.decoder().decode(encoded);

        assertEquals("9", decoded.get("firstName").asText());
        assertEquals("Falcon", decoded.get("lastName").asText());

        // prove data encoded in Bson cannot be decoded by other codec
        DecodeException decodeException = null;
        try {
            Codec<JsonNode, byte[]> otherCodec = Codec.REGISTRY.get().getCodec("JacksonCodec(JsonNode,Smile)", codecFactoryRegistry);
            otherCodec.decoder().decode(encoded);
        } catch (DecodeException e) {
            decodeException = e;
        }
        assertNotNull(decodeException);
    }
}