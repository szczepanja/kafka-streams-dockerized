ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "kafka-streams"
  )

libraryDependencies += "org.apache.kafka" % "kafka-streams" % "3.1.0"
libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "3.1.0"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.36"
libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.36"

enablePlugins(DockerPlugin)

docker / dockerfile := {
  val jarFile: File = (Compile / packageBin / sbt.Keys.`package`).value
  val classpath = (Compile / managedClasspath).value
  val mainclass = (Compile / packageBin / mainClass).value.getOrElse(sys.error("Expected exactly one main class"))
  val jarTarget = s"/app/${jarFile.getName}"
  val classpathString = classpath.files.map("/app/" + _.getName)
    .mkString(":") + ":" + jarTarget
  new Dockerfile {
    from("openjdk:8-jre")
    add(classpath.files, "/app/")
    add(jarFile, jarTarget)
    entryPoint("java", "-cp", classpathString, mainclass)
  }
}

docker / imageNames := Seq(
  ImageName(s"${organization.value}/${name.value}:latest"),

  ImageName(
    namespace = Some(organization.value),
    repository = name.value,
    tag = Some("v" + version.value)
  )
)