import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream

import java.util.Properties

object DockerKafkaStreams extends App {

  import org.apache.kafka.streams.scala.ImplicitConversions._
  import org.apache.kafka.streams.scala.serialization.Serdes._

  val builder = new StreamsBuilder

  val lines = builder.stream[String, String]("input")

  val wordCounts = lines
    .flatMapValues(textLine => textLine.split("\\W+"))
    .groupBy((key, word) => word)
    .count
    .mapValues(_.toString)
  wordCounts.toStream.to("output")

  val topology = builder.build()

  val bootstrapServers = sys.env.getOrElse("ANJA_BOOTSTRAP_SERVERS", ":9092")
  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-dockerized-app")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass)
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass)

  val ks = new KafkaStreams(topology, props)

  ks.start()
}
