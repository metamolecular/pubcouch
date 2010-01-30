{
   "_id": "_design/cids",
   "language": "javascript",
   "views": {
       "all": {
           "map": "function(doc) { if (doc.cid !== null){emit(doc.cid, doc)}}"
       },
       "byID": {
           "map": "function(doc) { emit(doc._id, doc) }"
       }
   }
}