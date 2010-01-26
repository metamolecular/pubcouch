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
java_import 'com.metamolecular.pubcouch.pubchem.Snapshot'
java_import 'com.metamolecular.pubcouch.task.Pull'
java_import 'org.jcouchdb.db.Database'
java_import 'org.apache.commons.net.ftp.FTPClient'

# For now, just connect and print the pubchem substance/compound id
# for each record.
desc "Pulls PubChem Snapshot Structures"
namespace :snapshot do
  task :pull do
    snapshot = Snapshot.new
    snapshot.connect 'anonymous', ''
    database = Database.new 'localhost', 'pubchem'

    task = Pull.new database, snapshot.getCompounds
    task.setMaxRecords 500
    task.run
    
    # Need to do this if streamer is interrupted - don't know why
    # (yet). For now it works.
    snapshot.disconnect
    snapshot.connect 'anonymous', ''
    
    task = Pull.new database, snapshot.getSubstances
    task.setMaxRecords 500
    task.run
  end
end
