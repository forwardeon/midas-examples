import sbt._
import Keys._

object StroberBuild extends Build {
  lazy val dramsim = settingKey[Unit]("compile DRAMSim2")
  override lazy val settings = super.settings ++ Seq(
    scalaVersion := "2.11.7",
    scalacOptions ++= Seq("-deprecation","-unchecked"),
    dramsim := s"make -C strober-test ${baseDirectory.value}/strober-test/libdramsim.a".!
  )
  lazy val chisel    = project in file("riscv-mini/chisel")
  lazy val firrtl    = project in file("riscv-mini/firrtl")
  lazy val interp    = project in file("riscv-mini/interp") dependsOn firrtl
  lazy val testers   = project in file("riscv-mini/testers") dependsOn (chisel, interp)
  lazy val tutorial  = project dependsOn testers
  lazy val cde       = project in file("riscv-mini/cde") dependsOn chisel
  lazy val junctions = project in file("riscv-mini/junctions") dependsOn cde
  lazy val mini      = project in file("riscv-mini") dependsOn (junctions, testers)
  lazy val widgets   = project in file("strober/src/main/scala/widgets") dependsOn junctions
  lazy val memModel  = project in file("midas-memory-model") dependsOn widgets
  lazy val strober   = project dependsOn (memModel, testers)
  lazy val root      = project in file(".") settings (settings:_*) dependsOn (
    tutorial, mini % "compile->compile;test->test", strober)
}
