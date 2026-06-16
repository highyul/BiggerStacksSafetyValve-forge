# ExtendedStackThrottler

`ExtendedStackThrottler` は、**BiggerStacks** 等のModによってアイテムの最大スタック数が数万個規模に拡張された環境において、大量クラフトやレシピ自動配置が引き起こす**無限ループ、サーバーのフリーズ、およびクラッシュ（強制終了）を未然に防ぐためのセーフティネット（安全弁）Mod**です。

---

## 💡 開発背景と解決する課題

### 巨大スタック環境における「処理ループの暴走」
通常、Minecraftのスタック上限は64個ですが、これを数万個に拡張するMod環境では、クラフト時の内部の計算回数が膨大な数に跳ね上がります。

1. **一括クラフト（シフトクリック）**: 材料が尽きるまでクラフトを繰り返す処理が、数万回規模で一瞬の間に実行されてしまいます。
2. **JEI等のレシピ自動転送（+ボタンのシフトクリック）**: 大量に保持されたアイテムの計算処理が追いつかなくなり、ボトルネック化します。

これらの結果、ゲームの処理が完全に占有され、画面が数秒〜数分間フリーズしたり、サーバーが応答停止と判断して強制終了（クラッシュ）してしまいます。本Modは、これらの**ループ処理に対して安全なブレーキ（制限）をかけ、ゲームがスムーズに動き続ける状態を維持**します。

---

## 🛠️ 主な機能

- **バニラのクラフト制限**: 通常の作業台等での一括クラフト時に、1回のアクションで実行される計算回数を安全な上限値（デフォルト: 8192回）で強制的にストップさせます。
- **FastBench（FastWorkbench）との互換・連携**: 高速クラフトMod `FastBench（FastWorkbench）` が導入されている場合、その特殊な処理によって発生する暴走ループを検知し、安全な上限値（デフォルト: 8192回）で処理を遮断。フリーズを完全に防止します。
- **自動的な機能切り替え**: 起動時に他のMod（FastWorkbench等）が導入されているかを自動で検知し、競合（不具合）を起こさずに必要な修正パッチだけを適用します。

---

## 📐 実装予定の機能

- **AE2 & Refined Storage への対応**:
  大型倉庫Modのクラフト画面や自動クラフトシステムに対応。プレイヤーの画面操作を通さないバックグラウンドでの巨大なクラフト処理に対しても、安全な上限値でブレーキをかけます。
- **JEI レシピ自動転送のフリーズ対策**:
  JEI（Just Enough Items）のレシピ自動配置機能（[+] ボタンのシフトクリック）に介入。スタック数が数万個ある環境でも、アイテムの配置計算によってフリーズしたり、サーバーが応答停止する現象を抑制します。


# ExtendedStackThrottler

`ExtendedStackThrottler` is a **safety-net (failsafe) mod designed to prevent infinite loops, server freezes, and crashes (forced shutdowns)** caused by mass crafting and recipe auto-placement in modded environments where item stack limits have been extended to tens of thousands by mods such as **BiggerStacks**.

---

## 💡 Background and Problems Addressed

### Runaway Processing Loops in Extreme Stack Environments

Under normal Minecraft conditions, item stack sizes are capped at 64. However, in modded environments where this limit is expanded to tens of thousands, the internal computation cost of crafting operations increases dramatically.

1. **Bulk Crafting (Shift-click crafting)**  
   Crafting continues repeatedly until materials are depleted, resulting in tens of thousands of iterations executed almost instantaneously.

2. **JEI Recipe Transfer (Shift-click “+” button)**  
   When transferring large quantities of items via recipe helper mods, the system may become bottlenecked due to excessive item calculation and placement operations.

As a result, the game can become fully occupied by these operations, leading to multi-second or multi-minute freezes, or in worst cases, server watchdog-triggered shutdowns (crashes). This mod introduces a **controlled braking mechanism (execution limits)** to ensure stable gameplay.

---

## 🛠️ Core Features

- **Vanilla Crafting Limiter**  
  During bulk crafting in standard crafting tables, the number of executed crafting iterations per action is forcibly capped at a safe threshold (default: 8192 operations).

- **FastBench / FastWorkbench Compatibility Layer**  
  Detects high-speed crafting behavior introduced by `FastBench (FastWorkbench)` and applies the same safety cap (default: 8192 operations) to prevent runaway loops and freezes.

- **Automatic Feature Switching**  
  On startup, the mod automatically detects the presence of other mods (e.g., FastWorkbench) and selectively applies only the necessary patches to avoid conflicts and ensure compatibility.

---

## 📐 Planned Features

- **AE2 & Refined Storage Support**  
  Extends protection to large storage and automated crafting systems. Even background crafting processes that bypass the player UI are constrained by safe execution limits.

- **JEI Recipe Transfer Freeze Prevention**  
  Intercepts JEI (Just Enough Items) recipe auto-transfer functionality (shift-click “+” button). Prevents freezes and server hangups caused by item placement calculations in environments with extremely large stack sizes.