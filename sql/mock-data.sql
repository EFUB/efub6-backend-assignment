-- =====================================================================
-- 게시글 검색(GET /posts/search?keyword=...) 테스트용 목데이터 (MySQL)
--
-- 실행: mysql -u root -p efub < sql/mock-data.sql
-- 여러 번 실행해도 됨: 맨 위에서 이전에 넣은 목데이터(@mock.test 회원)를 먼저 지움
-- =====================================================================

-- ---------------------------------------------------------------------
-- 0. 이전 목데이터 정리 (자식 테이블 → 부모 테이블 순서)
-- ---------------------------------------------------------------------
DELETE FROM post_like
WHERE post_id IN (SELECT id FROM (
        SELECT p.id FROM post p JOIN member m ON p.member_id = m.member_id
        WHERE m.email LIKE '%@mock.test') t)
   OR member_id IN (SELECT member_id FROM (
        SELECT member_id FROM member WHERE email LIKE '%@mock.test') t);

DELETE FROM comment
WHERE post_id IN (SELECT id FROM (
        SELECT p.id FROM post p JOIN member m ON p.member_id = m.member_id
        WHERE m.email LIKE '%@mock.test') t);

DELETE FROM comments
WHERE post_id IN (SELECT id FROM (
        SELECT p.id FROM post p JOIN member m ON p.member_id = m.member_id
        WHERE m.email LIKE '%@mock.test') t)
   OR member_id IN (SELECT member_id FROM (
        SELECT member_id FROM member WHERE email LIKE '%@mock.test') t);

DELETE p FROM post p
JOIN member m ON p.member_id = m.member_id
WHERE m.email LIKE '%@mock.test';

DELETE b FROM board b
JOIN member m ON b.member_id = m.member_id
WHERE m.email LIKE '%@mock.test';

DELETE FROM member WHERE email LIKE '%@mock.test';

-- ---------------------------------------------------------------------
-- 1. 회원
--    비밀요원은 익명 글만 씀 → 닉네임으로 검색해도 나오면 안 됨
-- ---------------------------------------------------------------------
INSERT INTO member (email, password, nickname, university, student_id, status, created_at, updated_at) VALUES
('gamja@mock.test',  'mock-password', '개발하는감자', '이화여자대학교', '2312345', 'REGISTER', '2026-10-01 09:00:00', '2026-10-01 09:00:00'),
('spring@mock.test', 'mock-password', '스프링러버',   '이화여자대학교', '2223456', 'REGISTER', '2026-10-01 09:00:00', '2026-10-01 09:00:00'),
('dawn@mock.test',   'mock-password', '새벽코더',     '이화여자대학교', '2134567', 'REGISTER', '2026-10-01 09:00:00', '2026-10-01 09:00:00'),
('secret@mock.test', 'mock-password', '비밀요원',     '이화여자대학교', '2445678', 'REGISTER', '2026-10-01 09:00:00', '2026-10-01 09:00:00');

SET @gamja  := (SELECT member_id FROM member WHERE email = 'gamja@mock.test');
SET @spring := (SELECT member_id FROM member WHERE email = 'spring@mock.test');
SET @dawn   := (SELECT member_id FROM member WHERE email = 'dawn@mock.test');
SET @secret := (SELECT member_id FROM member WHERE email = 'secret@mock.test');

-- ---------------------------------------------------------------------
-- 2. 게시판
--    'QnA게시판'은 대소문자 무시 검색(keyword=qna) 확인용
-- ---------------------------------------------------------------------
INSERT INTO board (member_id, name, description, notice, created_at, updated_at) VALUES
(@gamja,  '자유게시판', '아무 이야기나 자유롭게', '서로 존중해 주세요',   '2026-10-02 09:00:00', '2026-10-02 09:00:00'),
(@spring, '스터디모집', '스터디원을 구하는 곳',   '모집 마감 시 글 수정', '2026-10-02 09:00:00', '2026-10-02 09:00:00'),
(@gamja,  '맛집탐방',   '학교 근처 맛집 공유',     NULL,                   '2026-10-02 09:00:00', '2026-10-02 09:00:00'),
(@dawn,   'QnA게시판',  '개발 질문과 답변',        '질문 전에 검색 먼저', '2026-10-02 09:00:00', '2026-10-02 09:00:00');

