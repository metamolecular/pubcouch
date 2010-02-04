// http://192.168.1.102:5984/synonyms/_view/cids/all?key="{cid}"
{
   "_id": "_design/cids",
   "language": "javascript",
   "views": {
       "all": {
           "map": "function(doc) { emit(doc._id, doc) }"
       },
       "by_cid": {
           "map": "function(doc) { if (doc.cid !== null){emit(doc.cid, doc)}}"
       }
   }
}