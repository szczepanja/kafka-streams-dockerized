import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.StreamsBuilder

import java.util.Properties

object DockerKafkaStreams extends App {

  import org.apache.kafka.streams.scala.ImplicitConversions._
  import org.apache.kafka.streams.scala.serialization.Serdes._

  val builder = new StreamsBuilder

  val inputTopic = "input"
  val lines = builder.stream[String, String](inputTopic)

  val wordCounts = lines
    .flatMapValues(textLine => textLine.split("\\W+"))
    .groupBy((_, word) => word)
    .count
    .mapValues(_.toString)

  val outputTopic = "output"
  wordCounts.toStream.to(outputTopic)

  val topology = builder.build()

  val bootstrapServers = sys.env.getOrElse("ANJA_BOOTSTRAP_SERVERS", ":9092")
  val appIdConfig = sys.env.getOrElse("APP_ID", "stream-dockerized-app")

  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, appIdConfig)
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass)
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass)

  val ks = new KafkaStreams(topology, props)

  ks.start()
}
