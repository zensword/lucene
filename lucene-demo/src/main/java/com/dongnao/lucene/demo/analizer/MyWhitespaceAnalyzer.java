package com.dongnao.lucene.demo.analizer;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public class MyWhitespaceAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer source = new MyWhitespaceTokenizer();
		TokenStream filter = new MyLowerCaseTokenFilter(source);
		return new TokenStreamComponents(source, filter);
	}

	static class MyWhitespaceTokenizer extends Tokenizer {

		// 需要记录的属性
		// 词
		MyCharAttribute charAttr = this.addAttribute(MyCharAttribute.class);

		char[] buffer = new char[255];
		int length = 0;
		int c;

		@Override
		public boolean incrementToken() throws IOException {
			// 清除所有的词元属性
			clearAttributes();
			length = 0;
			while (true) {
				c = this.input.read();

				if (c == -1) {
					if (length > 0) {
						// 复制到charAttr
						this.charAttr.setChars(buffer, length);
						return true;
					} else {
						return false;
					}
				}

				if (Character.isWhitespace(c)) {
					if (length > 0) {
						// 复制到charAttr
						this.charAttr.setChars(buffer, length);
						return true;
					}
				}

				buffer[length++] = (char) c;
			}
		}

	}

	public static class MyLowerCaseTokenFilter extends TokenFilter {

		public MyLowerCaseTokenFilter(TokenStream input) {
			super(input);
		}

		MyCharAttribute charAttr = this.addAttribute(MyCharAttribute.class);

		@Override
		public boolean incrementToken() throws IOException {
			boolean res = this.input.incrementToken();
			if (res) {
				char[] chars = charAttr.getChars();
				int length = charAttr.getLength();
				if (length > 0) {
					for (int i = 0; i < length; i++) {
						chars[i] = Character.toLowerCase(chars[i]);
					}
				}
			}
			return res;
		}

	}

	public static interface MyCharAttribute extends Attribute {
		void setChars(char[] buffer, int length);

		char[] getChars();

		int getLength();

		String getString();
	}

	public static class MyCharAttributeImpl extends AttributeImpl
			implements MyCharAttribute {
		private char[] chatTerm = new char[255];
		private int length = 0;

		@Override
		public void setChars(char[] buffer, int length) {
			this.length = length;
			if (length > 0) {
				System.arraycopy(buffer, 0, this.chatTerm, 0, length);
			}
		}

		public char[] getChars() {
			return this.chatTerm;
		}

		public int getLength() {
			return this.length;
		}

		@Override
		public String getString() {
			if (this.length > 0) {
				return new String(this.chatTerm, 0, length);
			}
			return null;
		}

		@Override
		public void clear() {
			this.length = 0;
		}

		@Override
		public void reflectWith(AttributeReflector reflector) {

		}

		@Override
		public void copyTo(AttributeImpl target) {

		}
	}

	public static void main(String[] args) {

		String text = "An AttributeSource contains a list of different AttributeImpls, and methods to add and get them. ";

		try (Analyzer ana = new MyWhitespaceAnalyzer();
				TokenStream ts = ana.tokenStream("aa", text);) {
			MyCharAttribute ca = ts.getAttribute(MyCharAttribute.class);
			ts.reset();
			while (ts.incrementToken()) {
				System.out.print(ca.getString() + "|");
			}
			ts.end();
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
