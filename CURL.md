# API Routes

## health check 
```bash
curl -i http://localhost:8000/actuator/health
```

## ingest
```bash
curl -i -X POST "http://localhost:8000/ingest"
```

## claims
GET
```bash
curl -i -X GET "http://localhost:8000/claims"
```

## contracts
GET all
```bash
curl http://localhost:8080/contracts
```
GET by id
```bash
curl http://localhost:8080/contracts/ELV-100455
````
