// http://192.168.1.102:5984/synonyms/_view/synonyms/all?key="{_id}"
// http://192.168.1.102:5984/synonyms/_view/synonyms/by_cid?key="{cid}"
// http://192.168.1.102:5984/synonyms/_view/synonyms/by_synonym?key="{synonym}"
// http://192.168.1.102:5984/synonyms/_view/synonyms/by_sid?key="{sid}"

{
   "_id": "_design/synonyms",
   "language": "javascript",
   "views": {
       "all": {
           "map": "function(doc) { emit(doc._id, doc) }"
       },
       "by_synonym": {
           "map": "function(doc) { if (doc.synonym !== null){emit(doc.synonym, doc)}}"
       },
       "by_cid": {
           "map": "function(doc) { if (doc.cid !== null){emit(doc.cid, doc)}}"
       },
       "by_sid": {
           "map": "function(doc) { if (doc.sid !== null){emit(doc.sid, doc)}}"
       }   
   }
}