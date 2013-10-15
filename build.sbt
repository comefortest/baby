compileOrder in Compile := CompileOrder.Mixed

transitiveClassifiers := Seq("sources")

EclipseKeys.withSource := true

unmanagedSourceDirectories in Compile <+= baseDirectory( _ / "src" / "java" )