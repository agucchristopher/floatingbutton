import React, { useEffect } from "react";
import { View, Button, Alert } from "react-native";
import FloatingWidgetService from "./FloatingWidget.js";

export default function App() {
  const setupWidget = async () => {
    try {
      const hasPermission = await FloatingWidgetService.checkPermission();

      if (!hasPermission) {
        await FloatingWidgetService.requestPermission();
      }

      await FloatingWidgetService.showWidget();
    } catch (error) {
      Alert.alert("Error", "Failed to show floating widget");
    }
  };

  return (
    <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
      <Button title="Show Floating Widget" onPress={setupWidget} />
      <Button
        title="Hide Floating Widget"
        onPress={() => FloatingWidgetService.hideWidget()}
      />
    </View>
  );
}
