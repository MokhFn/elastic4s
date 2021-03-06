package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.mappings.MappingContentBuilder
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object CreateIndexContentBuilder {

  def apply(d: CreateIndexDefinition): XContentBuilder = {
    val source = XContentFactory.jsonBuilder().startObject()

    if (d.settings.settings.nonEmpty || d.analysis.nonEmpty) {
      source.startObject("settings")

      if (d.settings.settings.nonEmpty) {
        source.startObject("index")

        d.settings.settings foreach {
          case (key, value) =>
            source.field(key, value)
        }

        source.endObject()
      }

      d.analysis.foreach(_.build(source))

      source.endObject() // end settings
    }

    if (d.mappings.nonEmpty) {
      source.startObject("mappings")
      for (mapping <- d.mappings) {
        source.startObject(mapping.`type`)
        MappingContentBuilder.build(mapping, source)
        source.endObject()
      }
      source.endObject()
    }

    source.endObject()
    source
  }
}
