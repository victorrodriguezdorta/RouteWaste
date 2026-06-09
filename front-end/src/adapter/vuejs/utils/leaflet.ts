import {
  divIcon,
  Icon,
  latLngBounds,
  layerGroup,
  map,
  marker,
  polyline,
  tileLayer,
} from 'leaflet';
import iconUrl from 'leaflet/dist/images/marker-icon.png';
import iconRetinaUrl from 'leaflet/dist/images/marker-icon-2x.png';
import shadowUrl from 'leaflet/dist/images/marker-shadow.png';
import 'leaflet/dist/leaflet.css';

// Vite bundles marker images with hashed URLs; Leaflet defaults break in production (Docker).
Icon.Default.mergeOptions({
  iconUrl,
  iconRetinaUrl,
  shadowUrl,
});

export type LeafletApi = {
  divIcon: typeof divIcon;
  latLngBounds: typeof latLngBounds;
  layerGroup: typeof layerGroup;
  map: typeof map;
  marker: typeof marker;
  polyline: typeof polyline;
  tileLayer: typeof tileLayer;
};

const leafletApi: LeafletApi = {
  divIcon,
  latLngBounds,
  layerGroup,
  map,
  marker,
  polyline,
  tileLayer,
};

/** Leaflet empaquetado localmente (evita CDN unpkg y avisos de Tracking Prevention). */
export async function loadLeaflet(): Promise<LeafletApi> {
  return leafletApi;
}
