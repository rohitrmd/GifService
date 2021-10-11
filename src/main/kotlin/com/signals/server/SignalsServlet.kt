package com.signals.server

import com.google.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.util.EntityUtils
import java.util.logging.Level
import java.util.logging.Logger
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SignalsServlet @Inject constructor(private val httpClient: HttpClient) : HttpServlet() {

    private val baseUrl = "https://api.giphy.com/v1/gifs/search"
    private val apiKey = "RfZgPlnUDuaHlBg8PKbdFFZDy47RSpwy" // this key can be fetched from secret store
    private val logger = Logger.getLogger(this.javaClass.name)

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        // Use async processing in servlets for scalability (https://docs.oracle.com/javaee/7/tutorial/servlets012.htm)
        val asyncContext = req.startAsync()

        // Use kotlin coroutines to serve requests
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val queryString = req.getParameter("q")

                val httpGet = HttpGet(baseUrl)
                val uri = URIBuilder(httpGet.uri)
                    .addParameter("api_key", apiKey)
                    .addParameter("q", queryString)
                    .build()

                httpGet.uri = uri
                val response = httpClient.execute(httpGet)

                val dataResponse = EntityUtils.toString(response.entity, "UTF-8")
                // resp.contentType = "text/html"
                resp.writer.println("$dataResponse")
            } catch (e: Exception) {
                logger.log(Level.SEVERE, e.message)
                resp.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error while serving your request")
            } finally {
                asyncContext.complete()
            }
        }
    }
}