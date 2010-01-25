# A collection of convenience tasks for creating and synchronizing
# a PubCouch database.
#
# Requires JRuby on your path. Be sure to fill in your email address
# and compile the jarfile first with:
#
# $ ant jar.
#
# Usage:
# $ jruby -S rake snapshot:pull

require 'java'
Dir["lib/*.jar"].each { |jar| require jar }
Dir["build/jar/*.jar"].each { |jar| require jar }
java_import 'com.metamolecular.pubcouch.archive.Snapshot'

# For now, just connect and print the pubchem substance/compound id
# for each record.
desc "Pulls PubChem Snapshot Structures"
namespace :snapshot do
  task :pull do
    snapshot = Snapshot.new
    snapshot.connect 'anonymous', ''
    streamer = snapshot.getCompounds
    
    streamer.iterator.each do |record|
      puts record.get("PUBCHEM_COMPOUND_CID")
    end
  end
end
