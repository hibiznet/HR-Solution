# hibiznet-hr-backend

Spring Boot 기반 HR MVP 백엔드입니다.

## 스택
- Java 17
- Spring Boot 3.3.x
- Spring Security + JWT
- Redis Refresh Token 저장
- Spring Data JPA
- Flyway
- MariaDB (기준 DB)

## 기본 실행
로컬에서 MariaDB/Redis를 띄웁니다.

```bash
docker compose -f docker-compose-local.yml up -d
```

local profile로 실행합니다.

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

## 기본 계정
- username: `admin`
- password: `admin1234!`

## 인증 API
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `GET /api/auth/me`

## 부서 API
- `GET /api/departments?page=0&size=10&keyword=&isActive=`
- `GET /api/departments/all`
- `GET /api/departments/{id}`
- `POST /api/departments`
- `PUT /api/departments/{id}`
- `DELETE /api/departments/{id}`

## 직원 API
- `GET /api/employees?page=0&size=10&keyword=&departmentId=&statusCode=`
- `GET /api/employees/all`
- `GET /api/employees/{id}`
- `POST /api/employees`
- `PUT /api/employees/{id}`
- `DELETE /api/employees/{id}`

## 권한
- 조회: 로그인 사용자
- 등록/수정/삭제: `ROLE_ADMIN`, `ROLE_HR_MANAGER`

## 응답 포맷
```json
{
  "success": true,
  "data": {},
  "message": null
}
```
