import { divIcon, type DivIcon } from 'leaflet';

export type MapPinTone = 'muted' | 'primary' | 'success';

const TONE_FILL: Record<MapPinTone, string> = {
  muted: 'rgb(var(--v-theme-map-pin-muted))',
  primary: 'rgb(var(--v-theme-primary))',
  success: 'rgb(var(--v-theme-success))',
};

function buildPinSvg(fill: string): string {
  return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 36" width="28" height="42" aria-hidden="true" style="display:block;filter:drop-shadow(0 2px 3px rgba(var(--v-theme-neutral-base),0.35))"><path fill="${fill}" stroke="rgb(var(--v-theme-surface))" stroke-width="1.1" stroke-linejoin="round" d="M12 1C6.48 1 2 5.48 2 11c0 7.75 10 23 10 23s10-15.25 10-23C22 5.48 17.52 1 12 1z"/><circle cx="12" cy="11" r="3.6" fill="rgb(var(--v-theme-surface))"/></svg>`;
}

/** SVG map pin for Leaflet (avoids broken default marker images under Vite). */
export function createMapPinIcon(tone: MapPinTone = 'success', interactive = false): DivIcon {
  const className = interactive ? 'map-pin-icon map-pin-icon--interactive' : 'map-pin-icon';

  return divIcon({
    className,
    html: `<div class="map-pin-icon__pin">${buildPinSvg(TONE_FILL[tone])}</div>`,
    iconSize: [28, 42],
    iconAnchor: [14, 40],
  });
}
