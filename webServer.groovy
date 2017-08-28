/**
 * Daniel Orozco 13312
 * Web Server with Groovy
 */
import com.sun.net.httpserver.*

// Tipos que se soportan
final TYPES = [
	"css": "text/css",
	"html": "text/html",
	"jpg": "image/jpeg",
	"js": "application/javascript",
	"png": "image/png",
]

def port = System.properties.port?.toInteger() ?: 2345
def root = new File(System.properties.webroot ?: "webapp/")
def server = HttpServer.create(new InetSocketAddress(port), 0)

server.createContext("/", { HttpExchange exchange ->	
	try {
		if (!"GET".equalsIgnoreCase(exchange.requestMethod)) {			
	        exchange.sendResponseHeaders(405, 0)
			exchange.responseBody.close()
			return
		}

		def path = exchange.requestURI.path
		println "GET $path"

		def file = new File(root, path.substring(1))
		if (file.isDirectory()) {
			file = new File(file, "index.html")
		}
		if (file.exists()) {
			exchange.responseHeaders.set("Content-Type",
				TYPES[file.name.split(/\./)[-1]] ?: "text/plain")
	        exchange.sendResponseHeaders(200, 0)
	        file.withInputStream {
				exchange.responseBody << it
	        }
			exchange.responseBody.close()
		} else {		
	        exchange.sendResponseHeaders(404, 0)
			exchange.responseBody.close()
		}
	} catch(e) {
		e.printStackTrace()
	}
} as HttpHandler)

server.start()
println "Web Server started on port ${port}"
