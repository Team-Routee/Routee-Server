# Routee <img width="100" alt="로고 이미지" src="https://github.com/user-attachments/assets/5a69a5aa-603e-460e-bc67-0d1ec17a3a8b" align="left" />
<h3>정상이 아닌, 여정을 기록하는 트렌디한 등산 경험 플랫폼</h3>

<br/>

> 긴 호흡의 운동에서 남긴 사진과 운동 데이터, 추억을 하나로 연결해 누구나 쉽게 편집하고 공유할 수 있도록 돕는 플랫폼

<br/>

## 🧑‍🤝‍🧑 Members

<div align="center">
  <table width="100%">
  <tr>
    <td align="center"><a href="https://github.com/Kyoung-M1N">박경민</a></td>
    <td align="center"><a href=https://github.com/youtheyeon">유서연</a></td>
    <td align="center"><a href=https://github.com/khj011219">김현준</a></td>
  </tr>
  <tr>
    <td align="center"><img width="243" height="387" alt="KakaoTalk_Photo_2026-07-17-00-59-47 002" src="https://github.com/user-attachments/assets/aa7092af-5fe1-4e86-8ee2-a15f994918df" /></td>
    <td align="center"><img width="243" height="387" alt="KakaoTalk_Photo_2026-07-17-00-59-47 001" src="https://github.com/user-attachments/assets/fce6a2b0-9e5b-453a-9e2e-a33f946109c3" /></td>
    <td align="center"><img width="243" height="387" alt="KakaoTalk_Photo_2026-07-17-00-59-47 003" src="https://github.com/user-attachments/assets/cd33c9c9-d3bb-4ff8-9078-8fb82210c930" /></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/Kyoung-M1N">@Kyoung-M1N</a></td>
    <td align="center"><a href="https://github.com/youtheyeon">@youtheyeon</a></td>
    <td align="center"><a href="https://github.com/khj011219">@khj011219</a></td>
  </tr>
</table>
</div>

<br/>

## 🔑 핵심 기능

| 기능 | 설명 | 상태 |
| --- | --- | --- |
| 운동 | GPS 기반 산행 기록(거리·시간·고도·경로), 사진·메모 타임라인 기록 | 구현됨 (`routee-activity`) |
| 기록 편집 | 산행 종료 후 리캡(대표 사진, 통계) 생성 및 조회 | 구현됨 (`routee-activity`) |
| 아카이브 | 날짜별 산행 목록/요약 조회 | 구현됨 (`routee-member`) |
| 공식 코스 | 코스 정보 제공, 코스 기반 기록 | 예정 (`routee-course`, 스캐폴딩 단계) |
| 크루 | 크루 산행방 초대, 공동 기록 | 예정 (미구현) |

<br/>

## 🏗️ 아키텍처 방향

`routee`는 **모듈형 모놀리스(Modular Monolith)** 를 지향합니다.

- 런타임은 하나의 Spring Boot 애플리케이션입니다(`routee-app`이 조립·실행).
- 물리 모듈 경계는 Gradle 서브프로젝트(도메인 단위)이며, 각 모듈은 `api`(공개
  인터페이스)와 `internal`(비공개 구현)로 나뉘어 서로 `api`를 통해서만 상호작용합니다.
- Spring Modulith로 모듈 간 의존·경계를 검증합니다(`ModularityTests`).
- 현재는 단일 배포 단위지만, 도메인 모듈이 향후 개별 서비스로 분리(MSA 추출)될
  수 있는 형태를 목표로 설계합니다 — 어댑터는 바뀌어도 도메인 로직은 바뀌지
  않는 것을 원칙으로 합니다.

모듈 목록과 각 모듈의 책임, 계층별 규칙(UseCase, Command/Query, Domain Event,
Named Interface 등)은 [AGENTS.md](./AGENTS.md)에 상세히 정리되어 있습니다.

<br/>

## 🛠 기본 스택

| 구분 | 스택 |
| --- | --- |
| Language | Java 25 |
| Framework | Spring Boot 4.0.7 |
| Architecture support | Spring Modulith 2.0.7 |
| Build | Gradle (Groovy DSL) |
| Database | PostgreSQL + PostGIS (공간 데이터), Hibernate Spatial, JTS |
| Cache / Session | Redis |
| Storage | AWS S3 (Presigned URL) |
| Auth | JWT, OIDC (Apple) |
| ID | TSID (`hypersistence-utils`) |
| API Docs | springdoc-openapi (Swagger UI) |
| Observability | Micrometer Tracing + OpenTelemetry → Grafana Tempo |
| Infra | Docker Compose, Nginx(Blue-Green), GitHub Actions |

<br/>

## 🧷 ERDiagram

<img width="1457" height="475" alt="스크린샷 2026-07-17 오전 1 09 14" src="https://github.com/user-attachments/assets/50d04e03-13d3-4e04-bd77-aa53c8b8e5db" />

<br/>

## 🐳 Infrastructure

<table>
  <tr>
    <td>
      <img width="1131" height="741" alt="image" src="https://github.com/user-attachments/assets/de16c519-146d-4647-abf5-d49835c2212e" />
    </td>
  </tr>
</table>
