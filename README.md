# booking-service
Service that supports booking of appartments, cottage etc 
This service will include two main rest api's endpoints
- get bookings
- post create a new booking

The mongodb connection url is defined by env variable SPRING_DATA_MONGODB_URI

## Running Tests

Tests use Testcontainers to spin up a MongoDB container. **Docker must be running** before executing tests.

On macOS with Colima, the following environment variables must be set:

```bash
export DOCKER_CONTEXT=colima
export DOCKER_HOST=unix:///Users/kaolle/.colima/default/docker.sock
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
export TESTCONTAINERS_RYUK_DISABLED=true
```

Then start Colima and run tests:
```bash
colima start        # start Docker runtime
mvn test            # run tests
```

To auto-start Colima on login:
```bash
brew services start colima
```

## Security Vulnerability Scan (OWASP)

Run a dependency vulnerability scan against the NVD database:

```bash
mvn dependency-check:check
```

The report is generated at `target/dependency-check-report.html`.

**First run** downloads the NVD database (~200 MB, takes 3-5 minutes). Subsequent runs are incremental and fast.

To speed up NVD database updates, get a free API key at https://nvd.nist.gov/developers/request-an-api-key and set:

```bash
export NVD_API_KEY=your-key-here
mvn dependency-check:check
```

The build fails if any dependency has a CVE score ≥ 7 (High or Critical). To suppress false positives, add entries to `owasp-suppressions.xml`.
