package io.aftersound.weave.example;

import io.aftersound.weave.common.CommonKeyFilters;
import io.aftersound.weave.common.Dictionary;
import io.aftersound.weave.common.Key;
import io.aftersound.weave.common.parser.IntegerParser;
import io.aftersound.weave.common.parser.LongParser;
import io.aftersound.weave.common.parser.StringParser;

import java.util.Collection;
import java.util.List;

public class ConsumerConfigKeyDictionary extends Dictionary {

    public static final Key<String> GROUP_ID = Key.of(
            "group.id",
            new StringParser()
    ).description(
            "A unique string that identifies the consumer group this consumer belongs to. This property is required if "
            + "the consumer uses either the group management functionality by using <code>subscribe(topic)</code> or "
            + "the Kafka-based offset management strategy."
    ).markAsRequired();

    public static final Key<String> GROUP_INSTANCE_ID = Key.of(
            "group.instance.id",
            new StringParser()
    ).description(
            "A unique identifier of the consumer instance provided by the end user. Only non-empty strings are "
            + "permitted. If set, the consumer is treated as a static member, which means that only one instance with "
            + " this ID is allowed in the consumer group at any time. This can be used in combination with a larger "
            + " session timeout to avoid group rebalances caused by transient unavailability (e.g. process restarts). "
            + "If not set, the consumer will join the group as a dynamic member, which is the traditional behavior."
    );

    public static final Key<Integer> MAX_POLL_RECORDS = Key.of(
            "max.poll.records",
            new IntegerParser().defaultValue(500)
    ).description(
            "The maximum number of records returned in a single call to poll()."
    );

    public static final Key<Long> MAX_POLL_INTERVAL = Key.of(
            "max.poll.interval.ms",
            new LongParser().defaultValue(30000L)
    ).description(
            "The maximum delay between invocations of poll() when using "
            + "consumer group management. This places an upper bound on the amount of time that the consumer can be "
            + "idle before fetching more records. If poll() is not called before expiration of this timeout, then the "
            + "consumer is considered failed and the group will rebalance in order to reassign the partitions to "
            + "another member. For consumers using a non-null <code>group.instance.id</code> which reach this timeout, "
            + "partitions will not be immediately reassigned. Instead, the consumer will stop sending heartbeats and "
            + "partitions will be reassigned after expiration of <code>session.timeout.ms</code>. This mirrors the "
            + " behavior of a static consumer which has shutdown."
    );

    public static final Key<Long> SESSION_TIMEOUT = Key.of(
            "session.timeout.ms",
            new LongParser().defaultValue(10000L)
    ).description(
            "The timeout used to detect client failures when using "
            + "Kafka's group management facility. The client sends periodic heartbeats to indicate its liveness "
            + "to the broker. If no heartbeats are received by the broker before the expiration of this session "
            + "timeout, then the broker will remove this client from the group and initiate a rebalance. Note that the "
            + "value must be in the allowable range as configured in the broker configuration by "
            + "<code>group.min.session.timeout.ms</code> and <code>group.max.session.timeout.ms</code>."
    );

    public static final Key<String> BOOTSTRAP_SERVERS = Key.of(
            "bootstrap.servers",
            new StringParser()
    ).description(
            "A list of host/port pairs to use for establishing the initial connection to the Kafka cluster. The client "
            + "will make use of all servers irrespective of which servers are specified here for bootstrapping&mdash;"
            + "this list only impacts the initial hosts used to discover the full set of servers. This list should be "
            + "in the form <code>host1:port1,host2:port2,...</code>. Since these servers are just used for the initial "
            + " connection to discover the full cluster membership (which may change dynamically), this list need not "
            + "contain the full set of servers (you may want more than one, though, in case a server is down)."
    );

    public static final Key<Integer> METADATA_MAX_AGE = Key.of(
            "metadata.max.age.ms",
            new IntegerParser().defaultValue(30000)
    ).description(
            "The period of time in milliseconds after which we force a refresh of metadata even if we haven't seen any "
            + "partition leadership changes to proactively discover any new brokers or partitions."
    );

    public static final Key<Integer> SEND_BUFFER = Key.of(
            "send.buffer.bytes",
            new IntegerParser().defaultValue(128 * 1024)
    ).description(
            "The size of the TCP send buffer (SO_SNDBUF) to use when sending data. If the value is -1, the OS default "
            + "will be used."
    );

    public static final Key<String> TOPIC = Key.of(
            "topic",
            new StringParser()
    ).description(
            "The name of topic to consume from"
    ).markAsRequired();

    public static final Key<List<Integer>> PARTITIONS = Key.of(
            "partitions",
            new IntegerListParser(",")
    ).description(
            "The partitions of topic to consume. When missing, all partitions of specified topic will be consumed."
    );

    public static final Collection<Key<?>> CONSUMER_CONFIG_KEYS;
    static {
        lockDictionary(ConsumerConfigKeyDictionary.class);
        CONSUMER_CONFIG_KEYS = getDeclaredKeys(ConsumerConfigKeyDictionary.class, CommonKeyFilters.ANY);
    }

}
