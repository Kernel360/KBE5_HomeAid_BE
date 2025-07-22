<div align="center">
  <img width="200" height="200" alt="HomeAid Logo" src="https://github.com/user-attachments/assets/a0a7e916-8bf1-43f5-ae6a-53f4e24573b0" />

<h3> HomeAid  Readme ✅</h3>

[<img src="https://img.shields.io/badge/-readme.md-important?style=flat&logo=google-chrome&logoColor=white" />]() [<img src="https://img.shields.io/badge/release-v1.0.0-yellow?style=flat&logo=google-chrome&logoColor=white" />]()
<br/> [<img src="https://img.shields.io/badge/프로젝트 기간-2025.05.14~2025.07.18-green?style=flat&logo=&logoColor=white" />]()
</div>

## 📚 목차

1. [🏠 HomeAid 소개](#-homeaid---생활-서비스-매칭-플랫폼)
2. [🚀 배포 주소](#-배포-주소)
3. [📌 서비스 이용 방법](#-서비스-이용-방법)
4. [🖥️ 화면 구성](#️-화면-구성)
5. [🧩 주요 기능](#-주요-기능)
    - [👤 수요자 기능](#-수요자-고객)
    - [👷 매니저 기능](#-매니저-공급자)
    - [🧑‍💼 관리자 기능](#-관리자)
6. [🛠 기술 스택](#-기술-스택)
    - [🧩 Backend](#-backend)
    - [🗄️ Database & Cache](#-database--cache)
    - [☁️ Infra / DevOps](#-infra--devops)
    - [🤝 협업 도구](#-협업-도구)
7. [🗂 ERD](#erd)
8. [📐 아키텍쳐](#아키텍쳐)
9. [👨‍👩‍👧‍👦 팀원 소개](#팀원-소개)
---
## 🏠 HomeAid - 생활 서비스 매칭 플랫폼

> 고객과 매니저를 연결하여 **청소, 세탁, 가사 등 다양한 생활 서비스를 손쉽게 예약**할 수 있는 플랫폼입니다.

**💡 해결하고자 하는 문제**
- 고객이 직접 **검증된 매니저**를 쉽고 빠르게 예약할 수 있도록 지원
- 매니저가 **자신에게 맞는 지역과 시간**을 설정하여 유연하게 일할 수 있는 환경 제공
- 관리자에게는 **매칭, 결제, 정산을 효율적으로 관리**할 수 있는 **백오피스 시스템** 제공

## 🚀 배포 주소
www.homeaid-service.com

## 📌 서비스 이용 방법

> **이용 흐름 안내**  
> [고객] 서비스 예약
> 
> [관리자] 서비스 제공 매니저 매칭  
>
> [매니저] 매칭 수락  
>
> [고객] 최종 수락 및 결제

---

### 🧑‍💼 관리자 계정
- **ID**: `010-0000-0000`
- **PW**: `Password1!`

### 👤 고객 계정
- **ID**: `010-1111-2222`
- **PW**: `Password1!`

### 👷 매니저 계정
- **ID**: `010-8989-8989`
- **PW**: `Password1!`

## 🖥️ 화면 구성

| 사용자 화면 #1 - 서비스 옵션/위치 입력 | 사용자 화면 #2 - 결제 방식 선택 |
|:--------------------------------------:|:-------------------------------:|
| <img src="https://github.com/user-attachments/assets/c7e05c74-82a6-4472-aef8-136c09cf5e64" width="400"/> | <img src="https://github.com/user-attachments/assets/3caf1ee4-8fe3-470f-a761-5bfe82de3c1d" width="400"/> |
| 관리자 화면 #1 - 매칭 관리 | 매니저 화면 #1 - 매칭 수락 |
| <img src="https://github.com/user-attachments/assets/96f88ab1-1a9d-43c7-8a83-fd97e158015f" width="400"/> | <img src="https://github.com/user-attachments/assets/a57ad94e-ca48-4df2-9d4e-4bc15bf139d1" width="400"/> |
| 매니저 화면 #2 - 근무 진행 | 사용자 화면 #3 - 리뷰 작성 |
| <img src="https://github.com/user-attachments/assets/de7da09f-8042-47c5-8536-7f0111d355bd" width="400"/> | <img src="https://github.com/user-attachments/assets/ba178314-6427-4234-b47c-58362abfbe1e" width="400"/> |

## 🧩 주요 기능
### 👤 수요자 (고객)

- **서비스 예약**
    - 서비스 시간/옵션 선택
    - 위치 입력
    - 매칭된 매니저 최종 확인

- **리뷰 작성**
    - 서비스 완료 후 매니저에 대한 리뷰 작성

- **마이페이지**
    - 내 정보 수정
    - 주소 관리
    - 작성한 리뷰 보기
    - 관리자와 1:1 문의
    - 결제 내역 확인

---

### 👷 매니저 (공급자)

- **프로필 관리**
    - 활동 지역, 요일, 시간 등록
    - 경력 및 프로필 사진 등록
    - 매니저 인증 서류 제출 (신분증, 범죄경력, 건강검진서 등)

- **매칭 관리**
    - 매칭 상태별 리스트 확인
    - 매칭 수락 / 거절 (사유 입력 포함)

- **서비스 작업**
    - 예약된 서비스 체크인 / 체크아웃

- **리뷰 관리**
    - 고객에 대한 리뷰 작성
    - 받은 리뷰 및 평균 평점 확인

- **관리자 상담**
    - 게시글 등록, 답변, 삭제
    - 비공개글 여부 및 상태 필터링

- **정산 확인**
    - 월별 서비스 완료 건수 확인
    - 지급액 및 정산일 확인

---

### 🧑‍💼 관리자

- **대시보드**
    - 오늘 매출, 총 매출, 환불 금액 등 그래프 시각화

- **회원 관리**
    - 수요자 리스트 및 상세 조회
    - 매니저 서류 승인 및 활동 상태 변경

- **결제 / 정산 관리**
    - 수요자 결제 내역 조회 및 환불 요청 처리
    - 매니저 주 단위 정산 및 지급 관리

- **매칭 알고리즘**
    - 지역, 시간대, 평점, 거리 등을 고려한 매니저 추천

- **리뷰 관리**
    - 수요자 및 매니저가 작성한 리뷰 목록 확인 및 제재

- **통계 / 모니터링**
    - 회원 수, 예약 수, 정산 금액 등 일/주/월 단위 통계

- **문의글 관리**
    - 수요자 / 매니저 문의글 조회
    - 문의글에 대한 답변 작성

## 🛠 기술 스택
### 🧩 Backend
![Java](https://img.shields.io/badge/Java-17-007396?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7+-6DB33F?logo=spring-boot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?logo=spring-security)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-ORM-6DB33F)
![OAuth2](https://img.shields.io/badge/OAuth2-Google-4285F4?logo=google)

### 🗄️ Database & Cache
![MySQL](https://img.shields.io/badge/AWS%20RDS-MySQL-4479A1?logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-InMemory-EF2D5E?logo=redis&logoColor=white)
![S3](https://img.shields.io/badge/AWS-S3-569A31?logo=amazon-aws&logoColor=white)

### ☁️ Infra / DevOps
![AWS EC2](https://img.shields.io/badge/AWS-EC2-FF9900?logo=amazon-aws&logoColor=white)
![Jenkins](https://img.shields.io/badge/Jenkins-CI/CD-D24939?logo=jenkins&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Container-2496ED?logo=docker&logoColor=white)
![Grafana Loki](https://img.shields.io/badge/Grafana-Loki-F46800?logo=grafana&logoColor=white)
![Promtail](https://img.shields.io/badge/Promtail-Log%20Collector-000000)

### 🤝 협업 도구
![GitHub](https://img.shields.io/badge/GitHub-Version%20Control-181717?logo=github)
![Notion](https://img.shields.io/badge/Notion-Documentation-000000?logo=notion)
![Discord](https://img.shields.io/badge/Discord-Communication-5865F2?logo=discord&logoColor=white)

## 🗂ERD
![image](https://github.com/user-attachments/assets/97ea4163-2575-4c24-b2f2-45619ec5dc47)

## 📐아키텍쳐
![image](https://github.com/user-attachments/assets/647d90c0-1898-4dc0-b7a4-70d73a21faac)

## 👨‍👩‍👧팀원 소개
<table>
<tr align="center">
   <th>상우진 [팀장](Backend)</th>
   <th>강희진(Backend)</th>
   <th>김숙현(Backend)</th>
   <th>안지현(Backend)</th>
</tr>
<tr align ="center">  
   <td><a href="https://github.com/SangWJDev"><img src="https://avatars.githubusercontent.com/u/80154292?v=4" width="100"></a></td>
   <td><a href="https://github.com/heeejinin"><img src="https://avatars.githubusercontent.com/u/150228975?v=4" width="100"></a></td>
   <td><a href="https://github.com/soohoioa"><img src="https://avatars.githubusercontent.com/u/151589776?v=4" width="100"></a></td>
   <td><a href="https://github.com/jhroom"><img src="https://avatars.githubusercontent.com/u/110217121?v=4" width="100"></a></td>
</tr>
<tr align ="center">
    <td>
       - 설계 총괄<br>
       - 매칭, 예약, 작업기록 기능 개발<br>
       - 인프라 및 CI/CD 파이프라인 구축
    <td></td>
    <td></td>
    <td></td>
</tr>
</table>