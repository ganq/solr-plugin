package com.mysoft.b2b.solr;

import com.mysoft.b2b.solr.db.ScoreReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.ValueSourceScorer;
import org.apache.lucene.queries.function.docvalues.DocTermsIndexDocValues;
import org.apache.lucene.queries.function.docvalues.DoubleDocValues;
import org.apache.lucene.queries.function.valuesource.DoubleFieldSource;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueDouble;
import org.apache.solr.schema.StrFieldSource;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;

public class SupplierValueSourceParser extends ValueSourceParser {

	public SupplierValueSourceParser() {
		super();
		System.out.println("========初始化MyValueSourceParser实例==========");
        Timer timer = new Timer();
        timer.schedule(new ScoreReader(),1000,3 * 1000 * 60);
	}

	@Override
	public ValueSource parse(FunctionQParser fp) throws SyntaxError {
		System.out.println("========开始执行MyValueSourceParser的parse方法==========");
		String field = fp.parseId();
		// 我们这里是要根据域field的值从而可以关联一个得分。我这个Field是字符串形式的。
		// StrKeywordsFieldSource
		// 是继承StrFieldSource，solr有什么类型的Filed有相应的ValueSource。
        String [] fieldAndType = field.split("\\|");
        if(fieldAndType.length != 2){
            System.out.println("=====function query error======");
            return null;
        }
        if (fieldAndType[1].equals("string")) {
            return new StrKeywordsFieldSource(fieldAndType[0]);
        }else if (fieldAndType[1].equals("double")){
            return new DoubleKeywordFiledSource(fieldAndType[0]);
        }
        return null;
	}

}
class DoubleKeywordFiledSource extends DoubleFieldSource{
    DoubleKeywordFiledSource(String field) {
        super(field);
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final FieldCache.Doubles arr = cache.getDoubles(readerContext.reader(), field, parser, true);
        final Bits valid = cache.getDocsWithField(readerContext.reader(), field);
        return new DoubleDocValues(this) {
            @Override
            public double doubleVal(int doc) {
                return arr.get(doc);
            }

            @Override
            public boolean exists(int doc) {
                return arr.get(doc) != 0 || valid.get(doc);
            }

            @Override
            public ValueSourceScorer getRangeScorer(IndexReader reader, String lowerVal, String upperVal, boolean includeLower, boolean includeUpper) {
                double lower,upper;

                if (lowerVal==null) {
                    lower = Double.NEGATIVE_INFINITY;
                } else {
                    lower = Double.parseDouble(lowerVal);
                }

                if (upperVal==null) {
                    upper = Double.POSITIVE_INFINITY;
                } else {
                    upper = Double.parseDouble(upperVal);
                }

                final double l = lower;
                final double u = upper;


                if (includeLower && includeUpper) {
                    return new ValueSourceScorer(reader, this) {
                        @Override
                        public boolean matchesValue(int doc) {
                            double docVal = doubleVal(doc);
                            return docVal >= l && docVal <= u;
                        }
                    };
                }
                else if (includeLower && !includeUpper) {
                    return new ValueSourceScorer(reader, this) {
                        @Override
                        public boolean matchesValue(int doc) {
                            double docVal = doubleVal(doc);
                            return docVal >= l && docVal < u;
                        }
                    };
                }
                else if (!includeLower && includeUpper) {
                    return new ValueSourceScorer(reader, this) {
                        @Override
                        public boolean matchesValue(int doc) {
                            double docVal = doubleVal(doc);
                            return docVal > l && docVal <= u;
                        }
                    };
                }
                else {
                    return new ValueSourceScorer(reader, this) {
                        @Override
                        public boolean matchesValue(int doc) {
                            double docVal = doubleVal(doc);
                            return docVal > l && docVal < u;
                        }
                    };
                }
            }

            @Override
            public ValueFiller getValueFiller() {
                return new ValueFiller() {
                    private final MutableValueDouble mval = new MutableValueDouble();

                    @Override
                    public MutableValue getValue() {
                        return mval;
                    }

                    @Override
                    public void fillValue(int doc) {
                        mval.value = arr.get(doc);
                        mval.exists = mval.value != 0 || valid.get(doc);
                    }
                };
            }


        };
    }
}
class StrKeywordsFieldSource extends StrFieldSource {

	// 必须的
	public StrKeywordsFieldSource(String field) {
		super(field);
		// TODO Auto-generated constructor stub
	}

	@Override
	public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
		// TODO Auto-generated method stub
		return new DocTermsIndexDocValues(this, readerContext, field) {

			@Override
			protected String toTerm(String readableValue) {
				// TODO Auto-generated method stub
				return readableValue;
			}

			@Override
			public Object objectVal(int doc) {
				// TODO Auto-generated method stub
				return strVal(doc);
			}

			@Override
			public float floatVal(int doc) {
				String val = strVal(doc);
                Integer score = ScoreReader.getSupplierScoreById(val);
                return score;
			}

			@Override
			public String toString(int doc) {
				return description() + '=' + strVal(doc);
			}

		};
	}

}
