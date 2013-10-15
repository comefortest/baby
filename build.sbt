compileOrder in Compile := CompileOrder.Mixed

transitiveClassifiers := Seq("sources")

EclipseKeys.withSource := true

unmanagedSourceDirectories in Compile <+= baseDirectory( _ / "src" / "java" )

scalacOptions ++= Seq("-encoding", "UTF-8")