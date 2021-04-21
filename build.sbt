val sparkVersion = "2.4.0-cdh6.2.0"
val hadoopVersion = "3.0.0-cdh6.2.0"
scalaVersion := "2.11.12"
lazy val commonSettings = Seq(
  offline := true,
  organization := "com.telstra.bidhadls",
  updateOptions := updateOptions.value.withCachedResolution(true),
  // Modified scala version from 2.10.5 to 2.11.12
  scalaVersion := "2.11.12",
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen"
  ),
  // For spark2 changed Cloudera resolver URL and added resolver for eigenbase
  resolvers += "Cloudera" at "https://repository.cloudera.com/cloudera/cloudera-repos/",
  resolvers += "eed3si9n-sbt-plugins" at "https://bar.t-dev.corp.telstra.com/nexus/content/repositories/sbt-plugins/",
  resolvers += "eigenbase-libraries" at "https://conjars.org/repo/",
  artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
    s"${name.value}.jar"
  },
  libraryDependencies ++= Seq(
    "junit" % "junit" % "4.12" % Test,
    "org.scalatest" %% "scalatest" % "3.0.1" % Test,
    "org.scalamock" %% "scalamock-scalatest-support" % "3.5.0" % Test,
    "eigenbase" % "eigenbase-properties" % "1.1.4"
  ),
  assemblyJarName in assembly := s"${name.value}.jar",
  assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    case PathList("javax", "xml", xs@_*) => MergeStrategy.first
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  },
  fork := true,
  test in assembly := {},
  parallelExecution in Test := false
)
lazy val metadata = (project in file("framework-adls/metadata-adls"))
  .settings(commonSettings,
    name := "metadata-adls",
    logLevel in assembly := Level.Error,
    libraryDependencies ++= Seq(
      "io.spray" %% "spray-json" % "1.3.3"
    )
  )

lazy val common = (project in file("framework-adls/common-adls"))
  .settings(commonSettings,
    name := "common-adls",
    logLevel in assembly := Level.Error,
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % Provided exclude("javax.servlet", "servlet-api"),
      "org.apache.spark" %% "spark-sql" % sparkVersion % Provided exclude("javax.servlet", "servlet-api"),
      "org.apache.hadoop" % "hadoop-hdfs" % hadoopVersion % Provided exclude("javax.servlet", "servlet-api"),
      "com.microsoft.sqlserver" % "mssql-jdbc" % "7.2.1.jre8" exclude("com.microsoft.azure", "azure-keyvault"),
      "org.apache.hadoop" % "hadoop-hdfs" % hadoopVersion % Test classifier "tests" exclude("javax.servlet", "servlet-api"),
      "org.apache.hadoop" % "hadoop-minicluster" % hadoopVersion % Test exclude("javax.servlet", "servlet-api"),
      "org.apache.hadoop" % "hadoop-common" % hadoopVersion % Test classifier "tests" exclude("javax.servlet", "servlet-api"),
      "org.apache.avro" % "avro" % "1.8.2-cdh6.2.0" % Test,
      "com.typesafe" % "config" % "1.3.1" % Test,
      "org.apache.hadoop" % "hadoop-azure-datalake" % hadoopVersion
    )
  )
  .aggregate(metadata)
  .dependsOn(metadata)
