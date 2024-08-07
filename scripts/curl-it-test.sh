#!/bin/bash
# Wait for the application to start (by checking the response from a health endpoint)
while ! curl -s http://localhost:8080/api/players?name=Maximilian%20Braune | grep '177251'; do
  echo "Waiting for the application to start..."
  sleep 5
done

echo "Success!"