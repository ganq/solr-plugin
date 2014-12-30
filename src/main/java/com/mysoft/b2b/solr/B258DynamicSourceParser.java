/**
 * Copyright mysoft Limited (c) 2014. All rights reserved.
 * This software is proprietary to and embodies the confidential
 * technology of mysoft Limited. Possession, use, or copying
 * of this software and media is authorized only pursuant to a
 * valid written license from mysoft or an authorized sublicensor.
 */
package com.mysoft.b2b.solr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.IntDocValues;
import org.apache.lucene.queries.function.valuesource.OrdFieldSource;
import org.apache.solr.common.SolrException;
import org.apache.solr.schema.IntField;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

/**
 * ganq: Change to the actual description of this class
 * 
 * @version Revision History
 * 
 *          <pre>
 * Author     Version       Date        Changes
 * ganq    1.0           2014年8月22日     Created
 * 
 * </pre>
 * @since b2b 2.0.0
 */

public class B258DynamicSourceParser extends ValueSourceParser {
	public ValueSource parse(FunctionQParser fp) throws SyntaxError {
		String field = fp.parseArg();
		ValueSource v1 = getValueSource(fp, field);
		List<ValueSource> sources = fp.parseValueSourceList();
		return new B258DynamicSource(sources.toArray(new ValueSource[sources.size()]), v1);
	}

	public ValueSource getValueSource(FunctionQParser fp, String field) {
		if (field == null)
			return null;
		SchemaField f = fp.getReq().getSchema().getField(field);
		if (f.getType().getClass() == IntField.class) {
			throw new SolrException(SolrException.ErrorCode.BAD_REQUEST,
					"Can't use ms() function on non-numeric legacy date field " + field);
		}
		return f.getType().getValueSource(f, fp);
	}
}

class B258DynamicSource extends ValueSource {
	public ValueSource[] valueSource;
	public ValueSource fieldSource;

	public B258DynamicSource(ValueSource[] valueSource, ValueSource fieldSource) {
		this.valueSource = valueSource;
		this.fieldSource = fieldSource;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
		final FunctionValues fieldVals = fieldSource.getValues(context, readerContext);
		final FunctionValues[] valsArr = new FunctionValues[valueSource.length];
		System.err.println("valueSource.length-------------" + valueSource.length);
		for (int i = 0; i < valueSource.length; i++) {
			valsArr[i] = valueSource[i].getValues(context, readerContext);
			System.err.println(valsArr[i]);
		}
		return new IntDocValues(this) {
			@Override
			public int intVal(int doc) {
				String source = fieldVals.strVal(doc);
				System.err.println("source------------------" + source);
				System.err.println("valsArr.length-----" + valsArr.length);
				System.err.println("doc----" + doc);
				for (FunctionValues fv : valsArr) {
					int ss = fv.intVal(doc);
					System.err.println("args-----" + ss);
					if (doc > 7) {
						return 2;
					}
				}
				return 1;
			}

			@Override
			public String toString(int doc) {
				return name() + '(' + fieldVals.strVal(doc) + ')';
			}
		};
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o.getClass() == OrdFieldSource.class
				&& this.fieldSource.equals(((B258DynamicSource) o).fieldSource);
	}

	@Override
	public int hashCode() {
		return B258DynamicSource.class.hashCode() + fieldSource.hashCode();
	}

	@Override
	public String description() {
		return name();
	}

	public String name() {
		return "b258dynamic";
	}
}