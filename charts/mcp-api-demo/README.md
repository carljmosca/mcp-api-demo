# mcp-api-demo Helm Chart

This chart deploys the Quarkus-based mcp-api-demo along with a PostgreSQL database, a Service, and an optional Ingress.

## Prerequisites
- Kubernetes 1.23+
- Helm 3.9+
- An Ingress controller (if using Ingress)

## Install

- Default install (with bundled PostgreSQL and Ingress disabled or set a host):

```sh
helm install mcp charts/mcp-api-demo \
  --set ingress.enabled=true \
  --set ingress.hosts[0].host=mcp.local
```

- Override the image tag:

```sh
helm upgrade --install mcp charts/mcp-api-demo \
  --set image.tag=latest
```

- Use existing PostgreSQL (disable bundled one) and point the app to it:

```sh
helm upgrade --install mcp charts/mcp-api-demo \
  --set postgres.enabled=false \
  --set env.QUARKUS_DATASOURCE_JDBC_URL="jdbc:postgresql://mydb:5432/app" \
  --set env.QUARKUS_DATASOURCE_USERNAME=app \
  --set env.QUARKUS_DATASOURCE_PASSWORD=app
```

## Values

- image.repository: Container image repo (default ghcr.io/moscac/mcp-api-demo)
- image.tag: Image tag
- service.port: Service port (default 8080)
- ingress.enabled: Whether to create an Ingress
- ingress.className: IngressClass name (e.g., nginx)
- ingress.hosts: List of hosts and paths
- postgres.enabled: Deploy bundled PostgreSQL (default true)
- postgres.auth.username/password/database: Credentials
- postgres.persistence.enabled/size/storageClass: PVC settings
- probes.liveness/readiness.path and timings: Configure HTTP probe paths. Set to `/q/health/live` and `/q/health/ready` if `quarkus-smallrye-health` is enabled.
  Defaults to `/q/openapi` (provided by SmallRye OpenAPI, already included).
- mcp.enabled/name/version/path: Configure the MCP server exposure (defaults enable at `/mcp`).

## Notes

- The application enables Hibernate import of `import.sql` by default to seed data, mirroring docker-compose.
- If you add the Quarkus SmallRye Health extension, set:
  - `--set probes.liveness.path=/q/health/live`
  - `--set probes.readiness.path=/q/health/ready`
  Otherwise, the default `/q/openapi` is used for both probes.

## Uninstall

```sh
helm uninstall mcp
```