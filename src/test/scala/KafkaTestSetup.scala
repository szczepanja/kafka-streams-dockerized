import org.apache.kafka.streams.KeyValue

trait KafkaTestSetup {
  val inputTopic = s"inputTopic"
  val outputTopic = s"outputTopic"

  val inputValues = List(
    "Hello Hello Hello",
    "All streams lead to Kafka",
    "Join Kafka Summit",
    "И теперь пошли русские слова"
  )

  val expectedWordCounts: List[KeyValue[String, Int]] = List(
    new KeyValue("test", 1),
    new KeyValue("hello hello", 2),
    new KeyValue("join join join", 3),
  )
}