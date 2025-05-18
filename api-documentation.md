# Microservices API Documentation

## Overview
This documentation describes the APIs for our microservices architecture, which includes:
- Authentication Service
- Order Service
- Item Service
- API Gateway

## Base URLs
- Service Registry: `http://localhost:8761`
- API Gateway: `http://localhost:8080`
- Auth Service: `http://localhost:8082`
- Order Service: `http://localhost:8083`
- Item Service: `http://localhost:8084`

## Authentication
All endpoints except `/api/auth/register` and `/api/auth/login` require a valid JWT token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

## Rate Limiting
All endpoints are rate-limited. If you exceed the rate limit, you'll receive a 429 Too Many Requests response.

## API Endpoints

### Authentication Service

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
    "username": "string",
    "password": "string"
}
```

**Response**
```json
{
    "token": "string",
    "username": "string",
    "role": "string"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "string",
    "password": "string"
}
```

**Response**
```json
{
    "token": "string",
    "username": "string",
    "role": "string"
}
```

#### Get User Info
```http
GET /userinfo
Authorization: Bearer <jwt_token>
```

**Response**
```json
{
    "sub": "string",
    "name": "string",
    "email": "string",
    "email_verified": boolean,
    "picture": "string"
}
```

### Order Service

#### Create Order
```http
POST /api/orders
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
    "userId": "string",
    "items": [
        {
            "itemId": "string",
            "quantity": integer
        }
    ]
}
```

**Response**
```json
{
    "id": "string",
    "userId": "string",
    "status": "PENDING|PROCESSING|COMPLETED|CANCELLED",
    "items": [
        {
            "itemId": "string",
            "quantity": integer,
            "price": number
        }
    ],
    "totalAmount": number,
    "createdAt": "string",
    "updatedAt": "string"
}
```

#### Get Orders by User ID
```http
GET /api/orders/user/{userId}
Authorization: Bearer <jwt_token>
```

**Response**
```json
[
    {
        "id": "string",
        "userId": "string",
        "status": "string",
        "items": [
            {
                "itemId": "string",
                "quantity": integer,
                "price": number
            }
        ],
        "totalAmount": number,
        "createdAt": "string",
        "updatedAt": "string"
    }
]
```

#### Get Order by ID
```http
GET /api/orders/{orderId}
Authorization: Bearer <jwt_token>
```

**Response**
```json
{
    "id": "string",
    "userId": "string",
    "status": "string",
    "items": [
        {
            "itemId": "string",
            "quantity": integer,
            "price": number
        }
    ],
    "totalAmount": number,
    "createdAt": "string",
    "updatedAt": "string"
}
```

#### Update Order Status
```http
PUT /api/orders/{orderId}/status?status={status}
Authorization: Bearer <jwt_token>
```

**Response**
```json
{
    "id": "string",
    "userId": "string",
    "status": "string",
    "items": [
        {
            "itemId": "string",
            "quantity": integer,
            "price": number
        }
    ],
    "totalAmount": number,
    "createdAt": "string",
    "updatedAt": "string"
}
```

### Item Service

#### Create Item
```http
POST /items
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
    "name": "string",
    "description": "string",
    "price": number,
    "stockQuantity": integer
}
```

**Response**
```json
{
    "id": "string",
    "name": "string",
    "description": "string",
    "price": number,
    "stockQuantity": integer,
    "createdAt": "string",
    "updatedAt": "string"
}
```

#### Get All Items
```http
GET /items
Authorization: Bearer <jwt_token>
```

**Response**
```json
[
    {
        "id": "string",
        "name": "string",
        "description": "string",
        "price": number,
        "stockQuantity": integer,
        "createdAt": "string",
        "updatedAt": "string"
    }
]
```

#### Get Item by ID
```http
GET /items/{id}
Authorization: Bearer <jwt_token>
```

**Response**
```json
{
    "id": "string",
    "name": "string",
    "description": "string",
    "price": number,
    "stockQuantity": integer,
    "createdAt": "string",
    "updatedAt": "string"
}
```

#### Update Item
```http
PUT /items/{id}
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
    "name": "string",
    "description": "string",
    "price": number,
    "stockQuantity": integer
}
```

**Response**
```json
{
    "id": "string",
    "name": "string",
    "description": "string",
    "price": number,
    "stockQuantity": integer,
    "createdAt": "string",
    "updatedAt": "string"
}
```

#### Delete Item
```http
DELETE /items/{id}
Authorization: Bearer <jwt_token>
```

**Response**
- 204 No Content

#### Update Stock
```http
PATCH /items/{id}/stock?quantity={quantity}
Authorization: Bearer <jwt_token>
```

**Response**
```json
{
    "id": "string",
    "name": "string",
    "description": "string",
    "price": number,
    "stockQuantity": integer,
    "createdAt": "string",
    "updatedAt": "string"
}
```

## Error Responses

### 400 Bad Request
```json
{
    "error": "string"
}
```

### 401 Unauthorized
```json
{
    "error": "Invalid credentials"
}
```

### 403 Forbidden
```json
{
    "error": "Access denied"
}
```

### 404 Not Found
```json
{
    "error": "Resource not found"
}
```

### 429 Too Many Requests
```json
{
    "error": "Rate limit exceeded. Please try again later."
}
```

### 500 Internal Server Error
```json
{
    "error": "An unexpected error occurred"
}
```

## Data Models

### AuthRequest
```json
{
    "username": "string",
    "password": "string"
}
```

### AuthResponse
```json
{
    "token": "string",
    "username": "string",
    "role": "string"
}
```

### OrderRequest
```json
{
    "userId": "string",
    "items": [
        {
            "itemId": "string",
            "quantity": integer
        }
    ]
}
```

### OrderResponse
```json
{
    "id": "string",
    "userId": "string",
    "status": "string",
    "items": [
        {
            "itemId": "string",
            "quantity": integer,
            "price": number
        }
    ],
    "totalAmount": number,
    "createdAt": "string",
    "updatedAt": "string"
}
```

### ItemRequest
```json
{
    "name": "string",
    "description": "string",
    "price": number,
    "stockQuantity": integer
}
```

### ItemResponse
```json
{
    "id": "string",
    "name": "string",
    "description": "string",
    "price": number,
    "stockQuantity": integer,
    "createdAt": "string",
    "updatedAt": "string"
}
``` 