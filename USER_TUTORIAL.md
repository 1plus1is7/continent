# Continent 플러그인 사용자 튜토리얼

이 문서는 **Continent** 플러그인의 주요 시스템을 플레이어 입장에서 쉽게 이해할 수 있도록 정리한 가이드입니다. 명령어 앞에는 `/`를 붙여 게임 내 채팅창에서 입력하세요.

## 1. 크라운 경제 시스템
- `crown balance` : 현재 보유 크라운 확인
- `crown exchange` : 금괴를 크라운으로 환전
- `crown convert` : 크라운으로 금괴 구매
- `crown pay <플레이어> <금액>` : 다른 플레이어에게 크라운 송금

환율은 중앙은행에서 관리되며 서버 상황에 따라 변동됩니다.

## 2. 국가 시스템
- `nation create <이름>` : 새로운 국가 설립
- `nation disband` : 국가 해산
- `nation claim` / `nation unclaim` : 영토 점령 및 해제
- `nation invite <플레이어>` : 국가 초대
- `nation members` : 현재 구성원 목록
- `nation treasury balance|deposit|withdraw` : 국가 금고 관리
- `nation setspawn` / `nation spawn` : 국가 스폰 위치 설정 및 이동
- `nation chest` : 국가 전용 창고 열기
- `nation menu` : GUI 기반 관리 메뉴

국가별로 고유한 색상과 이름을 지정할 수 있으며 모든 국가는 주간 유지비를 납부해야 합니다.

## 3. 전쟁 시스템
- `war declare <국가>` : 전쟁 선포 (촌장 전용)
- `war status` : 현재 전쟁 현황 확인
- `war surrender` : 항복 선언

전쟁 중에는 상대 국가 영토를 파괴할 수 있으며, 코어 블록의 체력이 0이 되면 패배합니다.

## 4. 시장과 경제 활동
- `market` : 플레이어 시장 GUI 열기
- `enterprise register <이름> <업종>` : 개인 기업 설립
- `job` : 직업 선택 및 정보 확인

아이템을 사고팔거나 기업을 운영하여 더 많은 크라운을 벌어 보세요. 자세한 기업 시스템은 `ENTERPRISE_SYSTEM.md`에서 확인할 수 있습니다.

## 5. 연합과 특별 기능
- `union create <이름>` : 국가 연합 생성
- `union join <연합>` : 기존 연합 가입
- `specialty` : 국가 특산품 관리
- `guide list` : 제공되는 가이드북 목록 확인
- `menu` : 서버 주요 기능을 모은 메뉴 열기

- 플레이어가 사망해도 경험치 레벨은 잃지 않습니다.

이 외에도 연구, 계약, 채팅 등 다양한 요소가 있으며 데이터는 서버 내 YAML 파일로 안전하게 저장됩니다.

플레이하면서 도움이 필요하면 언제든 `/guide <주제>` 명령을 사용해 더 자세한 설명을 읽어 보세요. 즐거운 플레이 되시길 바랍니다!
