package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.testkit.{ElasticSugar, SharedElasticSugar}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.mockito.MockitoSugar

import scala.collection.JavaConverters._

class MultiGetTest extends FlatSpec with MockitoSugar with SharedElasticSugar {

  client.execute {
    createIndex("coldplay").shards(2).mappings(
      mapping("albums").fields(
        textField("name").stored(true),
        intField("year").stored(true)
      )
    )
  }.await

  client.execute(
    bulk(
      indexInto("coldplay" / "albums") id 1 fields("name" -> "parachutes", "year" -> 2000),
      indexInto("coldplay" / "albums") id 3 fields("name" -> "x&y", "year" -> 2005) ,
      indexInto("coldplay" / "albums") id 5 fields("name" -> "mylo xyloto", "year" -> 2011)  ,
      indexInto("coldplay" / "albums") id 7 fields("name" -> "ghost stories", "year" -> 2015)
    ).refresh(RefreshPolicy.IMMEDIATE)
  ).await

  blockUntilCount(4, "coldplay")

  "a multiget request" should "retrieve documents by id" in {

    val resp = client.execute(
      multiget(
        get(3).from("coldplay/albums"),
        get(5) from "coldplay/albums",
        get(7) from "coldplay/albums"
      )
    ).await

    resp.size shouldBe 3

    resp.items.head.id shouldBe "3"
    resp.items.head.exists shouldBe true

    resp.items(1).id shouldBe "5"
    resp.items(1).exists shouldBe true

    resp.items.last.id shouldBe "7"
    resp.items.last.exists shouldBe true
  }

  it should "set exists=false for missing documents" in {

    val resp = client.execute(
      multiget(
        get(3).from("coldplay/albums"),
        get(711111) from "coldplay/albums"
      )
    ).await

    resp.size shouldBe 2
    resp.items.head.exists shouldBe true
    resp.items.last.exists shouldBe false
  }

  it should "retrieve documents by id with selected fields" in {

    val resp = client.execute(
      multiget(
        get(3) from "coldplay/albums" storedFields("name", "year"),
        get(5) from "coldplay/albums" storedFields "name"
      )
    ).await

    resp.size shouldBe 2
    resp.items.head.response.fields.keySet shouldBe Set("name", "year")
    resp.items.last.response.fields.keySet shouldBe Set("name")
  }

  it should "retrieve documents by id with fetchSourceContext" in {

    val resp = client.execute(
      multiget(
        get(3) from "coldplay/albums" fetchSourceContext Seq("name", "year"),
        get(5) from "coldplay/albums" storedFields "name" fetchSourceContext Seq("name")
      )
    ).await
    assert(2 === resp.items.size)
    assert(resp.items.head.original.getResponse.getSource.asScala.keySet === Set("name", "year"))
    assert(resp.items.last.original.getResponse.getSource.asScala.keySet === Set("name"))
  }
}
