package io.github.dziodzi;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.openjdk.jmh.annotations.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(value = {Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class SimpleProducerConsumerBenchmark {
    
    private KafkaProducer<String, String> kafkaProducer;
    private KafkaConsumer<String, String> kafkaConsumer;
    private RabbitMQProducer rabbitProducer;
    private RabbitMQConsumer rabbitConsumer;
    
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
        kafkaConsumer = new KafkaConsumer<>(kafkaConsumerProps);
        
        rabbitProducer = new RabbitMQProducer();
        rabbitConsumer = new RabbitMQConsumer();
    }
    
    @Benchmark
    public void testKafkaProducerConsumer() {
        long startTime = System.nanoTime();
        kafkaProducer.send(new ProducerRecord<>("test-topic", "key", "message"));
        long producerLatency = System.nanoTime() - startTime;
        totalProducerLatencyKafka += producerLatency;
        
        long consumerStartTime = System.nanoTime();
        kafkaConsumer.subscribe(Collections.singletonList("test-topic"));
        kafkaConsumer.poll(Duration.ofMillis(1000));
        long consumerProcessingTime = System.nanoTime() - consumerStartTime;
        totalConsumerProcessingTimeKafka += consumerProcessingTime;
    }
    
    @Benchmark
    public void testRabbitMQProducerConsumer() {
        long startTime = System.nanoTime();
        rabbitProducer.send("message");
        long producerLatency = System.nanoTime() - startTime;
        totalProducerLatencyRabbit += producerLatency;
        
        long consumerStartTime = System.nanoTime();
        rabbitConsumer.consume();
        long consumerProcessingTime = System.nanoTime() - consumerStartTime;
        totalConsumerProcessingTimeRabbit += consumerProcessingTime;
    }
    
    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("SimpleProducerConsumerBenchmark_report.txt", true))) {
            writer.write("Benchmark, Mode, Throughput (ops/s), Latency (ms), Consumer Processing Time (ms)\n");
            
            writer.write(String.format("testKafkaProducerConsumer, %s, %.2f, %.2f, %.2f\n",
                    Mode.AverageTime,
                    (double) totalProducerLatencyKafka / kafkaProducer.metrics().size(),
                    (double) totalConsumerProcessingTimeKafka / 1.0,
                    (double) totalProducerLatencyKafka / 1.0));
            
            writer.write(String.format("testRabbitMQProducerConsumer, %s, %.2f, %.2f, %.2f\n",
                    Mode.AverageTime,
                    (double) totalProducerLatencyRabbit / 1.0,
                    (double) totalConsumerProcessingTimeRabbit / 1.0,
                    (double) totalProducerLatencyRabbit / 1.0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}