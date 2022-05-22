import java.util.Properties
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.serialization.Serdes._
import org.scalatest.flatspec.AnyFlatSpec

class DockerKS_Spec extends AnyFlatSpec with KafkaTestSetup {

  def testShouldCountWords(): Unit = {
    val streamsConfiguration = getStreamsConfiguration
    val builder = new StreamsBuilder
    val lines = builder.stream[String, String](inputTopic)

    val wordCounts = lines
      .flatMapValues(textLine => textLine.split("\\W+"))
      .groupBy((_, word) => word)
      .count
      .mapValues(_.toString)

    wordCounts.toStream.to(outputTopic)

    import org.apache.kafka.streams.KafkaStreams
    val streams = new KafkaStreams(builder.build(), streamsConfiguration)
    streams.start()

    streams.close()

    "testShouldCountWords" should "count words" in {
      expectedWordCounts.sortBy(_.key)
    }
  }

  private def getStreamsConfiguration: Properties = {
    val streamsConfiguration: Properties = new Properties()

    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-test")
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, ":9092")
    streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "10000")
    streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    streamsConfiguration
  }
}
