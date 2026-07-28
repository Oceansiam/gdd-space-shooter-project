# Space Shooter

A side-scrolling space shoot-'em-up built for the CSX4615 Game Development course
project. Extended from the
[mchayapol/gdd-space-invaders-project](https://github.com/mchayapol/gdd-space-invaders-project)
starter codebase.

## Team

| Name | Student ID |
|---|---|
| Taian Chen | 6630027 |
| Kriidipas Kongsakul | 6640031 |

## Overview

Pilot a ship from the left edge of the screen and fight through two full stages of
enemies, side-scrolling shoot-'em-up style, ending in a boss fight against the
Mother Hawk.

- **Stage 1** - clear 50 enemies to advance. Enemies: a shared animated ship type,
  plus a fast "speed assassin" enemy that drifts up and down as it closes in.
- **Stage 2** - clear 60 enemies to summon the boss. Enemies here are bigger and
  tougher: an animated two-frame mecha (fires two parallel rows of shots) and a
  faster ship reused from Stage 1's art.
- **Boss fight** - the Mother Hawk is a multi-hit "bullet sponge" with its own
  health bar, movement pattern, and a multi-frame explosion sequence on death.
- Power-up levels, and the ship itself, carry over from Stage 1 into Stage 2
  instead of resetting.

## Controls

| Key / Action | Effect |
|---|---|
| Arrow keys | Move the ship |
| Space | Fire |
| Title screen | Click **START**, or press Space/Enter |
| Game Over / Win screen | Click **PLAY AGAIN** (or press Space) to retry, **MAIN MENU** (or Esc) to return to the title screen |

## Features

**Stages & progression**
- Two full stages, each with its own enemy roster, background, and difficulty
- A capped number of enemies on screen at once, so the action stays readable
  even as the spawn rate ramps up
- Kill-progress counter (`kills / target`) shown at the top of the HUD
- Player stats (speed level, shot level, lives) carry over from Stage 1 into
  Stage 2

**Enemies**
- Multiple distinct enemy types per stage, each with its own art, movement
  pattern (straight-line, sine-wave drift, fast approach), and - where
  applicable - its own animation
- The boss survives many hits instead of dying in one, tracked with a visible
  health bar, and has its own entrance and attack pattern

**Power-ups**
- **Speed Up** - 2 stages, each adding to the ship's movement speed
- **Multi-shot** - 4 stages, each adding one more simultaneous bullet fired in a
  fanned spread
- **Heart Up** - restores one life, capped at the stage's max lives

**Player**
- A heart-based lives system with a brief invulnerability window (and visual
  flicker) after taking a hit, instead of dying to any single touch
- Animated engine-thruster flicker on the player ship

**Presentation**
- Custom title screen with team credits and a clickable Start button
- Parallax-scrolling backgrounds and a twinkling starfield per stage
- HUD showing score, kill progress, speed, shot level, and remaining lives
- Game Over and Victory screens with Play Again / Main Menu options

## Running the game

```bash
cd gdd-space-shooter-project
javac -d out $(find src/gdd -name "*.java")
java -cp out gdd.Main
```

Run these commands from inside the `gdd-space-shooter-project` folder - asset
paths (e.g. `src/images/...`, `src/audio/...`) are resolved relative to the
working directory the game is launched from, not the location of the `.java`
files. If you're launching from an IDE instead, make sure its run
configuration's working directory is set to this folder.

## Project structure

```
src/gdd/
  Game.java           Top-level JFrame; switches between title/stage/boss screens
  Global.java         Shared constants (sizes, speeds, image paths, tuning values)
  ImageUtil.java       Image loading/scaling/tinting helpers
  scene/              TitleScene, Scene1 (Stage 1), Scene2 (Stage 2 + boss)
  sprite/             Player, Enemy, and all enemy/boss/projectile subclasses
  powerup/            SpeedUp, MultiShot, HeartUp
src/images/           Sprites, backgrounds, and title/HUD art
src/audio/            Background music and sound effects
```

## References

- Starter codebase: [mchayapol/gdd-space-invaders-project](https://github.com/mchayapol/gdd-space-invaders-project),
  itself based on [janbodnar/Java-Space-Invaders](https://github.com/janbodnar/Java-Space-Invaders)
- Sprite references: [The Spriters Resource](https://www.spriters-resource.com/)
