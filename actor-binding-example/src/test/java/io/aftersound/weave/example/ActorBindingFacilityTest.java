package io.aftersound.weave.example;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.client.ClientFactory;
import io.aftersound.weave.client.Endpoint;
import io.aftersound.weave.utils.MapBuilder;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ActorBindingFacilityTest {

    /**
     * Control: Endpoint
     * Actor: io.aftersound.weave.client.ClientFactory
     * Product: client of any type
     * @throws Exception
     */
    @Test
    public void testClientFactoryBindings() throws Exception {
        ActorBindings<Endpoint, ClientFactory<?>, Object> clientFactoryBindings =
                ActorBindingsUtil.loadActorBindings(
                        Arrays.asList(
                                "io.aftersound.weave.client.MyDBClientFactory",
                                "io.aftersound.weave.client.MyDB2ClientFactory"
                        ),
                        Endpoint.class,
                        Object.class,
                        false
                );

        ActorRegistry<ClientFactory<?>> clientFactoryRegistry = new ActorFactory<>(clientFactoryBindings)
                .createActorRegistryFromBindings(false);

        // MyDBClient
        Endpoint endpoint = Endpoint.of(
                "MyDB",
                "mydb.test.client",
                MapBuilder.<String, String>hashMap().build()
        );
        Object client = clientFactoryRegistry.get(endpoint.getType()).createClient(endpoint);
        assertEquals("io.aftersound.weave.client.MyDBClient", client.getClass().getName());

        // MyDB2Client
        endpoint = Endpoint.of(
                "MyDB2",
                "mydb2.test.client",
                MapBuilder.<String, String>hashMap().build()
        );
        client = clientFactoryRegistry.get(endpoint.getType()).createClient(endpoint);
        assertEquals("io.aftersound.weave.client.MyDB2Client", client.getClass().getName());
    }

}
