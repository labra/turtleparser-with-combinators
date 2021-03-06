package es.weso.rdf.turtle.parser

import org.scalatest.FunSuite

import scala.io.Source

import es.weso.rdf.nodes._
import scala.util.parsing.input.CharArrayReader
import es.weso.rdf.triples._

class NTriplesParserSuite extends FunSuite with NTriplesParser {

  test("parser string") {
    val parser = NTriplesParser
    assert(parser.parseAll(parser.string, "\"hello\"").get === "hello")
  }

  test("parser absolute URI") {
    val parser = NTriplesParser
    assert(parser.parseAll(parser.absoluteURI, "http://example.org").get ===
      "http://example.org")
  }

  test("parser uriref") {
    val parser = NTriplesParser
    assertParse(uriref, "<http://example.org>", IRI("http://example.org"))
  }

  test("bNode") {
    val parser = NTriplesParser
    assertParse(nodeID, "_:b0", BNodeId("b" + 0))
  }

  test("basic string literal") {
    val parser = NTriplesParser
    assertParse(literal, "\"john\"", LangLiteral("john", Lang("")))
  }

  test("basic string literal with language") {
    val parser = NTriplesParser
    assertParse(literal, "\"perro\"@es", LangLiteral("perro", Lang("es")))
  }

  test("literal with scaped unicode char") {
    val parser = NTriplesParser
    assertParse(literal, "\"Jos\\u00E9\"", LangLiteral("Jos\u00e9", Lang("")))
  }

  test("typed literal") {
    val parser = NTriplesParser
    assertParse(literal, "\"23\"^^<http://example.org/datatype>",
      new DatatypeLiteral("23", IRI("http://example.org/datatype")))
  }

  test("basic triple") {
    val parser = NTriplesParser
    assertParse(triple, "<a> <b> <c>.", Some(RDFTriple(IRI("a"), IRI("b"), IRI("c"))))
  }

  test("basic triple with spaces") {
    val parser = NTriplesParser
    assertParse(triple, "<a> <b> <c> . ", Some(RDFTriple(IRI("a"), IRI("b"), IRI("c"))))
  }

  test("basic triple with spaces and tabs") {
    val parser = NTriplesParser
    assertParse(triple, " <http://example.org/resource3> 	 <http://example.org/property>	 <http://example.org/resource2> 	.	 ",
      Some(RDFTriple(
        IRI("http://example.org/resource3"),
        IRI("http://example.org/property"),
        IRI("http://example.org/resource2")
      )))
  }

  test("basic triple with spaces and tabs starting by spaces & tabs") {
    val parser = NTriplesParser
    assertParse(triple, " 	 <http://example.org/resource3> 	 <http://example.org/property>	 <http://example.org/resource2> 	.	 ",
      Some(RDFTriple(
        IRI("http://example.org/resource3"),
        IRI("http://example.org/property"),
        IRI("http://example.org/resource2")
      )))
  }

  test("two triples") {
    val parser = NTriplesParser
    val ts = """<a> <b> <c> . 
    		   |<a> <b> <d> .""".stripMargin

    assertParse(ntripleDoc, ts,
      Stream(
        RDFTriple(IRI("a"), IRI("b"), IRI("c")),
        RDFTriple(IRI("a"), IRI("b"), IRI("d"))
      ))
  }

  test("two triples with comment") {
    val parser = NTriplesParser
    val ts = """# A comment 
      		   |<a> <b> <c> . 
    		   |<a> <b> <d> .""".stripMargin

    assertParse(ntripleDoc, ts,
      Stream(
        RDFTriple(IRI("a"), IRI("b"), IRI("c")),
        RDFTriple(IRI("a"), IRI("b"), IRI("d"))
      ))
  }

  test("two triples with comments") {
    val parser = NTriplesParser
    val ts = """# A comment 
      		   |<a> <b> <c> .
      		   |# Another comment
    		   |<a> <b> <d> .
               |# And a comment at the end
               |""".stripMargin

    assertParse(ntripleDoc, ts,
      Stream(
        RDFTriple(IRI("a"), IRI("b"), IRI("c")),
        RDFTriple(IRI("a"), IRI("b"), IRI("d"))
      ))
  }

  test("Example from file 0") {
    val input = Source.fromURL(getClass.getResource("/test0.nt")).mkString
    assertParse(ntripleDoc, input,
      Stream(RDFTriple(IRI("http://example.org/a"), IRI("http://example.org/b"), IRI("http://example.org/c"))))
  }

  test("Example from file 1") {
    val input = Source.fromURL(getClass.getResource("/test1.nt")).mkString
    assertParse(
      ntripleDoc, input,
      Stream(
        RDFTriple(IRI("http://example.org/a"), IRI("http://example.org/b"), IRI("http://example.org/c")),
        RDFTriple(IRI("http://example.org/a"), IRI("http://example.org/b"), IRI("http://example.org/d"))
      )
    )
  }

  test("Example from file 2") {
    val input = Source.fromURL(getClass.getResource("/test2.nt")).mkString
    val triples = parseAll(ntripleDoc, input).get.toSet
    val expected = Stream(
      RDFTriple(IRI("http://example.org/a"), IRI("http://example.org/b"), BNodeId("b" + 0)),
      RDFTriple(IRI("http://example.org/a"), IRI("http://example.org/b"), BNodeId("b" + 1)),
      RDFTriple(BNodeId("b" + 0), IRI("http://example.org/b"), IRI("http://example.org/c")),
      RDFTriple(BNodeId("b" + 0), IRI("http://example.org/b"), IRI("http://example.org/d")),
      RDFTriple(BNodeId("b" + 1), IRI("http://example.org/b"), BNodeId("b" + 0)),
      RDFTriple(BNodeId("b" + 1), IRI("http://example.org/b"), IRI("http://example.org/e"))
    ).toSet
    assert(triples == expected)
  }

  /*    test("Example from file 3") {
    val input = Source.fromURL(getClass.getResource("/test3.nt")).mkString
    val triples = parseAll(ntripleDoc, input).get.toSet
    val expected = Stream(
    			RDFTriple(IRI("http://example.org/resource1"),IRI("http://example.org/property"),IRI("http://example.org/resource2")),
    			RDFTriple(BNodeId("b" + 0),IRI("http://example.org/property"),IRI("http://example.org/resource2")),
    			RDFTriple(IRI("http://example.org/resource2"),IRI("http://example.org/property"),BNodeId("b" + 0)),
    			RDFTriple(IRI("http://example.org/resource3"),IRI("http://example.org/property"),IRI("http://example.org/resource2"))
  				).toSet
     assert(triples == expected)
  } */

  test("Example from w3c file") {
    val input = Source.fromURL(getClass.getResource("/test.nt")).mkString
    val positionedInput = new CharArrayReader(input.toArray)
    val res = parseAll(ntripleDoc, input)
    res match {
      case x: Success[_] => {
        assert(res.get.toList.length === 30)
      }
      case x: Error =>
        println("ERROR: " + x)
        fail
      case x: Failure =>
        println("FAILURE" + x)
        fail
    }
  }

  def assertParse[T](parser: Parser[T], input: String, expected: T) {
    val res = parseAll(parser, input)
    res match {
      case x: Success[_] => assert(res.get === expected)
      case x: Error =>
        println("ERROR on input: " + input)
        println(res)
        fail
      case x: Failure =>
        println("Failure on input: " + input)
        println(res)
        fail
    }
  }
}