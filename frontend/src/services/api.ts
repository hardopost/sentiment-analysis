import axios from 'axios';

// Create a reusable Axios instance
const api = axios.create({
  baseURL: 'http://localhost:8080', // Spring Boot backend URL
  headers: {
    'Content-Type': 'application/json',
  },
});

export default api;