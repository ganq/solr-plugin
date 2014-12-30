/**
 * Copyright mysoft Limited (c) 2014. All rights reserved.
 * This software is proprietary to and embodies the confidential
 * technology of mysoft Limited. Possession, use, or copying
 * of this software and media is authorized only pursuant to a
 * valid written license from mysoft or an authorized sublicensor.
 */
package com.mysoft.b2b.solr;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.util.BytesRef;

/**
 * ganq: Change to the actual description of this class
 * @version   Revision History
 * <pre>
 * Author     Version       Date        Changes
 * ganq    1.0           2014年8月7日     Created
 *
 * </pre>
 */

public class MySimilarity extends DefaultSimilarity{

	@Override
	public float tf(float freq) {
		
		//return super.tf(freq);
		return 1.0f;
	}
	
	@Override
	public float lengthNorm(FieldInvertState arg0) {
		
		//return super.lengthNorm(arg0);
		return 1.0f;
	}
	
	@Override
	public float coord(int overlap, int maxOverlap) {
		
		return super.coord(overlap, maxOverlap);
		//return 1.0f;
	}

	@Override
	public boolean getDiscountOverlaps() {
		
		return super.getDiscountOverlaps();
	}

	@Override
	public float idf(long docFreq, long numDocs) {
		
		//return super.idf(docFreq, numDocs);
		return 1.0f;
	}

	@Override
	public float queryNorm(float sumOfSquaredWeights) {
		
		//return super.queryNorm(sumOfSquaredWeights);
		return 1.0f;
	}

	@Override
	public float scorePayload(int doc, int start, int end, BytesRef payload) {
		
		return super.scorePayload(doc, start, end, payload);
	}

	@Override
	public void setDiscountOverlaps(boolean v) {
		
		super.setDiscountOverlaps(v);
	}

	@Override
	public float sloppyFreq(int distance) {
		
		return super.sloppyFreq(distance);
	}
}
