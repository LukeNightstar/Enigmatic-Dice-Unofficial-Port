# Enigmatic Dice — 26.1.2 포팅 설계

작성일: 2026-10-17

## 의도

Enigmatic Dice(원작: JrDemiurge, [Demis-Enigmatic-Dice](https://github.com/JrDemiurg/Demis-Enigmatic-Dice), 1.20.1 Forge, MIT)는
Enigmatic Legacy Plus의 애드온이다. Legacy Plus는 이미 본인 포크(`LukeNightstar/Enigmatic-Legacy-Plus`, 브랜치 `26.1.2`)에서
Minecraft 26.1.2 / NeoForge `26.1.2.122`로 포팅되어 있다. Dice도 같은 버전으로 옮겨서 **실제 게임에서 Legacy Plus 26.1.2와 함께 쓰는 것**이 목적이다.

출발점은 앞서 비공개로 진행한 1.21.1 포팅본(NeoForge 21.1.187, `mod_version=1.21.1-1.4.3`)이다.
원작 최신(1.3.1-1.20.1)의 내용은 이 포팅본에 들어 있다. 예외 두 가지:
- 미완성 자리표시 무기 4종(`kyomu`, `phoenix`, `fire_crystal_greatsword`, `fire_crystal_spear` — 원작의 `UnfinishedWeapon`)은 아이템 등록이 빠졌다.
  처리 방법은 "버그 수정" 4번 (사용자 결정: 제거).
- Giant's Ring·Ring of Agility·Divine Shield의 Curios 툴팁 한 줄을 숨기던 클라이언트 믹스인(`ClientEventHandlerMixin`)이 빠졌다.
  표시상의 차이일 뿐이라 1.21.1을 따라 넣지 않는다 (내 판단).

## 저장소

- 로컬: `/Users/delta/Desktop/Minecraft/Enigmatic-Dice-Unofficial-Port`
- GitHub: `LukeNightstar/Enigmatic-Dice-Unofficial-Port` (공개). 생성·push는 해당 단계에서 사용자 확인 후 진행.
- 브랜치 두 개로 관리 (사용자 결정):
  - `1.21.1` — 1.21.1 포팅본. 첫 커밋 `57a8eb0`이 비공개 포팅본 원본 그대로.
  - `26.1.2` — 26.1.2 포팅. `1.21.1`의 공통 작업이 끝난 뒤 그 위에서 분기.
- GitHub 기본 브랜치: `26.1.2` (내 판단 — 주로 쓸 버전).
- 커밋 작성자는 사용자 git 설정(Lunar) 그대로.

## 목표

1. `26.1.2` 브랜치: Minecraft 26.1.2 / NeoForge `26.1.2.122` / Curios `15.0.0+26.1.2` / Java 25에서 빌드되고,
   Legacy Plus 26.1.2와 함께 게임에서 동작한다.
2. 두 브랜치 공통:
   - `README.md` — Legacy Plus 26.1.2 README 형식을 따른다. 비공식 포팅임을 밝히고, 원작자와 원작 저장소, MIT 라이선스 승계를 명시.
     Credits 표: 원작(JrDemiurge) / 비공식 1.21.1·26.1.2 포팅(LukeNightstar).
   - `LICENSE` — MIT 전문. 원작 저장소에는 LICENSE 파일이 없고 `gradle.properties`의 `mod_license=MIT`가 유일한 표기이므로,
     저작권자를 `JrDemiurge`(원작)와 `LukeNightstar`(포팅 변경분) 두 줄로 적는다 (내 판단).
   - `ko_kr.json` 한국어 번역 신규 추가. `en_us.json` 키 전체를 번역한다(누락 키 0).
   - 1.21.1에서 이어진 버그 수정 (아래 "버그 수정").
   - `mods.toml` / `gradle.properties`의 `mod_authors`에 포팅 담당 추가: `JrDemiurge, LukeNightstar` (내 판단).
3. 게임 동작·밸런스·설정 기본값은 1.21.1과 동일하게 유지.

## 비목표

- 기능 추가·제거, 밸런스 변경 (예외: 버그 수정 3의 미완성 무기 잔재 제거).
- 원작 1.20.1 Forge 브랜치 유지보수.
- CurseForge/Modrinth 배포 (사용자가 원하면 별도 작업).
- 1.21.1 브랜치의 인게임 재검증 (빌드 확인까지만; 아래 "검증").

## 안티 목표 (기술적으로 돌아가도 실패로 보는 것)

- 26.1.2에서 이벤트·아이템이 "조용히" 빠지는 것: 컴파일을 통과시키려고 기능을 주석 처리하거나 no-op로 바꾸는 것은 금지.
  API가 사라져 그대로 옮길 수 없는 기능은 동등 동작으로 재구현하고, 불가능하면 작업을 멈추고 사용자에게 보고한다.
- 원작자 표기·라이선스 누락.
- Legacy Plus jar(라이선스: Enigmatic Legacy License)를 저장소에 커밋하는 것.

## 제약

- Legacy Plus 코드는 Dice에서 직접 import하지 않는다(확인함). 참조는 ID 문자열뿐이다:
  설정 JSON의 아이템 ID 22개(`earth_heart_fragment`, `astral_dust` 등 — 모두 Legacy Plus 26.1.2에 존재 확인)와 인챈트 `eternal_binding_curse`.
  → 빌드 의존성은 필요 없고, Legacy Plus는 개발 실행(runClient) 환경에만 넣는다.
  → 1.21.1 `build.gradle`의 `compileOnly files('libs/enigmaticlegacyplus-1.21.1-0.6.0.jar')`는 쓰이지 않으므로 제거.
- 26.1.2는 난독화가 없어져 Parchment를 쓰지 않는다(Legacy Plus 26.1.2도 미사용). Gradle 9.2.1, ModDevGradle `2.0.148`,
  Java 25 툴체인 — Legacy Plus 26.1.2의 `build.gradle`/wrapper 설정을 그대로 따른다.
- API 마이그레이션 방법은 Legacy Plus `26.1.2` 브랜치의 32개 커밋(`8651544`…`32409a2`)을 1차 참고 자료로 쓴다.
  레지스트리, 데이터 컴포넌트, 렌더 상태(render state), item model definition, 레시피 형식, 패킷, 믹스인 대상 변경이 모두 이미 해결되어 있다.
- 소스 규모: Java 123개(아이템·큐리오, 주사위 이벤트 약 45종, 엔티티 1종 `DragonclawHook`, 효과, 패킷 3종, 믹스인 6개),
  리소스(모델·텍스처·사운드·언어 5종, 설정 JSON, 데이터팩).
- 선택 의존 모드: 설정 JSON의 `requiredMod`로 Alex's Mobs/Caves 등 다른 모드 엔티티를 ID로 소환한다.
  해당 모드가 없으면 이벤트가 건너뛰어지는 기존 동작을 유지한다.
- Better Combat용 `data/enigmaticdice/weapon_attributes/*.json`은 그대로 유지(포맷 변경이 있으면 Better Combat 26.1.2 형식을 따름; 해당 모드가 없으면 무시됨).

## 버그 수정 (1.21.1에서 이어진 것)

1. **데이터 폴더 복수형**: `data/enigmaticdice/loot_tables/`, `recipes/`는 1.21부터 `loot_table/`, `recipe/`여야 한다.
   현재는 미믹·더미 상자 전리품 테이블 4개와 `moon` 레시피가 로드되지 않는다. 폴더명을 단수형으로 바꾸고,
   코드에서 이 테이블을 참조하는 경로(`ResourceKey`/`ResourceLocation`)도 함께 맞춘다. 레시피 JSON은 해당 버전 형식으로 갱신.
2. **영원한 속박 이벤트 ID**: `EternalBindingEvent`가 `enigmaticlegacy:eternal_binding`을 찾지만
   Legacy Plus의 실제 인챈트는 `enigmaticlegacyplus:eternal_binding_curse`. ID를 고친다.
3. **미완성 무기 4종 잔재** (사용자 결정: 제거): 아이템은 등록되지 않았는데 설정 JSON의 주사위 이벤트
   `enigmaticdice_give_kyomu` / `_phoenix` / `_fire_crystal_greatsword` / `_fire_crystal_spear`(rarity 1000, 활성)가 남아 있어,
   당첨되면 빈 아이템과 "선물" 메시지만 나온다. 이 4개 이벤트를 기본 설정에서 지우고, 남은 모델·텍스처·번역 키
   (`item.enigmaticdice.<id>`, `tooltip.enigmaticdice.unfinished_item_*`)·`weapon_attributes/<id>.json`도 지운다.
   주석 처리된 Kyomu 관련 코드(`KyomuNameplateOverlay`, `LivingHurtLowestHandler`의 Kyomu 부분)도 정리한다.
   사용자 config 자동 갱신 기능이 있으므로, 기존 사용자 설정 파일에 남은 이 이벤트들이 어떻게 처리되는지 확인하고
   존재하지 않는 아이템 ID를 가진 이벤트는 건너뛰도록(경고 로그) 방어 코드를 둔다.
4. 포팅 중 같은 종류(존재하지 않는 ID·경로를 조용히 무시하는 코드)를 더 발견하면 고치고, 커밋 메시지와 README "Modifications"에 기록한다.

## 작업 순서

1. `1.21.1` 브랜치 — 공통 작업: `.gitignore`(완료), README, LICENSE, `ko_kr.json`, 버그 수정 1~3, 미사용 jar 의존성 제거, `mod_authors`.
   `./gradlew build` 통과 확인.
2. `26.1.2` 브랜치 분기 — 빌드 설정 교체(Gradle wrapper, ModDevGradle, Java 25, neo/curios 버전, Parchment 제거,
   `mod_version=26.1.2-1.4.3`, mods.toml 버전 범위: `minecraft [26.1.2]`, `curios [15.0.0+26.1.2,)`,
   `enigmaticlegacyplus [26.1.2-1.1.2,)`).
3. Java 소스 포팅 — 컴파일 오류를 영역별로(레지스트리 → 아이템/큐리오 → 이벤트·효과 → 엔티티·클라이언트 렌더 → 패킷 → 믹스인) 해결, 영역별 커밋.
4. 리소스 포팅 — 26.1.2 형식: `assets/enigmaticdice/items/*.json`(item model definition), 모델 텍스처 참조 형식,
   태그·레시피·전리품 테이블 형식, 사운드, Curios 슬롯 데이터.
5. 실행 검증(아래).
6. README에 "Modifications"와 현재 상태 정리, GitHub 생성·push(사용자 확인 후).

## 검증

- `1.21.1`: `./gradlew build` 성공. 인게임 확인은 사용자 몫.
- `26.1.2`:
  1. `./gradlew build` 성공, 경고 중 deprecated-for-removal 사용 없음.
  2. `runClient`에 Legacy Plus 26.1.2 jar(포크 `26.1.2` 브랜치에서 빌드) + Patchouli + Curios를 넣고 실행.
     로그에 `enigmaticdice` 관련 ERROR/믹스인 실패/데이터팩 로드 실패가 없어야 한다.
  3. 새 월드에서: 모든 아이템이 크리에이티브 탭에 텍스처와 함께 보이는지, 언어를 한국어로 바꿨을 때 번역 키가 노출되지 않는지,
     `/enigmaticDice <eventType> true` 명령으로 주사위 이벤트를 강제 실행해 대표 이벤트(아이템 지급, 소환, 텔레포트, 폭발, 영원한 속박)가 동작하는지,
     무기(Soul Eater, Unequal Exchange, Crucible of Rile 등) 사용과 큐리오 장착 효과가 동작하는지, `moon` 레시피와 미믹 전리품이 동작하는지.
  4. 데디케이티드 서버(`runServer`)가 클라이언트 클래스 로딩 없이 기동하는지.
- 최종 인게임 확인은 사용자가 실제 모드팩에서 한다.

## 빌더에게 맡기는 것

- API 대체 방식의 세부 선택(Legacy Plus가 쓴 방식을 우선).
- 커밋 단위와 메시지.
- `ko_kr.json` 용어 선택 — Legacy Plus 26.1.2 포크의 한국어 번역(`assets/enigmaticlegacyplus/lang/ko_kr.json`)과 용어를 맞춘다.
