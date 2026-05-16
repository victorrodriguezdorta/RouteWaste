import 'leaflet/dist/leaflet.css';
import * as L from 'leaflet';

/** Leaflet empaquetado localmente (evita CDN unpkg y avisos de Tracking Prevention). */
export async function loadLeaflet(): Promise<typeof L> {
  return L;
}

export { L };
