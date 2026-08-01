# Security for APIs: Standards

Reference for the three questions every request to a production API has to answer, and the standards that answer each one.

---

## Core Idea

APIs now carry the large majority of web traffic, which also makes them the large majority of the attack surface. Security for an API breaks down into three separable questions, each answered by a different mechanism: who are you (authentication), what are you allowed to do (authorization), and how often are you allowed to do it (rate limiting). Skipping any one of them doesn't make the other two redundant; a valid, authenticated caller can still do more damage than they should if authorization is missing, and an authorized caller can still overwhelm the system if nothing limits their request rate. The OWASP API Security Top 10 is the industry-standard reference for the vulnerability classes that show up when one of these questions goes unanswered, and Broken Object Level Authorization, Broken Authentication, and Lack of Resources & Rate Limiting map directly onto the three questions above.

## Authentication vs. Authorization

| | Authentication | Authorization |
| :--- | :--- | :--- |
| **Question answered** | Who are you? | What are you allowed to do? |
| **Common mechanism** | OAuth 2.0 / OIDC, short-lived JWT access tokens | Role-based (RBAC), attribute-based (ABAC), or scope-based access control |
| **Failure mode if skipped** | Anyone can call the API as anyone | An authenticated caller acts outside their intended scope |

Authentication in a modern API typically means a short-lived JWT access token, often around fifteen minutes, paired with a refresh token stored as an HTTP-only, Secure, `SameSite` cookie rather than in `localStorage`, which is straightforward to steal via a cross-site scripting bug. Authorization then decides what that authenticated identity can actually touch. Role-based access control, where permissions attach to a role like `ADMIN` or `USER`, is the simplest model to reason about and maps directly onto Spring Security's method-security annotations such as `@PreAuthorize("hasRole('ADMIN')")`. Attribute-based and scope-based models exist for cases where role alone isn't granular enough, at the cost of more implementation and testing effort.

## Rate Limiting

Rate limiting defends against a caller, malicious or just misbehaving, making requests faster than the system can safely absorb. The token bucket algorithm is the common Java implementation, most often via the Bucket4j library: a bucket refills with tokens at a fixed rate, each request consumes one token, and an empty bucket means the request gets rejected. This naturally allows short bursts of activity while still enforcing a steady average rate over time. Production systems often apply different limits per tier, tighter for unauthenticated endpoints, looser for authenticated ones, tighter again for sensitive mutation endpoints. A rate limiter that only lives in application memory has a well-known failure mode: behind a load balancer with several instances, each instance keeps its own bucket, silently multiplying the real limit by the number of instances. A shared store like Redis is what makes the limit real across a cluster, though a single-instance in-memory bucket is enough to learn the mechanism hands-on.

## Where This Fits the Contract

The OpenAPI spec from Day 2 is where authentication requirements belong once they exist: security schemes are a first-class part of the OpenAPI document, not a side note. Adding basic security rules to the shared API is filling in a piece the Day 2 contract was always missing, not bolting on something unrelated to it.

## Common Pitfalls

Treating a valid token as sufficient on its own is the most common mistake; broken object- and function-level authorization, where an authenticated user reaches a resource or action they shouldn't, is one of the most cited real-world API vulnerability classes. Rate limiting only at a gateway and never per-user or per-endpoint fails to stop abuse from a caller who is otherwise fully authenticated.
