<img width="2300" alt="Routee Banner" src="https://github.com/user-attachments/assets/0545f1e5-1adf-4f2e-96c8-7ca4e2e46243" />

# Routee <img width="100" alt="Routee Logo" src="https://github.com/user-attachments/assets/5a69a5aa-603e-460e-bc67-0d1ec17a3a8b" align="left" />

<h3>정상이 아닌, 여정을 기록하는 트렌디한 등산 경험 플랫폼</h3>

<br/>

> 긴 호흡의 운동에서 남긴 사진과 운동 데이터, 추억을 하나로 연결해 누구나 쉽게 편집하고 공유할 수 있도록 돕는 플랫폼

<br/>

## <img width="24" alt="shape1" src="https://github.com/user-attachments/assets/a00194e1-b34c-4ad4-8d7a-ba1cae42313b" align="absmiddle" /> Members

<div align="center">
  <table width="100%">
  <tr>
    <td align="center"><a href="https://github.com/Kyoung-M1N">박경민</a></td>
    <td align="center"><a href="https://github.com/youtheyeon">유서연</a></td>
    <td align="center"><a href="https://github.com/khj011219">김현준</a></td>
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

## <img width="28" alt="shape2" src="https://github.com/user-attachments/assets/41e09367-88fc-4b6b-9ada-d59a75b01ac9" align="absmiddle" /> Key Features

| 기능    | 설명                                       | 상태                            |
|-------|------------------------------------------|-------------------------------|
| 운동    | GPS 기반 산행 기록(거리·시간·고도·경로), 사진·메모 타임라인 기록 | 구현됨 (`routee-activity`)       |
| 기록 편집 | 산행 종료 후 리캡(대표 사진, 통계) 생성 및 조회            | 구현됨 (`routee-activity`)       |
| 아카이브  | 날짜별 산행 목록/요약 조회                          | 구현됨 (`routee-member`)         |
| 공식 코스 | 코스 정보 제공, 코스 기반 기록                       | 예정 (`routee-course`, 스캐폴딩 단계) |
| 크루    | 크루 산행방 초대, 공동 기록                         | 예정 (미구현)                      |

<br/>

## <img width="30" alt="shape3" src="https://github.com/user-attachments/assets/8891ea8a-b28c-4028-ab9c-22cb51a7d4e0" align="absmiddle" /> Architecture

Routee 서버는 **모듈형 모놀리스(Modular Monolith)** 구조를 기반으로 설계했습니다.

| Module            | Responsibility                                          |
|-------------------|---------------------------------------------------------|
| `routee-app`      | Spring Boot 애플리케이션의 진입점으로, 각 도메인 모듈을 조립하고 실행합니다.        |
| `routee-common`   | 공통 응답, 예외 처리, 인증 필터, 타입 변환 등 여러 모듈에서 사용하는 공통 기능을 제공합니다. |
| `routee-auth`     | Apple OIDC 로그인, JWT 발급 및 재발급 등 사용자 인증 흐름을 담당합니다.        |
| `routee-member`   | 회원 프로필과 월별·날짜별 활동 요약 등 사용자 중심 조회 기능을 담당합니다.             |
| `routee-activity` | 산행 기록, 이동 경로, 사진 타임라인, 운동 통계와 리캡 데이터를 관리합니다.            |
| `routee-course`   | 공식 등산 코스와 코스 기반 기록 기능을 담당하며 현재 확장을 준비하고 있습니다.           |
| `routee-external` | AWS S3를 비롯한 외부 시스템과의 연동을 어댑터 형태로 제공합니다.                 |

- 하나의 Spring Boot 애플리케이션으로 실행되며, `routee-app`이 각 도메인 모듈을 조립합니다.
- 도메인별 Gradle 서브프로젝트를 구성해 물리적인 모듈 경계를 명확하게 분리했습니다.
- 각 모듈은 외부에 공개하는 `api`와 내부 구현을 담는 `internal` 패키지로 구분합니다.
- 모듈 간 통신은 공개된 UseCase, Command, Query, Domain Event를 통해 이루어집니다.
- Spring Modulith의 `ModularityTests`를 활용해 순환 참조와 잘못된 모듈 의존성을 검증합니다.
- 데이터베이스와 외부 서비스에 대한 접근은 어댑터에 위임해 핵심 도메인 로직과 인프라 코드를 분리합니다.

현재는 하나의 배포 단위로 운영하지만, 필요할 경우 도메인 모듈을 독립 서비스로 분리할 수 있는 구조를 지향합니다.
<br/>
모듈별 공개 인터페이스와 의존 규칙은 [AGENTS.md](./AGENTS.md)에 상세히 정리되어 있습니다.

<br/>

## <img width="24" alt="shape4" src="https://github.com/user-attachments/assets/70ea7cb3-25a4-498d-9903-be311083c9f4" align="absmiddle" /> Tech Stack

| 구분                   | 스택                                                    |
|----------------------|-------------------------------------------------------|
| Language             | Java 25                                               |
| Framework            | Spring Boot 4.0.7                                     |
| Architecture support | Spring Modulith 2.0.7                                 |
| Build                | Gradle (Groovy DSL)                                   |
| Database             | PostgreSQL + PostGIS (공간 데이터), Hibernate Spatial, JTS |
| Cache / Session      | Redis                                                 |
| Storage              | AWS S3 (Presigned URL)                                |
| Auth                 | JWT, OIDC (Apple)                                     |
| ID                   | TSID (`hypersistence-utils`)                          |
| API Docs             | springdoc-openapi (Swagger UI)                        |
| Observability        | Micrometer Tracing + OpenTelemetry → Grafana Tempo    |
| Infra                | Docker Compose, Nginx(Blue-Green), GitHub Actions     |

<br/>

## <img width="28" alt="shape6" src="https://github.com/user-attachments/assets/80089e5b-7754-4f0e-af17-8f662ea5e402" align="absmiddle" /> ERDiagram

<img width="1457" height="475" alt="스크린샷 2026-07-17 오전 1 09 14" src="https://github.com/user-attachments/assets/50d04e03-13d3-4e04-bd77-aa53c8b8e5db" />

<br/>

## <img width="24" alt="shape5" src="https://github.com/user-attachments/assets/6a6721bd-6342-499a-b056-884360ef2e48" align="absmiddle" /> Infrastructure

<table>
  <tr>
    <td>
      <img width="1131" height="741" alt="image" src="https://github.com/user-attachments/assets/de16c519-146d-4647-abf5-d49835c2212e" />
    </td>
  </tr>
</table>
