package pl.touk.mockserver.client

import org.apache.commons.lang3.StringEscapeUtils

class AddMockRequestData {
    String name
    String path
    Integer port
    String predicate
    String response
    Boolean soap
    Integer statusCode
    Method method
    String responseHeaders
    String requestHeaders

    void setPredicate(String predicate) {
        this.predicate = StringEscapeUtils.escapeXml11(predicate)
    }

    void setResponse(String response) {
        this.response = StringEscapeUtils.escapeXml11(response)
    }

    void setResponseHeaders(String responseHeaders) {
        this.responseHeaders = StringEscapeUtils.escapeXml11(responseHeaders)
    }

    void setRequestHeaders(String requestHeaders) {
        this.requestHeaders = StringEscapeUtils.escapeXml11(requestHeaders)
    }

    enum Method {
        POST,
        GET,
        DELETE,
        PUT,
        TRACE,
        HEAD,
        OPTIONS,
        PATCH
    }
}
