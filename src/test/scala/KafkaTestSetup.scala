import org.apache.kafka.streams.KeyValue

trait KafkaTestSetup {
  val inputTopic = s"inputTopic"
  val outputTopic = s"outputTopic"

  val expectedWordCounts: List[KeyValue[String, Int]] = List(
    new KeyValue("test", 1),
    new KeyValue("hello hello", 2),
    new KeyValue("join join join", 3),
  )
}