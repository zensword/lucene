package com.dongnao.lucene.demo.search;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.dongnao.lucene.demo.analizer.ik.IKAnalyzer4Lucene7;

public class SearchQueryDemo {

	/**
	 * lucene 搜索查询示例
	 */
	public static void main(String[] args) throws IOException, ParseException {
		// 使用的分词器
		Analyzer analyzer = new IKAnalyzer4Lucene7(true);
		// 索引存储目录
		Directory directory = FSDirectory.open(Paths.get("f:/test/indextest"));
		// 索引读取器
		IndexReader indexReader = DirectoryReader.open(directory);
		// 索引搜索器
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		// 要搜索的字段
		String filedName = "name";

		// 1、词项查询
		Query query1 = new TermQuery(new Term(filedName, "thinkpad"));
		System.out.println("************** 词项查询 ******************");
		System.out.println("query:  " + query1.toString());
		doSearch(query1, indexSearcher);

		// 2、布尔查询
		Query query2 = new TermQuery(new Term("simpleIntro", "英特尔"));
		BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
		booleanQueryBuilder.add(query1, Occur.SHOULD);
		booleanQueryBuilder.add(query2, Occur.MUST);
		BooleanQuery booleanQuery = booleanQueryBuilder.build();

		// 可像下一行这样写
		// BooleanQuery booleanQuery = new BooleanQuery.Builder()
		// .add(query1, Occur.SHOULD).add(query2, Occur.MUST).build();

		System.out.println("************** 布尔查询 ******************");
		System.out.println("query:  " + booleanQuery.toString());
		doSearch(booleanQuery, indexSearcher);

		// 使用完毕，关闭、释放资源
		indexReader.close();
		directory.close();
	}

	private static void doSearch(Query query, IndexSearcher indexSearcher)
			throws IOException {
		// 搜索，得到TopN的结果（结果中有命中总数，topN的scoreDocs（评分文档（文档id，评分）））
		TopDocs topDocs = indexSearcher.search(query, 10); // 前10条

		System.out.println("**** 查询结果 ");
		// 获得总命中数
		System.out.println("总命中数：" + topDocs.totalHits);
		// 遍历topN结果的scoreDocs,取出文档id对应的文档信息
		for (ScoreDoc sdoc : topDocs.scoreDocs) {
			// 根据文档id取存储的文档
			Document hitDoc = indexSearcher.doc(sdoc.doc);
			System.out.println("-------------- docId=" + sdoc.doc + ",score="
					+ sdoc.score);
			// 取文档的字段
			System.out.println("prodId:" + hitDoc.get("prodId"));
			System.out.println("name:" + hitDoc.get("name"));
			System.out.println("simpleIntro:" + hitDoc.get("simpleIntro"));
			System.out.println("price:" + hitDoc.get("price"));

			System.out.println();
		}

	}
}
