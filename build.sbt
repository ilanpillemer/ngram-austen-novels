organization := "com.ilanpillemer"

name := "ngram-austen-novels"

version := "0.1.0"

scalaVersion := "2.12.3"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-target:jvm-1.8",
  "-unchecked",
  "-Ywarn-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Xlint"
)

// scalacOptions in (Compile, doc) ++= baseDirectory.map {
//   (bd: File) => Seq[String](
//      "-sourcepath", bd.getAbsolutePath,
//      "-doc-source-url", "https://github.com/mslinn/{name.value}/tree/master€{FILE_PATH}.scala"
//   )
// }.value

javacOptions ++= Seq(
  "-Xlint:deprecation",
  "-Xlint:unchecked",
  "-source", "1.8",
  "-target", "1.8",
  "-g:vars"
)

resolvers ++= Seq(
  "Hadoop Releases" at "https://repository.cloudera.com/content/repositories/releases/",
  "no fucking clue" at "https://repository.cloudera.com/artifactory/public/"
)

libraryDependencies ++= Seq(
  "junit" %  "junit" % "3.8.1" % "test",
  "org.apache.hadoop" %  "hadoop-core" % "2.6.0-mr1-cdh5.9.0",
  "org.apache.hadoop" %  "hadoop-common" % "2.7.0",
   "org.scalatest"     %% "scalatest"   % "3.0.3" % Test withSources()
  //"org.scalatest"     %% "scalatest"   % "3.0.3" % Test,
  //"junit"             %  "junit"       % "4.12"  % Test
)

logLevel := Level.Warn

// Only show warnings and errors on the screen for compilations.
// This applies to both test:compile and compile and is Info by default
logLevel in compile := Level.Warn

// Level.INFO is needed to see detailed output when running tests
logLevel in test := Level.Info

// define the statements initially evaluated when entering 'console', 'console-quick', but not 'console-project'
initialCommands in console := """
                                |""".stripMargin

cancelable := true

// sublimeTransitive := true

