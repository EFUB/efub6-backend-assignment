# efub6-backend-assignment
💚 이펍 6기 백엔드 인턴 과제 제출 레포지토리

# 1-1주차 과제

- API 명세서
  [API 명세서 작성](https://www.notion.so/1-REST-API-API-3299169e057a803bb6d4d13b4e03916e?source=copy_link)

- ERD
  https://www.erdcloud.com/d/Niat3sRST9mwsr8gD

# 1-2주차 과제

- member CRUD 코드 작성

# 12주차 과제

- QueryDSL을 이용한 게시글 동적 검색 구현
- 요청: `GET /boards/{boardId}/posts/search`
- 검색 조건(둘 중 하나 이상 필수)
  - `keyword`: 제목 또는 내용에 포함된 문자열
  - `writer`: 작성자 닉네임에 포함된 문자열
- 두 조건을 함께 전달하면 AND 조건으로 검색하며, 작성자 검색 결과에서는 익명 게시글을 제외합니다.

```http
GET /boards/1/posts/search?keyword=QueryDSL&writer=이펍
Authorization: Bearer {accessToken}
```
