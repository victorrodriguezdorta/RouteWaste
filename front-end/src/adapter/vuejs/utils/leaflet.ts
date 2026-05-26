import {
  divIcon,
  latLngBounds,
  layerGroup,
  map,
  marker,
  polyline,
  tileLayer,
} from 'leaflet';
import 'leaflet/dist/leaflet.css';

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
