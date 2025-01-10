package io.github.dziodzi;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.openjdk.jmh.annotations.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
@BenchmarkMode(value = {Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class ExtendedLoadBalancingBenchmark {

    private List<KafkaProducer<String, String>> kafkaProducers = new ArrayList<>();
    private KafkaConsumer<String, String> kafkaConsumer;
    private List<RabbitMQProducer> rabbitProducers = new ArrayList<>();
    private RabbitMQConsumer rabbitConsumer;

    private long totalProducerLatencyKafka = 0;
    private long totalConsumerProcessingTimeKafka = 0;
    private long totalProducerLatencyRabbit = 0;
    private long totalConsumerProcessingTimeRabbit = 0;

    @Setup(Level.Trial)
    public void setup() {
        String[] acksConfigs = {"0", "1", "all"};
        for (String acks : acksConfigs) {
            kafkaProducers.add(createKafkaProducer(acks));
        }

        for (int i = 0; i < 3; i++) {
            rabbitProducers.add(new RabbitMQProducer());
        }

        Properties kafkaConsumerProps = new Properties();
        kafkaConsumerProps.put("bootstrap.servers", "localhost:9092");
        kafkaConsumerProps.put("group.id", "test-group");
        kafkaConsumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConsumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConsumer = new KafkaConsumer<>(kafkaConsumerProps);

        rabbitConsumer = new RabbitMQConsumer();
    }

    private KafkaProducer<String, String> createKafkaProducer(String acks) {
        Properties kafkaProducerProps = new Properties();
        kafkaProducerProps.put("bootstrap.servers", "localhost:9092");
        kafkaProducerProps.put("acks", acks);
        kafkaProducerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<>(kafkaProducerProps);
    }

    @Benchmark
    public void testKafkaWithReplication() {
        for (KafkaProducer<String, String> producer : kafkaProducers) {
            long startTime = System.nanoTime();
            try {
                Future<RecordMetadata> future = producer.send(new ProducerRecord<>("test-topic", "key", "message"));
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            long producerLatency = System.nanoTime() - startTime;
            totalProducerLatencyKafka += producerLatency;
        }

        long consumerStartTime = System.nanoTime();
        kafkaConsumer.subscribe(Collections.singletonList("test-topic"));
        kafkaConsumer.poll(Duration.ofMillis(1000));
        long consumerProcessingTime = System.nanoTime() - consumerStartTime;
        totalConsumerProcessingTimeKafka += consumerProcessingTime;
    }

    @Benchmark
    public void testRabbitMQWithAcknowledgements() {
        long startTime = System.nanoTime();
        for (RabbitMQProducer producer : rabbitProducers) {
            producer.sendWithAck("message");
        }
        long producerLatency = System.nanoTime() - startTime;
        totalProducerLatencyRabbit += producerLatency;

        long consumerStartTime = System.nanoTime();
        rabbitConsumer.consumeWithAck();
        long consumerProcessingTime = System.nanoTime() - consumerStartTime;
        totalConsumerProcessingTimeRabbit += consumerProcessingTime;
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ExtendedLoadBalancingBenchmark_report.txt", true))) {
            writer.write("Benchmark, Mode, Throughput (ops/s), Latency (ms), Consumer Processing Time (ms)\n");

            writer.write(String.format("testKafkaWithReplication, %s, %.2f, %.2f\n",
                    Mode.AverageTime,
                    (double) totalProducerLatencyKafka / kafkaProducers.size(),
                    (double) totalConsumerProcessingTimeKafka / kafkaProducers.size()));

            writer.write(String.format("testRabbitMQWithAcknowledgements, %s, %.2f, %.2f\n",
                    Mode.AverageTime,
                    (double) totalProducerLatencyRabbit / rabbitProducers.size(),
                    (double) totalConsumerProcessingTimeRabbit / rabbitProducers.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}