lazy val replication = (project in file("framework-adls/replication-adls"))
  .settings(commonSettings,
    name := "replication-adls",
    logLevel in assembly := Level.Error,
    libraryDependencies ++= Seq(
      "com.microsoft.sqlserver" % "mssql-jdbc" % "7.2.1.jre8",
      "joda-time" % "joda-time" % "2.9.9",
      "org.joda" % "joda-convert" % "1.8.3",
      "com.microsoft.azure" % "adal4j" % "1.6.4",
      "com.microsoft.azure" % "azure-core" % "0.9.7",
      "com.microsoft.azure" % "azure-keyvault" % "0.9.7",
      "org.bouncycastle" % "bcprov-jdk15on" % "1.51",
      "commons-codec" % "commons-codec" % "1.10",
      "commons-lang" % "commons-lang" % "2.6",
      "org.apache.commons" % "commons-lang3" % "3.3.1",
      "commons-logging" % "commons-logging" % "1.1.3",
      "com.google.code.gson" % "gson" % "2.2.4",
      "org.apache.httpcomponents" % "httpclient" % "4.3.6",
      "org.apache.httpcomponents" % "httpcore" % "4.3.3",
      "javax.inject" % "javax.inject" % "1",
      "net.jcip" % "jcip-annotations" % "1.0",
      "com.sun.jersey" % "jersey-client" % "1.19",
      "com.sun.jersey" % "jersey-json" % "1.19",
      "net.minidev" % "json-smart" % "1.1.1",
      "com.nimbusds" % "lang-tag" % "1.4",
      "javax.mail" % "mail" % "1.4.7",
      "com.nimbusds" % "nimbus-jose-jwt" % "3.1.2",
      "com.nimbusds" % "oauth2-oidc-sdk" % "4.5",
      "org.slf4j" % "slf4j-api" % "1.7.5",
      "log4j" % "log4j" % "1.2.17",
      "org.apache.spark" %% "spark-core" % sparkVersion % Provided exclude("javax.servlet", "servlet-api"),
      "org.apache.spark" %% "spark-sql" % sparkVersion % Provided exclude("javax.servlet", "servlet-api"),
      "org.apache.spark" %% "spark-hive" % sparkVersion exclude("javax.servlet", "servlet-api"),
      "org.apache.hadoop" % "hadoop-hdfs" % hadoopVersion % Provided exclude("javax.servlet", "servlet-api"),
      "org.apache.hadoop" % "hadoop-hdfs" % hadoopVersion % Test classifier "tests" exclude("javax.servlet", "servlet-api"),
      "org.apache.hadoop" % "hadoop-common" % hadoopVersion % Test classifier "tests" exclude("javax.servlet", "servlet-api"),
      "org.apache.avro" % "avro" % "1.7.6-cdh5.10.0" % Test,
      "com.typesafe" % "config" % "1.3.1" % Test,
      "com.databricks" %% "spark-csv" % "1.3.2",
      "org.apache.commons" % "commons-csv" % "1.1",
      // For spark2 added Kudu Spar dependency.
      "org.apache.kudu" %% "kudu-spark2" % "1.7.0-cdh5.16.1"
    ),
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = true),
    assemblyMergeStrategy in assembly := {
      case PathList("org", "apache", "log4j", xs@_*) => MergeStrategy.first
      case PathList("org", "apache", xs@_*) => MergeStrategy.first
      case PathList("com", "esotericsoftware", xs@_*) => MergeStrategy.first
      case PathList("org", "apache", "spark", xs@_*) => MergeStrategy.first
      case PathList("org", "apache", "spark", xs@_*) => MergeStrategy.first
      case PathList("com", "google", "guava", xs@_*) => MergeStrategy.discard
      case PathList("META-INF", xs@_*) => MergeStrategy.discard

      case x => MergeStrategy.first
    }
  )
  .aggregate(metadata)
  .dependsOn(metadata, common % "provided->provided;compile->compile;test->test")


  lazy val dna_compactor = (project in file("inbound/dna_compactor"))
  .settings(commonSettings,
    name := "dna_compactor",
    logLevel in assembly := Level.Error)
  .aggregate(metadata)
  .dependsOn(common % "provided->provided;compile->compile;test->test")

  lazy val dna = (project in file("inbound/dna"))
  .settings(commonSettings,
    name := "dna",
    logLevel in assembly := Level.Error)
  .aggregate(metadata)
  .dependsOn(common % "provided->provided;compile->compile;test->test")

  lazy val notify_compaction = (project in file("inbound/notify_compaction"))
  .settings(commonSettings,
    name := "notify_compaction",
    logLevel in assembly := Level.Error)
  .aggregate(metadata)
  .dependsOn(common % "provided->provided;compile->compile;test->test")

  lazy val notify_app_monitor = (project in file("inbound/notify_app_monitor"))
  .settings(commonSettings,
    name := "notify_app_monitor",
    logLevel in assembly := Level.Error)
  .aggregate(metadata)
  .dependsOn(common % "provided->provided;compile->compile;test->test")


lazy val application_monitor = (project in file("inbound/application_monitor"))
  .settings(commonSettings,
    name := "application_monitor",
    logLevel in assembly := Level.Error)
  .aggregate(metadata)
  .dependsOn(common % "provided->provided;compile->compile;test->test")

lazy val mnpcis = (project in file("inbound/mnpcis"))
  .settings(commonSettings,
    name := "mnpcis",
    logLevel in assembly := Level.Error)
  .aggregate(metadata)
  .dependsOn(common % "provided->provided;compile->compile;test->test")

  lazy val gethelp_compaction = (project in file("inbound/gethelp_compaction"))
  .settings(commonSettings,
    name := "gethelp_compaction",
    logLevel in assembly := Level.Error)
  .aggregate(metadata)
  .dependsOn(common % "provided->provided;compile->compile;test->test")

  lazy val gethelp_app_monitor = (project in file("inbound/gethelp_app_monitor"))
  .settings(commonSettings,
    name := "gethelp_app_monitor",
    logLevel in assembly := Level.Error)
  .aggregate(metadata)
  .dependsOn(common % "provided->provided;compile->compile;test->test")


lazy val data_prep = (project in file("inbound/data_prep"))
  .settings(commonSettings,
    name := "data_prep",
    logLevel in assembly := Level.Error)
  .aggregate(metadata)
  .dependsOn(common % "provided->provided;compile->compile;test->test")


