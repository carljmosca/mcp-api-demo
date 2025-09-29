# mcp-api-demo Helm Chart

This chart deploys the Quarkus-based mcp-api-demo along with a PostgreSQL database, a Service, and an optional Ingress.

## Prerequisites
- Kubernetes 1.23+
- Helm 3.9+
- An Ingress controller (if using Ingress)

## Install

- Default install (with bundled PostgreSQL and Ingress disabled or set a host):

```sh
helm upgrade --install mcp charts/mcp-api-demo \
  --namespace mcp-demo --create-namespace \
  --set ingress.enabled=true \
  --set postgres.persistence.enabled=true \
  --set postgres.persistence.storageClass=local-path
```

```sh
helm upgrade --install mcp charts/mcp-api-demo \
    -n mcp-demo --create-namespace \
    --set postgres.persistence.enabled=true \
    --set postgres.persistence.storageClass=local-path
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
 - securityContext.*: Defaults are set to satisfy PodSecurity restricted (runAsNonRoot, seccomp RuntimeDefault, no privilege escalation, drop ALL caps).
 - pdb.app/postgres: Enable PodDisruptionBudgets (defaults: app maxUnavailable=1, postgres=0 for strict HA).
 - networkPolicy.enabled: Creates NetworkPolicies that restrict Postgres ingress to only app pods; app allows ingress broadly by default.

### Persistence & StorageClass

This chart creates a PersistentVolumeClaim for PostgreSQL when `postgres.persistence.enabled=true` (default).

Your cluster must either:

- Have a default StorageClass configured (the PVC will bind automatically), or
- You must set an explicit StorageClass via `--set postgres.persistence.storageClass=<your-class>`, or
- Disable persistence for development: `--set postgres.persistence.enabled=false` (uses `emptyDir:`).

Symptoms if no provisioner/default StorageClass exists:

```text
PVC Pending; event: "no persistent volumes available for this claim and no storage class is set"
```

Examples:

```sh
# Use an explicit storage class
helm upgrade --install mcp charts/mcp-api-demo \
  --namespace mcp-demo --create-namespace \
  --set ingress.enabled=true \
  --set postgres.persistence.storageClass=standard

# Or disable persistence (dev only)
helm upgrade --install mcp charts/mcp-api-demo \
  --namespace mcp-demo --create-namespace \
  --set ingress.enabled=true \
  --set postgres.persistence.enabled=false
```

### PodSecurity (restricted)

This chart ships with restricted-friendly securityContext defaults. If your cluster enforces the "restricted" profile, you should not see warnings on apply. You can override or disable via `securityContext` in values.

## Notes

- The application enables Hibernate import of `import.sql` by default to seed data, mirroring docker-compose.
- If you add the Quarkus SmallRye Health extension, set:
  - `--set probes.liveness.path=/q/health/live`
  - `--set probes.readiness.path=/q/health/ready`
  Otherwise, the default `/q/openapi` is used for both probes.

### Talos + local-path provisioner (PSA)

When using Rancher Local Path Provisioner on a Talos cluster with Pod Security Admission enforced, the helper pod uses hostPath and needs a privileged namespace. If you see PVC events like:

```
failed to create volume ... violates PodSecurity baseline: hostPath (container "helper"): hostPath volumes are not allowed
```

Label the `local-path-storage` namespace to privileged to allow provisioning:

```
kubectl label ns local-path-storage \
  pod-security.kubernetes.io/enforce=privileged \
  pod-security.kubernetes.io/warn=privileged \
  pod-security.kubernetes.io/audit=privileged --overwrite=true
```

Then recreate or reapply PVCs. This chart will bind PVCs once the provisioner can run.

### PostgreSQL non-root and PGDATA

This chart runs Postgres as non-root (uid/gid 70 for the alpine image). It also sets `PGDATA` to a subdirectory (`/var/lib/postgresql/data/pgdata`) so initialization does not attempt to chmod/chown the mount root, avoiding permission errors under restricted PodSecurity.

You can override `postgres.pgdata` and the security contexts in `values.yaml` if needed.

## Uninstall

```sh
helm uninstall mcp
```