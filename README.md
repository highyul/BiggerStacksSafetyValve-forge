#### 下部に日本語の説明があります
# BiggerStacksSafetyValve

`BiggerStacksSafetyValve` is a **safety-valve mod designed to prevent freezes, infinite loops, and server crashes in environments where item stack sizes have been dramatically increased by mods such as BiggerStacks.**

The mod protects the game by applying safe execution limits to crafting-related operations that would otherwise perform excessive numbers of iterations when handling extremely large item stacks.

## Supported Environment

- Minecraft Forge 47.4+
- Minecraft 1.20.1
- BiggerStacks compatible
- Applied Energistics 2 (AE2) compatible
- Just Enough Items (JEI) compatible
- tomsstorage compatible
- Refined Storage compatible

---

## 💡 Background

In vanilla Minecraft, item stacks are limited to 64 items.

However, when mods such as BiggerStacks increase stack sizes into the thousands or even tens of thousands, the computational cost of crafting-related operations can increase dramatically.

The following actions are particularly problematic:

### 1. Bulk Crafting (Shift-Click)

- Crafting repeats until materials are exhausted.
- In large-stack environments, this may result in thousands or tens of thousands of crafting iterations.

### 2. JEI Recipe Transfer

- Recipe auto-fill operations perform item matching, slot searching, and transfer calculations.
- Extremely large item counts can cause severe lag or complete freezes.

### 3. AE2 Crafting Terminals

- Large storage networks containing oversized stacks may trigger excessive calculations when determining craftable amounts or processing crafting requests.

When these operations spiral out of control, the game may become unresponsive, freeze for extended periods, or trigger server watchdog shutdowns.

`BiggerStacksSafetyValve` prevents these situations by applying safe execution limits before dangerous processing loops can consume the entire game thread.

---

## 🛠️ Features

### Crafting Safety Limits

Applies configurable safety caps to bulk crafting operations performed through vanilla crafting interfaces.

If an abnormal number of iterations is detected, processing is halted before it can freeze the game.

---

### JEI Recipe Transfer Protection

Adds protection for JEI recipe transfer operations, including both standard transfers and shift-click transfers.

Prevents excessive slot-searching and item-placement calculations from causing freezes in large-stack environments.

---

### AE2 Crafting Terminal Support

Supports Applied Energistics 2 crafting terminals and related interfaces.

Protects crafting request calculations and item insertion logic from generating excessive processing loads when handling oversized stacks.

---

### FastBench / FastWorkbench Compatibility

Automatically detects `FastBench` / `FastWorkbench` and applies dedicated safeguards to their optimized crafting implementations.

This prevents runaway crafting loops while maintaining compatibility with high-speed crafting mods.

---

### Automatic Compatibility Handling

Detects supported mods during startup and applies only the necessary patches.

This minimizes conflicts while ensuring the appropriate protections are active for the current modpack.

---

## 🎯 Problems Solved

- Long freezes caused by bulk crafting in BiggerStacks environments
- JEI recipe transfer lockups
- Excessive processing load in AE2 crafting terminals
- Runaway loops caused by FastBench/FastWorkbench integrations
- Server watchdog crashes and forced shutdowns
- Crafting-related hangs caused by extremely large stack sizes

---

## ⚙️ Design Philosophy

`BiggerStacksSafetyValve` does not alter crafting mechanics or game balance.

Instead, it focuses solely on preventing pathological processing behavior by applying safety limits where necessary.

The goal is to provide a lightweight and reliable protection layer that allows large-stack modpacks to remain stable even under extreme conditions.





# BiggerStacksSafetyValve

`BiggerStacksSafetyValve` は、**BiggerStacks などのModによってアイテムの最大スタック数が大幅に拡張された環境で発生するフリーズ、無限ループ、サーバークラッシュを防止するためのセーフティバルブ（安全弁）Mod**です。

本Modは、大量クラフトやレシピ自動配置によって発生する過剰なループ処理に上限を設けることで、ゲームやサーバーの安定性を維持します。

## 対応環境

- Minecraft Forge 47.4以上
- Minecraft 1.20.1
- BiggerStacks対応
- Applied Energistics 2（AE2）対応
- Just Enough Items（JEI）対応
- tomsstorage 対応
- Refined Storage 対応
---

## 💡 開発背景

通常のMinecraftでは、アイテムスタック数は64個に制限されています。

しかし、BiggerStacksのようなModによってスタック上限が数千〜数万個まで拡張されると、クラフト関連処理の計算量が急激に増加します。

特に以下のような操作で問題が発生しやすくなります。

### 1. 一括クラフト（シフトクリック）

- 材料が尽きるまでクラフト処理を繰り返します。
- 巨大スタック環境では数千〜数万回規模のループが発生する場合があります。

### 2. JEIレシピ転送

- レシピ自動配置時にアイテム検索やスロット探索が大量に実行されます。
- 極端なスタック数ではラグやフリーズの原因になります。

### 3. AE2クラフト端末

- 巨大スタックを保持したストレージネットワークでクラフト要求を行う際、クラフト可能数の計算や投入処理が大幅に増加する場合があります。

これらの処理が暴走すると、ゲームの応答停止やサーバーWatchdogによる強制終了を引き起こします。

`BiggerStacksSafetyValve` は、危険なループ処理に安全な実行上限を適用することで、こうした問題を未然に防ぎます。

---

## 🛠️ 主な機能

### クラフト処理の安全制限

通常のクラフトGUIや作業台で実行される一括クラフト処理に対して、安全な実行上限を適用します。

異常な回数のループが発生した場合でも、ゲーム全体がフリーズする前に処理を停止します。

---

### JEIレシピ転送の保護

JEIのレシピ自動配置機能（`+` ボタンおよびシフトクリック転送）を保護します。

巨大スタック環境で発生しやすい過剰なスロット探索や配置計算によるフリーズを防止します。

---

### AE2クラフト端末対応

Applied Energistics 2（AE2）のクラフト端末および関連GUIに対応しています。

巨大スタック環境でクラフト要求を行った際に発生する大量処理に対して安全制限を適用し、極端な負荷による停止を防ぎます。

---

### FastBench / FastWorkbench互換

`FastBench` / `FastWorkbench` が導入されている環境を自動検出し、専用の保護処理を適用します。

高速クラフトModによる大量ループ処理にも対応し、フリーズやクラッシュを防止します。

---

### 自動互換処理

起動時に対応Modの存在を自動検出し、必要なパッチのみを適用します。

不要な処理や競合を避けながら、各Mod環境に適した保護機能を提供します。

---

## 🎯 このModが解決する問題

- BiggerStacks環境での一括クラフトによる長時間フリーズ
- JEIレシピ転送時の応答停止
- AE2クラフト端末での過剰な計算負荷
- FastBench / FastWorkbench環境での暴走ループ
- サーバーWatchdogによる強制終了
- 極端なスタックサイズによるクラフト処理の暴走

---

## ⚙️ 設計方針

`BiggerStacksSafetyValve` はクラフトシステムそのものを変更するModではありません。

危険な処理にのみ安全上限を適用し、通常プレイ時の挙動やゲームバランスへの影響を最小限に抑えています。

巨大スタック環境でも安定してプレイできることを目的とした、軽量な保護Modです。