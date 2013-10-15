import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "baby"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "mysql" % "mysql-connector-java" % "5.1.24",
    jdbc,
    anorm)

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    templatesImport += "_root_.models.Pojos._")

}
