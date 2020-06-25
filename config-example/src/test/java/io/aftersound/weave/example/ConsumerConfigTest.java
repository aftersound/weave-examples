package io.aftersound.weave.example;

import io.aftersound.weave.config.ConfigException;
import io.aftersound.weave.config.ConfigUtils;
import io.aftersound.weave.config.Settings;
import io.aftersound.weave.utils.MapBuilder;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static io.aftersound.weave.example.ConsumerConfigKeyDictionary.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConsumerConfigTest {

    @Test
    public void testConsumerConfig() {
        // good config
        Map<String, String> configSource = MapBuilder.hashMap()
                .kv("group.id", "my.kakfa.consumer")
                .kv("group.instance.id", "my.kafka.consumer.instance")
                .kv("max.poll.records", "1000")
                .kv("max.poll.interval.ms", "45000")
                .kv("session.timeout.ms", "20000")
                .kv("bootstrap.servers", "192.168.0.1:2181,192.168.0.10:2181")
                .kv("topic", "twitter")
                .kv("partitions", "1,3,5,7")
                .build();

        Settings config = Settings.from(
                ConfigUtils.extractConfig(
                        configSource,
                        CONSUMER_CONFIG_KEYS
                )
        );

        assertEquals("my.kakfa.consumer", config.v(GROUP_ID));
        assertEquals("my.kafka.consumer.instance", config.v(GROUP_INSTANCE_ID));
        assertEquals(1000, config.v(MAX_POLL_RECORDS).intValue());
        assertEquals(45000L, config.v(MAX_POLL_INTERVAL).longValue());
        assertEquals(20000L, config.v(SESSION_TIMEOUT).longValue());
        assertEquals("192.168.0.1:2181,192.168.0.10:2181", config.v(BOOTSTRAP_SERVERS));
        assertEquals(30000, config.v(METADATA_MAX_AGE).intValue());
        assertEquals(128 * 1024, config.v(SEND_BUFFER).intValue());
        List<Integer> partitions = config.v(PARTITIONS);
        assertEquals(4, partitions.size());
        assertEquals(1, partitions.get(0).intValue());
        assertEquals(3, partitions.get(1).intValue());
        assertEquals(5, partitions.get(2).intValue());
        assertEquals(7, partitions.get(3).intValue());

        // config missing required
        configSource = MapBuilder.hashMap()
                .kv("max.poll.records", "1000")
                .kv("max.poll.interval.ms", "45000")
                .kv("session.timeout.ms", "20000")
                .kv("bootstrap.servers", "192.168.0.1:2181,192.168.0.10:2181")
                .build();

        ConfigException ce = null;
        try {
            Settings.from(
                    ConfigUtils.extractConfig(
                            configSource,
                            CONSUMER_CONFIG_KEYS
                    )
            );
        } catch (ConfigException e) {
            ce = e;
        }
        assertNotNull(ce);
    }

}
