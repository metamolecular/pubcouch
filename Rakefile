# A collection of convenience tasks for creating and synchronizing
# a PubCouch database.
#
# Requires JRuby on your path. Be sure to compile the jarfile first
# with:
#
# $ ant jar.
#
# Usage:
# $ jruby -S rake synonyms:pull

require 'java'
Dir["lib/*.jar"].each { |jar| require jar }
Dir["build/jar/*.jar"].each { |jar| require jar }
java_import 'com.metamolecular.pubcouch.task.Synonyms'
java_import 'com.metamolecular.pubcouch.task.Compounds'

desc "Pull synonyms from FTP as abbreviated Substance Records"
namespace :synonyms do
  task :pull do
    task = Synonyms.new 'localhost', 'synonyms'
    task.setMaxRecords -1
    task.snapshot
  end
end

desc "Pull all compounds from FTP as as abbreviated Compound records"
namespace :compounds do
  task :snapshot do
    task = Compounds.new 'localhost', 'compounds'
    task.setMaxRecords -1
    task.snapshot 3961839
  end
end

