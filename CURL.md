# health check 
curl -i http://localhost:8000/actuator/health

# hello test     
curl -i http://localhost:8000/hello

# ingest
curl -i -X POST "http://localhost:8000/ingest"
