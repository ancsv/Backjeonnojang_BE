백전노장 (Baekjeonnojang)

Spring Boot와 WebSocket(STOMP)을 기반으로 구현한 실시간 온라인 장기 게임 플랫폼

프로젝트 소개

백전노장은 사용자가 실시간으로 장기 게임을 즐길 수 있는 웹 기반 플랫폼입니다.

단순한 실시간 채팅이나 게임 서버 구현을 넘어, JWT 인증과 WebSocket 보안 구조를 적용하여 안전한 실시간 게임 환경을 구축하는 것을 목표로 개발하였습니다.

게임방 생성, 실시간 채팅, 게임 진행, 사용자 인증을 하나의 서버에서 관리하며, WebSocket 환경에서 발생할 수 있는 보안 취약점을 분석하고 직접 개선하였습니다.

시스템 아키텍처
Client
    │
    ▼
Spring Security
    │
HTTP Login
    │
JWT
    │
    ▼
STOMP CONNECT
    │
ChannelInterceptor
    │
JWT Validation
    │
Room Validation
    │
Sender Validation
    │
XSS Protection
    │
    ▼
WebSocket Broker
    │
    ▼
Game Room
    │
    ▼
Players
주요 기능
1. 실시간 장기 게임

WebSocket(STOMP)을 이용하여 플레이어 간 장기 게임을 실시간으로 동기화하였습니다.

게임 상태와 채팅 메시지를 서버를 통해 브로드캐스트하여 모든 참가자가 동일한 게임 상태를 유지하도록 구현하였습니다.

2. JWT 기반 사용자 인증

Spring Security와 JWT를 적용하여 로그인 및 사용자 인증을 구현하였습니다.

WebSocket 연결 시 JWT를 검증하여 인증된 사용자만 게임방에 입장할 수 있도록 구성하였습니다.

3. 게임방 관리

게임방 생성, 참가, 종료 기능을 구현하였으며, 참가 여부를 검증하여 허가되지 않은 사용자의 접근을 차단하였습니다.

4. WebSocket 보안 강화

기본 WebSocket 구조에서 발생할 수 있는 보안 취약점을 분석하고 다음과 같은 기능을 구현하였습니다.

JWT 기반 WebSocket 인증
게임방 참가자 검증
Sender 위조 방지
XSS 공격 방어

이를 통해 인증되지 않은 사용자의 접속과 메시지 위조를 방지하도록 설계하였습니다.

5. STOMP Pub/Sub 구조

STOMP Broker를 이용하여

/app
/topic

구조를 분리하고 서버를 통한 메시지 전달 방식을 적용하였습니다.

클라이언트 간 직접 통신을 방지하고 서버 중심의 안전한 메시지 처리를 구현하였습니다.

6. REST API

회원가입, 로그인, 게임방 관리 등의 기능은 REST API로 구현하였으며, Swagger를 적용하여 API 문서를 자동 생성하였습니다.

7. Docker 및 AWS 배포

Docker를 이용하여 애플리케이션을 컨테이너화하였으며,

AWS EC2
AWS RDS
GitHub Actions

를 이용한 CI/CD 환경을 구축하여 자동 배포를 구현하였습니다.

WebSocket 보안 구조
Client

↓

HTTP Login

↓

JWT

↓

STOMP CONNECT

↓

JWT Validation

↓

Room Validation

↓

Sender Validation

↓

XSS Protection

↓

Broker

↓

Game Room
기술 스택
Backend
Spring Boot
Spring Security
Spring Data JPA
WebSocket
STOMP
JWT
Swagger
Frontend
React
Database
MySQL
AWS RDS
DevOps
Docker
GitHub Actions
AWS EC2
Security
JWT Authentication
ChannelInterceptor
STOMP
XSS Protection
Sender Validation
Room Authorization
주요 특징
실시간 온라인 장기 게임 서버 구현
WebSocket(STOMP) 기반 게임 및 채팅
JWT 기반 사용자 인증
게임방 권한 검증
Sender 위조 방지
XSS 공격 방어
Docker 기반 운영 환경
GitHub Actions 기반 CI/CD
AWS EC2·RDS 배포
기대 효과
WebSocket 기반의 안정적인 실시간 장기 게임 환경 제공
JWT와 다층 보안 구조를 적용하여 인증 및 권한 관리를 강화
WebSocket 환경에서 발생할 수 있는 주요 보안 취약점을 개선하여 안전한 실시간 통신 환경을 구현
Docker와 GitHub Actions 기반 CI/CD를 통해 운영 및 배포 효율성을 향상
