import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd, DockerChmodType}

lazy val root = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean, DockerPlugin)
  .settings(
    name := "code-challenge",
    version := "1.0.0",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice,
      javaJdbc,
      "com.h2database" % "h2" % "1.4.199",
      "org.xerial" % "sqlite-jdbc" % "3.30.1"
    ),
    testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-a", "-v"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-Werror")
  )



//Docker
daemonUser in Docker := "root"
dockerBaseImage := "openjdk:8-jre-alpine"
dockerRepository := Some("diegopiriz")
dockerExposedPorts := Seq(9000)
dockerEntrypoint := Seq("bin/%s" format executableScriptName.value)
dockerChmodType := DockerChmodType.UserGroupWriteExecute
dockerCmd := Seq("-Dpidfile.path=/dev/null", "-Dlog.file.root=/var/log", "-J-XX:+UnlockExperimentalVMOptions", "-J-XX:+UseCGroupMemoryLimitForHeap")
dockerCommands := dockerCommands.value.flatMap {
  case cmd@Cmd("FROM", _) => List(cmd, ExecCmd("RUN", "apk", "--update", "add", "bash"))
  case other => List(other)
}
