package com.dongnao.lucene.demo.analizer;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.dongnao.lucene.demo.analizer.ik.IKAnalyzer4Lucene7;

public class IkAnalyzerTestDemo {

	private static void doToken(TokenStream ts) throws IOException {
		ts.reset();
		CharTermAttribute cta = ts.getAttribute(CharTermAttribute.class);
		while (ts.incrementToken()) {
			System.out.print(cta.toString() + "|");
		}
		System.out.println();
		ts.end();
		ts.close();
	}

	public static void main(String[] args) throws IOException {
		String etext = "Analysis is one of the main causes of slow indexing. Simply put, ";
		// String chineseText = "张三说的确实在理。";
		String chineseText = "厉害了我的国一经播出，受到各方好评，强烈激发了国人的爱国之情、自豪感！";
		// IKAnalyzer 细粒度切分
		try (Analyzer ik = new IKAnalyzer4Lucene7();) {
			TokenStream ts = ik.tokenStream("content", etext);
			System.out.println("IKAnalyzer中文分词器 细粒度切分，英文分词效果：");
			doToken(ts);
			ts = ik.tokenStream("content", chineseText);
			System.out.println("IKAnalyzer中文分词器 细粒度切分，中文分词效果：");
			doToken(ts);
		}

		// IKAnalyzer 智能切分
		try (Analyzer ik = new IKAnalyzer4Lucene7(true);) {
			TokenStream ts = ik.tokenStream("content", etext);
			System.out.println("IKAnalyzer中文分词器 智能切分，英文分词效果：");
			doToken(ts);
			ts = ik.tokenStream("content", chineseText);
			System.out.println("IKAnalyzer中文分词器 智能切分，中文分词效果：");
			doToken(ts);
		}
	}
}
