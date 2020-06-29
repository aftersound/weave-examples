package io.aftersound.weave.client;

public interface ClientFactory<CLIENT> {
    CLIENT createClient(Endpoint endpoint);
    void destroyClient(CLIENT client);
}
