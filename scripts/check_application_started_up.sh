#!/bin/bash
# Wait for the application to start (by checking the response from a health endpoint)
while ! curl -s http://localhost:8080/api/health | grep 'UP'; do
  echo "Waiting for the application to start..."
  sleep 5
done

echo "Application is up and running."