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

package com.metamolecular.pubcouch.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class RecordSet implements Iterable
{
  private BufferedReader reader;

  public RecordSet(BufferedReader reader)
  {
    this.reader = reader;
  }

  public Iterator<Record> iterator()
  {
    return new RecordIterator();
  }

  private class RecordIterator implements Iterator
  {
    public boolean hasNext()
    {
      try
      {
        reader.mark(1);
        if (reader.read() != -1)
        {
          reader.reset();
          return true;
        }
      }
      catch (IOException e)
      {
        throw new RuntimeException("Error accessing the underlying datastream.", e);
      }

      return false;
    }

    public Object next()
    {
      Record result = null;

      try
      {
        result = new Record(reader);
      }
      catch(IOException e)
      {
        throw new RuntimeException(e);
      }
      
      return result;
    }

    public void remove()
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }
}
