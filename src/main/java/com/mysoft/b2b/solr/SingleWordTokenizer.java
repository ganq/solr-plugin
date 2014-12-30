package com.mysoft.b2b.solr;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.IOException;
import java.io.Reader;

public class SingleWordTokenizer extends Tokenizer {


    /**
     * Construct a token stream processing the given input.
     *
     * @param input
     */
    public SingleWordTokenizer(Reader input) {
        super(input);
    }

    /**
     * Construct a token stream processing the given input using the given AttributeFactory.
     *
     * @param factory
     * @param input
     */
    public SingleWordTokenizer(AttributeFactory factory, Reader input) {
        super(factory, input);
    }

    private int offset = 0, bufferIndex=0, dataLen=0;
    private final static int MAX_WORD_LEN = 255;
    private final static int IO_BUFFER_SIZE = 1024;
    private final char[] buffer = new char[MAX_WORD_LEN];
    private final char[] ioBuffer = new char[IO_BUFFER_SIZE];


    private int length;
    private int start;

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

    private final void push(char c) {

        if (length == 0) start = offset-1;            // start of token
        buffer[length++] = Character.toLowerCase(c);  // buffer it

    }

    private final boolean flush() {

        if (length>0) {
            //System.out.println(new String(buffer, 0,
            //length));
            termAtt.copyBuffer(buffer, 0, length);
            offsetAtt.setOffset(correctOffset(start), correctOffset(start+length));
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();

        length = 0;
        start = offset;


        while (true) {

            final char c;
            offset++;

            if (bufferIndex >= dataLen) {
                dataLen = input.read(ioBuffer);
                bufferIndex = 0;
            }

            if (dataLen == -1) {
                offset--;
                return flush();
            } else
                c = ioBuffer[bufferIndex++];


            switch(Character.getType(c)) {

                case Character.DECIMAL_DIGIT_NUMBER:
                case Character.LOWERCASE_LETTER:
                case Character.UPPERCASE_LETTER:
                  /*  push(c);
                    if (length == MAX_WORD_LEN) return flush();
                    break;*/

                case Character.OTHER_LETTER:
                    if (length>0) {
                        bufferIndex--;
                        offset--;
                        return flush();
                    }
                    push(c);
                    return flush();

                default:
                    if (length>0) return flush();
                    break;
            }
        }
    }

    @Override
    public final void end() throws IOException {
        super.end();
        // set final offset
        final int finalOffset = correctOffset(offset);
        this.offsetAtt.setOffset(finalOffset, finalOffset);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        offset = bufferIndex = dataLen = 0;
    }}
