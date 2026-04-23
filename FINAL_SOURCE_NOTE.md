# HiBizNet HR Final Full Source Snapshot

이 압축본은 아래를 포함한 전체 소스 스냅샷입니다.

- backend/
  - 인증/JWT/Redis Refresh Token
  - 직원/부서/근태/휴가/사용자권한/코드관리/감사로그 소스
- frontend/
  - 로그인/대시보드/직원/부서/근태/휴가/사용자권한/코드관리/감사로그 UI 소스

포함 의도:
- 패치 조각이 아니라 전체 프로젝트 트리 형태로 제공
- 로컬에서 백엔드/프론트를 각각 import 해서 실행 가능한 기준 소스 제공

참고:
- backend 쪽 Gradle Wrapper 파일은 포함되어 있지 않습니다.
- 필요 시 로컬에서 `gradle wrapper`를 1회 실행해 `gradlew`, `gradlew.bat`를 생성해서 사용하세요.