# APIs: Purpose, Nomenclature and Types

Reference for what an API is, the vocabulary this course uses, what publishing one buys you, and what it costs.

---

## Core Idea

An API is a contract between a provider and a consumer. The provider promises that a specific request will produce a specific response. The consumer writes code against that promise and against nothing else. Everything valuable about APIs and everything painful about them follows from that single sentence: because the consumer depends only on the contract, the provider can change the implementation freely, and because the consumer depends on the contract, the provider cannot change it freely.

## Two Independent Axes

The phrase "type of API" answers two different questions, and mixing them causes most of the confusion in this area. Keep them separate.

| Axis | Values | Question it answers |
| :--- | :--- | :--- |
| Audience | Open, Partner, Internal, Composite | Who may call it, under what agreement |
| Protocol | REST, SOAP, RPC, GraphQL, WebSocket | How the call is shaped on the wire |

An API has one value on each axis. A partner API delivered over gRPC is perfectly ordinary. This document covers the audience axis; the protocol axis has its own reference.

## Audience Types

| Type | Consumer | Auth posture | Cost of a breaking change |
| :--- | :--- | :--- | :--- |
| Open | Anyone who signs up | API key or OAuth, public docs, published deprecation policy | Highest, because you cannot contact every caller |
| Partner | Named organizations under contract | OAuth client credentials or mutual TLS, SLA, negotiated windows | Moderate, because the list of callers is known |
| Internal | Teams inside your organization | Service identity, usually behind a gateway or mesh | Lowest per change, highest in aggregate |
| Composite | Usually an internal or partner caller | Inherits from the layer it fronts | Varies, since it bundles several downstream calls |

Internal APIs are where organizations under-invest, and it shows up as sprawl: hundreds or thousands of endpoints spread across several gateways with no single catalog. An endpoint nobody can list is an endpoint nobody can secure, version, or retire.

## Vocabulary Used Throughout This Course

| Term | Definition |
| :--- | :--- |
| Resource | The thing the API exposes, named by a noun, such as `/orders` |
| Endpoint | A method plus a path acting on a resource, such as `GET /api/v1/orders/42` |
| Contract | The machine-readable description of requests, responses, and errors |
| Provider and consumer | Who serves the API, and who calls it |
| Payload | The request or response body |
| Safe | The call does not change server state |
| Idempotent | Repeating the call leaves the same server state |
| Gateway | The component fronting one or more APIs for auth, routing, and rate limiting |

## What You Gain

Decoupling comes first, because it enables the rest. A consumer that depends on `GET /api/v1/accounts/42` does not know or care whether the data comes from a relational table, a cache, or a third-party service, so the provider can replace any of that without a conversation. Reuse follows: one documented capability serves the web app, the mobile app, the nightly batch job, and a partner. Composability follows from reuse, and it is the point at which the API stops being plumbing and becomes the product surface.

Two claims are worth resisting. APIs do not make systems simpler; they relocate complexity from a function call into a network hop and a contract. APIs are not faster than in-process calls; they are slower, and you accept that in exchange for independent deployment.

## What You Owe

The contract becomes someone else's dependency, which means your change becomes their outage.

- Twitter's 2018 API changes broke Tweetbot and Twitterrific, produced a public backlash under `#breakingmytwitter`, and forced a delay.
- Google Maps Platform's repricing cut the free tier from roughly 750,000 to 25,000 calls per month, and consumers reported order-of-magnitude cost increases.
- Google's earlier API retirements, including the Translate API, drew hundreds of negative responses and a partial reversal.

You also owe the network: latency you did not have before, partial failures, and therefore timeouts, retries, and idempotency. And you owe an inventory, because sprawl is what accumulates when nobody is responsible for the list.

## What Can Go Wrong

The common failure is treating "API" as a synonym for "REST endpoint" and skipping the contract because the team is small. The contract is the deliverable; REST is one way to express it. Small teams grow into sprawl, and retrofitting a catalog onto four hundred undocumented endpoints costs far more than writing them down as they were built.
