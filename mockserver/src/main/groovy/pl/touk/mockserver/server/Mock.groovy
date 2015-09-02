package pl.touk.mockserver.server

import groovy.transform.EqualsAndHashCode
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import pl.touk.mockserver.api.common.Method

import java.util.concurrent.CopyOnWriteArrayList

@PackageScope
@EqualsAndHashCode(excludes = ["counter"])
@Slf4j
class Mock implements Comparable<Mock> {
    final String name
    final String path
    final int port
    String predicateClosureText = '{ _ -> true }'
    String responseClosureText = '''{ _ -> '' }'''
    String responseHeadersClosureText = '{ _ -> [:] }'
    Closure predicate = toClosure(predicateClosureText)
    Closure response = toClosure(responseClosureText)
    Closure responseHeaders = toClosure(responseHeadersClosureText)
    boolean soap = false
    int statusCode = 200
    Method method = Method.POST
    int counter = 0
    final List<MockEvent> history = new CopyOnWriteArrayList<>()

    Mock(String name, String path, int port) {
        if (!(name)) {
            throw new RuntimeException("Mock name must be given")
        }
        this.name = name
        this.path = path
        this.port = port
    }

    boolean match(Method method, MockRequest request) {
        return this.method == method && predicate(request)
    }

    MockResponse apply(MockRequest request) {
        log.debug("Mock $name invoked")
        ++counter
        String responseText = response(request)
        String response = soap ? wrapSoap(responseText) : responseText
        Map<String, String> headers = responseHeaders(request)
        MockResponse mockResponse = new MockResponse(statusCode, response, headers)
        history << new MockEvent(request, mockResponse)
        return mockResponse
    }

    private static String wrapSoap(String request) {
        """<?xml version='1.0' encoding='UTF-8'?>
            <soap-env:Envelope xmlns:soap-env='http://schemas.xmlsoap.org/soap/envelope/' xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing">
                <soap-env:Body>${request}</soap-env:Body>
            </soap-env:Envelope>"""
    }

    void setPredicate(String predicate) {
        if (predicate) {
            this.predicateClosureText = predicate
            this.predicate = toClosure(predicate)
        }
    }

    private Closure toClosure(String predicate) {
        if (predicate ==~ /(?m).*System\s*\.\s*exit\s*\(.*/) {
            throw new RuntimeException('System.exit is forbidden')
        }
        GroovyShell sh = new GroovyShell(this.class.classLoader);
        return sh.evaluate(predicate) as Closure
    }

    void setResponse(String response) {
        if (response) {
            this.responseClosureText = response
            this.response = toClosure(response)
        }
    }

    void setSoap(Boolean soap) {
        this.soap = soap ?: false
    }

    void setStatusCode(String statusCode) {
        if (statusCode) {
            this.statusCode = Integer.valueOf(statusCode)
        }
    }

    void setMethod(Method method) {
        if (method) {
            this.method = method
        }
    }

    void setResponseHeaders(String responseHeaders) {
        if (responseHeaders) {
            this.responseHeadersClosureText = responseHeaders
            this.responseHeaders = toClosure(responseHeaders)
        }
    }

    @Override
    int compareTo(Mock o) {
        return name.compareTo(o.name)
    }
}
