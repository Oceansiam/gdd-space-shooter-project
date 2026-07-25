# Cropped sprites — browse & pick later

Each folder below came from one sprite sheet you uploaded. Inside each folder:

- `_<name>_preview.png` — the **original full sheet** with every detected
  piece outlined in red and numbered. Open this first to see what's what.
- `..._01_WxH.png`, `..._02_WxH.png`, etc. — the individual cropped pieces,
  already made transparent (background removed) so they're ready to drop
  into the game like your existing `player.png` / `alien.png`. The number
  matches the label in the preview image, and `WxH` is the crop size in
  pixels.

## Folder guide

| Folder | Source | Notes |
|---|---|---|
| `darius_mother_hawk_boss` | Darius II — Mother Hawk | 2 full ship poses + separate wing/blade pieces + a small weapon-icon row. Good boss candidate. |
| `gradius_big_core_boss` | Gradius III — Big Core MK III | Built from many separate armor-plate graphics rather than clean poses — pick the couple of largest pieces for a boss "core" look, ignore the tiny ones. |
| `metroid_gunship_player` | Metroid — Fusion Starship | **Cleanest one of the batch.** Two rows of 7 near-identical 182×106 frames — this is an actual turn/bank rotation animation, evenly spaced (basically a real grid). Great if you want an animated player ship later. |
| `sigma_star_enemy_ships` | Sigma Star Saga | ~15 distinct small enemy/boss ships, each already its own crop. Good enemy variety pool. |
| `silius_exodus_boss` | Journey to Silius — Giant Exodus | One huge boss split into a top half + bottom half (pieces #1–#2), plus lots of small palette/effect swatches. Use #1+#2 as the main boss body. |
| `vic_viper_player` | Parodius — Vic Viper | This is a dense labeled reference sheet (weapon icons, power-up icons, ship + explosion frames), not just animation frames — separation is rougher here since it's information-dense. Piece #1 (256×130) has the main ship + icon bar. |
| `zig01_player` | Zero Wing — ZIG-01 | Very clean: ~8 ship bank-angle poses, ~9 engine-thruster puffs, a few trail/beam graphics. Good player-ship candidate with banking animation. |
| `nemesis_enemy_ships` | Nemesis '94 / Gradius 2 | A handful of distinct enemy warship variants. |
| `powerups_challenger_tank` | Supervision — Challenger Tank | 5 clean 20×20 power-up icons (piece #1 is just the whole bordered strip, ignore it). |
| `powerups_galaxy_fighter` | Supervision — Galaxy Fighter | 4 clean 20×20 power-up icons (same deal, ignore #1). |
| `powerups_matta_blatta` | Supervision — Matta Blatta | 2 clean 28×28 icons (#10, #11) plus some tiny fragments from a thin decorative strip — the 28×28 ones are the useful icons. |

## How to actually use one later

Your game currently loads **one static image per sprite type** — no
animation system. So for a first pass:

1. Pick a crop (e.g. `zig01_player_04_32x27.png`).
2. Rename it to something like `player.png` (or add a new `IMG_...` constant
   in `Global.java` pointing at it).
3. Drop it in `src/images/`, exactly like the existing art.

If you want a sprite to actually animate (e.g. the Metroid gunship's 14
rotation frames, or ZIG-01's banking poses), that needs a bigger change —
`Sprite` holding an array of frames plus a frame timer that cycles them.
That's a good "phase 2" once you've picked which specific ship/boss you're
going with — just tell me which folder + which numbered pieces and I'll
wire it up.

## A heads-up on sourcing

These are all fan-made rips (mostly from The Spriters Resource) of
copyrighted commercial game art — Darius, Gradius, Metroid, Zero Wing,
Parodius, etc. Several sheets even have "credit not necessary" notes from
the rippers, but the underlying art is still owned by Taito/Konami/Nintendo/
Toaplan. That's almost certainly fine for a personal/school project, but
worth keeping in mind if this game ever gets published or distributed
publicly — you'd want either original art or a clearer license at that
point.
