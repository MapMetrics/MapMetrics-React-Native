import { MapView } from "@maplibre/maplibre-react-native";
import { Alert } from "react-native";

import { sheet } from "../../styles/sheet";

export function ShowMap() {
  return (
    <MapView 
      style={sheet.matchParent} 
      mapStyle="fileName=91cf50f5-e3cb-45d3-a1ab-f2f575f6c9b2/urbcalm.json"
      mapToken="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.MapmetricsStyleURL .mAtqzwAPCcQhyEr45AgPaCFor4hePZu7tpoUrJUGwGs"
      onWillStartLoadingMap={() => Alert.alert('Map', 'Will start loading map')}
      onDidFinishLoadingMap={() => Alert.alert('Map', 'Did finish loading map')}
      onDidFailLoadingMap={() => Alert.alert('Map', 'Did fail loading map')}
      onDidFinishLoadingStyle={() => Alert.alert('Map', 'Did finish loading style')}
    />
  );
}
