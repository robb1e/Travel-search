package com.gu.travel.search

import net.liftweb.json.JsonAST.{JInt, JValue}
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonParser._
import dispatch._
import dispatch.thread.ThreadSafeHttpClient
import org.apache.http.params.HttpParams
import org.apache.http.conn.params.ConnRouteParams
import net.liftweb.json._
import net.liftweb.json.Printer._

case class ContentApiResponse(currentPage: Int, pages: Int, results: List[Result])
case class Result(webTitle: String, webUrl: String)

object TagSearch extends Application {
  implicit val formats = net.liftweb.json.DefaultFormats
  val initResponse = HttpClient("http://content.guardianapis.com/tags?page-size=50&format=json&type=keyword&section=travel")
  val contentApiResponse = (parse(initResponse) \ "response").extract[ContentApiResponse]

  var results: List[Result] = List()
  for (i <- 1 until contentApiResponse.pages) {
    val response = HttpClient("http://content.guardianapis.com/tags?page-size=50&format=json&type=keyword&section=travel&page=" + i.toString)
    val r = (parse(response) \ "response").extract[ContentApiResponse].results
    r.map{res => results = res :: results}
  }

  val json = compact(render(Extraction.decompose(results)))

  println(json)

}


object HttpClient {

  def get(uri: String): String = apply(url(uri))

  def post(uri: String, params: Map[String, String]) = apply(url(uri) << params)

  def apply(request: Request) = ThreadSafeHttp(request as_str)

  object ThreadSafeHttp extends Http with thread.Safety{
    // This is there to NOT go through the proxy, as that won't recognise localhost addresses
    override def make_client = new ThreadSafeHttpClient(new Http.CurrentCredentials(None), maxConnections = 50, maxConnectionsPerRoute = 50) {
      override protected def configureProxy(params: HttpParams) = {
        ConnRouteParams.setDefaultProxy(params, null)
        params
      }
    }
  }
}