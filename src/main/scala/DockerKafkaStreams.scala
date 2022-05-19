import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream

import java.util.Properties

object DockerKafkaStreams extends App {

  val builder = new StreamsBuilder

  import org.apache.kafka.streams.scala.ImplicitConversions._
  import org.apache.kafka.streams.scala.serialization.Serdes._

  val source: KStream[String, String] = builder.stream[String, String]("input")

  val solution = source.groupBy((_, value) => value).count.toStream

  source.to("output")

  val topology = builder.build()
  println(topology.describe())

  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-dockerized-app")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass)
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass)

  val ks = new KafkaStreams(topology, props)

  ks.start()
}
