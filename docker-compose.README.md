# Auth Service Docker Compose Setup

This Docker Compose configuration sets up a complete authentication service with the following components:

## Services

### Core Services

- **user-service** - User management, authentication, and profile operations (Port 8081)
- **token-service** - JWT token generation, validation, and refresh (Port 8082)

### Infrastructure Services

- **postgres** - PostgreSQL database for user data (Port 5432)
- **redis** - Redis for caching and session management (Port 6379)

### Monitoring Services

- **prometheus** - Metrics collection (Port 9090)
- **grafana** - Monitoring dashboards (Port 3000)

### Gateway Service

- **nginx** - API Gateway for routing and load balancing (Port 80)

## Quick Start

### Prerequisites

- Docker and Docker Compose installed
- At least 4GB of available RAM
- Ports 80, 3000, 5432, 6379, 8081, 8082, 9090 available

### Starting the Services

1. Clone the repository and navigate to the project root
2. Start all services:

```bash
docker-compose up -d
```

3. Check service status:

```bash
docker-compose ps
```

4. View logs:

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f user-service
```

### Stopping the Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: This will delete all data)
docker-compose down -v
```

## Service Endpoints

### User Service (via API Gateway)

- **Base URL**: http://localhost/api/users
- **Health Check**: http://localhost/user-service/actuator/health
- **Metrics**: http://localhost/user-service/actuator/prometheus

### Token Service (via API Gateway)

- **Base URL**: http://localhost/api/tokens
- **Health Check**: http://localhost/token-service/actuator/health
- **Metrics**: http://localhost/token-service/actuator/prometheus

### Direct Service Access

- **User Service**: http://localhost:8081
- **Token Service**: http://localhost:8082

### Infrastructure

- **PostgreSQL**: localhost:5432
    - Database: `auth_db`
    - Username: `auth_user`
    - Password: `auth_password`
- **Redis**: localhost:6379
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000
    - Username: `admin`
    - Password: `admin`

## API Examples

### Create User

```bash
curl -X POST http://localhost/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "name": "Test User",
    "password": "password123",
    "roles": ["USER"]
  }'
```

### Login

```bash
curl -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

### Get User Profile

```bash
curl -X GET http://localhost/api/users/{user-id}/profile \
  -H "Authorization: Bearer {jwt-token}"
```

## Configuration

### Environment Variables

Both services support these environment variables:

#### Common Variables

- `SPRING_PROFILES_ACTIVE=docker`
- `JWT_SECRET=mySecretKey`
- `REDIS_HOST=redis`
- `REDIS_PORT=6379`

#### User Service Specific

- `POSTGRES_HOST=postgres`
- `POSTGRES_PORT=5432`
- `POSTGRES_DB=auth_db`
- `POSTGRES_USER=auth_user`
- `POSTGRES_PASSWORD=auth_password`

#### Token Service Specific

- `JWT_EXPIRATION=3600` (1 hour)
- `JWT_REFRESH_EXPIRATION=86400` (24 hours)

### Customizing Configuration

1. **Database Configuration**: Modify the PostgreSQL environment variables in `docker-compose.yml`
2. **Redis Configuration**: Update Redis settings in the compose file
3. **Service Configuration**: Edit the `application-docker.yml` files in each service
4. **Nginx Configuration**: Modify `deploy/nginx/nginx.conf` for routing changes

## Database Schema

The PostgreSQL database includes:

- `users` - User accounts and authentication data
- `user_profiles` - Extended user profile information
- `audit_logs` - System audit trail

Sample data is automatically loaded on first startup.

## Monitoring

### Prometheus Metrics

- Service health and performance metrics
- Custom application metrics
- JVM metrics

### Grafana Dashboards

- Pre-configured dashboards for service monitoring
- Custom alerts and notifications

## Development

### Building Services

```bash
# Build specific service
docker-compose build user-service

# Build all services
docker-compose build
```

### Debugging

```bash
# Access service logs
docker-compose logs -f user-service

# Access container shell
docker-compose exec user-service /bin/bash

# View database
docker-compose exec postgres psql -U auth_user -d auth_db
```

## Production Considerations

### Security

- Change default passwords
- Use proper SSL certificates
- Implement proper firewall rules
- Use secrets management

### Scalability

- Configure multiple service replicas
- Use external database and Redis clusters
- Implement proper load balancing

### Monitoring

- Set up alerts in Grafana
- Configure log aggregation
- Implement health checks

## Troubleshooting

### Common Issues

1. **Port Conflicts**
    - Check if ports are already in use
    - Modify port mappings in docker-compose.yml

2. **Memory Issues**
    - Ensure sufficient RAM available
    - Adjust JVM heap settings if needed

3. **Database Connection Issues**
    - Verify PostgreSQL is healthy
    - Check database credentials

4. **Service Not Starting**
    - Check service logs: `docker-compose logs [service-name]`
    - Verify dependencies are healthy

### Health Checks

All services include health checks. Monitor status with:

```bash
docker-compose ps
```

Healthy services will show `healthy` status.

## Support

For issues and questions:

1. Check service logs first
2. Verify all dependencies are running
3. Review configuration files
4. Check network connectivity between services