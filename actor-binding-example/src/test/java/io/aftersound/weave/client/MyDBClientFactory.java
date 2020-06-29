package io.aftersound.weave.client;

import io.aftersound.weave.common.NamedType;

public class MyDBClientFactory implements ClientFactory<MyDBClient> {

    public static final NamedType<Endpoint> COMPANION_CONTROL_TYPE = NamedType.of("MyDB", Endpoint.class);
    public static final NamedType<MyDBClient> COMPANION_PRODUCT_TYPE = NamedType.of("MyDB", MyDBClient.class);

    @Override
    public MyDBClient createClient(Endpoint endpoint) {
        return new MyDBClient();
    }

    @Override
    public void destroyClient(MyDBClient myDBClient) {
    }
}
