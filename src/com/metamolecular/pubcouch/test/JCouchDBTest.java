/*
 * PubCouch - A CouchDB Interface for PubChem
 *
 * Copyright (c) 2010 Metamolecular, LLC
 *
 * http://metamolecular.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.metamolecular.pubcouch.test;

import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.jcouchdb.db.Database;
import org.jcouchdb.exception.NotFoundException;

/**
 * Test to make sure we can connect to CouchDB.
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class JCouchDBTest extends TestCase
{

  public void testConnect() throws Exception
  {
    Database db = new Database("localhost", "jcouchdb");
    Map<String, String> atts = new HashMap();

    atts.put("_id", "test-document");
    atts.put("ultimate_answer", "42");

    // not sure why db.createOrUpdateDocument gives 409...
    try
    {
      db.delete(db.getDocument(HashMap.class, "test-document"));
    }
    catch (NotFoundException ignore)
    {
    }

    db.createDocument(atts);
    Map<String, String> doc = db.getDocument(HashMap.class, "test-document");

    assertEquals(doc.get("ultimate_answer"), "42");
    db.delete(doc);
    assertTrue(db.listDocuments(null, null).getRows().isEmpty());
  }
}
