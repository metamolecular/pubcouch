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

# function(doc) {
#   if(doc.PUBCHEM_GENERIC_REGISTRY_NAME == '14797-55-8') {
#     emit(null, {
#       molfile: doc.MOLFILE,
#       url: doc.PUBCHEM_EXT_SUBSTANCE_URL,
#       sid: doc.PUBCHEM_SUBSTANCE_ID,
#       synonym: doc.PUBCHEM_SUBSTANCE_SYNONYM
#     });
#   }
# }

require 'java'
Dir["lib/*.jar"].each { |jar| require jar }
Dir["build/jar/*.jar"].each { |jar| require jar }
java_import 'com.metamolecular.pubcouch.pubchem.Snapshot'
java_import 'com.metamolecular.pubcouch.task.PullSynonyms'
java_import 'com.metamolecular.pubcouch.task.Pull'
java_import 'org.jcouchdb.db.Database'
java_import 'org.apache.commons.net.ftp.FTPClient'

# Format:
# submitter: organization submitting synonym
# pubchem_substance_id: PubChem substance record where synonym defined
# pubchem_compound_id: PubChem compound record containing connection table info
# uri: submitter-supplied link to record with synonym

desc "Pull synonyms from FTP as abbreviated Substance Records"
namespace :synonyms do
  task :pull do
    task = PullSynonyms.new 'localhost', 'synonyms'
    task.setMaxRecords 5000
    
    task.run
  end
end

# For now, just connect and print the pubchem substance/compound id
# for each record.
desc "Pulls PubChem Structures from FTP"
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
