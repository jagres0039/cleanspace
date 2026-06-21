# CleanSpace — Icon Pack

Brand mark: a rounded-square (squircle) with a green diagonal gradient, a white
"swipe/check" stroke, and three shrinking sparkle dots (the "clean" gesture).

## Colors

| Token | Hex |
|-------|-----|
| Green light | `#5BE291` |
| Green base  | `#28B870` |
| Green deep  | `#178A51` |
| Mark        | `#FFFFFF` |

Gradient: linear, top-left -> bottom-right, stops at 0 / 0.52 / 1.

## Files in this repo (fully vector — no PNG needed for API 26+)

```
app/src/main/res/
  drawable/ic_launcher_background.xml   gradient background
  drawable/ic_launcher_foreground.xml   check + particles
  drawable/ic_launcher_monochrome.xml   themed-icon layer
  mipmap-anydpi-v26/ic_launcher.xml       adaptive icon
  mipmap-anydpi-v26/ic_launcher_round.xml  round adaptive icon
design/icon.svg                          master source (512x512)
```

The adaptive icon covers Android 8.0 (API 26) and up. For API 24-25 legacy
launchers, drop the per-density PNG mipmaps (`mipmap-mdpi` ... `mipmap-xxxhdpi`)
from the delivered `CleanSpace-launcher-icons.zip` into `app/src/main/res/`.

## Play Store

Use `ic_launcher-playstore.png` (512x512) from the zip for the store listing.

## Regenerating PNGs from the SVG

```bash
npx sharp-cli -i design/icon.svg -o ic_launcher.png resize 512 512
```
