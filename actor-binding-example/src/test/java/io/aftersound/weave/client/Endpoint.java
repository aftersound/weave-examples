package io.aftersound.weave.client;

import io.aftersound.weave.metadata.Control;

import java.util.Map;

public final class Endpoint implements Control {

    private String type;
    private String id;
    private Map<String, String> options;

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public static Endpoint of(String type, String id, Map<String, String> options) {
        Endpoint endpoint = new Endpoint();
        endpoint.setType(type);
        endpoint.setId(id);
        endpoint.setOptions(options);
        return endpoint;
    }
}
