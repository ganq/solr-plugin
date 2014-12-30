package com.mysoft.b2b.solr;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;

import java.io.Reader;
import java.util.Map;


public class SingleWordTokenizerFactory extends TokenizerFactory {
    public SingleWordTokenizerFactory(Map<String, String> args) {
        super(args);
        assureMatchVersion();
        if (!args.isEmpty())
            throw new IllegalArgumentException("Unknown parameters: " + args);
    }

    @Override
    public SingleWordTokenizer create(AttributeSource.AttributeFactory attributeFactory, Reader reader) {
        return new SingleWordTokenizer(attributeFactory, reader);
    }
}
