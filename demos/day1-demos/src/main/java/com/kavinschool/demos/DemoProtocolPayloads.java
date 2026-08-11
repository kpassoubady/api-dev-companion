/**
 * Demo - The Same Operation in Four Protocols
 * Day 1 - Session 3
 *
 * Goal: Print the exact wire shape of one operation, "fetch account 42", as
 * REST, SOAP, JSON-RPC, and gRPC, so the resource-versus-operation split is
 * visible rather than described.
 *
 * The second half prints the failure case for each protocol. That is where the
 * difference bites: a missing account is 404 in REST, HTTP 500 with a SOAP
 * fault in SOAP, and HTTP 200 with an error member in JSON-RPC. Monitoring
 * built on HTTP status codes misreads two of the four.
 *
 * No API key needed. No network. Pure JDK, printed strings only.
 *
 * Run from day1/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoProtocolPayloads
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.util.List;

public class DemoProtocolPayloads {

    record Protocol(String name, String style, String request, String failure, String contract) {}

    static final Protocol REST = new Protocol(
        "REST",
        "resource-oriented: the URL names the thing, the method names the verb",
        """
        GET /api/v1/accounts/42 HTTP/1.1
        Host: bank.example
        Accept: application/json

        (no body)

        --> 200 OK   Content-Type: application/json
        {"id":42,"owner":"Ada Lovelace","balance":1250.00}""",
        """
        --> 404 Not Found   Content-Type: application/problem+json
        {"type":"https://bank.example/problems/account-not-found",
         "title":"Account not found","status":404,
         "detail":"No account with id 999","instance":"/api/v1/accounts/999"}""",
        "OpenAPI document, external and optional"
    );

    static final Protocol SOAP = new Protocol(
        "SOAP",
        "operation-oriented: one endpoint, the operation lives in the envelope",
        """
        POST /accounts HTTP/1.1
        Host: bank.example
        Content-Type: text/xml; charset=utf-8
        SOAPAction: "http://bank.example/accounts/getAccount"

        <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
          <soap:Body>
            <getAccount xmlns="http://bank.example/accounts">
              <accountId>42</accountId>
            </getAccount>
          </soap:Body>
        </soap:Envelope>

        --> 200 OK   Content-Type: text/xml""",
        """
        --> 500 Internal Server Error   Content-Type: text/xml
        <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
          <soap:Body>
            <soap:Fault>
              <faultcode>soap:Client</faultcode>
              <faultstring>Account not found</faultstring>
            </soap:Fault>
          </soap:Body>
        </soap:Envelope>

        Note: a business outcome arrives as a transport-level 500.""",
        "WSDL, built in and mandatory, with XML Schema types"
    );

    static final Protocol JSON_RPC = new Protocol(
        "JSON-RPC 2.0",
        "operation-oriented: one endpoint, the operation is a 'method' field",
        """
        POST /rpc HTTP/1.1
        Host: bank.example
        Content-Type: application/json

        {"jsonrpc":"2.0","method":"getAccount","params":{"accountId":42},"id":1}

        --> 200 OK
        {"jsonrpc":"2.0","result":{"id":42,"owner":"Ada Lovelace"},"id":1}""",
        """
        --> 200 OK
        {"jsonrpc":"2.0","error":{"code":-32001,"message":"Account not found"},"id":1}

        Note: a failure arrives as an HTTP success. Every status-code-based
        alert, retry policy, and gateway metric reads this as healthy.""",
        "none by default; conventions or an out-of-band schema"
    );

    static final Protocol GRPC = new Protocol(
        "gRPC",
        "operation-oriented: a typed method on a service, Protobuf over HTTP/2",
        """
        service AccountService {
          rpc GetAccount (GetAccountRequest) returns (Account);
        }

        message GetAccountRequest { int64 account_id = 1; }
        message Account { int64 id = 1; string owner = 2; double balance = 3; }

        Call:  AccountService/GetAccount  { account_id: 42 }
        Wire:  binary Protobuf frames, not human readable
        --> gRPC status 0 (OK) + Account message""",
        """
        --> gRPC status 5 (NOT_FOUND) + status message "Account not found"

        Note: gRPC carries its own status space, separate from HTTP codes.
        Debugging needs grpcurl; curl and a browser are not enough.""",
        ".proto file, mandatory, code-generated for both sides"
    );

    static final List<Protocol> PROTOCOLS = List.of(REST, SOAP, JSON_RPC, GRPC);

    /**
     * Prints the happy-path wire shape for all four protocols side by side.
     * Learners compare where the operation name lives: in the HTTP method
     * and path (REST) versus inside the body or envelope (SOAP, RPC, gRPC).
     */
    static void printRequests() {
        System.out.println("=".repeat(72));
        System.out.println("THE HAPPY PATH: fetch account 42");
        System.out.println("=".repeat(72));
        for (Protocol p : PROTOCOLS) {
            System.out.println("\n### " + p.name());
            System.out.println("Style:    " + p.style());
            System.out.println("Contract: " + p.contract());
            System.out.println();
            p.request().lines().forEach(line -> System.out.println("    " + line));
        }
    }

    /**
     * Prints the failure path for each protocol. Learners see that a missing
     * account is 404 in REST, HTTP 500 with a SOAP fault in SOAP, and HTTP
     * 200 with an error member in JSON-RPC -- status-code-based monitoring
     * misreads two of the four.
     */
    static void printFailures() {
        System.out.println("\n" + "=".repeat(72));
        System.out.println("THE FAILURE PATH: account 999 does not exist");
        System.out.println("=".repeat(72));
        for (Protocol p : PROTOCOLS) {
            System.out.println("\n### " + p.name());
            p.failure().lines().forEach(line -> System.out.println("    " + line));
        }
    }

    /**
     * Prints a comparison table showing where each protocol stores the
     * operation name and which HTTP method it uses. Learners see that REST
     * is the only one that leverages the full set of HTTP methods.
     */
    static void printSummary() {
        System.out.println("\n" + "=".repeat(72));
        System.out.println("WHERE THE OPERATION NAME LIVES");
        System.out.println("=".repeat(72));
        System.out.printf("%-14s %-22s %s%n", "PROTOCOL", "OPERATION NAME IS IN", "HTTP METHOD USED");
        System.out.printf("%-14s %-22s %s%n", "REST", "the method + path", "GET, POST, PUT, PATCH, DELETE");
        System.out.printf("%-14s %-22s %s%n", "SOAP", "the XML envelope", "POST only");
        System.out.printf("%-14s %-22s %s%n", "JSON-RPC 2.0", "the 'method' field", "POST only");
        System.out.printf("%-14s %-22s %s%n", "gRPC", "the .proto service", "POST only (HTTP/2 framed)");
    }

    /**
     * Orchestrates the demo: happy paths, failure paths, then the comparison
     * table. Learners walk away understanding that REST puts the verb in the
     * HTTP method and the outcome in the status code, so generic tooling
     * understands it -- SOAP and RPC put both inside the body, trading
     * tooling compatibility for stronger contracts.
     */
    public static void main(String[] args) {
        printRequests();
        printFailures();
        printSummary();

        System.out.println("\nTakeaway: REST puts the verb in the HTTP method and the outcome in the status");
        System.out.println("code, so generic caches, proxies, and dashboards understand it. SOAP and RPC");
        System.out.println("put both inside the body, which buys stronger contracts and costs you every");
        System.out.println("tool that only speaks HTTP.");
    }
}
