import java.util.Properties
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.serialization.Serdes._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class DockerKS_Spec extends AnyFlatSpec with KafkaTestSetup {

  private def getStreamsConfiguration: Properties = {
    val streamsConfiguration: Properties = new Properties()

    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-test")
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, ":9092")
    streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "10000")
    streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    streamsConfiguration
  }

  def testCountWords(): Unit = {
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
  }

  it should "sort list by key" in {
    expectedWordCounts.sortBy(_.key)
  }

  it should "count occurrences of words in list" in {
    val expected = expectedWordCounts.map(_.value)
    val actual = expectedWordCounts.map(_.key.mkString("").split(" ").length)
    expected shouldBe actual
  }
}
