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

package com.metamolecular.pubcouch.record;

import com.metamolecular.pubcouch.filter.RecordFilter;
import java.util.Iterator;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class FilterRecordStreamer implements RecordStreamer
{
  private RecordStreamer streamer;
  private RecordFilter filter;

  public FilterRecordStreamer(RecordStreamer streamer, RecordFilter filter)
  {
    this.streamer = streamer;
    this.filter = filter;
  }
  
  public Iterator<Record> iterator()
  {
    return new RecordIterator();
  }

  private class RecordIterator implements Iterator
  {
    private Iterator<Record> iterator;
    private Record next;

    private RecordIterator()
    {
      this.iterator = streamer.iterator();
      advance();
    }

    public boolean hasNext()
    {
      return next != null;
    }

    public Object next()
    {
      Record result = next;
      advance();
      return result;
    }

    public void remove()
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    private void advance()
    {
      this.next = null;

      while (iterator.hasNext())
      {
        Record test = iterator.next();

        if (filter.pass(test))
        {
          this.next = test;
          break;
        }
      }
    }
  }
}
