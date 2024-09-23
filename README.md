## 시연 영상
- 차트: https://youtu.be/sH0vQYZbY-s

## 아키텍처
![Cryptor](https://github.com/user-attachments/assets/a586f650-d7e5-4683-a4f1-249e37ae268b)

## 프로젝트 목표
- 가상 거래소 시스템을 구현하는 것이 목표 입니다.
- scale-out 구조로 설계하여, 대용량 트래픽 처리가 가능하도록 구현하는 것이 목표입니다.
- 서버의 성능을 빠르게 하는 것이 목표입니다.

## 기술적 이슈 해결 과정
- [#1] Redis에 JSON 포멧 데이터 저장의 비효율성

## TODO
- [] 주문 체결 시 많은 양의 주문에 대해 쓰기락을 걸어 생기는 병목을 병렬 처리 및 일정 청크 단위로 락을 잡아 성능 개선
- [] 스케줄링 서버 최초 로드 시 모든 캔들 데이터 레디스에 업로드 시, 멀티 스레드를 활용하여 시간 단축하기
- [] Redis 캔들 key에 날짜 기준으로 샤딩을 적용하여, 많은 데이터를 저장할 수 있게하고, 레디스 커멘드 병목을 줄이기
- [] 과거 캔들 조회에 Lazy Loading 적용
- [] Ngrider를 이용하여 성능 테스트