SET @free  := (SELECT board_id FROM board WHERE name = '자유게시판' AND member_id = @gamja);
SET @study := (SELECT board_id FROM board WHERE name = '스터디모집' AND member_id = @spring);
SET @food  := (SELECT board_id FROM board WHERE name = '맛집탐방'   AND member_id = @gamja);
SET @qna   := (SELECT board_id FROM board WHERE name = 'QnA게시판'  AND member_id = @dawn);

-- ---------------------------------------------------------------------
-- 3. 게시글 10개 (작성일이 모두 달라서 최신순 정렬 확인 가능)
--    view_count는 DB에 NOT NULL + 기본값 없음으로 남아 있어서 0을 넣음
-- ---------------------------------------------------------------------
INSERT INTO post (board_id, member_id, is_anonymous, title, content, view_count, created_at, updated_at) VALUES
-- [1] 제목에 QueryDSL
(@free,  @gamja,  0, 'QueryDSL 처음 써봤어요',      'Q클래스 생성 때문에 한참 헤맸네요. 컴파일 에러가 있으면 안 만들어진대요.', 0, '2026-11-01 10:00:00', '2026-11-01 10:00:00'),
-- [2] 제목에 소문자 querydsl, QnA게시판
(@qna,   @dawn,   0, 'querydsl fetchJoin 질문',     'fetchJoin이랑 그냥 join은 무슨 차이가 있나요?',                              0, '2026-11-02 10:00:00', '2026-11-02 10:00:00'),
-- [3] 스터디모집 게시판 (제목/내용에는 '스터디' 없음 → 게시판 이름으로만 걸림)
(@study, @spring, 0, 'JPA 같이 공부하실 분',        '매주 토요일 오후에 모여서 김영한 강의 같이 들어요.',                         0, '2026-11-03 10:00:00', '2026-11-03 10:00:00'),
-- [4] 내용에만 '스터디'
(@free,  @dawn,   0, '오늘 회고',                   '스터디 끝나고 집 가는 길에 쓰는 글. 오늘도 수고했다.',                       0, '2026-11-04 10:00:00', '2026-11-04 10:00:00'),
-- [5] 내용에 '떡볶이'
(@food,  @gamja,  0, '학교 앞 분식집 후기',         '떡볶이가 진짜 맛있어요. 순대도 추천!',                                       0, '2026-11-05 10:00:00', '2026-11-05 10:00:00'),
-- [6] 익명 + 내용에 '떡볶이' → 검색은 되지만 작성자는 '익명'으로 보여야 함
(@food,  @secret, 1, '익명으로 남기는 맛집',        '정문 쪽 떡볶이 집 사장님이 정말 친절해요.',                                  0, '2026-11-06 10:00:00', '2026-11-06 10:00:00'),
-- [7] 익명 (비밀요원 닉네임으로 검색해도 나오면 안 됨)
(@free,  @secret, 1, '고민 상담',                   '요즘 코딩이 너무 어려워요. 다들 어떻게 공부하세요?',                         0, '2026-11-07 10:00:00', '2026-11-07 10:00:00'),
-- [8] 개발하는감자가 쓴 익명 글 + 내용에 영어 'Spring'
(@qna,   @gamja,  1, '부끄러운 질문',               'Spring Bean이 정확히 뭔가요? 너무 기초라 익명으로 물어봐요.',                0, '2026-11-08 10:00:00', '2026-11-08 10:00:00'),
-- [9] 제목에 '%' (LIKE 와일드카드 이스케이프 확인용)
(@free,  @spring, 0, '100% 환불 받는 법',           '영수증은 꼭 챙기세요.',                                                       0, '2026-11-09 10:00:00', '2026-11-09 10:00:00'),
-- [10] 스터디모집 게시판 + 제목에 '스터디' + 내용에 '감자'
(@study, @dawn,   0, '알고리즘 스터디원 구해요',    '백준 골드 목표입니다. 감자 같은 초보도 환영해요!',                           0, '2026-11-10 10:00:00', '2026-11-10 10:00:00');
