package com.sksamuel.elastic4s

import org.elasticsearch.common.xcontent.XContentBuilder

/** @author Stephen Samuel */
abstract class TokenFilter(val name: String) {
  def build(source: XContentBuilder): Unit = {}
}

case object ReverseTokenFilter extends TokenFilter("reverse")
case object TrimTokenFilter extends TokenFilter("trim")

case class StandardTokenFilter(override val name: String) extends TokenFilter(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "standard")
  }
}

case class AsciiFoldingTokenFilter(override val name: String) extends TokenFilter(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "asciifolding")
  }
}

case class KStemTokenFilter(override val name: String) extends TokenFilter(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "kstem")
  }
}

case class PorterStemTokenFilter(override val name: String) extends TokenFilter(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "porterStem")
  }
}

case class TruncateTokenFilter(override val name: String,
                               length: Int = 10) extends TokenFilter(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "truncate")
    source.field("length", length)
  }
}

case class LengthTokenFilter(override val name: String,
                             min: Int = 0,
                             max: Int = Integer.MAX_VALUE) extends TokenFilter(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "length")
    if (min > 0) source.field("min", min)
    if (max < Integer.MAX_VALUE) source.field("max", max)
  }
}

case class UniqueTokenFilter(override val name: String,
                             onlyOnSamePosition: Boolean = false) extends TokenFilter(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "unique")
    source.field("only_on_same_position", onlyOnSamePosition)
  }
}

case class KeywordMarkerTokenFilter(override val name: String,
                                    keywords: Iterable[String] = Nil,
                                    ignoreCase: Boolean = false) extends TokenFilter(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "keyword_marker")
    if (keywords.size > 0) source.field("keywords", keywords.toArray[String]: _*)
    if (ignoreCase) source.field("ignore_case", ignoreCase)
  }
}

case class ElisionTokenFilter(override val name: String,
                              articles: Iterable[String]) extends TokenFilter(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "elision")
    source.field("articles", articles.toArray[String]: _*)
  }
}

case class LimitTokenFilter(override val name: String,
                            maxTokenCount: Int = 1,
                            consumeAllTokens: Boolean = false) extends TokenFilter(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "stop")
    if (maxTokenCount > 1) source.field("max_token_count", maxTokenCount)
    if (consumeAllTokens) source.field("consume_all_tokens", consumeAllTokens)
  }
}

case class StopTokenFilter(override val name: String,
                           stopwords: Iterable[String] = Nil,
                           enablePositionIncrements: Boolean = true,
                           ignoreCase: Boolean = false) extends TokenFilter(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "stop")
    source.field("stopwords", stopwords.toArray[String]: _*)
    source.field("enable_position_increments", enablePositionIncrements)
    if (ignoreCase) source.field("ignore_case", ignoreCase)
  }
}

case class PatternCaptureTokenFilter(override val name: String,
                                     patterns: Iterable[String],
                                     preserveOriginal: Boolean = true) extends TokenFilter(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "pattern_capture")
    source.field("patterns", patterns.toArray[String]: _*)
    source.field("preserve_original", preserveOriginal)
  }
}

case class PatternReplaceTokenFilter(override val name: String,
                                     pattern: String,
                                     replacement: String) extends TokenFilter(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "pattern_replace")
    source.field("pattern", pattern)
    source.field("replacement", replacement)
  }
}

case class CommongGramsTokenFilter(override val name: String,
                                   commonWords: Iterable[String],
                                   ignoreCase: Boolean = false,
                                   queryMode: Boolean = false) extends TokenFilter(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "common_grams")
    source.field("common_words", commonWords.toArray[String]: _*)
    source.field("ignore_case", ignoreCase)
    source.field("query_mode", queryMode)
  }
}

case class SnowballTokenFilter(override val name: String,
                               language: String = "English") extends TokenFilter(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "snowball")
    source.field("language", language)
  }
}

case class StemmerOverrideTokenFilter(override val name: String,
                                      rules: Array[String]) extends TokenFilter(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "stemmer_override")
    source.field("rules", rules: _*)
  }
}
