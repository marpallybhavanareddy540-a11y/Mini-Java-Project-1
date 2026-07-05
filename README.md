# Connect Four — for young champions 🔴🟡

A colorful, kid-friendly desktop implementation of the classic Connect Four game, built with Java Swing.

![Java](https://img.shields.io/badge/Java-21-orange)
![Swing](https://img.shields.io/badge/GUI-Swing-blue)
![License](https://img.shields.io/badge/License-MIT-green)

## Features

- 🧑‍🤝‍🧑 **Play with a Friend** — local two-player mode, take turns dropping pieces
- 🤖 **Play with AI** — two difficulty levels:
  - **Easy** — mostly random moves, occasionally grabs an obvious win
  - **Hard** — wins when possible, blocks your winning moves, prefers center columns
- 🎨 Custom-painted UI with gradients, rounded buttons, and animated hover effects
- 🏆 Score tracking across rounds (Red vs Gold/AI)
- 🖱️ Simple point-and-click controls — hover over a column to preview your drop, click to play

## Requirements

- Java Development Kit (JDK) 8 or higher (tested on JDK 21)

## How to Run

Clone the repository and run from the command line:

```bash
git clone https://github.com/<your-username>/<your-repo>.git
cd <your-repo>
javac ConnectFourSwing.java
java ConnectFourSwing
```

> **Tip:** If you rebuild after editing the source, clear old compiled classes first to avoid stale-bytecode issues:
> ```bash
> rm -f *.class
> javac ConnectFourSwing.java
> java ConnectFourSwing
> ```

## How to Play

1. Launch the app and choose **Play with a Friend** or **Play with AI**.
2. If playing against the AI, pick a difficulty: **Easy** or **Hard**.
3. Players take turns clicking a column to drop their piece (Red goes first).
4. First to connect four pieces in a row — horizontally, vertically, or diagonally — wins the round!
5. Click **New Round** to keep playing and build up your score, or **Menu** to return to the main screen.

## Project Structure

```
ConnectFourSwing.java   # Single-file application (UI, game logic, and simple AI)
LICENSE                 # MIT License
README.md               # This file
```

## License

This project is licensed under the [MIT License](LICENSE) — free to use, modify, and distribute, with attribution.
