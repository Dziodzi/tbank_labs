package io.github.dziodzi;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.openjdk.jmh.annotations.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(value = {Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class MultipleConsumersBenchmark {
    
    private KafkaProducer<String, String> kafkaProducer;
    private List<KafkaConsumer<String, String>> kafkaConsumers = new ArrayList<>();
    private RabbitMQProducer rabbitProducer;
    private List<RabbitMQConsumer> rabbitConsumers = new ArrayList<>();
    
    private long totalProducerLatencyKafka = 0;
    private long totalConsumerProcessingTimeKafka = 0;
    private long totalProducerLatencyRabbit = 0;
    private long totalConsumerProcessingTimeRabbit = 0;
    
    @Setup(Level.Trial)
    public void setup() {
        Properties kafkaProducerProps = new Properties();
        kafkaProducerProps.put("bootstrap.servers", "localhost:9092");
        kafkaProducerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        
        Properties kafkaConsumerProps = new Properties();
        kafkaConsumerProps.put("bootstrap.servers", "localhost:9092");
        kafkaConsumerProps.put("group.id", "test-group");
        kafkaConsumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConsumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        
        kafkaProducer = new KafkaProducer<>(kafkaProducerProps);
        for (int i = 0; i < 3; i++) {
            kafkaConsumers.add(new KafkaConsumer<>(kafkaConsumerProps));
        }
        rabbitProducer = new RabbitMQProducer();
        for (int i = 0; i < 3; i++) {
            rabbitConsumers.add(new RabbitMQConsumer());
        }
    }
    
    @Benchmark
    public void testKafkaMultipleConsumers() {
        long startTime = System.nanoTime();
        kafkaProducer.send(new ProducerRecord<>("test-topic", "key", "message"));
        long producerLatency = System.nanoTime() - startTime;
        totalProducerLatencyKafka += producerLatency;
        
        long consumerStartTime = System.nanoTime();
        for (KafkaConsumer<String, String> consumer : kafkaConsumers) {
            consumer.subscribe(Collections.singletonList("test-topic"));
            consumer.poll(Duration.ofMillis(1000));
        }
        long consumerProcessingTime = System.nanoTime() - consumerStartTime;
        totalConsumerProcessingTimeKafka += consumerProcessingTime;
    }
    
    @Benchmark
    public void testRabbitMQMultipleConsumers() {
        long startTime = System.nanoTime();
        rabbitProducer.send("message");
        long producerLatency = System.nanoTime() - startTime;
        totalProducerLatencyRabbit += producerLatency;
        
        long consumerStartTime = System.nanoTime();
        for (RabbitMQConsumer consumer : rabbitConsumers) {
            consumer.consume();
        }
        long consumerProcessingTime = System.nanoTime() - consumerStartTime;
        totalConsumerProcessingTimeRabbit += consumerProcessingTime;
    }
    
    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("MultipleConsumersBenchmark_report.txt", true))) {
            writer.write("Benchmark, Mode, Throughput (ops/s), Latency (ms), Consumer Processing Time (ms)\n");
            
            writer.write(String.format("testKafkaMultipleConsumers, %s, %f, %f, %f\n",
                    Mode.AverageTime,
                    (double) totalProducerLatencyKafka / kafkaProducer.metrics().size(),
                    (double) totalConsumerProcessingTimeKafka / kafkaConsumers.size(),
                    (double) totalProducerLatencyKafka / kafkaConsumers.size()));
            
            writer.write(String.format("testRabbitMQMultipleConsumers, %s, %f, %f, %f\n",
                    Mode.AverageTime,
                    (double) totalProducerLatencyRabbit / 1,
                    (double) totalConsumerProcessingTimeRabbit / rabbitConsumers.size(),
                    (double) totalProducerLatencyRabbit / rabbitConsumers.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}