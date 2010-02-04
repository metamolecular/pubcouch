# A collection of convenience tasks for creating and synchronizing
# a PubCouch database.
#
# Requires JRuby on your path. Be sure to fill in your email address
# and compile the jarfile first with:
#
# $ ant jar.
#
# Usage:
# $ jruby -S rake synonyms:pull

require 'java'
Dir["lib/*.jar"].each { |jar| require jar }
Dir["build/jar/*.jar"].each { |jar| require jar }
java_import 'com.metamolecular.pubcouch.task.PullSynonyms'
java_import 'com.metamolecular.pubcouch.task.PullCompounds'

desc "Pull synonyms from FTP as abbreviated Substance Records"
namespace :synonyms do
  task :pull do
    task = PullSynonyms.new 'localhost', 'synonyms'
    task.setMaxRecords -1
    task.run
  end
end

