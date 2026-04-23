# HiBizNet HR Frontend

React + TypeScript + Vite 기반 HR 관리자 프론트엔드입니다.

## 포함 범위
- JWT 로그인 / 자동 토큰 재발급
- 권한별 메뉴 제어
- 직원 CRUD 화면
- 부서 CRUD 화면
- 공통 API 에러 처리
- 토스트 메시지
- 테이블 필터 / 페이지네이션

## 실행
```bash
npm install
cp .env.example .env
npm run dev
```

## 백엔드 API 전제
인증:
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `GET /api/auth/me`

직원:
- `GET /api/employees?page=&size=&keyword=&departmentId=&statusCode=&employmentTypeCode=`
- `GET /api/employees/{id}`
- `POST /api/employees`
- `PUT /api/employees/{id}`
- `DELETE /api/employees/{id}`

부서:
- `GET /api/departments?page=&size=&keyword=&parentId=&isActive=`
- `GET /api/departments/{id}`
- `POST /api/departments`
- `PUT /api/departments/{id}`
- `DELETE /api/departments/{id}`

## 응답 형식
아래 두 형식을 모두 흡수하도록 구성했습니다.

1. Envelope 형식
```json
{ "success": true, "data": { ... }, "message": null }
```

2. 직접 데이터 형식
```json
{ ... }
```

목록은 아래 둘 다 대응합니다.
- Spring Page 형식: `{ content, totalElements, totalPages, page, size }`
- 배열 형식: `[ ... ]`
