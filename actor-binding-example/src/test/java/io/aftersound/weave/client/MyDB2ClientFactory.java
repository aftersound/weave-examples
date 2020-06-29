package io.aftersound.weave.client;

import io.aftersound.weave.common.NamedType;

public class MyDB2ClientFactory implements ClientFactory<MyDB2Client> {

    public static final NamedType<Endpoint> COMPANION_CONTROL_TYPE = NamedType.of("MyDB2", Endpoint.class);
    public static final NamedType<MyDB2Client> COMPANION_PRODUCT_TYPE = NamedType.of("MyDB2", MyDB2Client.class);

    @Override
    public MyDB2Client createClient(Endpoint endpoint) {
        return new MyDB2Client();
    }

    @Override
    public void destroyClient(MyDB2Client myDB2Client) {
    }
}
