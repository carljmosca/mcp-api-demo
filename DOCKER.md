# Docker Compose Setup

This project includes Docker Compose configuration to run the MCP API Demo application with PostgreSQL.

## Files

- `docker-compose.yml` - Main configuration for production-like deployment
- `docker-compose.override.yml` - Development overrides (automatically applied)

## Quick Start

### Using Published Image (Production)

```bash
# Start the application with PostgreSQL
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop the services
docker-compose down
```

### Local Development

```bash
# Build and run locally (uses override file automatically)
docker-compose up --build

# Run in background
docker-compose up -d --build

# View application logs
docker-compose logs -f app

# View database logs  
docker-compose logs -f postgres
```

## Services

### PostgreSQL Database
- **Image**: postgres:15-alpine
- **Port**: 5432
- **Database**: mcp_api_demo
- **Username**: quarkus
- **Password**: quarkus
- **Volume**: postgres_data (persistent storage)

### Application
- **Production Image**: ghcr.io/carljmosca/mcp-api-demo:latest
- **Development**: Builds from local Dockerfile
- **Port**: 8080
- **Debug Port**: 5005 (development only)

## Environment Variables

The application is configured with the following environment variables:

- `QUARKUS_DATASOURCE_JDBC_URL`: PostgreSQL connection URL
- `QUARKUS_DATASOURCE_USERNAME`: Database username
- `QUARKUS_DATASOURCE_PASSWORD`: Database password
- `QUARKUS_HIBERNATE_ORM_DATABASE_GENERATION`: Set to `drop-and-create`
- `QUARKUS_HIBERNATE_ORM_SQL_LOAD_SCRIPT`: Loads `import.sql` with sample data

## API Endpoints

Once running, the application will be available at:

- **API Base**: http://localhost:8080
- **Tickets Endpoint**: http://localhost:8080/tickets
- **Health Check**: http://localhost:8080/q/health

## Database Access

To connect to the PostgreSQL database directly:

```bash
# Using docker exec
docker-compose exec postgres psql -U quarkus -d mcp_api_demo

# Using local psql client
psql -h localhost -p 5432 -U quarkus -d mcp_api_demo
```

## Troubleshooting

### Application won't start
- Check if PostgreSQL is healthy: `docker-compose ps`
- Check logs: `docker-compose logs postgres`
- Ensure port 5432 isn't already in use

### Database connection issues
- Verify PostgreSQL is running and healthy
- Check network connectivity between services
- Review application logs for connection errors

### No tickets returned from API
If `/tickets` returns an empty array:

1. **Check if import.sql was loaded:**
   ```bash
   # Check application logs for SQL execution
   docker-compose logs app | grep INSERT
   
   # Connect to database and verify data
   docker-compose exec postgres psql -U quarkus -d mcp_api_demo -c "SELECT COUNT(*) FROM ticket;"
   ```

2. **Verify Hibernate configuration:**
   ```bash
   # Look for schema creation and data loading in logs
   docker-compose logs app | grep -E "(drop|create|import\.sql)"
   ```

3. **Manual data verification:**
   ```bash
   # Connect to PostgreSQL and check tables
   docker-compose exec postgres psql -U quarkus -d mcp_api_demo
   \dt
   SELECT * FROM ticket LIMIT 5;
   ```

4. **Force restart with fresh database:**
   ```bash
   docker-compose down -v
   docker-compose up -d
   ```

### Development debugging
- Remote debugging is available on port 5005 in development mode
- Use your IDE to connect to localhost:5005

## Data Persistence

PostgreSQL data is persisted in a Docker volume named `postgres_data`. To reset the database:

```bash
# Stop services and remove volumes
docker-compose down -v

# Start fresh
docker-compose up -d
```