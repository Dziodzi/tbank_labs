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
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class LoadBalancingBenchmark {
    
    private List<KafkaProducer<String, String>> kafkaProducers = new ArrayList<>();
    private KafkaConsumer<String, String> kafkaConsumer;
    private List<RabbitMQProducer> rabbitProducers = new ArrayList<>();
    private RabbitMQConsumer rabbitConsumer;
    
    private long totalProducerLatencyKafka = 0;
    private long totalConsumerProcessingTimeKafka = 0;
    private long totalProducerLatencyRabbit = 0;
    private long totalConsumerProcessingTimeRabbit = 0;
    private long totalDeliveryDelayKafka = 0;
    private long totalDeliveryDelayRabbit = 0;
    
    @Setup(Level.Trial)
    public void setup() throws IOException {
        Properties kafkaProducerProps = new Properties();
        kafkaProducerProps.put("bootstrap.servers", "localhost:9092");
        kafkaProducerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        
        Properties kafkaConsumerProps = new Properties();
        kafkaConsumerProps.put("bootstrap.servers", "localhost:9092");
        kafkaConsumerProps.put("group.id", "test-group");
        kafkaConsumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConsumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        
        for (int i = 0; i < 3; i++) {
            kafkaProducers.add(new KafkaProducer<>(kafkaProducerProps));
            rabbitProducers.add(new RabbitMQProducer());
        }
        kafkaConsumer = new KafkaConsumer<>(kafkaConsumerProps);
        rabbitConsumer = new RabbitMQConsumer();
    }
    
    @Benchmark
    public void testKafkaLoadBalancing() {
        long startTime = System.nanoTime();
        for (KafkaProducer<String, String> producer : kafkaProducers) {
            producer.send(new ProducerRecord<>("test-topic", "key", "message"));
        }
        long producerLatency = System.nanoTime() - startTime;
        totalProducerLatencyKafka += producerLatency;
        
        long consumerStartTime = System.nanoTime();
        kafkaConsumer.subscribe(Collections.singletonList("test-topic"));
        kafkaConsumer.poll(Duration.ofMillis(1000));
        long consumerProcessingTime = System.nanoTime() - consumerStartTime;
        totalConsumerProcessingTimeKafka += consumerProcessingTime;
    }
    
    @Benchmark
    public void testRabbitMQLoadBalancing() {
        long startTime = System.nanoTime();
        for (RabbitMQProducer producer : rabbitProducers) {
            producer.send("message");
        }
        long producerLatency = System.nanoTime() - startTime;
        totalProducerLatencyRabbit += producerLatency;
        
        long consumerStartTime = System.nanoTime();
        rabbitConsumer.consume();
        long consumerProcessingTime = System.nanoTime() - consumerStartTime;
        totalConsumerProcessingTimeRabbit += consumerProcessingTime;
    }
    
    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("LoadBalancingMultipleConsumersBenchmark_report.txt", true))) {
            writer.write("Benchmark, Mode, Throughput (ops/s), Latency (ms), Consumer Processing Time (ms), Message Delivery Delay (ms)\n");
            
            writer.write(String.format("testKafkaLoadBalancing, %s, %.2f, %.2f, %.2f\n",
                    Mode.AverageTime,
                    (double) totalProducerLatencyKafka / kafkaProducers.size(),
                    (double) totalConsumerProcessingTimeKafka / kafkaProducers.size(),
                    (double) totalDeliveryDelayKafka / kafkaProducers.size()));
            
            writer.write(String.format("testRabbitMQLoadBalancing, %s, %.2f, %.2f, %.2f\n",
                    Mode.AverageTime,
                    (double) totalProducerLatencyRabbit / rabbitProducers.size(),
                    (double) totalConsumerProcessingTimeRabbit / rabbitProducers.size(),
                    (double) totalDeliveryDelayRabbit / rabbitProducers.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}