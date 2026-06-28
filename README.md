# BiggerStacksSafetyValve

`BiggerStacksSafetyValve` is a **safety valve mod designed to prevent freezes and server crashes that occur in environments where the maximum item stack size has been significantly expanded by mods like BiggerStacks.**

In addition to setting an upper limit on excessive loop processing during mass crafting, it improves game and server stability by optimizing the recipe auto-fill algorithm itself.

---

## Supported Environments

- **Minecraft Version:** 1.20.1
- **Required Mod Environment:** Minecraft Forge 47.4 or higher

### Dedicated Patch Targets
Dedicated patches and optimizations are applied to the following systems and mods to prevent performance degradation:
- **Vanilla**
- **Applied Energistics 2 (AE2)**
- **Just Enough Items (JEI)** (Rewrites recipe transfer processing to $O(1)$ arithmetic calculation)
- **Tom's Storage**
- **Refined Storage**
- **FastWorkbench**

### Confirmed Working & Compatible
Normal operation has been confirmed in environments utilizing the following mods:
- **BiggerStacks** (The core stack size expansion mod)
- **EMI** (Since EMI natively adopts an efficient approach that does not rely on loop processing, it can be used together safely)

---

## Key Features (Background and Countermeasures)

### 1. Execution Count Limitation in Various Crafting Terminals (Vanilla, AE2, RS, etc.)
When performing bulk crafting via shift-clicking in environments with extremely large stack sizes, a massive number of crafting loops run within a single tick, causing the game or server to freeze. This feature directly applies a **safety limit on the maximum number of crafting operations allowed in a single action** to Vanilla, various mod crafting terminals, and FastWorkbench's unique crafting processes. This preemptively prevents thread lockups caused by runaway processing.

### 2. Algorithmic Optimization for JEI Recipe Transfer
Unlike other crafting processes where a hard count limit is applied, this feature optimizes the processing logic itself for JEI's automatic recipe placement (the `+` button). It eliminates the heavy loop process that scans inventory slots sequentially to match items, rewriting it into a computational logic that utilizes **$O(1)$ mathematical formulas**. Because it calculates the required amount of items mathematically in an instant, transfers are completed extremely fast even in giant stack environments.

### 3. Automatic Compatibility Handling
Upon startup, the mod automatically detects the presence of supported mods and dynamically applies restrictions and patches only to the targeted crafting terminals and logics.

---

## Problems Solved by This Mod

- Long freezes caused by bulk crafting (shift-clicking) in various mod crafting terminals
- Unresponsiveness caused by scanning loops during JEI recipe transfers
- Long freezes in FastWorkbench environments
- Server Watchdog forced crashes resulting from crafting processes monopolizing the main thread

---

## Design Philosophy

`BiggerStacksSafetyValve` does not alter the core specifications of the crafting system, item behaviors, or game balance.

For recipe transfer processes like JEI, it rewrites the heavy scanning loops into **instant $O(1)$ calculations**. For other inventory and crafting processes, it functions as a protective layer that imposes an upper limit (default value: 8192). Its sole purpose is to allow modpacks with giant stack environments to operate safely and stably, even under extreme stack sizes.

---

# BiggerStacksSafetyValve (日本語)

`BiggerStacksSafetyValve` は、**BiggerStacks などのModによってアイテムの最大スタック数が大幅に拡張された環境で発生するフリーズ、サーバークラッシュを防止するためのセーフティバルブ（安全弁）Mod**です。

大量クラフトにおける過剰なループ処理に上限を設定するほか、レシピ自動配置のアルゴリズム自体を最適化することで、ゲームやサーバーの安定性を向上させます。

---

## 対応環境

- **Minecraft バージョン:** 1.20.1
- **前提Mod環境:** Minecraft Forge 47.4以上

### 専用パッチ適用対象
以下のシステムおよびModに対して、パフォーマンス低下を防ぐための専用パッチ・最適化を適用します：
- **Vanilla (バニラ)**
- **Applied Energistics 2 (AE2)**
- **Just Enough Items (JEI)** (レシピ転送処理を $O(1)$ の算術計算へ書き換え)
- **Tom's Storage**
- **Refined Storage**
- **FastWorkbench**

### 動作確認済み・互換
以下のModとの併用環境で正常に動作することを確認しています：
- **BiggerStacks** (スタック上限拡張コア)
- **EMI** (EMIは標準でループ処理に依存しない効率的アプローチを採用しているため、安全に併用可能です)

---

##  主な機能（背景と対策）

### 1. 各種クラフト端末での実行回数制限（バニラ、AE2、RS、等）
非常に大きなスタックサイズ環境でシフトクリックによる一括クラフトを行うと、1ティック内に膨大なクラフトループが走り、ゲームやサーバーがフリーズします。本機能は、バニラや各種Modのクラフト端末、およびFastWorkbenchの独自クラフト処理に対して、**1回のアクションで実行可能なクラフト回数の上限（安全制限）**を直接適用します。これにより、処理の暴走によるスレッドの停止を未然に防ぎます。

### 2. JEI レシピ転送のアルゴリズム最適化
回数制限をかける他のクラフト処理とは異なり、JEIのレシピ自動配置（`+` ボタン）に対しては処理自体の最適化を行います。インベントリのスロットを順に走査してアイテムを照合する重いループ処理を排除し、**$O(1)$ の数式計算** を用いた計算ロジックへと書き換えます。アイテムの必要量を数学的に一瞬で算出するため、巨大スタック環境でも極めて高速に転送を完了します。

### 3. 自動互換処理
起動時に導入されている対応Modの存在を自動検出し、対象のクラフトターミナルやロジックに対してのみ動的に制限・パッチを適用します。

---

## このModが解決する問題

- 各種Modのクラフト端末での一括クラフト（シフトクリック）による長時間フリーズ
- JEIレシピ転送時の走査ループによる応答停止
- FastWorkbench環境での長時間フリーズ
- クラフト処理のメインスレッド占有に起因するサーバーWatchdogの強制終了

---

## ️ 設計方針

`BiggerStacksSafetyValve` はクラフトシステムそのものの仕様や、アイテムの挙動、ゲームバランスを変更するModではありません。

JEIなどのレシピ転送処理に対しては、重い走査ループを排除し**瞬時に終わる $O(1)$ 計算** へリライトを行います。その他のインベントリやクラフト処理に対しては、上限を設定(デフォルト値8192)する保護レイヤーとして機能します。巨大スタック環境のModパックを、極端なスタックサイズでも安全かつ安定して動作させることを目的としています。