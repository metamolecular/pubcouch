# A collection of convenience tasks for creating and synchronizing
# a PubCouch database.
#
# Requires JRuby on your path. Be sure to fill in your email address
# and compile the jarfile first with:
# $ ant jar.
#
# Usage:
# $ jruby -S rake snapshot:pull

require 'java'
Dir["lib/*.jar"].each { |jar| require jar }
Dir["build/jar/*.jar"].each { |jar| require jar }
java_import 'com.metamolecular.pubcouch.archive.Snapshot'

# Currently just connects and prints the first structure from the record
# stream.
desc "Pulls PubChem Snapshot Structures"
namespace :snapshot do
  task :pull do
    snapshot = Snapshot.new
    snapshot.connect 'anonymous', 'me@example.com'
    streamer = snapshot.getStructures
    puts streamer.iterator.next.molfile
  end
end
