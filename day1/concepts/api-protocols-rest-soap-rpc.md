# API Protocols: REST, SOAP and RPC

Reference for the three protocol families, how each shapes the same request on the wire, and how to choose between them per boundary.

---

## The One Distinction That Organizes Everything

REST is resource-oriented. The URL names a thing, the HTTP method names the verb, and the HTTP status code carries the outcome. SOAP and RPC are operation-oriented. The URL names a single endpoint, the body names the procedure to run, and the transport status says little about what happened. Caching, discoverability, tooling, and error handling all follow from this one difference, so it is worth getting right before anything else.

A related correction: REST is an architectural style layered over HTTP, not a protocol. SOAP is a protocol. This matters on Day 2, when the contract for a REST API turns out to be an optional external document (OpenAPI) while the contract for a SOAP service is mandatory and built in (WSDL).

## The Same Operation in Four Shapes

Fetching account 42 looks like this in REST, where the method and path carry the whole request:

```http
GET /api/v1/accounts/42 HTTP/1.1
Accept: application/json
```

In SOAP it is always a `POST` to one endpoint, with the operation named inside the envelope:

```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <getAccount xmlns="http://bank.example/accounts">
      <accountId>42</accountId>
    </getAccount>
  </soap:Body>
</soap:Envelope>
```

In JSON-RPC 2.0 it is also a `POST` to one endpoint, with the operation in a `method` field:

```json
{"jsonrpc": "2.0", "method": "getAccount", "params": {"accountId": 42}, "id": 1}
```

In gRPC the operation is a method on a service declared in a `.proto` file and serialized as Protobuf over HTTP/2:

```proto
service AccountService {
  rpc GetAccount (GetAccountRequest) returns (Account);
}
```

The detail students find most surprising is where errors go. A missing account is `404` in REST. In SOAP it is HTTP `500` carrying a `soap:Fault`, so a business outcome arrives as a transport failure. In JSON-RPC it is HTTP `200` carrying an `error` member, so a failure arrives as a success. Any monitoring built on HTTP status codes reads those two cases wrong unless it is written specifically for them.

## Comparison

| Dimension | REST | SOAP | RPC (JSON-RPC, gRPC) |
| :--- | :--- | :--- | :--- |
| Organizing unit | Resource | Operation in an envelope | Procedure |
| Transport | HTTP, all methods | Usually HTTP POST | HTTP POST, or HTTP/2 for gRPC |
| Payload | Usually JSON | XML only | JSON or Protobuf |
| Contract | OpenAPI, external and optional | WSDL, built in and mandatory | `.proto`, mandatory for gRPC |
| Error signal | HTTP status code | `soap:Fault` inside HTTP 500 | Error object inside HTTP 200, or gRPC status |
| HTTP caching | Native for `GET` | No | No |
| Debuggable with curl | Yes | Awkwardly | Not for gRPC |

## Where Each One Wins in 2026

REST remains the default at the public edge. It carries roughly 83% of public APIs and around 89% enterprise usage, and it wins there because of native HTTP caching, universal tooling, and the fact that anyone can reproduce a bug with curl.

gRPC has become the standard for internal service-to-service traffic at organizations that care about throughput, with reported serialization gains of seven to ten times over JSON for heavy workloads. It is a poor fit for a public API that third parties consume from a browser.

GraphQL sits at roughly 25% enterprise adoption, down from a peak near 40%, and its durable pattern is backend-for-frontend: internal traffic stays REST or gRPC while external clients consume one graph.

SOAP persists in banking, insurance, government, and telecom, and not out of nostalgia. It offers three things REST does not provide out of the box:

- A machine-enforced contract with strict XML Schema typing
- Message-level security through WS-Security, so signatures travel with the message rather than terminating at TLS
- Transactional and reliable-messaging semantics through the WS-\* stack

When an integration must be signed, typed, and non-repudiable across several hops, SOAP is the shortest path.

The practical consequence is that protocol choice is a per-boundary decision, not a company-wide policy. The common 2026 architecture is REST at the public edge, GraphQL or a typed RPC layer for internal frontends, and gRPC between services.

## What Can Go Wrong

Two mistakes recur. The first is declaring SOAP dead, which surprises the student who maintains one next month. The second is comparing protocols on payload size alone, when contract enforcement, security model, and available tooling are what actually decide real projects.
