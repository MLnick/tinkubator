package com.tinkerpop.gremlin.scala

import com.tinkerpop.blueprints.{Graph, Vertex, Edge}
import java.lang.{Iterable => JIterable}

/**Adds convenience methods to [[com.tinkerpop.blueprints.Graph]]. */
class ScalaGraph(val graph: Graph) {
  /**Returns all vertices. */
//  def V: GremlinScalaPipeline[Graph, Vertex] =
//    new GremlinScalaPipeline[Graph, Vertex].start(graph).V.asInstanceOf[GremlinScalaPipeline[Graph, Vertex]]
  def V: GremlinScalaPipeline[JIterable[Vertex], Vertex] =
	new GremlinScalaPipeline[JIterable[Vertex], Vertex].start(graph.getVertices()).asInstanceOf[GremlinScalaPipeline[JIterable[Vertex], Vertex]]
  // TODO MP remove
//new GremlinPipeline(graph.getVertices())
  //return new GremlinGroovyPipeline(((Graph) delegate).getVertices());
  /**Returns the vertices with the specified IDs. */
  def V(ids: Any*): Iterable[Vertex] = ids.map(graph.getVertex(_)) //TODO should'nt V and V(1,2) return the same type???

  /**Returns the vertex with the specified ID. */
  def v(id: Any): Vertex = graph getVertex id

  /**Returns all edges. */
  def E: GremlinScalaPipeline[JIterable[Edge], Edge] =
    new GremlinScalaPipeline[JIterable[Edge], Edge].start(graph.getEdges()).asInstanceOf[GremlinScalaPipeline[JIterable[Edge], Edge]]

  /**Returns the edges with the specified IDs. */
  def E(ids: Any*): Iterable[Edge] = ids map {
    graph getEdge _
  }

  /**Returns the edge with the specified ID. */
  def e(id: Any): Edge = graph getEdge id

  def -> : GremlinScalaPipeline[Graph, Graph] =
    new GremlinScalaPipeline[Graph, Graph].start(graph).asInstanceOf[GremlinScalaPipeline[Graph, Graph]];

  //TODO def += for addVertex and addEdge?
}

/**Implicit conversions between [[com.tinkerpop.blueprints.Graph]] and [[com.tinkerpop.gremlin.scala.ScalaGraph]]. */
object ScalaGraph {
  implicit def wrap(graph: Graph) = new ScalaGraph(graph)

  implicit def unwrap(wrapper: ScalaGraph) = wrapper.graph
}
