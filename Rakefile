require 'java'
Dir["lib/*.jar"].each { |jar| require jar }
Dir["build/jar/*.jar"].each { |jar| require jar }
java_import 'com.metamolecular.pubcouch.model.Snapshot'

desc "Pushes gem to gemcutter.org"
task :default do
  snapshot = Snapshot.new
  snapshot.connect 'anonymous', 'me@example.com'
  streamer = snapshot.getStructures
  puts streamer.iterator.getNext.molfile
end
