resolvers ++= Seq(
  "repo.novus snaps" at "http://repo.novus.com/snapshots/",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Guardian Github Releases" at "http://guardian.github.com/maven/repo-releases"
)

libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.8.3",
  "net.liftweb" %% "lift-json" % "2.2"
)