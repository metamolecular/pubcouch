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
package com.metamolecular.pubcouch.filter;

import com.metamolecular.pubcouch.record.Record;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class CompositeFilter implements RecordFilter
{
  private List<RecordFilter> filters;

  public CompositeFilter()
  {
    this.filters = new ArrayList();
  }

  public void addFilter(RecordFilter filter)
  {
    filters.add(filter);
  }

  public boolean pass(Record record)
  {
    for (RecordFilter filter : filters)
    {
      if (!filter.pass(record))
      {
        return false;
      }
    }

    return true;
  }

  public boolean abort()
  {
    for (RecordFilter filter : filters)
    {
      if (filter.abort())
      {
        return true;
      }
    }

    return false;
  }
}
